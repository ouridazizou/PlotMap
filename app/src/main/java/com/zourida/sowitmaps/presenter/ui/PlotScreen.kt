@file:OptIn(ExperimentalMaterial3Api::class)

package com.zourida.sowitmaps.presenter.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.zourida.sowitmaps.presenter.main.PlotViewModel


@Composable
fun PlotScreen(
    vm: PlotViewModel
) {
    val context = LocalContext.current
    var drawnPoints by remember { mutableStateOf(listOf<LatLng>()) }

    var dropDownExpanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1)}

    val  plots by vm.plots.collectAsState()

    var projection by remember { mutableStateOf<Projection?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(33.5731, -7.5898), 6f)
    }
    var drawMode by remember { mutableStateOf(false) }

    var saveShowDialog by remember { mutableStateOf(false) }
    var nameField by remember { mutableStateOf(TextFieldValue("")) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            ExposedDropdownMenuBox(
                expanded = dropDownExpanded,
                onExpandedChange = { dropDownExpanded = !dropDownExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = if (selectedIndex in plots.indices) plots[selectedIndex].plot.name else "Saved plots",
                    onValueChange = {},
                    label = { Text("Select Saved plot") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth())
                ExposedDropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = false },
                ){
                    plots.forEachIndexed { index, plot ->
                        DropdownMenuItem(
                            text = { Text(text = plot.plot.name) },
                            onClick = {
                                selectedIndex = index
                                dropDownExpanded = false

                                val latLngs = plot.points
                                    .sortedBy { it.orderIndex }
                                    .map { LatLng(it.x, it.y) }

                                animateToBounds(cameraPositionState, latLngs)

                                drawnPoints = latLngs
                            }
                        )
                    }
                }
            }

            OutlinedButton(onClick = {drawnPoints = emptyList()}) {
                Text(text = "Clear")
            }

            Button(
                onClick = {
                    if (drawnPoints.size < 3){
                        Toast.makeText(context, "Add at least 3 Points", Toast.LENGTH_SHORT).show()
                    }else{
                        nameField = TextFieldValue("")
                        saveShowDialog = true
                    }
                }) {
                Text("Save")
            }
        }

        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxSize()){
            // Map gestures are enabled only when NOT drawing
            val ui = remember(drawMode) {
                MapUiSettings(
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = !drawMode,
                    scrollGesturesEnabled = !drawMode,
                    rotationGesturesEnabled = !drawMode,
                    tiltGesturesEnabled = !drawMode
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                // Tap to add a vertex
                onMapClick = { latLng ->
                    drawnPoints = drawnPoints + latLng
                },
                // Long-press to undo last point (optional)
                onMapLongClick = {
                    if (drawnPoints.isNotEmpty()) {
                        drawnPoints = drawnPoints.dropLast(1)
                    }
                },
                uiSettings = ui
            ) {
                // Show a marker for each tapped point
                drawnPoints.forEachIndexed { index, p ->
                    Marker(
                        state = rememberMarkerState(position = p),
                        title = "Point ${index + 1}"
                    )
                }

                // Draw edges while user is tapping
                if (drawnPoints.isNotEmpty()) {
                    Polyline(
                        points = drawnPoints,
                        width = 6f,
                        color = Color.Black
                    )
                }

                // When we have at least 3 points, fill a polygon
                if (drawnPoints.size >= 3) {
                    Polygon(
                        points = drawnPoints,
                        fillColor = Color.Blue.copy(alpha = 0.2f),
                        strokeColor = Color.Blue,
                        strokeWidth = 6f
                    )
                }

                MapEffect { map -> projection = map.projection }
            }

        }
    }

    if (saveShowDialog) {
        AlertDialog(
            onDismissRequest = { saveShowDialog = false },
            title = { Text("Save Plot") },
            text = {
                OutlinedTextField(
                    value = nameField,
                    onValueChange = { nameField = it },
                    label = { Text("Plot Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = nameField.text.trim()
                        if (name.isNotEmpty()) {
                            vm.savePlot(name, drawnPoints) {
                                Toast.makeText(context, "Plot saved successfully", Toast.LENGTH_SHORT).show()
                                saveShowDialog = false

                            }
                        } else {
                            Toast.makeText(context, "Plot name cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = { OutlinedButton (onClick = { saveShowDialog = false }) { Text("Cancel") } }
        )
    }
}

private fun animateToBounds(camera: CameraPositionState, points: List<LatLng>){
    if (points.isEmpty()) return
    val builder = LatLngBounds.builder()
    points.forEach {
        builder.include(it)
    }
    val bounds = builder.build()

    camera.move(CameraUpdateFactory.newLatLngBounds(bounds, 100))
}
