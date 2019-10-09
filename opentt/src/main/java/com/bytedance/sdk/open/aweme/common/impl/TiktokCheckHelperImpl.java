package com.bytedance.sdk.open.aweme.common.impl;

import android.content.Context;

import com.bytedance.sdk.open.aweme.base.BaseCheckHelperImpl;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;


/**
 * 主要功能：检查Tiktok的相关
 * author: ChangLei
 * since: 2019/4/1
 */
public class TiktokCheckHelperImpl extends BaseCheckHelperImpl {

    public TiktokCheckHelperImpl(Context context) {
        super(context);
    }

    @Override
    protected int getAuthRequestApi() {
        return ParamKeyConstants.REQUIRED_API_VERSION.AUTH_REQUIRE_API;
    }

    @Override
    public String getSignature() {
        return "aea615ab910015038f73c47e45d21466";
    }

    @Override
    public String getPackageName() {
        return "com.ss.android.ugc.trill";
    }
}
