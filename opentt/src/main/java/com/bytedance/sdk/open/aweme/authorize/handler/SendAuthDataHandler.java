package com.bytedance.sdk.open.aweme.authorize.handler;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.constants.TikTokConstants;
import com.bytedance.sdk.open.aweme.common.handler.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.common.handler.TikTokDataHandler;

/**
 * auth 请求/结果的 数据解析
 * Created by yangzhirong on 2018/10/8.
 */
public class SendAuthDataHandler implements TikTokDataHandler {
    @Override
    public boolean handle(int type, Bundle bundle, TikTokApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == TikTokConstants.ModeType.SEND_AUTH_REQUEST) {
            Authorization.Request request = new Authorization.Request(bundle);
            if (request.checkArgs()) {
                // 处理调空格，否则服务端不认
                if (request.scope != null) {
                    request.scope = request.scope.replace(" ","");
                }
                if (request.optionalScope1 != null) {
                    request.optionalScope1 = request.optionalScope1.replace(" ", "");
                }
                if (request.optionalScope0 != null) {
                    request.optionalScope0 = request.optionalScope0.replace(" ", "");
                }
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == TikTokConstants.ModeType.SEND_AUTH_RESPONSE) {
            Authorization.Response response = new Authorization.Response(bundle);
            if (response.checkArgs()) {
                eventHandler.onResp(response);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
