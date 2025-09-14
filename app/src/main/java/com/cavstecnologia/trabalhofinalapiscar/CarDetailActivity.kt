package com.cavstecnologia.trabalhofinalapiscar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cavstecnologia.trabalhofinalapiscar.databinding.ActivityCarDetailBinding
import com.cavstecnologia.trabalhofinalapiscar.model.Car
import com.cavstecnologia.trabalhofinalapiscar.model.CarValue
import com.cavstecnologia.trabalhofinalapiscar.service.RetrofitClient
import com.cavstecnologia.trabalhofinalapiscar.service.safeApiCall
import com.cavstecnologia.trabalhofinalapiscar.service.Result
import com.cavstecnologia.trabalhofinalapiscar.ui.loadUrl
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCarDetailBinding;
    private lateinit var car: Car;
    //private lateinit var mMap: GoogleMap;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge();
        binding = ActivityCarDetailBinding.inflate(layoutInflater);
        setContentView(binding.root);
        setupView();
        loadItem();
        //setupGoogleMap(); TODO GOOGLE MAP
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
            //Log.d("Hello World", "Carregou o Detalhe de $result")
        }
    }

    private fun handleSuccess() {
        binding.name.text = car.value.name;
        binding.year.text = car.value.year;
        binding.licence.setText(car.value.licence);
        binding.image.loadUrl(car.value.imageUrl);
        //loadCarLocationInGoogleMap(); TODO GOOGLE MAP
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

    companion object{
        private const val ARG_ID = "carId";
        fun newIntent(context: Context, carId: String) = Intent(context, CarDetailActivity::class.java).apply { putExtra("carId", carId) }
    }
}