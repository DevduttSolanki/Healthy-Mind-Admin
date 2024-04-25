package com.example.healthymindadmin

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var firebase_auth: FirebaseAuth
    private lateinit var firebase_db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var profile_img: CircleImageView
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var progressbar: ProgressBar
    private lateinit var txtChangePass: TextView

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    private var img_uri: Uri? = null
    private val select_img = registerForActivityResult(ActivityResultContracts.GetContent()) {
        img_uri = it
        profile_img.setImageURI(img_uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val fragmentview = inflater.inflate(R.layout.fragment_profile, container, false)


        profile_img = fragmentview.findViewById(R.id.profile_img)
        name = fragmentview.findViewById(R.id.profile_name)
        email = fragmentview.findViewById(R.id.profile_email)
        progressbar = fragmentview.findViewById(R.id.progressbar)
        txtChangePass = fragmentview.findViewById(R.id.changepassRedirectText)


        val change_img_txt: CircleImageView = fragmentview.findViewById(R.id.profile_img)

        val save_changes_txt: TextView = fragmentview.findViewById(R.id.profile_save_changes)


        firebase_auth = FirebaseAuth.getInstance()
        firebase_db = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        change_img_txt.setOnClickListener(this)
        save_changes_txt.setOnClickListener(this)

        readData()
        changePassword()

        return fragmentview
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.profile_img -> select_img.launch("image/*")
            R.id.profile_save_changes -> updateData()
        }
    }

    private fun readData() {
        progressbar.visibility = View.VISIBLE

        val currentUser = firebase_auth.currentUser

        if (currentUser != null) {
            val db_ref = firebase_db.getReference("admins")
            db_ref.child(currentUser.uid).get().addOnSuccessListener { dataSnapshot ->


                if (dataSnapshot.exists()) {
                    progressbar.visibility = View.GONE
                    name.setText(dataSnapshot.child("name").value.toString())
                    email.setText(dataSnapshot.child("email").value.toString())

                    if (isAdded) {
                        Glide.with(this).load(dataSnapshot.child("profile_img").value)
                            .placeholder(R.drawable.profile).into(profile_img)
                    }
                }else {
                    progressbar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Admin does not exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                progressbar.visibility = View.GONE
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case where currentUser is null
            progressbar.visibility = View.GONE
            Toast.makeText(requireContext(), "Admin not authenticated", Toast.LENGTH_SHORT).show()
        }
    }


    private fun changePassword() {

        txtChangePass.setOnClickListener {

            val builder = AlertDialog.Builder(requireActivity())
            val view = layoutInflater.inflate(R.layout.dialog_changepassword, null)
            val userPW = view.findViewById<EditText>(R.id.forgot_pw_txt)


            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                if (userPW.text.toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter your registered email.", Toast.LENGTH_SHORT).show()
                } else {
                    compareEmailChangepassword(userPW)
                    dialog.dismiss()
                }
            }

            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }

            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }
    }

    private fun updateData() {


        val name_txt = name.text.toString()
        val email_txt = email.text.toString()

        progressbar.visibility = View.VISIBLE

        if (name_txt.trim().isEmpty() ||  email_txt.trim().isEmpty())  {

            if (name_txt.trim().isEmpty()) {
                progressbar.visibility = View.GONE
                name.error = "Enter your Name."
            }

            if (email_txt.trim().isEmpty()) {
                progressbar.visibility = View.GONE
                email.error = "Enter Email."
            }

        } else if (!email_txt.matches(emailPattern.toRegex())) {
            progressbar.visibility = View.GONE
            email.error = "Enter valid Email format."

        }else {

            val storage_ref = storage.getReference("profile_img")
                .child(firebase_auth.currentUser!!.uid).child("profileImg.jpg")
            val db_ref = firebase_db.reference.child("admins").child(firebase_auth.currentUser!!.uid)

            if (img_uri != null) {
                storage_ref.delete()
                storage_ref.putFile(img_uri!!).addOnSuccessListener {

                    storage_ref.downloadUrl.addOnSuccessListener {

                        val updated_data = mapOf(
                            "name" to name_txt,
                            "email" to email_txt,
                            "profile_img" to it.toString()
                        )
                        db_ref.updateChildren(updated_data).addOnCompleteListener {
                            if (it.isSuccessful) {
                                progressbar.visibility = View.GONE
                                Toast.makeText(requireContext(), "Data updated", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                progressbar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "Something went wrong, please try again later.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            } else {
                val updated_data = mapOf(
                    "name" to name_txt,
                    "email" to email_txt
                )
                db_ref.updateChildren(updated_data).addOnCompleteListener {
                    if (it.isSuccessful) {
                        progressbar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Data updated", Toast.LENGTH_SHORT).show()
                    } else {
                        progressbar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Something went wrong, please try again later.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun compareEmailChangepassword(email: EditText) {

        val view = layoutInflater.inflate(R.layout.dialog_forgotpassword, null)
        val userEmail = view.findViewById<EditText>(R.id.forgot_pw_txt)

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail.text.toString()).matches()) {
            firebase_auth.sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Check your Email", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

}