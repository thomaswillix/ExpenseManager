package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.expensemanager.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            drawerLayout = findViewById(R.id.drawer_layout)
            if (drawerLayout.isDrawerOpen(GravityCompat.END)){
                drawerLayout.closeDrawer(GravityCompat.END)
            }
            showDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.my_toolbar)
        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        homeFragment = HomeFragment()
        statsFragment = StatsFragment()
        setFragment(homeFragment)
        setSupportActionBar(toolbar)

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        naviView = findViewById(R.id.nav_view)
        naviView.setNavigationItemSelectedListener (this)

        bottomNavigationView = findViewById(R.id.bottomNavigationBar)
        frameLayout = findViewById(R.id.main_frame)
        //Navigation from the bottomNavigationBar
        @Suppress("DEPRECATION")
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
    private fun showDialog(){
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Are you sure?")
            setMessage("Do you want to log out?")
            setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                finish()
            }
            setNegativeButton("No", null)
            show()
        }
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

        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displaySelectedListener(item.itemId)
        return true
    }

    override fun onResume() {
        super.onResume()
        getProfileData()
    }

    private fun getProfileData(){
        user = FirebaseAuth.getInstance().currentUser!!
        val name :String
        if(user.displayName != null){
            name = user.displayName!!
        } else{
            val email = user.email
            name = email?.substring(0, email.indexOf("@")).toString()
        }
        toolbar.title = "Welcome,\n$name"
    }
}