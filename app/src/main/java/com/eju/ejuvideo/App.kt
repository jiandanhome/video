package com.eju.ejuvideo

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.eju.video.EjuVideo
import com.gyf.immersionbar.ImmersionBar

class App:Application() {
    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object:ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity?.let{
                    ImmersionBar.with(activity)
                        .fitsSystemWindows(false)
                        .statusBarColorInt(Color.TRANSPARENT)
                        .statusBarDarkFont(true)
                        .init();
                }
            }
            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

        })


        EjuVideo.init(this, {
            Log.i("sck220", "onCreate: ${it}")
        })

    }
}