package com.example.daniellecoulter.ad340_coulter

import android.os.Bundle
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast

class DisplayMessageActivity() : AppCompatActivity(), Parcelable {
    //declare unique tag for log method
    private val TAG = DisplayMessageActivity::class.java.getSimpleName()

    constructor(parcel: Parcel) : this() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate() Restoring previous state")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        //get support actionbar corresponding and up nav
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val intent = intent

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.textView)

        textView.setText(message)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_display_message, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this@DisplayMessageActivity, "Settings Menu", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_problem -> {
                Toast.makeText(this@DisplayMessageActivity, "Report a Problem", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_search -> {
                Toast.makeText(this@DisplayMessageActivity, "Search", Toast.LENGTH_LONG).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DisplayMessageActivity> {
        override fun createFromParcel(parcel: Parcel): DisplayMessageActivity {
            return DisplayMessageActivity(parcel)
        }

        override fun newArray(size: Int): Array<DisplayMessageActivity?> {
            return arrayOfNulls(size)
        }
    }

    //Add log methods. 7 total: create,start,restart,resume,pause,stop,destroy
    //#1: onCreate
    /*@Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Log.d(TAG, "Activity created");
    }//end onCreate
    */
    //#2: onStart
    @Override
    fun onStart(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Log.d(TAG, "app started");
    }//end onStart

    //#3: onResume
    @Override
    fun onResume(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Log.d(TAG, "Activity resumed from previous state");
    }//end onResume

    //#4: onRestart
    @Override
    fun onRestart(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Log.d(TAG, "Activity restarted from previous state");
    }//end onRestart

    //#5: onPause
    @Override
    fun onPause(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Log.d(TAG, "Activity paused");
    }//end onPause

    //#6: onStop
    @Override
    fun onStop(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Log.d(TAG, "Activity stopped");
    }//end onStop

    //#7: onDestroy
    @Override
    fun onDestroy(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Log.d(TAG, "Activity destroyed");
    }//end onDestroy
}
