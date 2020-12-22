package com.example.cafebazar.utility.api


import com.example.cafebazar.model.FoursqureModel.Meta
import com.example.cafebazar.model.FoursqureModel.Response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Zohre Niayeshi on 19,December,2020 niayesh1993@gmail.com
 **/
class ApiResultModel {

    @SerializedName("meta")
    var meta: Meta? = null

    @SerializedName("response")
    @Expose
    var response: Response? = null

}