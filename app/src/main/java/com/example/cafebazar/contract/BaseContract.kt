package com.example.cafebazar.contract

interface BaseContract {
    interface View {
    }

    interface Presenter<V : View> {
        fun onAttach(view: V)
        fun onViewCreated()
        fun onDestroy()
    }
}