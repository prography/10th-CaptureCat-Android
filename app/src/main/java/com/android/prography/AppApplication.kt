package com.android.prography

import android.app.Application
import com.prography.capturecat.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import com.prography.util.MixpanelUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AppApplication : Application() {

    @Inject
    lateinit var mixpanelUtil: MixpanelUtil

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        mixpanelUtil.initialize(BuildConfig.MIXPANEL_PROJECT_TOKEN)
    }
}