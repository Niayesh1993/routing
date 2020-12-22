package com.example.cafebazar.activity.detailscreen

import android.content.Context
import androidx.room.Room
import com.example.cafebazar.contract.VenueDetailContract
import com.example.cafebazar.model.Location
import com.example.cafebazar.model.Venue
import com.example.cafebazar.model.database.AppDb
import com.example.cafebazar.presenter.BasePresenter
import com.example.cafebazar.service.UserService
import com.example.cafebazar.utility.Error
import com.example.cafebazar.utility.Utils
import com.example.cafebazar.utility.api.ApiCallbackListener
import com.example.cafebazar.utility.api.ApiResultModel

class DetailScreenPresenter(applicationContext: Context, view: DetailScreenActivity) :
    BasePresenter<VenueDetailContract.View>(),
    VenueDetailContract.Presenter {

    var userService: UserService? = null
    var utils: Utils? = null
    var context: Context
    var venue: Venue? = null
    private var mView: DetailScreenActivity? = null
    private lateinit var Venue_db: AppDb
    private lateinit var Location_db: AppDb

    init {
        context = applicationContext
        mView = view
    }

    override fun onViewCreated() {
        super.onViewCreated()
        mView?.initView()
        utils = Utils(context)
        userService = UserService(context)
        venue = Venue()
        Venue_db =
            Room.databaseBuilder(context, AppDb::class.java, "VenueDB").build()
        Location_db =
            Room.databaseBuilder(context, AppDb::class.java, "LocationDB").build()
    }

    override fun get_venue_detail(id: String?) {
        if (utils!!.isNetworkAvailable()) {
            userService!!.get_venue_detail(id, object : ApiCallbackListener {
                override fun onSucceed(data: ApiResultModel) {
                    if (data.meta!!.getCode() === 200) {
                        if (data.response!!.venue != null) {
                            data.response!!.venue.let {
                                if (it != null) {
                                    mView?.RefreshView(it)
                                }
                            }
                        }
                    }
                }

                override fun onError(errors: MutableList<Error>?) {

                }
            })
        } else {
            Fetch_from_database_withId(id)
        }
    }

    override fun Fetch_from_database_withId(venueId: String?) {

        val thread = Thread {

            try {
                if (venueId != null) {
                    try {
                        if (Venue_db.venueDAO().selectVenue(venueId).size > 0) {
                            Venue_db.venueDAO().selectVenue(venueId).forEach() {
                                venue!!.id = it.VenueId
                                venue!!.name = it.Vname
                                venue!!.referralId = it.referralId


                                Location_db.locationDAO().selectLocation(it.VenueId).forEach() {
                                    var location = Location()
                                    location.address = it.address
                                    location.cc = it.cc
                                    location.city = it.city
                                    location.country = it.country
                                    location.distance = it.distance
                                    location.lat = it.lat
                                    location.lng = it.lng
                                    location.state = it.state

                                    venue!!.location = location
                                }
                            }
                        }

                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
            }

        }
        thread.start()
        thread.join()
        venue?.let { Set_Venue_Data(it) }

    }

    override fun Set_Venue_Data(venue: Venue) {
        if (venue != null) {
            mView?.RefreshView(venue)
        }
    }

}