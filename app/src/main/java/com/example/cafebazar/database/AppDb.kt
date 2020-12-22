package com.example.cafebazar.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cafebazar.model.database.caller.LocationDAO
import com.example.cafebazar.model.database.caller.VenueDAO


/**
 * Created by Zohre Niayeshi on 19,December,2020 niayesh1993@gmail.com
 **/
@Database(entities = [(VenueEntity::class),(LocationEntity::class)],version = 1)
abstract class AppDb : RoomDatabase() {

    abstract fun venueDAO(): VenueDAO
    abstract fun locationDAO(): LocationDAO

}