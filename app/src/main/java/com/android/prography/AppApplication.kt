package com.android.prography

import android.app.Application
import com.prography.capturecat.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import com.prography.util.MixpanelUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        MixpanelUtil.initialize(this, BuildConfig.MIXPANEL_PROJECT_TOKEN)
    }
}