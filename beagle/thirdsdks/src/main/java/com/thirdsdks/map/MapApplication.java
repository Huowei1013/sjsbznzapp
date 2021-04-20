package com.thirdsdks.map;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import com.beagle.component.app.IApplication;

import com.thirdsdks.map.track.Track;

/**
 * Created by zhaotao on 2021/01/05 18:13.
 */

public class MapApplication implements IApplication {

    public Track track;

    @Override
    public void attachBaseContext(Application application) {

    }

    @Override
    public void onCreate(Application application) {
        LocationService.getLocationService(application);
        SDKInitializer.initialize(application);
        track = new Track(application);
    }
}
