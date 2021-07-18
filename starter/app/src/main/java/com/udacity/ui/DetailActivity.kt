package com.udacity.ui

import android.app.NotificationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.udacity.R
import com.udacity.util.NotificationBody
import com.udacity.util.cancelNotifications
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private val notificationBody by lazy { intent.getSerializableExtra(NOTIFICATION_BODY) as NotificationBody }

    private val fileName: TextView by lazy { findViewById(R.id.tv_file_name) }
    private val status: TextView by lazy { findViewById(R.id.tv_status) }
    private val okButton: MaterialButton by lazy { findViewById(R.id.mbt_details_ok) }

    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(
            this@DetailActivity,
            NotificationManager::class.java
        ) as NotificationManager
    }

    companion object {
        const val NOTIFICATION_BODY = "status"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        notificationManager.cancelNotifications()
        initView()
    }

    private fun initView() {
        setupTexts()
        setupStatusTextColor()
        setupButtonClickListener()
    }

    private fun setupTexts() {
        fileName.text = notificationBody.title
        status.text = notificationBody.status
    }

    private fun setupStatusTextColor() {
        when (notificationBody.status) {
            getString(R.string.success) -> {
                status.setTextColor(resources.getColor(R.color.success))
            }
            getString(R.string.fail) -> {
                status.setTextColor(resources.getColor(R.color.error))
            }
        }
    }

    private fun setupButtonClickListener() {
        okButton.setOnClickListener {
            onBackPressed()
        }
    }
}
