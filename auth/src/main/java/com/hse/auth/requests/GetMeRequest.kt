package com.hse.auth.requests

import com.google.gson.Gson
import com.hse.auth.models.MeDataEntity
import com.hse.auth.utils.fromJson

class GetMeRequest : com.hse.network.Request<MeDataEntity>("https://api.hseapp.ru/pf/users/me") {

    override fun parse(response: String): MeDataEntity {
        return Gson().fromJson(response)
    }
}