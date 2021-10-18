package com.example.top100downloaderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var getButton: Button
    lateinit var adapter: RVTopApp
    lateinit var rvMain: RecyclerView

    var topApps = mutableListOf<TopApp>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Top 10/100 Downloader App"

        getButton = findViewById(R.id.btnGet)
        rvMain = findViewById(R.id.rvMain)

        getButton.setOnClickListener { getFeeds(10) }

    }

    private fun getFeeds(limit: Int){
        CoroutineScope(Dispatchers.IO).launch{
            async {
                fetchData(limit)
            }.await()
            withContext(Dispatchers.Main){
                adapter = RVTopApp(topApps)
                rvMain.adapter = adapter
                rvMain.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    private fun fetchData(limit: Int){
        val parser = XMLParser()
        val url = URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=$limit/xml")
        val urlConnection = url.openConnection() as HttpURLConnection
        topApps = urlConnection.inputStream?.let {
            parser.parse(it)
        } as MutableList<TopApp>

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.top_10_apps -> getFeeds(10)
            R.id.top_100_apps -> getFeeds(100)
        }
        return super.onOptionsItemSelected(item)
    }
}