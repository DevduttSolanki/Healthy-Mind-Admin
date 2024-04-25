package com.example.healthymindadmin

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.healthymindadmin.Adapters.CategoryAdapter
import com.example.healthymindadmin.Models.CategoryModel
import com.example.healthymindadmin.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Date

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    private lateinit var drawer_layout: DrawerLayout
    private lateinit var bottom_nav: BottomNavigationView

    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var addCategoryImg: CircleImageView
    private lateinit var txtEnterCategoryName: EditText
    private lateinit var btnUploadCategory: Button
    private lateinit var viewFetchImg: View
    private lateinit var imageUri: Uri
    private lateinit var dialog: Dialog
    private lateinit var progressDialog: ProgressDialog
    private var list: ArrayList<CategoryModel> = ArrayList()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        adapter = CategoryAdapter(this, list)


        list = ArrayList()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading")
        progressDialog.setMessage("Please wait.")

        //drawer menu
        drawer_layout = binding.drawerLayout

        val drawer_view=binding.drawerView
        setSupportActionBar(binding.toolbar)

        drawer_view.setNavigationItemSelectedListener(this)
        val toggle= ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.open_nav,R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        bottom_nav = binding.bottomMenu



        if(savedInstanceState==null)
        {
            replaceFragment(HomeFragment())

            //on first time when activity created toolbar title
            supportActionBar?.title = "HOME"
        }

        bottomNavItemSelect()
        backPress()
        showdilogandfunction()
    }

    private fun replaceFragment(fa: Fragment) {
        val transaction: FragmentTransaction =supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fa)
        transaction.commit()
    }

    private fun backPress() {
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(drawer_layout.isDrawerOpen(GravityCompat.START)){
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
                else{
                    //onBackPressedDispatcher.onBackPressed()
                    finishAffinity()
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.nav_my_profile -> {

                replaceFragment(ProfileFragment())
                binding.toolbar.title="PROFILE"
            }
            R.id.nav_home -> {
                replaceFragment(HomeFragment())
                binding.toolbar.title="HOME"
            }

            R.id.nav_appointments -> {
                replaceFragment(AppointmentRequestsFragment())
                binding.toolbar.title="APPOINTMENT REQUESTS"
            }

            R.id.nav_users_test_results -> {
                replaceFragment(UserTestResultFragment())
                binding.toolbar.title="USER TEST RESULTS"
            }

            R.id.nav_manage_appointments -> {
                replaceFragment(AppointmentsFragment())
                binding.toolbar.title="MANAGE SCHEDULE"
            }

            R.id.nav_settings -> {
                replaceFragment(SettingsFragment())
                binding.toolbar.title="SETTINGS"
            }
            R.id.nav_users -> {
                replaceFragment(AppUsersFragment())
                binding.toolbar.title="APP USERS"
            }

            R.id.nav_add_category-> {
                dialog.show()
            }


            R.id.nav_logout -> {
                val intent= Intent(this,LoginActivity::class.java)
                signOut()
                Toast.makeText(this,"log out", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                this.finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun bottomNavItemSelect() {

        bottom_nav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.bottom_nav_home ->{
                    replaceFragment(HomeFragment())
                    binding.toolbar.title="HOME"
                }

                R.id.bottom_nav_profile->{

                    replaceFragment(ProfileFragment())
                    binding.toolbar.title="PROFILE"
                }

                R.id.bottom_nav_Appointment ->{

                    replaceFragment(AppointmentRequestsFragment())
                    binding.toolbar.title="APPOINTMENT REQUESTS"
                }

                R.id.bottom_nav_addCategory ->{
                    dialog.show()
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
//        if(id==R.id.notification){
//            replaceFragment(NotificationFragment())
//            binding.toolbar.title="NOTIFICATION"
//            return true
//        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        val shared_pref=getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        with(shared_pref.edit()){
            clear()
            apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_toolbar,menu)
        return true
    }

    private fun showdilogandfunction(){

        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_category)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        btnUploadCategory = dialog.findViewById(R.id.btnUploadCategory)
        txtEnterCategoryName = dialog.findViewById(R.id.txtEnterCategoryName)
        addCategoryImg = dialog.findViewById(R.id.addCategoryImg)
        viewFetchImg = dialog.findViewById(R.id.viewFetchImg)

        viewFetchImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 1)
        }

        database.reference.child("categories").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    list.clear()
                    for (dataSnapshot in snapshot.children) {
                        val categoryName =
                            dataSnapshot.child("categoryname").getValue(String::class.java)
                        val categoryImg =
                            dataSnapshot.child("categoryimg").getValue(String::class.java)
                        val categoryKey = dataSnapshot.key

                        categoryName?.let {
                            categoryImg?.let { it1 ->
                                categoryKey?.let { it2 ->
                                    list.add(
                                        CategoryModel(it, it1, it2)
                                    )
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Category does not exist.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        btnUploadCategory.setOnClickListener {

            val name = txtEnterCategoryName.text.toString()

            if (!::imageUri.isInitialized) {
                Toast.makeText(
                    this,
                    "Please upload category Image.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (name.isEmpty()) {
                txtEnterCategoryName.error = "Enter category name"
            } else {
                progressDialog.show()
                uploadData()
            }
        }

    }

    private fun uploadData() {
        val categoryName = txtEnterCategoryName.text.toString()

        // Check if category name is empty
        if (categoryName.isEmpty()) {
            txtEnterCategoryName.error = "Enter category name"
            return
        }

        // Check if category name already exists
        database.reference.child("categories")
            .orderByChild("categoryname")
            .equalTo(categoryName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Category name already exists
                        Toast.makeText(this@MainActivity, "Category name must be unique.", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    } else {
                        // Category name is unique, proceed with upload
                        val reference = storage.reference.child("category")
                            .child(Date().time.toString())

                        reference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val categoryModel = CategoryModel(
                                    categoryName,
                                    uri.toString()
                                )

                                database.reference.child("categories").push()
                                    .setValue(categoryModel)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@MainActivity, "Data Uploaded.", Toast.LENGTH_SHORT)
                                            .show()
                                        addCategoryImg.setImageResource(R.drawable.gallery)
                                        txtEnterCategoryName.setText("")
                                        progressDialog.dismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                                        progressDialog.dismiss()
                                    }
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {


            imageUri = data.data!!
            addCategoryImg.setImageURI(imageUri)
        }
    }
}