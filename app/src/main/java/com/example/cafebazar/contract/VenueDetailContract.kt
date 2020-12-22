package com.example.cafebazar.contract

import com.example.routingapp.model.Venue

interface VenueDetailContract {

    interface View : BaseContract.View {
        fun initView()
        fun RefreshView(venue: Venue)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun get_venue_detail(id: kotlin.String?)
        fun Fetch_from_database_withId(venueId: kotlin.String?)
        fun Set_Venue_Data(venue: Venue)
    }
}