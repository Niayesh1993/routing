package com.example.cafebazar.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cafebazar.R
import com.example.cafebazar.activity.detailscreen.DetailScreenActivity
import com.example.cafebazar.model.Venue
import com.example.cafebazar.utility.Utils


/**
 * Created by Zohre Niayeshi on 18,December,2020 niayesh1993@gmail.com
 **/
class VenuRecycelerViewAdapter(var myVenues: MutableList<Venue>,
                               private  var context: Context):
    RecyclerView.Adapter<VenuRecycelerViewAdapter.ViewHolder>()
{
    private var utils: Utils

    init {

        this.myVenues = myVenues
        this.context = context
        utils = Utils(context)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.venue_view_holder_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myVenues.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name_txt.setText(myVenues[position].name)
        holder.address_txt.setText(myVenues[position].location?.address)
        holder.cv.setOnClickListener {
            val detail = Intent(context, DetailScreenActivity::class.java)
            detail.putExtra("VenueId", myVenues[position].id)
            context.startActivity(detail)
        }

    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView)
    {

        val view: View = mView
        var venue_img: ImageView
        var name_txt: TextView
        var address_txt: TextView
        var category_name_txt: TextView
        internal var cv: CardView
        internal var mItem: Venue? = null
        internal var number = 0

        init {

            venue_img = view.findViewById(R.id.ic_venue)
            name_txt = view.findViewById(R.id.txt_name)
            address_txt = view.findViewById(R.id.txt_address)
            category_name_txt = view.findViewById(R.id.txt_category_name)
            cv = view.findViewById(R.id.card_view)
        }

    }
}