package com.hse.auth.requests

import com.google.gson.Gson
import com.hse.auth.models.MeDataEntity
import com.hse.auth.utils.fromJson

class GetMeRequest(accessToken: String) :
    com.hse.network.Request<MeDataEntity>("https://api.hseapp.ru/v2/dump/me") {
    init {
        headers.put("Authorization", "Bearer $accessToken")
    }

    override fun parse(response: String): MeDataEntity {
        return Gson().fromJson(response)
    }
}