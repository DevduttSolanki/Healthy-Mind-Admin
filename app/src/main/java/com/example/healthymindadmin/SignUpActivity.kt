package com.example.healthymindadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.example.healthymindadmin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        binding.loginRedirectText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        passwordVisibility()
        signUp()
    }

    private fun signUp() {


        binding.signupButton.setOnClickListener {

            val name = binding.signupName.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()

            val emailPatternvalidation = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"


            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {

                if (name.isEmpty()) {
                    binding.signupName.error = "Name required"
                }
                if (email.isEmpty()) {
                    binding.signupEmail.error = "Email required"
                }
                if (password.isEmpty()) {
                    binding.signupPassword.error = "Password required"
                }

            } else if (name.isNotEmpty() &&  email.isNotEmpty() && password.isNotEmpty()) {

                if (!email.matches(emailPatternvalidation.toRegex())) {
                    binding.signupEmail.error = "Enter valid email format."

                } else if (password.length < 6) {
                    binding.signupPassword.error = "Password must be 6 characters long."

                } else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                        if (it.isSuccessful) {
                            val databaseReference =
                                database.reference.child("admins").child(auth.currentUser!!.uid)
                            val appadmins : admins = admins(
                                name,
                                email,
                                password,
                                auth.currentUser!!.uid
                            )

                            databaseReference.setValue(appadmins).addOnCompleteListener {

                                if (it.isSuccessful) {

                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "SignUp Successful.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    sendVerificationEmail()

                                } else {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Something went wrong , please try again later.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                    }.addOnFailureListener { e ->

                        Toast.makeText(this@SignUpActivity, "${e.message}.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this@SignUpActivity, "Please enter all details.", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun sendVerificationEmail() {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnCompleteListener {

            if (it.isSuccessful) {

                Toast.makeText(
                    this,
                    "Verification mail sent, please check your Email.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                Toast.makeText(
                    this,
                    "Something went wrong,please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun passwordVisibility() {
        binding.passwordToggle.setOnClickListener {
            // Toggle password visibility
            val isVisible = binding.signupPassword.transformationMethod == null
            binding.signupPassword.transformationMethod = if (isVisible) {
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
}