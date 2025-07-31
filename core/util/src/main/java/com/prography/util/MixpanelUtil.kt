package com.prography.util

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject
import timber.log.Timber

object MixpanelUtil {

    private var mixpanel: MixpanelAPI? = null

    fun initialize(context: Context, token: String) {
        try {
            mixpanel = MixpanelAPI.getInstance(context, token, false)
            Timber.d("🎯 Mixpanel initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to initialize Mixpanel")
        }
    }

    /**
     * 이벤트 트래킹
     */
    fun track(eventName: String, properties: Map<String, Any>? = null) {
        try {
            val jsonProps = properties?.let { mapToJsonObject(it) }
            mixpanel?.track(eventName, jsonProps)
            Timber.d("📊 Mixpanel event tracked: $eventName")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to track event: $eventName")
        }
    }

    /**
     * 사용자 식별
     */
    fun identify(userId: String) {
        try {
            mixpanel?.identify(userId)
            Timber.d("👤 Mixpanel user identified: $userId")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to identify user: $userId")
        }
    }

    /**
     * 사용자 프로필 설정
     */
    fun setUserProfile(properties: Map<String, Any>) {
        try {
            val jsonProps = mapToJsonObject(properties)
            mixpanel?.people?.set(jsonProps)
            Timber.d("👤 Mixpanel user profile updated")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to set user profile")
        }
    }

    /**
     * Super Properties 설정 (모든 이벤트에 자동 포함)
     */
    fun setSuperProperties(properties: Map<String, Any>) {
        try {
            val jsonProps = mapToJsonObject(properties)
            mixpanel?.registerSuperProperties(jsonProps)
            Timber.d("🌟 Mixpanel super properties set")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to set super properties")
        }
    }

    /**
     * 로그아웃 시 리셋
     */
    fun reset() {
        try {
            mixpanel?.reset()
            Timber.d("🔄 Mixpanel reset")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to reset Mixpanel")
        }
    }

    /**
     * 즉시 전송
     */
    fun flush() {
        try {
            mixpanel?.flush()
            Timber.d("🚀 Mixpanel events flushed")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to flush Mixpanel events")
        }
    }

    /**
     * 추적 중단
     */
    fun optOut() {
        try {
            mixpanel?.optOutTracking()
            Timber.d("🚫 Mixpanel tracking opted out")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to opt out tracking")
        }
    }

    /**
     * 추적 재개
     */
    fun optIn() {
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