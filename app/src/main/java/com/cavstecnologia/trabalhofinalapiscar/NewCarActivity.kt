package com.cavstecnologia.trabalhofinalapiscar

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.view.isVisible
import com.cavstecnologia.trabalhofinalapiscar.databinding.ActivityNewCarBinding
import com.cavstecnologia.trabalhofinalapiscar.model.CarLocation
import com.cavstecnologia.trabalhofinalapiscar.model.CarValue
import com.cavstecnologia.trabalhofinalapiscar.service.RetrofitClient
import com.cavstecnologia.trabalhofinalapiscar.service.safeApiCall
import com.cavstecnologia.trabalhofinalapiscar.service.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID


class NewCarActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityNewCarBinding;
    private lateinit var  mMap: GoogleMap;
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient;
    private lateinit var imageUri: Uri;
    private var selectedMarker: Marker? = null;
    private var imageFile: File? = null;
    private val cameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult( ActivityResultContracts.StartActivityForResult() ){
        if (it.resultCode == Activity.RESULT_OK){
            uploadImageToFirebase();
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCarBinding.inflate(layoutInflater);
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setupView();
        setupGoogleMap();
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            REQUEST_CODE_CAMERA -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) openCamera();
            else Toast.makeText(this, R.string.error_request_camera, Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        binding.mapContent.visibility = View.VISIBLE;
        mMap.setOnMapClickListener { latLng ->
            selectedMarker?.remove(); //limpa o marker atual caso exista

            //armazena o local do novo marker criado
            selectedMarker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
            )
        }
        getDeviceLocation();
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        binding.toolbar.setNavigationOnClickListener { finish(); }
        binding.saveCta.setOnClickListener { onSave(); }
        binding.takePictureCta.setOnClickListener { onTakePicture(); }
    }

    private fun onTakePicture(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PERMISSION_GRANTED){
            openCamera();
        } else {
            requestCamerePermission();
        }
    }

    private fun requestCamerePermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE_CAMERA);
    }

    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = createImageUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(intent);
    }

    private fun createImageUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd-HHmmss").format( Date() );
        val imageFileName = "JPEG_" + timeStamp + "_";

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        return FileProvider.getUriForFile(this, "com.cavstecnologia.trabalhofinalapiscar.fileprovider", imageFile!!);
    }

    private fun onSave() {
        if (!validateForm()) return;
        saveData();
    }

    private fun validateForm(): Boolean {
        if (binding.name.text.toString().isBlank()){
            Toast.makeText(this, getString(R.string.error_validate_form, "Name"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.year.text.toString().isBlank()){
            Toast.makeText(this, getString(R.string.error_validate_form, "Surname"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.licence.text.toString().isBlank()){
            Toast.makeText(this, getString(R.string.error_validate_form, "Age"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.imageUrl.text.toString().isBlank()){
            Toast.makeText(this, getString(R.string.error_validate_form, "Imagem"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedMarker == null){
            Toast.makeText(this, getString(R.string.error_validate_form_location), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment;
        mapFragment.getMapAsync(this);
    }

    private fun getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED){
            loadCurrentLocation();
        }else {
            //NÃO TENHO PERMISSÃO DE LOCALIZAÇÃO;
        }
    }

    //@SuppressLint("MissingPermission")
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun loadCurrentLocation() {
        mMap.isMyLocationEnabled = true;
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val currentLocationLatLng: LatLng; //LatLng(location.latitude, location.longitude);
            if (location == null) { currentLocationLatLng = LatLng(-23.550502, -46.633933); }
            else { currentLocationLatLng = LatLng(location.latitude, location.longitude); }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 15f))
        }
    }


    private fun saveData(){
        val name = binding.name.text.toString();
        val year = binding.year.text.toString();
        val licence = binding.licence.text.toString().uppercase();
        val imageUrl = binding.imageUrl.text.toString();
        val location = selectedMarker?.position?.let { position ->
            CarLocation(position.latitude, position.longitude);
        } ?: throw IllegalArgumentException ("Usuário deveria ter a localização nesse ponto");

        CoroutineScope(Dispatchers.IO).launch {
            val carValue = CarValue(
                SecureRandom().nextInt().toString(),
                year,
                name,
                licence,
                imageUrl,
                location
            )
            val result = safeApiCall { RetrofitClient.apiService.addCar(carValue) }
            withContext(Dispatchers.Main){
                when (result) {
                    is  Result.Error -> { Toast.makeText(this@NewCarActivity, R.string.error_create, Toast.LENGTH_SHORT ).show(); }
                    is Result.Success -> { Toast.makeText(this@NewCarActivity, getString(R.string.success_create, name), Toast.LENGTH_SHORT).show(); finish() }
                }
            }
        }
    }// fim saveData



    private fun uploadImageToFirebase() {
        val storageRef = FirebaseStorage.getInstance().reference;
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}.jpg");
        val baos = ByteArrayOutputStream();
        val imageBitmap = BitmapFactory.decodeFile(imageFile!!.path);
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        val data = baos.toByteArray();

        onLoadingImage(true);

        imagesRef.putBytes(data)
            .addOnFailureListener { Toast.makeText(this, R.string.error_upload_image, Toast.LENGTH_SHORT).show(); }
            .addOnSuccessListener {
                imagesRef.downloadUrl
                .addOnCompleteListener { onLoadingImage(false) }
                .addOnSuccessListener { uri -> binding.imageUrl.setText(uri.toString()); }
            }
    }

    private fun onLoadingImage(isLoading: Boolean){
        binding.loadImageProgress.isVisible = isLoading;
        binding.takePictureCta.isEnabled = !isLoading;
        binding.saveCta.isEnabled = !isLoading;
    }

    companion object{
        const val REQUEST_CODE_CAMERA = 101;
        fun newIntent(context: Context) = Intent(context, NewCarActivity::class.java );
    }
}




