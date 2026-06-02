package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource

object ReasonBuilder {
    fun build(
        title: String,
        detail: String,
        impact: ReasonImpact,
        source: ReasonSource
    ): Reason = Reason(
        title = title,
        detail = detail,
        impact = impact,
        source = source
    )
}
