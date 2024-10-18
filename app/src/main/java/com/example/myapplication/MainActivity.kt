package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState



import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
    import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp

import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            RouteApp()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



















@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteApp() {
    // Puntos predefinidos para las rutas
    val chia = LatLng(4.8631, -74.0324)
    val terminalZipa = LatLng(4.7963, -74.0270)
    val tabio = LatLng(4.9187, -74.0921)

    // Mapa de rutas predefinidas
    val lugares = mapOf(
        "Chía" to chia,
        "Terminal de Zipa" to terminalZipa,
        "Tabio" to tabio
    )

    // Estados para la selección del origen, destino y la ruta seleccionada
    var selectedOrigin by remember { mutableStateOf<String?>(null) }
    var selectedDestination by remember { mutableStateOf<String?>(null) }
    var selectedRoute by remember { mutableStateOf<List<LatLng>?>(null) }
    var showMap by remember { mutableStateOf(false) }

    // Interfaz de selección
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selecciona tu ubicación", style = MaterialTheme.typography.bodyLarge)

        // Dropdown para origen
        DropdownMenuComponent(
            label = "Donde estoy",
            options = lugares.keys.toList(),
            selectedOption = selectedOrigin,
            onOptionSelected = { selectedOrigin = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown para destino
        DropdownMenuComponent(
            label = "A donde quiero ir",
            options = lugares.keys.toList(),
            selectedOption = selectedDestination,
            onOptionSelected = { selectedDestination = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para mostrar el mapa
        Button(
            onClick = {
                if (selectedOrigin != null && selectedDestination != null) {
                    // Establece la ruta seleccionada
                    val originPoint = lugares[selectedOrigin!!]
                    val destinationPoint = lugares[selectedDestination!!]
                    selectedRoute = listOf(originPoint!!, destinationPoint!!)
                    showMap = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir")
        }

        // Mostrar el mapa si se ha seleccionado una ruta
        if (showMap && selectedRoute != null) {
            Spacer(modifier = Modifier.height(16.dp))
            MapScreen(selectedRoute)
        }
    }
}

@Composable
fun DropdownMenuComponent(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedOption ?: "") }

    Column {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = currentSelection.ifEmpty { "Selecciona..." },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                label = { Text(label) }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            currentSelection = option
                            expanded = false
                            onOptionSelected(option)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MapScreen(routePoints: List<LatLng>?) {
    // Define un punto inicial para centrar el mapa
    val initialPosition = routePoints?.firstOrNull() ?: LatLng(4.8631, -74.0324)

    // Estado de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    // Muestra el mapa
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        cameraPositionState = cameraPositionState
    ) {
        // Dibujar la Polyline si la ruta ha sido seleccionada
        routePoints?.let {
            Polyline(
                points = it,
                color = Color.Blue,
                width = 5f
            )

            // Mover la cámara al primer punto de la ruta seleccionada
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it[0], 12f))
        }
    }
}












@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}