package com.example.cointrol

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.example.cointrol.ui.savings.SavingsFragment
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Losowanie sentencji w menu bocznym
        setSupportActionBar(binding.appBarMain.toolbar)
        val quotes = listOf(
            "Save today, enjoy tomorrow.",
            "Small savings, big dreams.",
            "Spend less, save more.",
            "Saving is smart, not stingy.",
            "Future wealth starts now."
        )
        val random = Random(System.currentTimeMillis())
        val randomQuote = quotes[random.nextInt(quotes.size)]
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.random_quote).text = randomQuote

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_savings, R.id.nav_income, R.id.nav_outcome), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                openSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun openSettings() {
        Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}