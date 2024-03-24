package com.example.healthymindadmin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.healthymindadmin.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    private lateinit var drawer_layout: DrawerLayout
    private lateinit var bottom_nav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

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
                replaceFragment(AppointmentsFragment())
                binding.toolbar.title="APPOINTMENTS"
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
                showDialogAddCategory()
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

    private fun showDialogAddCategory() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_category)

        // Initialize your dialog views and handle interactions
        // For example, you can set onClickListener to buttons in your dialog

        // Show the dialog
        dialog.show()
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

                    replaceFragment(AppointmentsFragment())
                    binding.toolbar.title="APPOINTMENTS"
                }

                R.id.bottom_nav_addCategory ->{
                    showDialogAddCategory()
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==R.id.notification){
            replaceFragment(NotificationFragment())
            binding.toolbar.title="NOTIFICATION"
            return true
        }
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
}


