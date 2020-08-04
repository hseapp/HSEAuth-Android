package com.hse.auth.ui.models

import com.hse.auth.ui.models.BaseEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEntity(
    val id: Int?,
    var name: String? = null,
    var email: String? = null,
    var originalEmail: String? = null,
    var avatar: String? = null
) : BaseEntity