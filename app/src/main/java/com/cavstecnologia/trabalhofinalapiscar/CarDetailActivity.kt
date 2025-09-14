package com.cavstecnologia.trabalhofinalapiscar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cavstecnologia.trabalhofinalapiscar.databinding.ActivityCarDetailBinding
import com.cavstecnologia.trabalhofinalapiscar.model.Car
import com.cavstecnologia.trabalhofinalapiscar.service.RetrofitClient
import com.cavstecnologia.trabalhofinalapiscar.service.safeApiCall
import com.cavstecnologia.trabalhofinalapiscar.service.Result
import com.cavstecnologia.trabalhofinalapiscar.ui.loadUrl
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityCarDetailBinding;
    private lateinit var car: Car;
    private lateinit var mMap: GoogleMap;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge();
        binding = ActivityCarDetailBinding.inflate(layoutInflater);
        setContentView(binding.root);
        setupView();
        loadItem();
        setupGoogleMap();
    }

    private fun setupGoogleMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment;
        mapFragment.getMapAsync(this)
    }

    private fun loadItem() {
        val carId = intent.getStringExtra(ARG_ID) ?: "";
        Log.d("TAG", "CarId: $carId");

        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getCar(carId) } //alterado ApiService para retorar um CarValue como result

            withContext(Dispatchers.Main) {
                when (result){
                    is Result.Success -> {
                        car = result.data;
                        Log.d("TAG", "Carregou o Detalhe de $result")
                        handleSuccess();
                    }
                    is Result.Error -> {
                        handleError();
                    }
                }
            }
        }
    }

    private fun handleSuccess() {
        binding.name.text = car.value.name;
        binding.year.text = car.value.year;
        binding.licence.setText(car.value.licence);
        binding.image.loadUrl(car.value.imageUrl);
        loadCarLocationInGoogleMap();
    }

    private fun loadCarLocationInGoogleMap() {
        car.value.place.apply {
            binding.googleMapContent.visibility = View.VISIBLE;
            val latLong = LatLng(lat, long);
            mMap.addMarker(MarkerOptions().position(latLong));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 17f))
        }
    }

    private fun handleError() {}

    private fun setupView() {
        setSupportActionBar(binding.toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish(); }
        binding.deleteCTA.setOnClickListener { deleteCar(); }
        binding.editCTA.setOnClickListener { editCar(); }
    }

    private fun deleteCar(){
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall {
                RetrofitClient.apiService.deleteCar(car.id)
            }
            //TODO IMPLEMENTAR ATUALIZAÇÃO PARA OS OUTROS CAMPOS TAMBÉM (MODIFICAR DE TEXTVIEW PARA EDITTEXT NO LAYOUT
            withContext(Dispatchers.Main){
                when(result){
                    is Result.Success -> { Toast.makeText(this@CarDetailActivity, R.string.success_delete, Toast.LENGTH_SHORT).show(); finish(); }
                    is Result.Error -> { Toast.makeText(this@CarDetailActivity, R.string.error_delete, Toast.LENGTH_SHORT).show(); }
                }
            }
        }
    }

    private fun editCar() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("TAG", "Car ID: ${car.id}\nCar Licence: ${binding.licence.text}\nCar Value: ${car.value}")
            val result = safeApiCall {
                RetrofitClient.apiService.updateCar(car.id, car.value.copy(licence = binding.licence.text.toString() ))
            }
            Log.d("TAG", "Carregou o Detalhe de $result")
            //TODO IMPLEMENTAR ATUALIZAÇÃO PARA OS OUTROS CAMPOS TAMBÉM (MODIFICAR DE TEXTVIEW PARA EDITTEXT NO LAYOUT
            withContext(Dispatchers.Main){
                when(result){
                    is Result.Error -> { Toast.makeText(this@CarDetailActivity, R.string.error_update, Toast.LENGTH_SHORT ).show();}
                    is Result.Success<*> -> { Toast.makeText(this@CarDetailActivity, R.string.success_update, Toast.LENGTH_SHORT ).show(); finish() };
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        if (::car.isInitialized){
            loadItemLocationInGoogleMap();
        }
    }

    private fun loadItemLocationInGoogleMap(){
        car.value.place.apply {
            binding.googleMapContent.visibility = View.VISIBLE;
            val latLong = LatLng(lat, long);
            mMap.addMarker(MarkerOptions().position(latLong));//.title(name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 17f))
        }
    }

    companion object{
        private const val ARG_ID = "carId";
        fun newIntent(context: Context, carId: String) = Intent(context, CarDetailActivity::class.java).apply { putExtra("carId", carId) }
    }
}