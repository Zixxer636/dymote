package com.example.erik.dymote

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.ToggleButton
import dymote.Dymote
import org.jetbrains.anko.doAsync
import android.widget.LinearLayout

class RemoteActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_remote)

        val linlaHeaderProgress = findViewById(R.id.linlaHeaderProgress) as LinearLayout
        linlaHeaderProgress.visibility = View.VISIBLE

        val callbacks = SpeakerCallbacks(this)
        Dymote.registerJavaCallback(callbacks)

        Dymote.connect("192.168.2.172", 1901)


//        setSupportActionBar(toolbar)
//
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


    fun mute(view: View) {
        Dymote.mute()
    }

    fun volumeUp(view: View) {
        Dymote.volumeUp()
    }

    fun volumeDown(view: View) {
        Dymote.volumeDown()
    }

    fun togglePower(view: View) {
        val powerButton = findViewById(R.id.btnPower) as ToggleButton
        doAsync {
            if (powerButton.isChecked) {
                Dymote.powerOn();
            } else {
                Dymote.powerOff();
            }
        }
    }

    fun changeSource(view: View) {
        val source = view.tag;

        when (source) {
            "2" -> Dymote.sourceLine()
            "3" -> Dymote.sourceOptical()
            "4" -> Dymote.sourceCoax()
            "5" -> Dymote.sourceUsb()
        }
    }
}
