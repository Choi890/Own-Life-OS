package com.ownlifeos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact

@Composable
fun ReasonList(
    reasons: List<Reason>,
    modifier: Modifier = Modifier,
    limit: Int = 3
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        reasons.take(limit).forEach { reason ->
            Text(
                text = "${impactLabel(reason.impact)} ${reason.title}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = reason.detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ImpactChip(
    impact: ReasonImpact,
    modifier: Modifier = Modifier
) {
    AssistChip(
        modifier = modifier,
        onClick = {},
        label = { Text(impactLabel(impact)) }
    )
}

private fun impactLabel(impact: ReasonImpact): String = when (impact) {
    ReasonImpact.LOW -> "낮음"
    ReasonImpact.MEDIUM -> "중간"
    ReasonImpact.HIGH -> "높음"
}
