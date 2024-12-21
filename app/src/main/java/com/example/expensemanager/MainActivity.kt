package com.example.expensemanager

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.expensemanager.databinding.ActivityMainBinding
import com.example.expensemanager.databinding.DialogLogoutBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView

    // Firebase
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    // Fragments
    private lateinit var homeFragment: HomeFragment
    private lateinit var statsFragment: StatsFragment

    // Listener
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            }
            showDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase setup
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        // Configure the AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener { _ ->
            updateToolbarTitle(user)
        }
        auth.addAuthStateListener(authStateListener)

        toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)

        // Initialize fragments
        homeFragment = HomeFragment()
        statsFragment = StatsFragment()
        setFragment(homeFragment)

        // Drawer setup
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        bottomNavigationView = findViewById(R.id.bottomNavigationBar)

        // Bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
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

        // Handle back press
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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
            val name: String = user.displayName ?: user.email?.substringBefore("@") ?: "User"
            toolbar.title = "Welcome,\n$name"
        }
    }

    private fun showDialog() {
        val dialogBinding = DialogLogoutBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.btnReturn.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnLogout.setOnClickListener {
            dialog.dismiss()
            auth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.show()
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }

    private fun displaySelectedListener(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                HomeFragment()
            }
            R.id.home -> HomeFragment()
            R.id.stats -> StatsFragment().also { setFragment(it) }
            else -> Fragment()
        }

        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displaySelectedListener(item.itemId)
        return true
    }
}
