package com.cavstecnologia.trabalhofinalapiscar

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cavstecnologia.trabalhofinalapiscar.adapter.CarAdapter
import com.cavstecnologia.trabalhofinalapiscar.database.DatabaseBuilder
import com.cavstecnologia.trabalhofinalapiscar.database.model.UserLocation
import com.cavstecnologia.trabalhofinalapiscar.databinding.ActivityMainBinding
import com.cavstecnologia.trabalhofinalapiscar.service.RetrofitClient
import com.cavstecnologia.trabalhofinalapiscar.service.safeApiCall
import com.cavstecnologia.trabalhofinalapiscar.service.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient;
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

            requestLocationPermission();
            setupView();
        }

    override fun onResume() {
        super.onResume();
        fetchCars();
    }

    private fun setupView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this);
        binding.swipeRefreshLayout.setOnRefreshListener { fetchCars(); }
        binding.addCta.setOnClickListener { navigateToNewCar(); }
        binding.btnLogout.setOnClickListener { onLogout() }
    }

    private fun onLogout(){
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this, LoginActivity::class.java);
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
        startActivity(intent);
        finish();
    }

    private fun fetchCars() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getCars() };

            withContext(Dispatchers.Main) {
                binding.swipeRefreshLayout.isRefreshing = false;
                when (result) {
                    is Result.Success -> {
                        val adapter = CarAdapter(result.data) { car ->
                            startActivity(CarDetailActivity.newIntent( this@MainActivity, car.id) );
                        }
                        binding.recyclerView.adapter = adapter;
                    }
                    is Result.Error -> { Toast.makeText(this@MainActivity,"Erro ao carregar carros da API",Toast.LENGTH_SHORT).show(); }
                }
            }
        }
    }

    private fun navigateToNewCar(){
        startActivity(NewCarActivity.newIntent(this))
    }
    private fun requestLocationPermission() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) getLastLocation();
            else Toast.makeText(this, "Permissão de localização não concedida!", Toast.LENGTH_SHORT).show();
        }
        checkLocationPermissionAndRequest();
    }

    private fun checkLocationPermissionAndRequest() {
        when {
            checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED -> {
                getLastLocation();
            }
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                // Pedindo a permissão do usuário para ACCESS_FINE_LOCATION
                locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
            shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) -> {
                // Pedindo a permissão do usuário para ACCESS_COARSE_LOCATION
                locationPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            }
            else -> {
                // Fallback em caso de algum erro na verificação da permissão
                locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }


    private fun getLastLocation() {
        if (
            ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful) {
                val location: Location = task.result ?: Location("").apply { latitude = 0.0; longitude = 0.0 }
                Log.d("TAG", "Location: ${location}");
                Log.d("Hello World", "Lat: ${location.latitude} Long: ${location.longitude}");

                val userLocation = UserLocation(latitude = location.latitude, longitude = location.longitude)

                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseBuilder.getInstance()
                        .userLocationDao()
                        .insert(userLocation)
                }
            } else {
                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    companion object{
        //USAR ESSE CARA A PARTIR DE LOGINACTIVITY
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java);
    }
}

