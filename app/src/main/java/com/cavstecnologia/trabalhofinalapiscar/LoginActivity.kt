package com.cavstecnologia.trabalhofinalapiscar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cavstecnologia.trabalhofinalapiscar.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding;

    private lateinit var verificationId: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge();
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);

        setupView();
        verifyLoggedUser();
    }

    private fun setupView() {
        binding.btnSendSms.setOnClickListener { onSendVerificationCode(); }
        binding.btnVerifySms.setOnClickListener { onVerifyCode(); }
    }

    private fun verifyLoggedUser(){
        if(FirebaseAuth.getInstance().currentUser != null) navigateToMainActivity();
    }

    private fun navigateToMainActivity(){
        startActivity(MainActivity.newIntent(this));
        finish();
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java);
    }
}