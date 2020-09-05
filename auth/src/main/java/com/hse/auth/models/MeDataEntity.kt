package com.hse.auth.models

import com.google.gson.annotations.SerializedName
import com.hse.auth.ui.models.UserEntity

data class MeDataEntity(
    @SerializedName("avatar_url") var avatarUrl: String?
) : BaseDataEntity<UserEntity> {

    override fun toEntity(): UserEntity {
        return UserEntity(avatar = avatarUrl)
    }
}