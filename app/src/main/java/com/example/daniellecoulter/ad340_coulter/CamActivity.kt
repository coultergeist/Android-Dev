package com.example.daniellecoulter.ad340_coulter

import android.content.Context
import android.content.Intent
import android.graphics.Camera
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.camera_list_item.view.*
import android.net.ConnectivityManager
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import android.net.NetworkInfo
import android.widget.EditText
import com.example.daniellecoulter.ad340_coulter.R.id.cameraImage
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class CamActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    //create the array of cameras used in the adapter & json
    private var cameraArray: MutableList<CameraObject> = mutableListOf<CameraObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cam_layout)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewManager = LinearLayoutManager(this)
        viewAdapter = camAdapter(cameraArray)

        recyclerView = findViewById<RecyclerView>(R.id.camRecyclerView).apply {
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter

        }//end of recyclerView

        if (isConnected(this@CamActivity)) {
            GetJSON(this).execute("https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2")
        } else {
            Toast.makeText(this@CamActivity, "Could not establish connection", Toast.LENGTH_SHORT).show()
        }

    }

    /*
       got this info from tutorial:

       http://corochann.com/fast-easy-parcelable-implementation-with-android-studio-parcelable-plugin-641.html
       https://www.sitepoint.com/transfer-data-between-activities-with-android-parcelable/
    */

    //check for internet connection
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                // Wifi connection established
                Toast.makeText(context, activeNetwork.typeName, Toast.LENGTH_SHORT).show()
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                // otherwise connected using their own data plan
                Toast.makeText(context, activeNetwork.typeName, Toast.LENGTH_SHORT).show()
            }
            return true
        } else {
            return false
        }
    }

    /*
        Used this for reference: https://www.tutorialspoint.com/android/android_json_parser.htm
    */

    private inner class GetJSON(var mContext: CamActivity) : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(this@CamActivity, "JSON data is downloading", Toast.LENGTH_LONG).show()
        }

        override fun doInBackground(vararg params: String): String {

            //create connection and reader
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val inputStream = connection.getInputStream()

                reader = BufferedReader(InputStreamReader(inputStream))

                val buffer = StringBuffer()
                var jsonLine = reader.readLine()

                while ((jsonLine) != null) {
                    buffer.append(jsonLine + "\n")
                    //Log.d("Response: ", "> $line")   //here u ll get whole response...... :-)
                    jsonLine = reader.readLine()
                }//end of while
                return buffer.toString()

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (connection != null) {
                    connection.disconnect()
                }
                try {
                    if (reader != null) {
                        reader.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }//end of finally

            return "" //return empty string with no connection or data

        }//end of doInBackground

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            //JSON object from url
            var jObj: JSONObject = JSONObject(result)
            // gets the array of features which is full list
            var coords: JSONArray = jObj.getJSONArray("Features")

            for (i in 0..(coords.length() - 1)) {//Number of JSON objects
                val tempJason = JSONObject(coords[i].toString())

                val cameraNumber = JSONArray(tempJason.getString("Cameras")).length() - 1

                for (j in 0..cameraNumber) {

                    val camera: CameraObject = CameraObject()//a camera

                    camera.name = JSONArray(tempJason.getString("Cameras")).getJSONObject(j).getString("Description")
                    camera.imageURL = JSONArray(tempJason.getString("Cameras")).getJSONObject(j).getString("ImageUrl")
                    camera.type = JSONArray(tempJason.getString("Cameras")).getJSONObject(j).getString("Type")

                    //add each camera to list of cameras
                    cameraArray.add(camera)

                }
            }

            viewAdapter.notifyDataSetChanged();
        }
    }
}

class camAdapter(private val camArray: MutableList<CameraObject>) : RecyclerView.Adapter<camAdapter.ViewHolder>(){

        inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

            var cameraName: TextView
            var cameraImage: ImageView

            init{
                cameraName = itemView.findViewById(R.id.cameraName)
                cameraImage = itemView.findViewById(R.id.cameraImage)
            }//end init
        }//end ViewHolder inner class


        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int):ViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.camera_list_item,viewGroup,false)
            return ViewHolder(v)
        }

        //Use layout manager to now insert list items
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            var completeURL: String = ""

            if(camArray[position].type.equals("sdot")){
                completeURL = "http://www.seattle.gov/trafficcams/images/" + camArray[position].imageURL
            }else if(camArray[position].type.equals("wsdot")){
                completeURL = "http://images.wsdot.wa.gov/nw/" + camArray[position].imageURL
            }

            viewHolder.cameraName.text = camArray[position].name

            Picasso.get()
                    .load(completeURL)
                    .fit()
                    .centerInside()
                    .error(R.mipmap.ic_launcher_round)
                    .into(viewHolder.cameraImage)

        }
            //Populate and prep items for screen
            override fun getItemCount() = camArray.size-1

}
