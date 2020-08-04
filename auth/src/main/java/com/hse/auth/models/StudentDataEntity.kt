package com.hse.auth.models

import com.google.gson.annotations.SerializedName
import com.hse.auth.ui.models.UserEntity
import com.hse.auth.utils.toIntSafe

data class StudentDataEntity(
    @SerializedName("id") var id: String,
    @SerializedName("avatar_url") var avatarUrl: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("title") var title: String?
) : BaseDataEntity<UserEntity> {

    override fun toEntity() = UserEntity(
        id = id.toIntSafe(),
        name = title,
        email = email,
        originalEmail = email,
        avatar = avatarUrl
    )
}