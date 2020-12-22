package com.example.cafebazar.model.database.caller

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cafebazar.model.database.LocationEntity


/**
 * Created by Zohre Niayeshi on 19,December,2020 niayesh1993@gmail.com
 **/
@Dao
interface LocationDAO {

    @Insert
    fun saveLocation(locationEntity: LocationEntity)

    @Query("Select * from LocationEntity WHERE LocationId =:id")
    fun selectLocation(id: String): List<LocationEntity>

    @Query("DELETE FROM LocationEntity")
    fun DeleteLocation()
}