package com.example.cafebazar.activity.splashscreen

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import com.example.cafebazar.R
import com.example.cafebazar.activity.mainscreen.MainScreenActivity
import com.example.cafebazar.contract.SplashScreenContract

/**
 * Created by Zohre Niayeshi on 20,December,2020 niayesh1993@gmail.com
 **/
class SplashScreenActivity :
    AppCompatActivity(),
    SplashScreenContract.View
{
    private lateinit var presenter: SplashScreenPresenter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)
        presenter = SplashScreenPresenter(applicationContext)
        presenter.onViewCreated()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            val intent = Intent(this@SplashScreenActivity, MainScreenActivity::class.java)
            navigateToNextActivity(intent)
        } else
        {
            if (presenter.utils.checkAndRequestPermissions(this))
            {
                val intent = Intent(this@SplashScreenActivity, MainScreenActivity::class.java)
                navigateToNextActivity(intent)
            }
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)
        if (!presenter.checkPermission())
        {
            // Ask again for Permissions until user accepts
            presenter.utils.checkAndRequestPermissions(this)
        } else
        {
            // If all the permissions are accepted then proceed to next activity
            val intent = Intent(this@SplashScreenActivity, MainScreenActivity::class.java)
            navigateToNextActivity(intent)
        }
    }

    override fun navigateToNextActivity(intent: Intent) {
        Handler().postDelayed({
            startActivity(intent)
        }, presenter.SPLASH_TIME_OUT.toLong())
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}