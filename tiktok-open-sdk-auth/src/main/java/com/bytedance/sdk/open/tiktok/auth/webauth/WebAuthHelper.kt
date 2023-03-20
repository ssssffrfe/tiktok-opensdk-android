package com.bytedance.sdk.open.tiktok.auth.webauth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.auth.Auth
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.WEB_AUTH_ENDPOINT
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.WEB_AUTH_HOST
import com.bytedance.sdk.open.tiktok.auth.constants.Keys
import com.bytedance.sdk.open.tiktok.core.constants.Constants
import com.bytedance.sdk.open.tiktok.core.utils.Md5Utils.hexDigest
import com.bytedance.sdk.open.tiktok.core.utils.SignatureUtils.getMd5Signs
import com.bytedance.sdk.open.tiktok.core.utils.SignatureUtils.packageSignature

internal object WebAuthHelper {
    enum class OSFrom(val value: String) {
        WEBVIEW("webview"),
        BROWSER("browser")
    }

    private const val DEVICE_ANDROID = "android"

    fun composeLoadUrl(
        context: Context,
        redirectUrl: String,
        authRequest: Auth.Request,
        clientKey: String,
        osFrom: OSFrom
    ): String {
        val signs = getMd5Signs(context, authRequest.redirectUri)
        val builder = Uri.Builder()
            .scheme(Keys.WebAuth.SCHEMA_HTTPS)
            .authority(WEB_AUTH_HOST)
            .path(WEB_AUTH_ENDPOINT)
            .appendQueryParameter(Keys.WebAuth.QUERY_RESPONSE_TYPE, Keys.WebAuth.VALUE_RESPONSE_TYPE_CODE)
            .appendQueryParameter(Keys.WebAuth.QUERY_FROM, Keys.WebAuth.VALUE_FROM_OPENSDK)
            .appendQueryParameter(Keys.WebAuth.QUERY_PLATFORM, DEVICE_ANDROID)
            .appendQueryParameter(Keys.WebAuth.QUERY_OS_TYPE, DEVICE_ANDROID)
            .appendQueryParameter(Keys.WebAuth.QUERY_OS_FROM, osFrom.value)

        packageSignature(signs)?.let { builder.appendQueryParameter(Keys.WebAuth.QUERY_SIGNATURE, it) }
        builder.appendQueryParameter(Keys.WebAuth.QUERY_REDIRECT_URI, redirectUrl)
        with(authRequest) {
            builder.appendQueryParameter(Keys.WebAuth.QUERY_CLIENT_KEY, clientKey)
            state?.let {
                builder.appendQueryParameter(Keys.WebAuth.QUERY_STATE, it)
            }
            builder.appendQueryParameter(Keys.WebAuth.QUERY_SCOPE, scope)
            builder.appendQueryParameter(
                Keys.WebAuth.QUERY_ENCRYPTION_PACKAGE,
                hexDigest(this.redirectUri)
            )

            language?.let {
                builder.appendQueryParameter(Keys.WebAuth.QUERY_LANGUAGE, it)
            }
        }

        return builder.build().toString()
    }

    fun parseRedirectUriToAuthResponse(uri: Uri, extras: Bundle? = null): Auth.Response {
        val authCode = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_CODE)
        return if (authCode != null) {
            Auth.Response(
                authCode = authCode,
                state = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_STATE),
                grantedPermissions = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_SCOPE)
                    ?: "",
                errorCode = Constants.BaseError.OK,
                errorMsg = null,
            )
        } else {
            val errorCodeStr: String? = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_ERROR_CODE)
            val errorCode = try {
                errorCodeStr?.toInt() ?: Constants.BaseError.ERROR_UNKNOWN
            } catch (e: Exception) {
                Constants.BaseError.ERROR_UNKNOWN
            }
            val errorMsgStr: String? = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_ERROR_MESSAGE)
            Auth.Response(
                authCode = "",
                state = null,
                grantedPermissions = "",
                errorCode = errorCode,
                errorMsg = errorMsgStr,
                extras = extras
            )
        }
    }
}
