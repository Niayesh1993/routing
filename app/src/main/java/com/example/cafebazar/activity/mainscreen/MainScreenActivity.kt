package com.example.cafebazar.activity.mainscreen

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafebazar.R
import com.example.cafebazar.adapter.VenuRecycelerViewAdapter
import com.example.cafebazar.contract.MainScreenContract
import com.example.cafebazar.model.Venue
import com.example.cafebazar.receiver.BroadcastReceivers
import com.example.cafebazar.service.LocationService
import com.example.cafebazar.utility.Constants
import com.example.cafebazar.utility.SettingsManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Zohre Niayeshi on 20,December,2020 niayesh1993@gmail.com
 **/
class MainScreenActivity : AppCompatActivity(), OnMapReadyCallback,
    BroadcastReceivers.BroadcastListener,
    MainScreenContract.View
{

    private lateinit var mMap: GoogleMap
    var snackbar: Snackbar? = null
    private var myDialog: Dialog? = null
    private lateinit var presenter: MainScreenPresenter
    var receiver: BroadcastReceivers =
        BroadcastReceivers()
    lateinit var recyclerView: RecyclerView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen_activity)
        presenter = MainScreenPresenter(applicationContext, this)
        presenter.onViewCreated()
    }

    override fun onResume()
    {
        super.onResume()
        if (!presenter.utils!!.isMyServiceRunning(LocationService ::class.java))
        {
            val i = Intent(this@MainScreenActivity, LocationService::class.java)
            startService(i)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBroadcastReceiver(intent: Intent)
    {
        val action = if (intent == null) "" else intent.action
        if (BroadcastReceivers().ACTION_NEW_LOCATION.equals(action))
        {
            presenter.getLastLocation()
        }
    }

    override fun initView() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.main_screen_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val filter = IntentFilter()
        filter.addAction(BroadcastReceivers().ACTION_LOCATION_SERVICE)
        filter.addAction(BroadcastReceivers().ACTION_NEW_LOCATION)
        registerReceiver(receiver, filter)
        receiver.broadcastReceiver = this
        recyclerView = findViewById(R.id.list_of_venus)
        myDialog = Dialog(this)
        //SetUp Snackbar
        snackbar = Snackbar.make(
            findViewById(R.id.drawer_layout),
            R.string.general_no_internet_connection,
            Snackbar.LENGTH_LONG
        )
        snackbar!!.setAction("", null)
        snackbar!!.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.orangeRed))

        val view = snackbar!!.getView()
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        val tv = view.findViewById(R.id.snackbar_text) as TextView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        } else {
            tv.gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    override fun Show_venue(venues: ArrayList<Venue>)
    {
        if (venues.size > 0) {

            presenter.adapter = VenuRecycelerViewAdapter(venues, this)
            recyclerView.adapter = presenter.adapter
            recyclerView.itemAnimator = DefaultItemAnimator()

            val layoutmanager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutmanager

            recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener()
            {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState== RecyclerView.SCROLL_STATE_IDLE ) {
                        print("end")
                        if (presenter.offset != null){
                            //end of scroll
                            val ll =
                                SettingsManager.getDouble(Constants().PREF_LOC_LAT).toString() +
                                        "," + SettingsManager.getDouble(Constants().PREF_LOC_LON).toString()
                            presenter.get_venue(ll, presenter.limit, presenter.offset)
                        }
                        if (presenter.offset == null){
                            print("end of list")
                        }
                    }
                }

            })


        }
    }

    override fun showLocationPopup() {
        myDialog!!.setContentView(R.layout.enable_location_dialog)
        myDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog!!.show()
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val originLocation = LatLng(
            SettingsManager.getDouble(Constants().PREF_LOC_LAT),
            SettingsManager.getDouble(Constants().PREF_LOC_LON)
        )
        val originMarker = mMap.addMarker(
            MarkerOptions()
                .position(originLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin))
                .title(R.string.origin.toString())
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 20.0f))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

}