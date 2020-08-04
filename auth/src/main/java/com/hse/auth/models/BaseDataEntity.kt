package com.hse.auth.models

import com.hse.auth.ui.models.BaseEntity

interface BaseDataEntity<T : BaseEntity> {
    fun toEntity(): T
}