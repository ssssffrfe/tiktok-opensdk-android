package com.bytedance.sdk.open.tiktok.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bytedance.sdk.open.tiktok.core.appcheck.TikTokAppCheckFactory
import com.bytedance.sdk.open.tiktok.core.constants.Constants.APIType
import com.bytedance.sdk.open.tiktok.core.constants.Constants.TIKTOK.SHARE_ACTIVITY_NAME
import com.bytedance.sdk.open.tiktok.core.constants.Constants.TIKTOK.TIKTOK_SHARE_COMPONENT_PATH
import com.bytedance.sdk.open.tiktok.core.utils.AppUtils
import com.bytedance.sdk.open.tiktok.share.constants.Constants
import com.bytedance.sdk.open.tiktok.share.constants.Keys
import com.bytedance.sdk.open.tiktok.share.model.MediaContent

/**
 * Provides an interface for sharing media to TikTok.
 * @param context your component context
 * @param clientKey your app client key
 * @param apiEventHandler the event handler class which will be used to handle sharing result
 */
class ShareApi(
    private val context: Context,
    private val clientKey: String,
    private val apiEventHandler: ShareApiEventHandler,
) {

    companion object {
        @JvmStatic
        fun isShareSupported(context: Context) = TikTokAppCheckFactory.getApiCheck(context, APIType.SHARE) != null
        @JvmStatic
        fun isShareFileProviderSupported(context: Context) = (TikTokAppCheckFactory.getApiCheck(context, APIType.SHARE)?.isShareFileProviderSupported ?: false)
    }

    fun handleResultIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return false
        }
        val bundle = intent.extras ?: return false
        val type = bundle.getInt(Keys.Share.TYPE)
        if (type == Constants.SHARE_RESPONSE) {
            apiEventHandler.onResponse(bundle.toShareResponse())
            return true
        }
        return false
    }

    fun share(request: Share.Request): Boolean {
        apiEventHandler.onRequest(request)
        TikTokAppCheckFactory.getApiCheck(context, APIType.SHARE)?.let {
            return share(request, it.appPackageName)
        }
        apiEventHandler.onResponse(
            Share.Response(
                errorCode = Constants.SHARE_UNSUPPORTED_ERROR,
                errorMsg = "TikTok is not installed or doesn't support the sharing feature",
                state = null,
                subErrorCode = Constants.SHARE_UNSUPPORTED_ERROR,
            )
        )
        return false
    }

    private fun share(request: Share.Request, packageName: String): Boolean {
        if (!request.validate()) {
            return false
        }
        grantTikTokPermissionToSharedFiles(request)
        val intent = Intent().apply {
            component = ComponentName(
                packageName,
                AppUtils.componentClassName(TIKTOK_SHARE_COMPONENT_PATH, SHARE_ACTIVITY_NAME)
            )
            putExtras(
                request.toBundle(
                    clientKey = clientKey,
                )
            )
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            type = getShareContentType(request.mediaContent)
            action = getShareContentAction(request.mediaContent)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun grantTikTokPermissionToSharedFiles(request: Share.Request) {
        request.mediaContent.mediaPaths.forEach {
            context.grantUriPermission(
                "com.ss.android.ugc.trill",
                Uri.parse(it), Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            context.grantUriPermission(
                "com.zhiliaoapp.musically",
                Uri.parse(it), Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun getShareContentType(mediaContent: MediaContent): String {
        return if (mediaContent.mediaType == Share.MediaType.IMAGE) {
            "image/*"
        } else {
            "video/*"
        }
    }

    private fun getShareContentAction(mediaContent: MediaContent): String {
        return if (mediaContent.mediaPaths.size > 1) {
            Intent.ACTION_SEND_MULTIPLE
        } else {
            Intent.ACTION_SEND
        }
    }
}
