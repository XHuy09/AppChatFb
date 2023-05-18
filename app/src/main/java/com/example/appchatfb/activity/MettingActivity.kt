package com.example.appchatfb.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appchatfb.R
import com.example.appchatfb.adapter.ParticipantAdapter
import live.videosdk.rtc.android.Meeting
import live.videosdk.rtc.android.Participant
import live.videosdk.rtc.android.VideoSDK
import live.videosdk.rtc.android.listeners.MeetingEventListener

class MettingActivity : AppCompatActivity() {

    private var meeting: Meeting? = null
    private var micEnabled = true
    private var webcamEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metting)

        setActionListeners()

        val token1 = intent.getStringExtra("token")
        var token2 = intent.getStringExtra("token")
        val meetingId = ""
        val participantName1 = "vcuong"
        val participantName2 = "xhuy"


        val meeting2 = VideoSDK.initMeeting(
            this, meetingId, participantName2,
            true, true, null, null, false, null
        )
        VideoSDK.config(token2)
        meeting = VideoSDK.initMeeting(
            this@MettingActivity, meetingId, participantName1,
            micEnabled, webcamEnabled,null, null, false, null)

        meeting!!.addEventListener(meetingEventListener)

        meeting2!!.join()

        (findViewById<View>(R.id.tvMeetingId) as TextView).text = meetingId

        val rvParticipants = findViewById<RecyclerView>(R.id.rvParticipants)
        rvParticipants.layoutManager = GridLayoutManager(this, 2)
        rvParticipants.adapter = ParticipantAdapter(meeting!!)

    }
    private val meetingEventListener: MeetingEventListener = object : MeetingEventListener() {
        override fun onMeetingJoined() {
            Log.d("#meeting", "onMeetingJoined()")
        }

        override fun onMeetingLeft() {
            Log.d("#meeting", "onMeetingLeft()")
            meeting = null
            if (!isDestroyed) finish()
        }

        override fun onParticipantJoined(participant: Participant) {
            Toast.makeText(
                this@MettingActivity, participant.displayName + " joined",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onParticipantLeft(participant: Participant) {
            Toast.makeText(
                this@MettingActivity, participant.displayName + " left",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setActionListeners() {
        // toggle mic
        findViewById<View>(R.id.btnMic).setOnClickListener { view: View? ->
            if (micEnabled) {
                meeting!!.muteMic()
                Toast.makeText(this@MettingActivity, "Mic Muted", Toast.LENGTH_SHORT).show()
            } else {
                meeting!!.unmuteMic()
                Toast.makeText(this@MettingActivity, "Mic Enabled", Toast.LENGTH_SHORT).show()
            }
            micEnabled=!micEnabled
        }


        findViewById<View>(R.id.btnWebcam).setOnClickListener { view: View? ->
            if (webcamEnabled) {
                meeting!!.disableWebcam()
                Toast.makeText(this@MettingActivity, "Webcam Disabled", Toast.LENGTH_SHORT).show()
            } else {
                meeting!!.enableWebcam()
                Toast.makeText(this@MettingActivity, "Webcam Enabled", Toast.LENGTH_SHORT).show()
            }
            webcamEnabled=!webcamEnabled
        }

        findViewById<View>(R.id.btnLeave).setOnClickListener {
            meeting!!.leave()
        }
    }
}