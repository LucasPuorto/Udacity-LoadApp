package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.udacity.R
import com.udacity.customview.ButtonState
import com.udacity.customview.LoadingButton
import com.udacity.util.NotificationBody
import com.udacity.util.sendNotification

class MainActivity : AppCompatActivity() {

    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val radioGroup: RadioGroup by lazy { findViewById(R.id.rg_download_options) }
    private val glideOption: RadioButton by lazy { findViewById(R.id.rb_glide_option) }
    private val loadAppOption: RadioButton by lazy { findViewById(R.id.rb_load_app_option) }
    private val retrofitOption: RadioButton by lazy { findViewById(R.id.rb_retrofit_option) }
    private val loadingButton: LoadingButton by lazy { findViewById(R.id.custom_button) }

    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(
            this@MainActivity,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private var downloadID: Long = 0
    private var downloadUrl: String = ""
    private var downloadTitle: String = ""
    private var downloadDescription: String = ""

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"

        private const val EMPTY_SELECTION = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        initView()
    }

    private fun initView() {
        radioGroupCheckedChangeListener()
        setupButtonClickListener()
    }

    private fun setupButtonClickListener() {
        loadingButton.setOnClickListener {
            loadingButton.setState(ButtonState.Clicked)
            download()
        }
    }

    private fun radioGroupCheckedChangeListener() {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_glide_option -> {
                    setupDownloadOptions(
                        url = GLIDE_URL,
                        title = getString(R.string.glide_option),
                        description = getString(R.string.glide_option_description)
                    )
                }
                R.id.rb_load_app_option -> {
                    setupDownloadOptions(
                        url = LOAD_APP_URL,
                        title = getString(R.string.load_app_option),
                        description = getString(R.string.load_app_option_description)
                    )
                }
                R.id.rb_retrofit_option -> {
                    setupDownloadOptions(
                        url = RETROFIT_URL,
                        title = getString(R.string.retrofit_option),
                        description = getString(R.string.retrofit_option_description)
                    )
                }
            }
            download()
        }
    }

    private fun setupDownloadOptions(url: String, title: String, description: String) {
        downloadUrl = url
        downloadTitle = title
        downloadDescription = description
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                if (intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query().setFilterById(downloadID)
                    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                loadingButton.setState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    applicationContext = application,
                                    notificationBody = NotificationBody(
                                        title = title,
                                        status = "Success",
                                        description = "success"
                                    )
                                )
                                cursor.close()
                            } else {
                                loadingButton.setState(ButtonState.Error)
                                notificationManager.sendNotification(
                                    applicationContext = application,
                                    notificationBody = NotificationBody(
                                        title = title,
                                        status = "Fail",
                                        description = "failure"
                                    )
                                )
                                cursor.close()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        if (radioGroup.checkedRadioButtonId == EMPTY_SELECTION) {
            loadingButton.setState(ButtonState.Error)
            Toast.makeText(this@MainActivity, getString(R.string.empty_option), Toast.LENGTH_LONG).show()
        } else {
            loadingButton.setState(ButtonState.Loading)
            val request = DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(downloadTitle)
                .setDescription(downloadDescription)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager: DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)
        }
    }
}
