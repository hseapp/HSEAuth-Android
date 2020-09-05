package com.hse.auth.ui.models

import com.hse.auth.ui.models.BaseEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEntity(
    var avatar: String? = null
) : BaseEntity