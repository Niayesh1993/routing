package com.example.cafebazar.model

import android.os.Parcel
import android.os.Parcelable
import com.example.routingapp.model.FoursqureModel.Categories
import com.example.routingapp.model.FoursqureModel.Meta
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by Zohre Niayeshi on 19,December,2020 niayesh1993@gmail.com
 **/
class Venue(): Serializable, Parcelable {

    @SerializedName("id")
    var id: String = ""
    @SerializedName("name")
    var name: String = ""
//    @SerializedName("verified")
//    var verified: Boolean = true
    @SerializedName("referralId")
    var referralId: String = ""
    @SerializedName("location")
    var location: Location? = null
    @SerializedName("categories")
    var categories: List<Categories>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()

    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<Venue> {
        override fun createFromParcel(parcel: Parcel): Venue {
            return Venue(parcel)
        }

        override fun newArray(size: Int): Array<Venue?> {
            return arrayOfNulls(size)
        }
    }

}