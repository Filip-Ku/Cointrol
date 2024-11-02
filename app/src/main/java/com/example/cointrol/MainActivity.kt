package com.example.cointrol

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.example.cointrol.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

//        binding.appBarMain.fab.setOnClickListener { view ->
////            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                    .setAction("Action", null).show()
////        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu

        menu.findItem(R.id.nav_savings).title = HtmlCompat.fromHtml("<font color='#FFD700'>${menu.findItem(R.id.nav_savings).title}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        menu.findItem(R.id.nav_income).title = HtmlCompat.fromHtml("<font color='#32CD32'>${menu.findItem(R.id.nav_income).title}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        menu.findItem(R.id.nav_outcome).title = HtmlCompat.fromHtml("<font color='#FF6347'>${menu.findItem(R.id.nav_outcome).title}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_savings, R.id.nav_income, R.id.nav_outcome), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}