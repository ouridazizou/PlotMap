package com.zourida.sowitmaps.presenter.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zourida.sowitmaps.presenter.ui.appContent
import com.zourida.sowitmaps.presenter.ui.theme.SowitMapsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SowitMapsTheme {
                appContent()
            }
        }
    }
}