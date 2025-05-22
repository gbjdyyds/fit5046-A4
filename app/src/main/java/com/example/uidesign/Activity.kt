package com.example.ass4

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.example.uidesign.screens.HomeScreen
import com.example.uidesign.viewmodel.HomeViewModel
import com.example.uidesign.viewmodel.HomeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.app.AlertDialog
// -------------------- RegisterActivity --------------------
class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreenPlaceholder()
        }
    }
}

@Composable
fun RegisterScreenPlaceholder() {
    Text("Register Screen Placeholder")
}

// -------------------- ForgotPasswordActivity --------------------
class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgotPasswordScreenPlaceholder()
        }
    }
}

@Composable
fun ForgotPasswordScreenPlaceholder() {
    Text("Forgot Password Screen Placeholder")
}

// -------------------- HomeActivity --------------------
class HomeActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        homeViewModel = HomeViewModelFactory(application).create(HomeViewModel::class.java)

        // Register the permission launcher only once
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocationAndUpdateWeather()
            } else {
                Toast.makeText(this, "Permission denied. Showing Melbourne (Australia) weather.", Toast.LENGTH_SHORT).show()
                homeViewModel.fetchWeatherByLocation(-37.8136, 144.9631)
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showOpenSettingsDialog()
                }
            }
        }

        // Request location permission and update weather
        requestLocationAndUpdateWeather()

        setContent {
            HomeScreen(homeViewModel)
        }
    }

    private fun requestLocationAndUpdateWeather() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, locationPermission) == PackageManager.PERMISSION_GRANTED) {
            getLocationAndUpdateWeather()
        } else {
            // Show dialog to explain why location is needed
            AlertDialog.Builder(this)
                .setTitle("Location Permission Needed")
                .setMessage("This app needs location permission to show your local weather. Please allow location access.")
                .setPositiveButton("Allow") { _, _ ->
                    // Use the already-registered launcher
                    permissionLauncher.launch(locationPermission)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "Permission denied. Showing Melbourne (Australia) weather.", Toast.LENGTH_SHORT).show()
                    homeViewModel.fetchWeatherByLocation(-37.8136, 144.9631)
                }
                .show()
        }
    }

    private fun showOpenSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Location in Settings")
            .setMessage("You have permanently denied location permission. Please enable it in app settings to get local weather.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getLocationAndUpdateWeather() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    homeViewModel.fetchWeatherByLocation(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, "Location unavailable. Showing Melbourne (Australia) weather.", Toast.LENGTH_SHORT).show()
                    homeViewModel.fetchWeatherByLocation(-37.8136, 144.9631)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get location. Showing Melbourne (Australia) weather.", Toast.LENGTH_SHORT).show()
                homeViewModel.fetchWeatherByLocation(-37.8136, 144.9631)
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission error. Showing Melbourne (Australia) weather.", Toast.LENGTH_SHORT).show()
            homeViewModel.fetchWeatherByLocation(-37.8136, 144.9631)
        }
    }
}
