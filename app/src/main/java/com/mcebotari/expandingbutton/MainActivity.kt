package com.mcebotari.expandingbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatButton>(R.id.fail_task_button).setOnClickListener {
            findViewById<ExpandingButton>(R.id.main_button).finishLoading(false)
        }

        findViewById<AppCompatButton>(R.id.success_task_button).setOnClickListener {
            findViewById<ExpandingButton>(R.id.main_button).finishLoading()
        }

        findViewById<AppCompatButton>(R.id.reset_task_button).setOnClickListener {
            findViewById<ExpandingButton>(R.id.main_button).resetButton()
        }

        findViewById<ExpandingButton>(R.id.main_button).setOnClickListener {
            findViewById<ExpandingButton>(R.id.main_button).startLoadingAnimation()
        }
    }
}