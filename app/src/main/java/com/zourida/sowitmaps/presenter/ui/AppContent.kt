package com.zourida.sowitmaps.presenter.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.zourida.sowitmaps.presenter.main.PlotViewModel


@Composable
fun appContent(){
    val vm: PlotViewModel = hiltViewModel()
    PlotScreen(vm = vm)
}