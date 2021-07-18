package com.udacity.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.util.NotificationBody
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private val notificationBody by lazy { intent.getSerializableExtra(NOTIFICATION_BODY) as NotificationBody }

    companion object {
        const val NOTIFICATION_BODY = "status"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


    }

}
