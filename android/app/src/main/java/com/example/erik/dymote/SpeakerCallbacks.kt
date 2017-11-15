package com.example.erik.dymote

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.ToggleButton
import dymote.JavaCallback
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SpeakerCallbacks(val remoteActivity: RemoteActivity) : JavaCallback {
    override fun initialized() {
        Log.i("SpeakerCallbacks", "Initialized")

        doAsync {
            uiThread {
                val linlaHeaderProgress = remoteActivity.findViewById(R.id.linlaHeaderProgress) as LinearLayout
                linlaHeaderProgress.visibility = View.GONE
            }
        }
    }

    override fun muteChanged(p0: Boolean) {
        Log.i("SpeakerCallbacks", "Mute changed")

        doAsync {
            uiThread {
                val muteButton = remoteActivity.findViewById(R.id.btnMute) as ToggleButton
                muteButton.isChecked = p0
            }
        }
    }

    override fun sourceChanged(p0: Long) {
        Log.i("SpeakerCallbacks", "Source changed")
        doAsync {
            uiThread {
                val buttons: Array<ToggleButton> = arrayOf(
                        remoteActivity.findViewById(R.id.btnSource2) as ToggleButton,
                        remoteActivity.findViewById(R.id.btnSource3) as ToggleButton,
                        remoteActivity.findViewById(R.id.btnSource4) as ToggleButton,
                        remoteActivity.findViewById(R.id.btnSource5) as ToggleButton

                )

                for (button in buttons) {
                    if (button.tag.toString().toLong() == p0) {
                        button.isChecked = true;
                        button.isClickable = false;
                    } else {
                        button.isChecked = false;
                        button.isClickable = true;
                    }
                }
            }
        }
    }

    override fun volumeChanged(p0: Long) {
        Log.i("SpeakerCallbacks", "Volume changed")

        doAsync {
            uiThread {
                val volumeBar = remoteActivity.findViewById(R.id.volumeBar) as SeekBar
                volumeBar.progress = p0.toInt()
            }
        }
    }

    override fun powerChanged(p0: Boolean) {
        Log.i("SpeakerCallbacks", "Power changed")

        doAsync {
            uiThread {
                val powerButton = remoteActivity.findViewById(R.id.btnPower) as ToggleButton
                powerButton.isChecked = p0
            }
        }
    }
}
