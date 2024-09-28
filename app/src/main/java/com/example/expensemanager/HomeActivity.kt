package com.example.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.FirebaseUser

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var naviView: NavigationView
    private lateinit var fragment: Fragment
    private lateinit var ft: FragmentTransaction
    // Firebase
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.my_toolbar)
        user = FirebaseAuth.getInstance().currentUser!!

        if (user != null)
        {
            var name = ""
            if(user.displayName != null){
                name = user.displayName!!
            } else{
                val email = user.email
                name = email?.substring(0, email.indexOf("@")).toString()
            }
            toolbar.title = "Welcome $name"
        } else{
            toolbar.title = "Welcome user"
        }
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        naviView = findViewById(R.id.nav_view)
        naviView.setNavigationItemSelectedListener (this)
    }

    override fun onBackPressed() {
        drawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    fun displaySelectedListener (itemId: Int){
        when(itemId){
            R.id.profile -> {} // do nothing
            R.id.home -> {}
            R.id.stats -> {}
        }
        if (fragment != null){
            ft = getSupportFragmentManager().beginTransaction()
            ft.replace(R.id.main_frame, fragment)
            ft.commit()
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displaySelectedListener(item.itemId)
        return true
    }
}