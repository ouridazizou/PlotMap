package com.zourida.sowitmaps.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.zourida.sowitmaps.data.entity.Plot
import com.zourida.sowitmaps.data.entity.PlotPoint
import com.zourida.sowitmaps.data.model.PlotWithPoints
import kotlinx.coroutines.flow.Flow


@Dao
interface PlotDao {
    @Insert
    suspend fun insert(plot: Plot): Long

    @Insert
    suspend fun insertPoints(points: List<PlotPoint>)

    @Transaction
    @Query("SELECT * FROM plots ORDER BY name ASC")
    fun observePlotsWithPoints(): Flow<List<PlotWithPoints>>

}