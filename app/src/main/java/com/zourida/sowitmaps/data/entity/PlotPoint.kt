package com.zourida.sowitmaps.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "plot_points",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Plot::class,
            parentColumns = ["id"],
            childColumns = ["plotId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("plotId")]
)
data class PlotPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val x: Double,
    val y: Double,
    val plotId: Long,
    val orderIndex: Int

)