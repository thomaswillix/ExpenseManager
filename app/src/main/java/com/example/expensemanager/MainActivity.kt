package com.example.expensemanager

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var naviView: NavigationView
    private lateinit var fragment: Fragment
    private lateinit var ft: FragmentTransaction
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout

    // Firebase
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    //Fragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var statsFragment: StatsFragment
    
    // Listener
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)){
                drawerLayout.closeDrawer(GravityCompat.END)
            }
            showDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.myToolbar)
        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        // Configura el AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            updateToolbarTitle(user)
        }
        // Registrar el listener en onStart()
        auth.addAuthStateListener(authStateListener)
        setSupportActionBar(toolbar)
        homeFragment = HomeFragment()
        statsFragment = StatsFragment()
        setFragment(homeFragment)

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        drawerLayout = findViewById(R.id.drawerLayout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        naviView = findViewById(R.id.navView)
        naviView.setNavigationItemSelectedListener (this)

        bottomNavigationView = findViewById(R.id.bottomNavigationBar)
        frameLayout = findViewById(R.id.main_frame)
        //Navigation from the bottomNavigationBar
        bottomNavigationView.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.profile -> {
                        setFragment(homeFragment)
                        true
                    }
                    R.id.home -> {
                        setFragment(homeFragment)
                        true
                    }
                    R.id.stats -> {
                        setFragment(statsFragment)
                        true
                    }
                    else -> false
                }
        }
        
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun updateToolbarTitle(user: FirebaseUser?) {
        if (user != null) {
            val name: String
            // Si el usuario tiene un nombre de display, lo usamos
            if (user.displayName != null) {
                name = user.displayName!!
            } else {
                // Si no tiene nombre de display, usamos el email
                val email = user.email
                name = email?.substring(0, email.indexOf("@")) ?: "User"
            }

            // Actualizamos el título del toolbar
            toolbar.title = "Welcome,\n$name"
        }
    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_logout, null)
        builder.setView(view)
        val dialog = builder.create()
        view.findViewById<Button>(R.id.btnReturn).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            dialog.dismiss()
            auth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }
        if (dialog.window != null){
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }

    private fun setFragment(fragment: Fragment){
        val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }
    //Navigation for the LeftNavigationMenu
    private fun displaySelectedListener (itemId: Int){
        fragment = Fragment()
        when(itemId){
            R.id.profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                fragment = HomeFragment()
            }
            R.id.home -> {
                fragment = HomeFragment()
            }
            R.id.stats -> {
                fragment = StatsFragment()
                setFragment(statsFragment)
            }
        }
        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_frame, fragment)
        ft.commit()
        drawerLayout.closeDrawer(GravityCompat.START)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displaySelectedListener(item.itemId)
        return true
    }
}