package com.zourida.sowitmaps.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zourida.sowitmaps.data.dao.PlotDao
import com.zourida.sowitmaps.data.entity.Plot
import com.zourida.sowitmaps.data.entity.PlotPoint


@Database(
    entities = [Plot::class, PlotPoint::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun plotDao(): PlotDao

}