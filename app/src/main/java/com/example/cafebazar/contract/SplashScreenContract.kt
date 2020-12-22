package com.example.cafebazar.contract

import android.content.Intent

interface SplashScreenContract {

    interface View : BaseContract.View {

        fun navigateToNextActivity(intent: Intent)
    }

    interface Presenter : BaseContract.Presenter<View> {
    }
}