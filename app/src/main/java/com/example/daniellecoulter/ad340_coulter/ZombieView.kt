package com.example.daniellecoulter.ad340_coulter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso

class ZombieView : AppCompatActivity() {

    lateinit var titleView2: TextView
    lateinit var yearView2: TextView
    lateinit var directorView: TextView
    lateinit var descriptionView: TextView
    lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zombie_view)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        //get support actionbar corresponding and up nav
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val title = intent.getStringExtra("Title")
        val year = intent.getStringExtra("Year")
        val director = intent.getStringExtra("Director")
        val image = intent.getStringExtra("Image")
        val description = intent.getStringExtra("Description")

        //get views
        titleView2 = findViewById(R.id.titleView2)
        yearView2 = findViewById(R.id.yearView2)
        directorView = findViewById(R.id.directorView)
        descriptionView = findViewById(R.id.descriptionView)
        imageView = findViewById(R.id.imageView)

        //set view content
        titleView2.setText("Title: " + title)
        yearView2.setText("Year: " + year)
        directorView.setText("Director: " + director)
        descriptionView.setText("Description: " + description)

        Picasso.get()
                .load(image)
                .resize(850, 850)
                .centerCrop()
                .error(R.mipmap.ic_launcher_round)
                .into(imageView)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this@ZombieView, "Settings Menu", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_share -> {
                Toast.makeText(this@ZombieView, "Rate this movie", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_search -> {
                Toast.makeText(this@ZombieView, "Search", Toast.LENGTH_LONG).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
