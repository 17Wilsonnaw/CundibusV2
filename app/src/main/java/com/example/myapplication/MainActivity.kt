package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyAppNavigation()
            }
        }
    }
}

@Composable
fun MyAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") { RouteApp(navController) }
        composable("tripDetails/{origin}/{destination}/{estimatedTime}/{distance}/{fare}") { backStackEntry ->
            val origin = backStackEntry.arguments?.getString("origin")
            val destination = backStackEntry.arguments?.getString("destination")
            val estimatedTime = backStackEntry.arguments?.getString("estimatedTime")
            val distance = backStackEntry.arguments?.getString("distance")
            val fare = backStackEntry.arguments?.getString("fare")
            TripDetailsScreen(navController, origin, destination, estimatedTime!!, distance!!, fare!!)
        }
        composable("mapScreen/{origin}/{destination}") { backStackEntry ->
            val origin = backStackEntry.arguments?.getString("origin")
            val destination = backStackEntry.arguments?.getString("destination")
            MapScreen(navController, origin, destination)
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val validUsers = mapOf("user1" to "password1", "user2" to "password2")

    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (validUsers[username] == password) {
                    navController.navigate("home")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteApp(navController: NavHostController) {
    val chia = LatLng(4.8631, -74.0324)
    val terminalZipa = LatLng(4.7963, -74.0270)
    val tabio = LatLng(4.9187, -74.0921)

    val lugares = mapOf(
        "Chía" to chia,
        "Terminal de Zipa" to terminalZipa,
        "Tabio" to tabio
    )

    var selectedOrigin by remember { mutableStateOf<String?>(null) }
    var selectedDestination by remember { mutableStateOf<String?>(null) }
    var selectedRoute by remember { mutableStateOf<List<LatLng>?>(null) }
    var showMap by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selecciona tu ubicación", style = MaterialTheme.typography.bodyLarge)

        DropdownMenuComponent(
            label = "Donde estoy",
            options = lugares.keys.toList(),
            selectedOption = selectedOrigin,
            onOptionSelected = { selectedOrigin = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenuComponent(
            label = "A donde quiero ir",
            options = lugares.keys.toList(),
            selectedOption = selectedDestination,
            onOptionSelected = { selectedDestination = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedOrigin != null && selectedDestination != null) {
                    val originPoint = lugares[selectedOrigin!!]
                    val destinationPoint = lugares[selectedDestination!!]
                    selectedRoute = listOf(originPoint!!, destinationPoint!!)
                    showMap = true

                    navController.navigate("tripDetails/${selectedOrigin}/${selectedDestination}/30 mins/10 km/2.50 USD")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir")
        }

        if (showMap && selectedRoute != null) {
            Spacer(modifier = Modifier.height(16.dp))
            MapScreenContent(routePoints = selectedRoute!!)
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
fun MapScreen(navController: NavHostController, origin: String?, destination: String?) {
    val routePoints = listOf(LatLng(4.8631, -74.0324), LatLng(4.9187, -74.0921)) // Ejemplo de ruta

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Atrás")
        }
        MapScreenContent(routePoints = routePoints)
    }
}

@Composable
fun MapScreenContent(routePoints: List<LatLng>) {
    val initialPosition = routePoints.firstOrNull() ?: LatLng(4.8631, -74.0324)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        cameraPositionState = cameraPositionState
    ) {
        Polyline(points = routePoints, color = Color.Blue, width = 5f)
        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(initialPosition, 12f))
    }
}

@Composable
fun TripDetailsScreen(
    navController: NavHostController,
    origin: String?,
    destination: String?,
    estimatedTime: String,
    distance: String,
    fare: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Detalles del Viaje", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Origen: $origin", style = MaterialTheme.typography.bodyMedium)
        Text("Destino: $destination", style = MaterialTheme.typography.bodyMedium)
        Text("Tiempo Estimado: $estimatedTime", style = MaterialTheme.typography.bodyMedium)
        Text("Distancia: $distance", style = MaterialTheme.typography.bodyMedium)
        Text("Costo Aproximado: $fare", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("mapScreen/$origin/$destination")
        }) {
            Text("Iniciar Viaje")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Atrás")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        RouteApp(rememberNavController())
    }
}
