package com.example.cafebazar.utility.api

import com.example.cafebazar.utility.Error


/**
 * Created by Zohre Niayeshi on 19,December,2020 niayesh1993@gmail.com
 **/
interface ApiCallbackListener {

     fun onSucceed(data: ApiResultModel)
     fun onError(errors: MutableList<Error>?)
}