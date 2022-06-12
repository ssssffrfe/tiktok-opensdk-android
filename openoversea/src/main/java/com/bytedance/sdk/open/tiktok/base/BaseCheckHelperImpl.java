package com.bytedance.sdk.open.tiktok.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.common.constants.Keys;
import com.bytedance.sdk.open.tiktok.utils.AppUtil;
import com.bytedance.sdk.open.tiktok.utils.SignatureUtils;

/**
 * To check app is support authorization request or share request
 */
abstract public class BaseCheckHelperImpl implements IAPPCheckHelper {

    private Context mContext;

    public BaseCheckHelperImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean isAppSupportAuthorization() {
        return  isAppSupportAuthApi()
                && SignatureUtils.validateSign(mContext, getPackageName(), getSignature());
    }

    @Override
    public boolean isAppSupportShare() {
        return  isAppSupportShareApi(getPackageName(), getRemoteAuthEntryActivity(), Keys.API.MIN_SDK_NEW_VERSION_API);
    }

    /**
     * There is version requirement of sharing and authorizing. The minimum app version supporting sharing is 11.3.0.
     * This method is to check if app supports sharing and authorizing
     *
     * @param platformPackageName
     * @param remoteRequestEntry
     * @param requiredApi
     * @return
     */

    public boolean isAppSupportAPI(String platformPackageName, String remoteRequestEntry, int requiredApi) {
        if (mContext == null || TextUtils.isEmpty(platformPackageName)) {
            return false;
        }


        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(platformPackageName, AppUtil.buildComponentClassName(platformPackageName, remoteRequestEntry));
        intent.setComponent(componentName);
        ActivityInfo activityInfo = intent.resolveActivityInfo(mContext.getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY);
        int platformSdkVersion = getPlatformSDKVersion(platformPackageName, remoteRequestEntry);
        return activityInfo != null && activityInfo.exported && (platformSdkVersion >= requiredApi);
    }

    /**
     * To check if app supports authorizing
     * @return
     */
    private boolean isAppSupportAuthApi() {
        return isAppSupportAPI(getPackageName(), getRemoteAuthEntryActivity(), getAuthRequestApi());
    }


    public int getPlatformSDKVersion(String platformPackageName, String remoteRequestEntry) {
        if (mContext == null || TextUtils.isEmpty(platformPackageName)) {
            return Keys.SDK_VERSION_ERROR;
        }
        try {
            ComponentName componentName = new ComponentName(platformPackageName, AppUtil.buildComponentClassName(platformPackageName, remoteRequestEntry));
            ActivityInfo appInfo = mContext.getPackageManager().getActivityInfo(componentName, PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                return appInfo.metaData.getInt(Keys.SDK_VERSION_KEY, Keys.SDK_VERSION_ERROR);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Keys.SDK_VERSION_ERROR;
    }

    /**
     * To check if app supports sharing
     * @param platformPackageName
     * @param remoteRequestEntry
     * @param requiredApi
     * @return
     */
    private boolean isAppSupportShareApi(String platformPackageName, String remoteRequestEntry, int requiredApi) {
        return isAppSupportAPI(platformPackageName, remoteRequestEntry, requiredApi);
    }

    /**
     * To check if app is installed
     *
     * @return
     */
    public boolean isAppInstalled() {
        // for lite , getPackageInfo is not legal so should delete the function
        // authorize & share can use the same activity to test
        return isAppSupportAPI(getPackageName(), getRemoteAuthEntryActivity(), getAuthRequestApi());
    }
    

    @Override
    public String getRemoteAuthEntryActivity() {
        return "openauthorize.AwemeAuthorizedActivity";
    }

    @Override
    public boolean isAppSupportAPI(int requiredApi) {
        return isAppSupportAPI(getPackageName(), getRemoteAuthEntryActivity(),requiredApi);
    }

    /**
     *
     * @return
     */
    abstract protected int getAuthRequestApi();

    /**
     *
     * @return
     */
    public abstract String getSignature();
}
