package com.bytedance.sdk.open.tiktok.base;

import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.common.constants.Keys;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class AnchorObject {
    @SerializedName("anchor_business_type")
    int mAnchorBusinessType;
    @SerializedName("anchor_title")
    String mAnchorTitle;

    public int getAnchorBusinessType() {
        return mAnchorBusinessType;
    }

    public void setAnchorBusinessType(int mAnchorBusinessType) {
        this.mAnchorBusinessType = mAnchorBusinessType;
    }

    public String getAnchorTitle() {
        return mAnchorTitle;
    }

    public void setAnchorTitle(String mAnchorTitle) {
        this.mAnchorTitle = mAnchorTitle;
    }

    public String getAnchorContent() {
        return mAnchorContent;
    }

    public void setAnchorContent(String mAnchorContent) {
        this.mAnchorContent = mAnchorContent;
    }

    @SerializedName("anchor_content")
    String mAnchorContent;

    public void serialize(Bundle sendBundle) {
        if (sendBundle == null) {
            return;
        }

        Gson gson = new Gson();
        String result = gson.toJson(this);
        sendBundle.putString(Keys.Share.SHARE_ANCHOR_INFO, result);
    }

    public static AnchorObject unserialize(Bundle clientBundle) {
        if (clientBundle == null) {
            return null;
        }

        String info = clientBundle.getString(Keys.Share.SHARE_ANCHOR_INFO);
        try {
            if (!TextUtils.isEmpty(info)) {
                Gson gson = new Gson();
                return gson.fromJson(info, AnchorObject.class);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }
}
