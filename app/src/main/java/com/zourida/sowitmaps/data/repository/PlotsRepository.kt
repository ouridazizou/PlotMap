package com.zourida.sowitmaps.data.repository

import com.google.android.gms.maps.model.LatLng
import com.zourida.sowitmaps.data.dao.PlotDao
import com.zourida.sowitmaps.data.entity.Plot
import com.zourida.sowitmaps.data.entity.PlotPoint
import com.zourida.sowitmaps.data.model.PlotWithPoints
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlotsRepository @Inject constructor(
    private val plotDao: PlotDao
) {
    suspend fun savePlot(name: String, latLngs: List<LatLng>) {
        val id = plotDao.insert(Plot(name = name))
        val pts = latLngs.mapIndexed { i, ll ->
            PlotPoint(
                plotId = id,
                orderIndex = i,
                x = ll.latitude,
                y = ll.longitude
            )
        }
        plotDao.insertPoints(pts)
    }
    fun observeAll(): Flow<List<PlotWithPoints>> = plotDao.observePlotsWithPoints()
}