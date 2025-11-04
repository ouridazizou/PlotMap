package com.zourida.sowitmaps.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.zourida.sowitmaps.data.entity.Plot
import com.zourida.sowitmaps.data.entity.PlotPoint

data class PlotWithPoints(
    @Embedded
    val plot: Plot,
    @Relation(
        parentColumn = "id",
        entityColumn = "plotId",
        entity = PlotPoint::class
    )
    val points: List<PlotPoint>
) {
}