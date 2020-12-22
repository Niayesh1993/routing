package com.example.cafebazar.activity.mainscreen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.example.cafebazar.adapter.VenuRecycelerViewAdapter
import com.example.cafebazar.contract.MainScreenContract
import com.example.cafebazar.model.Venue
import com.example.cafebazar.model.database.AppDb
import com.example.cafebazar.model.database.LocationEntity
import com.example.cafebazar.model.database.VenueEntity
import com.example.cafebazar.presenter.BasePresenter
import com.example.cafebazar.service.UserService
import com.example.cafebazar.utility.Constants
import com.example.cafebazar.utility.Error
import com.example.cafebazar.utility.SettingsManager
import com.example.cafebazar.utility.Utils
import com.example.cafebazar.utility.api.ApiCallbackListener
import com.example.cafebazar.utility.api.ApiResultModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MainScreenPresenter(applicationContext: Context, view: MainScreenActivity) :
    BasePresenter<MainScreenContract.View>(),
    MainScreenContract.Presenter {
    private var mView: MainScreenActivity? = null
    private val client_id = "VKXHR2MKRJYMADL00X3T3M1K50YILADND1S2DQMCAK5UBV5V"
    private val client_secret = "LQD0XJ0BVJTWUTNU2VYCY42JEP2WXPSUZGX1MLK1G1ARWZ03"
    val limit: Int = 10
    lateinit var utils: Utils
    private var venues: ArrayList<Venue>? = null
    private var location: Location? = null
    var userService: UserService? = null
    var isGPSEnable = false
    var isNetworkEnable = false
    private var locationManager: LocationManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var current_location: Location? = null
    private lateinit var Venue_db: AppDb
    private lateinit var Location_db: AppDb
    var context: Context
    lateinit var adapter: VenuRecycelerViewAdapter
    internal var offset: Int? = 0

    init {
        context = applicationContext
        mView = view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated() {
        super.onViewCreated()
        SettingsManager.init(context)
        mView?.initView()
        utils = Utils(context)
        SettingsManager.setValue(Constants().CLIENT_ID, client_id)
        SettingsManager.setValue(Constants().CLIENT_SECRET, client_secret)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        userService = UserService(context)
        venues = ArrayList()
        Venue_db =
            Room.databaseBuilder(context, AppDb::class.java, "VenueDB").build()
        Location_db =
            Room.databaseBuilder(context, AppDb::class.java, "LocationDB").build()
        getLastLocation()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun getLastLocation() {
        if (utils!!.isGpsLocationEnabled()) {
            fusedLocationClient!!.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        current_location = task.result
                        checkDistance(current_location)

                    } else {
                        locationManager =
                            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        isGPSEnable =
                            locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        isNetworkEnable =
                            locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        location = null
                        if (location == null) {
                            if (locationManager!!.getAllProviders()
                                    .contains(LocationManager.NETWORK_PROVIDER)
                            ) {
                                locationManager!!.requestSingleUpdate(
                                    LocationManager.NETWORK_PROVIDER,
                                    object : LocationListener {
                                        override fun onLocationChanged(location: Location) {}
                                        override fun onStatusChanged(
                                            provider: String,
                                            status: Int,
                                            extras: Bundle
                                        ) {
                                        }

                                        override fun onProviderEnabled(provider: String) {}
                                        override fun onProviderDisabled(provider: String) {}
                                    },
                                    null
                                )
                                if (locationManager != null) {
                                    location =
                                        locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                    if (location != null) {
                                        current_location = location
                                        checkDistance(current_location)
                                    }
                                }
                            }
                        }

                    }
                }
        } else {
            mView?.showLocationPopup()
            Fetch_from_database()

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun checkDistance(currentLocation: Location?) {
        val distance: Double = distance(
            currentLocation!!.latitude,
            currentLocation!!.longitude,
            SettingsManager.getDouble(Constants().PREF_LOC_LAT),
            SettingsManager.getDouble(Constants().PREF_LOC_LON)
        )
        if (distance >= Constants().CONFIG_LOCATION_DISTANCE) {
            SettingsManager.setValue(Constants().PREF_LOC_LAT, currentLocation!!.latitude)
            SettingsManager.setValue(Constants().PREF_LOC_LON, currentLocation!!.longitude)
            val ll =
                currentLocation!!.getLatitude().toString() + "," + currentLocation!!.getLongitude()
                    .toString()
            get_venue(ll, limit, offset)
        } else if (distance < Constants().CONFIG_LOCATION_DISTANCE) {
            Fetch_from_database()
        }
    }

    override fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    override fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    override fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun get_venue(ll: String?, limit: Int?, offset: Int?) {
        if (utils!!.isNetworkAvailable()) {
            if (mView?.snackbar!!.isShown())
                mView?.snackbar!!.dismiss()
            try {
                userService!!.searsh_venue(ll, limit, offset, object : ApiCallbackListener {
                    override fun onSucceed(data: ApiResultModel) {
                        if (data.meta?.getCode() === 200) {
                            if (data!!.response!!.groups!![0]?.items!!.size > 0) {
                                for (item in data!!.response!!.groups!![0]?.items!!) {
                                    item.venue?.let { venues?.add(it) }
                                }

                                Set_Venue_Data(venues!!)
                                Add_to_database(venues!!)
                                if (offset != null) {
                                    this@MainScreenPresenter.offset = offset + 10
                                }

                            }
                        }
                    }

                    override fun onError(errors: MutableList<Error>?) {}
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            mView?.snackbar!!.show()

        }
    }

    override fun Add_to_database(VenueList: List<Venue>) {
        Clear_Database()
        val thread = Thread {
            try {
                for (item: Venue in VenueList) {

                    var venueEntity = VenueEntity()
                    venueEntity.ID = (0..1000).random()
                    venueEntity.VenueId = item.id!!
                    venueEntity.Vname = item.name!!
                    venueEntity.referralId = item.referralId!!
                    Venue_db.venueDAO().saveVenues(venueEntity)

                    if (item.location != null) {
                        var locationEntity = LocationEntity()
                        locationEntity.ID = (0..100).random()
                        locationEntity.LocationId = item.id!!
                        locationEntity.address = item.location!!.address!!
                        locationEntity.cc = item.location!!.cc!!
                        locationEntity.city = item.location!!.city!!
                        locationEntity.country = item.location!!.country!!
                        locationEntity.distance = item.location!!.distance
                        locationEntity.lat = item.location!!.lat!!
                        locationEntity.lng = item.location!!.lng!!
                        locationEntity.state = item.location!!.state!!
                        Location_db.locationDAO().saveLocation(locationEntity)

                    }

                }
            } catch (e: Exception) {
            }

        }
        thread.start()
        SettingsManager.setValue(Constants().PREF_FETCH_DATA, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun Fetch_from_database() {

        val thread = Thread {
            try {
                if (Venue_db.venueDAO().getAllVenues().size > 1) {
                    Venue_db.venueDAO().getAllVenues().forEach() {
                        var venue = Venue()
                        venue.id = it.VenueId
                        venue.name = it.Vname
                        venue.referralId = it.referralId

                        Location_db.locationDAO().selectLocation(it.VenueId).forEach() {
                            var location = com.example.cafebazar.model.Location()
                            location.address = it.address
                            location.cc = it.cc
                            location.city = it.city
                            location.country = it.country
                            location.distance = it.distance
                            location.lat = it.lat
                            location.lng = it.lng
                            location.state = it.state

                            venue.location = location
                        }
                        venues?.add(venue)
                    }
                } else {
                    val ll =
                        SettingsManager.getDouble(Constants().PREF_LOC_LAT).toString() +
                                "," + SettingsManager.getDouble(Constants().PREF_LOC_LON).toString()
                    get_venue(ll, limit, offset)
                }

            } catch (e: Exception) {
            }

        }
        thread.start()
        thread.join()
        venues?.let { Set_Venue_Data(it) }
    }

    override fun Clear_Database() {
        val thread = Thread {
            Venue_db.venueDAO().DeleteVenue()
            Location_db.locationDAO().DeleteLocation()
        }
        thread.start()
        thread.join()
    }

    override fun Set_Venue_Data(venues: ArrayList<Venue>) {
        if (venues.size > 0) {
            mView?.Show_venue(venues)
        }
    }

}