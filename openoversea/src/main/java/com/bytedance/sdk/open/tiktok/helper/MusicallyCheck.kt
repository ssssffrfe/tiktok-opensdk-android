package com.bytedance.sdk.open.tiktok.helper

import android.content.Context
import com.bytedance.sdk.open.tiktok.base.AppCheckBase

class MusicallyCheck(override val context: Context) : AppCheckBase(context) {
    override val signature: String = "194326e82c84a639a52e5c023116f12a"
    override val packageName: String = "com.zhiliaoapp.musically"
}
