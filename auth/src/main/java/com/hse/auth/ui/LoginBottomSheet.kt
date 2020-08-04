package com.hse.auth.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import com.hse.auth.AuthHelper
import com.hse.auth.R
import com.hse.auth.utils.Mode
import com.hse.core.common.onClick
import com.hse.core.ui.widgets.BottomSheet
import com.hse.core.ui.widgets.HseButton

class LoginBottomSheet(val activity: Activity, val requestCode: Int) : BottomSheet(activity) {

    override fun getView(): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.auth_bottom_sheet, null, false)
        view.findViewById<HseButton>(R.id.button).onClick {
            AuthHelper.login(activity, Mode.BASIC, requestCode)
            dismiss()
        }
        return view
    }

    override fun getBottomView() = null
}