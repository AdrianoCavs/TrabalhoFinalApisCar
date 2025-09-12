package com.cavstecnologia.trabalhofinalapiscar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cavstecnologia.trabalhofinalapiscar.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

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
        //navigateToMainActivity();
    }

    private fun onSendVerificationCode(){
        val cellphone = binding.cellphone.text.toString();
        val auth = FirebaseAuth.getInstance();
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(cellphone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) { }

                override fun onVerificationFailed(exception: FirebaseException) {
                    Toast.makeText(this@LoginActivity, getString(R.string.error_login, exception.message), Toast.LENGTH_SHORT).show();
                }
                override fun onCodeSent(verficationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verficationId, token);
                    this@LoginActivity.verificationId = verficationId;
                    Toast.makeText(this@LoginActivity, getString(R.string.verification_code_sent), Toast.LENGTH_SHORT).show();
                    binding.btnVerifySms.visibility = View.VISIBLE;
                    binding.veryfyCode.visibility = View.VISIBLE;
                }

            }).build(); //end .setCallbacks
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private fun onVerifyCode(){
        val verificationCode = binding.veryfyCode.text.toString();
        val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnFailureListener { //abaixo, pega a msg de exception retornada do FirebaseAuth, caso seja nula (?:) mostra a msg padrao
                val message = it.message ?: getString(R.string.error_login_verification);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
            .addOnSuccessListener { navigateToMainActivity() }
    }

    private fun navigateToMainActivity(){
        startActivity(newIntent(this));
        //val intent = Intent(this, MainActivity::class.java);
        //startActivity(intent);
        finish();
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java);
    }
}