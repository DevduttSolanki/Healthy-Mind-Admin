package com.example.healthymindadmin

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.healthymindadmin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this,SignUpActivity::class.java)
            startActivity(signupIntent)
        }

        binding.skip.setOnClickListener {
            val signupIntent = Intent(this,MainActivity::class.java)
            startActivity(signupIntent)
        }

        logIn()
        passwordVisibility()
        forgotPassword()
    }

    private fun forgotPassword() {
        binding.forgotpassRedirectText.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_forgotpassword, null)
            val userPW = view.findViewById<EditText>(R.id.forgot_pw_txt)
            builder.setView(view)
            val dialog = builder.create()

            // Initially set the password field to be hidden
            binding.loginPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                compareEmailForgotpassword(userPW)
                dialog.dismiss()
            }

            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }

            if (dialog.window != null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }
    }

    private fun passwordVisibility() {
        binding.passwordToggle.setOnClickListener {
            // Toggle password visibility
            val isVisible = binding.loginPassword.transformationMethod == null
            binding.loginPassword.transformationMethod = if (isVisible) {
                PasswordTransformationMethod.getInstance() // Hide password
            } else {
                null // Show password
            }
            // Change eye icon based on password visibility
            val icon = if (isVisible) {
                R.drawable.eye_open // Closed eye icon
            } else {
                R.drawable.eye_closed // Open eye icon
            }
            binding.passwordToggle.setImageResource(icon)
        }
    }

    private fun logIn() {
        binding.loginButton.setOnClickListener{

            val emailLog = binding.loginEmail.text.toString()
            val pwLog = binding.loginPassword.text.toString()

            if (emailLog.isEmpty() || pwLog.isEmpty()){

                if (emailLog.isEmpty()){
                    binding.loginEmail.error = "Please Enter Email Address."
                }
                if (pwLog.isEmpty()){
                    binding.loginPassword.error = "Please Enter your Password."
                }
                Toast.makeText(this,"Please Enter all Details", Toast.LENGTH_SHORT).show()

            }else if (!emailLog.matches(emailPattern.toRegex())) {

                Toast.makeText(this, "Enter valid Email Address format.", Toast.LENGTH_SHORT).show()

            } else{

                auth.signInWithEmailAndPassword(emailLog, pwLog).addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val user = auth.currentUser

                        if (user != null && user.isEmailVerified) {

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Optional: finish the current activity to prevent going back

                        } else {
                            // Email not verified, show toast and possibly prompt user to verify email again
                            Toast.makeText(this, "Please verify your email address.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Authentication failed, show toast for incorrect email or password
                        Toast.makeText(this, "Incorrect Email Address or Password.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun compareEmailForgotpassword(email: EditText) {

        val view = layoutInflater.inflate(R.layout.dialog_forgotpassword, null)
        val userEmail = view.findViewById<EditText>(R.id.forgot_pw_txt)

        if (email.text.toString().isEmpty()) {
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail.text.toString()).matches()) {
            auth.sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}