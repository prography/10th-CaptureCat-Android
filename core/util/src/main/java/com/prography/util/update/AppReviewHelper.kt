package com.prography.util.update

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.tasks.await
import timber.log.Timber

object AppReviewHelper {
    /**
     * 인앱 리뷰 바텀시트를 표시.
     * 실패 시 조용히 무시.
     */
    suspend fun showInAppReview(activity: Activity) {
        try {
            val manager = ReviewManagerFactory.create(activity)
            val request = manager.requestReviewFlow().await()
            manager.launchReviewFlow(activity, request).await()
            Timber.d("✅ In-app review launched successfully")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to show in-app review")
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}