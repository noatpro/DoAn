package com.nguyentoan.bepngon

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.nguyentoan.bepngon.util.SharePreferenceUtils

class MyApplication : Application()  {

    override fun onCreate() {
        super.onCreate()
        SharePreferenceUtils.init(this)
        Fresco.initialize(this)
    }
}