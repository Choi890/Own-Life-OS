package com.ownlifeos.ui.screens.morning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.automation.CheckInEstimator
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class MorningCheckInUiState(
    val date: String = DateUtils.todayKey(),
    val sleepHours: Double = 7.0,
    val mood: Int = 3,
    val bodyCondition: Int = 3,
    val burdenLevel: Int = 3,
    val memo: String = "",
    val autoFilled: Boolean = false,
    val estimateReasons: List<String> = emptyList(),
    val saved: Boolean = false
)

class MorningCheckInViewModel(
    private val checkInRepository: CheckInRepository,
    taskRepository: TaskRepository,
    reviewRepository: ReviewRepository
) : ViewModel() {
    private val date = DateUtils.todayKey()
    private val recentStart = DateUtils.daysBefore(date, 6)
    private var loadedExisting = false
    private var userEdited = false

    private val _uiState = MutableStateFlow(MorningCheckInUiState(date = date))
    val uiState: StateFlow<MorningCheckInUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                checkInRepository.observeByDate(date),
                checkInRepository.observeRange(recentStart, date),
                taskRepository.observeByDate(date),
                taskRepository.observeRange(recentStart, date),
                reviewRepository.observeRange(recentStart, date)
            ) { checkIn, recentCheckIns, todayTasks, recentTasks, recentReviews ->
                val estimate = CheckInEstimator.estimate(
                    date = date,
                    recentCheckIns = recentCheckIns,
                    todayTasks = todayTasks,
                    recentTasks = recentTasks,
                    recentReviews = recentReviews
                )
                Pair(checkIn, estimate)
            }.collect { (checkIn, estimate) ->
                if (checkIn != null && !loadedExisting) {
                    loadedExisting = true
                    _uiState.update {
                        it.copy(
                            sleepHours = checkIn.sleepHours,
                            mood = checkIn.mood,
                            bodyCondition = checkIn.bodyCondition,
                            burdenLevel = checkIn.burdenLevel,
                            memo = checkIn.memo,
                            autoFilled = false,
                            estimateReasons = emptyList(),
                            saved = true
                        )
                    }
                } else if (checkIn == null && !userEdited) {
                    _uiState.update {
                        it.copy(
                            sleepHours = estimate.sleepHours,
                            mood = estimate.mood,
                            bodyCondition = estimate.bodyCondition,
                            burdenLevel = estimate.burdenLevel,
                            memo = estimate.memo,
                            autoFilled = true,
                            estimateReasons = estimate.reasons,
                            saved = false
                        )
                    }
                }
            }
        }
    }

    fun updateSleepHours(value: Double) {
        val rounded = (value * 2).roundToInt() / 2.0
        userEdited = true
        _uiState.update { it.copy(sleepHours = rounded.coerceIn(0.0, 12.0), autoFilled = false, saved = false) }
    }

    fun updateMood(value: Int) {
        userEdited = true
        _uiState.update { it.copy(mood = value.coerceIn(1, 5), autoFilled = false, saved = false) }
    }

    fun updateBodyCondition(value: Int) {
        userEdited = true
        _uiState.update { it.copy(bodyCondition = value.coerceIn(1, 5), autoFilled = false, saved = false) }
    }

    fun updateBurdenLevel(value: Int) {
        userEdited = true
        _uiState.update { it.copy(burdenLevel = value.coerceIn(1, 5), autoFilled = false, saved = false) }
    }

    fun updateMemo(value: String) {
        userEdited = true
        _uiState.update { it.copy(memo = value, autoFilled = false, saved = false) }
    }

    fun save() {
        val current = _uiState.value
        viewModelScope.launch {
            checkInRepository.save(
                date = current.date,
                sleepHours = current.sleepHours,
                mood = current.mood,
                bodyCondition = current.bodyCondition,
                burdenLevel = current.burdenLevel,
                memo = current.memo
            )
            loadedExisting = true
            _uiState.update { it.copy(autoFilled = false, saved = true) }
        }
    }
}
