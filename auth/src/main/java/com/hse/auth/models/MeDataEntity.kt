package com.hse.auth.models

import com.google.gson.annotations.SerializedName
import com.hse.auth.ui.models.UserEntity

data class MeDataEntity(
    @SerializedName("profile") var user: StudentDataEntity?
) : BaseDataEntity<UserEntity> {

    override fun toEntity(): UserEntity {
        return user!!.toEntity()
    }
}