package com.example.cafebazar.model.FoursqureModel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Categories() : Serializable, Parcelable {

    @SerializedName("id")
    var id: String = ""
    @SerializedName("name")
    var name: String = ""
    @SerializedName("pluralName")
    var pluralName: String = ""
    @SerializedName("shortName")
    var shortName: String = ""
    @SerializedName("primary")
    var primary: Boolean = true

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()!!
        name = parcel.readString()!!
        pluralName = parcel.readString()!!
        shortName = parcel.readString()!!
        primary = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Categories> {
        override fun createFromParcel(parcel: Parcel): Categories {
            return Categories(parcel)
        }

        override fun newArray(size: Int): Array<Categories?> {
            return arrayOfNulls(size)
        }
    }
}