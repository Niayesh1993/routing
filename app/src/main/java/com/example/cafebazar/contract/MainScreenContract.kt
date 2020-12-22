package com.example.cafebazar.contract

import android.location.Location
import com.example.routingapp.model.Venue

interface MainScreenContract {
    interface View : BaseContract.View {
        fun initView()
        fun Show_venue(Venues: ArrayList<Venue>)
        fun showLocationPopup()
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getLastLocation()
        fun checkDistance(currentLocation: Location?)
        fun distance(
            lat1: Double,
            lon1: Double,
            lat2: Double,
            lon2: Double
        ): Double
        fun deg2rad(deg: Double): Double
        fun rad2deg(rad: Double): Double
        fun get_venue(ll: String?, limit: Int?, offset: Int?)
        fun Add_to_database(VenueList: List<Venue>)
        fun Fetch_from_database()
        fun Clear_Database()
        fun Set_Venue_Data(Venues: ArrayList<Venue>)
    }
}