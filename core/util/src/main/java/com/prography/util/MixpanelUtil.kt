package com.prography.util

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixpanelUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mixpanel: MixpanelAPI? = null

    companion object {
        private var instance: MixpanelUtil? = null

        fun getInstance(): MixpanelUtil? = instance

        internal fun setInstance(util: MixpanelUtil) {
            instance = util
        }

        // Static 편의 메서드들
        fun track(eventName: String, properties: Map<String, Any>? = null) {
            getInstance()?.trackInternal(eventName, properties)
        }

        fun identify(userId: String) {
            getInstance()?.identifyInternal(userId)
        }

        fun setUserProfile(properties: Map<String, Any>) {
            getInstance()?.setUserProfileInternal(properties)
        }

        fun setSuperProperties(properties: Map<String, Any>) {
            getInstance()?.setSuperPropertiesInternal(properties)
        }

        fun reset() {
            getInstance()?.resetInternal()
        }

        fun flush() {
            getInstance()?.flushInternal()
        }

        fun optOut() {
            getInstance()?.optOutInternal()
        }

        fun optIn() {
            getInstance()?.optInInternal()
        }
    }

    fun initialize(token: String) {
        try {
            mixpanel = MixpanelAPI.getInstance(context, token, false)
            setInstance(this) // Static 접근을 위한 인스턴스 설정
            Timber.d("🎯 Mixpanel initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to initialize Mixpanel")
        }
    }

    // Internal 메서드들 (실제 구현)
    internal fun trackInternal(eventName: String, properties: Map<String, Any>? = null) {
        try {
            val jsonProps = properties?.let { mapToJsonObject(it) }
            mixpanel?.track(eventName, jsonProps)
            Timber.d("📊 Mixpanel event tracked: $eventName")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to track event: $eventName")
        }
    }

    internal fun identifyInternal(userId: String) {
        try {
            mixpanel?.identify(userId)
            Timber.d("👤 Mixpanel user identified: $userId")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to identify user: $userId")
        }
    }

    internal fun setUserProfileInternal(properties: Map<String, Any>) {
        try {
            val jsonProps = mapToJsonObject(properties)
            mixpanel?.people?.set(jsonProps)
            Timber.d("👤 Mixpanel user profile updated")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to set user profile")
        }
    }

    internal fun setSuperPropertiesInternal(properties: Map<String, Any>) {
        try {
            val jsonProps = mapToJsonObject(properties)
            mixpanel?.registerSuperProperties(jsonProps)
            Timber.d("🌟 Mixpanel super properties set")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to set super properties")
        }
    }

    internal fun resetInternal() {
        try {
            mixpanel?.reset()
            Timber.d("🔄 Mixpanel reset")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to reset Mixpanel")
        }
    }

    internal fun flushInternal() {
        try {
            mixpanel?.flush()
            Timber.d("🚀 Mixpanel events flushed")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to flush Mixpanel events")
        }
    }

    internal fun optOutInternal() {
        try {
            mixpanel?.optOutTracking()
            Timber.d("🚫 Mixpanel tracking opted out")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to opt out tracking")
        }
    }

    internal fun optInInternal() {
        try {
            mixpanel?.optInTracking()
            Timber.d("✅ Mixpanel tracking opted in")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to opt in tracking")
        }
    }

    private fun mapToJsonObject(map: Map<String, Any>): JSONObject {
        val jsonObject = JSONObject()
        map.forEach { (key, value) ->
            jsonObject.put(key, value)
        }
        return jsonObject
    }
}