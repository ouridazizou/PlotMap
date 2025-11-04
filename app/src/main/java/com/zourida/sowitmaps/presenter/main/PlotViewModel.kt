package com.zourida.sowitmaps.presenter.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.zourida.sowitmaps.data.repository.PlotsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
class PlotViewModel @Inject constructor(
    private val plotsRepository: PlotsRepository
) : ViewModel() {

    val plots = plotsRepository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun savePlot(
        name: String,
        points: List<LatLng>,
        onDone : () -> Unit ){
        viewModelScope.launch {
            plotsRepository.savePlot(name, points)
            onDone()
        }
    }
}