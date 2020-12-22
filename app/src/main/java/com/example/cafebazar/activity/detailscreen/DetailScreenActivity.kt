package com.example.cafebazar.activity.detailscreen

import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.cafebazar.R
import com.example.cafebazar.contract.VenueDetailContract
import com.example.cafebazar.model.Location
import com.example.cafebazar.model.Venue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Created by Zohre Niayeshi on 20,December,2020 niayesh1993@gmail.com
 **/

class DetailScreenActivity : FragmentActivity(), OnMapReadyCallback, VenueDetailContract.View {

    private var name_txt: TextView? = null
    private var city_txt: TextView? = null
    private var country_txt: TextView? = null
    private var lat_txt: TextView? = null
    private var lng_txt: TextView? = null
    private var venueId: String? = null
    private var venue_location: Location? = null
    private lateinit var mMap: GoogleMap
    private lateinit var presenter: DetailScreenPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_screen_activity)
        presenter = DetailScreenPresenter(applicationContext, this)
        presenter.onViewCreated()

        if (intent != null) {
            venueId = intent.getStringExtra("VenueId")
            presenter.get_venue_detail(venueId)
        }
    }

    override fun onMapReady(p0: GoogleMap) {

        mMap = p0

        if (mMap != null && venue_location?.lat != null && venue_location?.lng != null) {
            val customerLocation = LatLng(
                venue_location!!.lat!!,
                venue_location!!.lng!!
            )

            val customerMarker = mMap!!.addMarker(
                MarkerOptions()
                    .position(customerLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
            )
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 14.0f))
        }

    }

    override fun initView() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.detail_screen_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        name_txt = findViewById(R.id.txt_name)
        city_txt = findViewById(R.id.city_Txt)
        country_txt = findViewById(R.id.country_Txt)
        lat_txt = findViewById(R.id.lat_Txt)
        lng_txt = findViewById(R.id.lng_Txt)
    }

    override fun RefreshView(venue: Venue) {
        venue_location = venue.location
        name_txt!!.setText(venue!!.name)
        city_txt!!.setText(venue_location!!.city.toString() + "-" + venue_location!!.country)
        country_txt!!.setText(venue_location!!.address)
        lat_txt!!.setText(java.lang.String.valueOf(venue_location!!.lat))
        lng_txt!!.setText(java.lang.String.valueOf(venue_location!!.lng))
        if (presenter.utils?.isNetworkAvailable()!!)
            onMapReady(mMap)

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
