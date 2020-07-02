package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.os.HandlerCompat.postDelayed
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ic_logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_in))
        handler.postDelayed({
            ic_logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_out))
            handler.postDelayed({},500)
            ic_logo.visibility = View.GONE
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        },1500)
    }

    }

