package com.example.cafebazar.activity.splashscreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.cafebazar.contract.SplashScreenContract
import com.example.cafebazar.presenter.BasePresenter
import com.example.routingapp.utility.Utils

class SplashScreenPresenter(applicationContext: Context) :
      BasePresenter<SplashScreenContract.View>(),
      SplashScreenContract.Presenter{

    val SPLASH_TIME_OUT = 3000
    lateinit var utils: Utils
    var context: Context

    init
    {
        context = applicationContext
    }

    override fun onViewCreated()
    {
        super.onViewCreated()
        utils = Utils(context)
    }

    override fun onAttach(view: SplashScreenContract.View)
    {
        super.onAttach(view)
        this.view = view
    }

    fun checkPermission(): Boolean
    {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }

}