package com.example.daniellecoulter.ad340_coulter

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.support.design.widget.NavigationView
import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.widget.DrawerLayout
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.ActionBar

import com.example.daniellecoulter.ad340_coulter.R.id.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.main_drawer_activity.*

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Create constant for logcat
    private val TAG = MainActivity::class.java.simpleName
    lateinit var SharedPreferences: SharedPreferences
    lateinit var SharedPreferencesHelper: SharedPreferencesHelper
    lateinit var context: Context
    lateinit var editText: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_drawer_activity)
        setSupportActionBar(toolbar)

        context = this.applicationContext

        //initialzing shared preferences
        SharedPreferences = this.getSharedPreferences("default", Context.MODE_PRIVATE);
        SharedPreferencesHelper = SharedPreferencesHelper(SharedPreferences);

        var toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        var savedName = SharedPreferencesHelper.getSharedPreferences();
        //setting default name to edit text view
        editText = this.findViewById(R.id.editText)
        editText.setText(savedName)



        movie_button.setOnClickListener { view ->
            val intent = Intent(this, ZombieRecycler::class.java).apply {
            }
            startActivity(intent)
        }

        //HW 5: display error for null/numeric entry to editText field
        button.setOnClickListener {
            val textInput = editText.text.toString()
            if(inputIsValid(textInput)){
                SharedPreferencesHelper.putSharedPreferencesHelper(textInput);

                val intent = Intent(this@MainActivity, DisplayMessageActivity::class.java)
                intent.putExtra("Enter a message", textInput)
                startActivity(intent)
            }//end of if string is not empty
            else{
                Toast.makeText(this@MainActivity, "Invalid Input", Toast.LENGTH_LONG).show()
            }
        }

        //HW6: button to show live cams
        button2.setOnClickListener{
            val intent = Intent(this, CamActivity::class.java).apply{
            }
            startActivity(intent)
        }

        button3.setOnClickListener{
            Toast.makeText(this@MainActivity, "This is button 2", Toast.LENGTH_LONG).show()
        }

        button4.setOnClickListener{
            Toast.makeText(this@MainActivity, "This is button 3", Toast.LENGTH_LONG).show()
        }
    }

    fun inputIsValid(string: String): Boolean {
        if(string.length == 0){
            return false
        }
        if(string.matches("-?\\d+(\\.\\d+)?".toRegex())){
            return false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this@MainActivity, "Settings Menu", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_problem -> {
                Toast.makeText(this@MainActivity, "Report a Problem", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_search -> {
                Toast.makeText(this@MainActivity, "Search", Toast.LENGTH_LONG).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean{
        when (item.itemId){
            R.id.nav_movies -> {
                val intent = Intent(this@MainActivity, ZombieRecycler::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_about -> {
                val intent = Intent(this@MainActivity, AboutAppActivity::class.java)
                startActivity(intent)
                true
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //Called when the user taps the Send button
    //Activity from Android Dev tutorial
    //https://developer.android.com/training/basics/firstapp/starting-activity.html

    fun sendMessage(view: View){
        //Do something in response to button

        //test breakpoint here

        val editText = findViewById<EditText>(R.id.editText)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }//end fun
}
