package com.bytedance.sdk.open.tiktok.base;

import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.common.constants.Keys;

import java.util.ArrayList;

public class ImageObject implements IMediaObject {

    private static final String TAG = "ImageObject";
    public ArrayList<String> mImagePaths;

    public ImageObject() {

    }

    public ImageObject(ArrayList<String> images) {
        this.mImagePaths = new ArrayList<>();
        this.mImagePaths.addAll(images);
    }

    @Override
    public void serialize(Bundle bundle) {
        bundle.putStringArrayList(Keys.IMAGE_PATH, this.mImagePaths);
    }

    @Override
    public void unserialize(Bundle bundle) {
        this.mImagePaths = bundle.getStringArrayList(Keys.IMAGE_PATH);
    }

    @Override
    public int type() {
        return IMediaObject.TYPE_IMAGE;
    }

    @Override
    public boolean checkArgs() {
        return true;
    }
}
