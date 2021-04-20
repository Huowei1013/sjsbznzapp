package com.thirdsdks.map.track.utils;

import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;

import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.TraceLocation;
import com.thirdsdks.map.track.CurrentLocation;
import com.thirdsdks.map.track.Track;

import java.util.List;

import static com.thirdsdks.map.track.utils.BitmapUtil.bmEnd;

/**
 * Created by zhaotao on 2021/01/05 18:13.
 */

public class MapUtil {

    private static MapUtil INSTANCE = new MapUtil();
    private MapStatus mapStatus = null;
    private Marker mMoveMarker = null;
    public TextureMapView mapView = null;
    public BaiduMap baiduMap = null;
    public LatLng lastPoint = null;

    public Marker getmMoveMarker() {
        return mMoveMarker;
    }
    public void setmMoveMarker(Marker mMoveMarker) {
        this.mMoveMarker = mMoveMarker;
    }

    /**
     * 路线覆盖物
     */
    public Overlay polylineOverlay = null;

    private MapUtil() {
    }

    public static MapUtil getInstance() {
        return INSTANCE;
    }

    public void init(TextureMapView view) {
        mapView = view;
        baiduMap = mapView.getMap();
        mapView.showZoomControls(false);
    }

    public void onPause() {
        if (null != mapView) {
            mapView.onPause();
        }
    }

    public void onResume() {
        if (null != mapView) {
            mapView.onResume();
        }
    }

    public void clear() {
        lastPoint = null;
        if (null != mMoveMarker) {
            mMoveMarker.remove();
            mMoveMarker = null;
        }
        if (null != polylineOverlay) {
            polylineOverlay.remove();
            polylineOverlay = null;
        }
        if (null != baiduMap) {
            baiduMap.clear();
            baiduMap = null;
        }
        mapStatus = null;
        if (null != mapView) {
            mapView.onDestroy();
            mapView = null;
        }
    }

    /**
     * 将轨迹实时定位点转换为地图坐标
     *
     * @param location
     * @return
     */
    public static LatLng convertTraceLocation2Map(TraceLocation location) {
        if (null == location) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            return null;
        }
        LatLng currentLatLng = new LatLng(latitude, longitude);
        if (CoordType.wgs84 == location.getCoordType()) {
            LatLng sourceLatLng = currentLatLng;
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(sourceLatLng);
            currentLatLng = converter.convert();
        }
        return currentLatLng;
    }

    /**
     * 将地图坐标转换轨迹坐标
     *
     * @param latLng
     * @return
     */
    public static com.baidu.trace.model.LatLng convertMap2Trace(LatLng latLng) {
        return new com.baidu.trace.model.LatLng(latLng.latitude, latLng.longitude);
    }

    /**
     * 将轨迹坐标对象转换为地图坐标对象
     *
     * @param traceLatLng
     * @return
     */
    public static LatLng convertTrace2Map(com.baidu.trace.model.LatLng traceLatLng) {
        return new LatLng(traceLatLng.latitude, traceLatLng.longitude);
    }

    /**
     * 设置地图中心：使用已有定位信息；
     *
     * @param trackApp
     */
    public void setCenter(Track trackApp) {
        if (!CommonUtil.isZeroPoint(CurrentLocation.latitude, CurrentLocation.longitude)) {
            LatLng currentLatLng = new LatLng(CurrentLocation.latitude, CurrentLocation.longitude);
            updateStatus(currentLatLng, false);
            return;
        }
        String lastLocation = trackApp.trackConf.getString(Constants.LAST_LOCATION, null);
        if (!TextUtils.isEmpty(lastLocation)) {
            String[] locationInfo = lastLocation.split(";");
            if (!CommonUtil.isZeroPoint(Double.parseDouble(locationInfo[1]),
                    Double.parseDouble(locationInfo[2]))) {
                LatLng currentLatLng = new LatLng(Double.parseDouble(locationInfo[1]),
                        Double.parseDouble(locationInfo[2]));
                updateStatus(currentLatLng, false);
                return;
            }
        }
    }

    public void updateStatus(LatLng currentPoint, boolean showMarker) {
        if (null == baiduMap || null == currentPoint) {
            return;
        }

        if (null != baiduMap.getProjection()) {
            Point screenPoint = baiduMap.getProjection().toScreenLocation(currentPoint);
            // 点在屏幕上的坐标超过限制范围，则重新聚焦底图
            if (screenPoint.y < 200 || screenPoint.y > Track.screenHeight - 500
                    || screenPoint.x < 200 || screenPoint.x > Track.screenWidth - 200
                    || null == mapStatus) {
                animateMapStatus(currentPoint, 15.0f);
            }
        } else if (null == mapStatus) {
            // 第一次定位时，聚焦底图
            setMapStatus(currentPoint, 15.0f);
        }
        if (showMarker) {
            addMarker(currentPoint);
        }
    }

    public Marker addOverlay(LatLng currentPoint, BitmapDescriptor icon, Bundle bundle) {
        OverlayOptions overlayOptions = new MarkerOptions().position(currentPoint)
                .icon(icon).zIndex(9).draggable(true);
        Marker marker = (Marker) baiduMap.addOverlay(overlayOptions);
        if (null != bundle) {
            marker.setExtraInfo(bundle);
        }
        return marker;
    }

    /**
     * 添加地图覆盖物
     */
    public void addMarker(LatLng currentPoint) {
        if (null == mMoveMarker) {
            mMoveMarker = addOverlay(currentPoint, BitmapUtil.bmArrowPoint, null);
            return;
        }

        if (null != lastPoint) {
            moveLooper(currentPoint);
        } else {
            lastPoint = currentPoint;
            mMoveMarker.setPosition(currentPoint);
        }
    }

    /**
     * 移动逻辑
     */
    public void moveLooper(LatLng endPoint) {
        mMoveMarker.setPosition(lastPoint);
        mMoveMarker.setRotate((float) CommonUtil.getAngle(lastPoint, endPoint));

        double slope = CommonUtil.getSlope(lastPoint, endPoint);
        // 是不是正向的标示（向上设为正向）
        boolean isReverse = (lastPoint.latitude > endPoint.latitude);
        double intercept = CommonUtil.getInterception(slope, lastPoint);
        double xMoveDistance = isReverse ? CommonUtil.getXMoveDistance(slope) : -1 * CommonUtil.getXMoveDistance(slope);

        for (double latitude = lastPoint.latitude; latitude > endPoint.latitude == isReverse; latitude =
                latitude - xMoveDistance) {
            LatLng latLng;
            if (slope != Double.MAX_VALUE) {
                latLng = new LatLng(latitude, (latitude - intercept) / slope);
            } else {
                latLng = new LatLng(latitude, lastPoint.longitude);
            }
            mMoveMarker.setPosition(latLng);
        }
    }

    public void drawHistoryTrack(List<LatLng> points, SortType sortType, int pos) {
        if (points == null || points.size() == 0) {
            return;
        }

        if (points.size() == 1) {
            if (pos == 1) {
                OverlayOptions startOptions = new MarkerOptions().position(points.get(0)).icon(BitmapUtil.bmStart)
                        .zIndex(9).draggable(true);
                baiduMap.addOverlay(startOptions);
                animateMapStatus(points.get(0), 18.0f);
                return;
            }
            return;
        }

        LatLng startPoint;
        LatLng endPoint;
        if (sortType == SortType.asc) {
            startPoint = points.get(0);
            endPoint = points.get(points.size() - 1);
        } else {
            startPoint = points.get(points.size() - 1);
            endPoint = points.get(0);
        }

        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions().position(startPoint).icon(BitmapUtil.bmStart).zIndex(9).draggable(true);
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions().position(endPoint).icon(bmEnd).zIndex(9).draggable(true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
        OverlayOptions polylineOptions = new PolylineOptions().width(10).points(points).dottedLine(true).customTexture(bitmapDescriptor);
        if (pos == 1) {
            baiduMap.addOverlay(startOptions);
        }
        if (pos == 2) {
            baiduMap.addOverlay(endOptions);
        }
        polylineOverlay = baiduMap.addOverlay(polylineOptions);
        if (pos == 2 || pos == 1) {
            animateMapStatus(points);
        }
    }

    /**
     * 绘制历史轨迹
     */
    public void drawHistoryTrack(List<LatLng> points, SortType sortType) {
        // 绘制新覆盖物前，清空之前的覆盖物
        if (baiduMap != null) {
            baiduMap.clear();
        }
        if (points == null || points.size() == 0) {
            if (null != polylineOverlay) {
                polylineOverlay.remove();
                polylineOverlay = null;
            }
            return;
        }

        if (points.size() == 1) {
            OverlayOptions startOptions = new MarkerOptions().position(points.get(0)).icon(BitmapUtil.bmStart)
                    .zIndex(9).draggable(true);
            baiduMap.addOverlay(startOptions);
            animateMapStatus(points.get(0), 18.0f);
            return;
        }

        LatLng startPoint;
        LatLng endPoint;
        if (sortType == SortType.asc) {
            startPoint = points.get(0);
            endPoint = points.get(points.size() - 1);
        } else {
            startPoint = points.get(points.size() - 1);
            endPoint = points.get(0);
        }

        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions()
                .position(startPoint)
                .icon(BitmapUtil.bmStart)
                .zIndex(9)
                .draggable(true);
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions().position(endPoint)
                .icon(bmEnd)
                .zIndex(9)
                .draggable(true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
        OverlayOptions polylineOptions = new PolylineOptions().width(10).points(points).dottedLine(true).customTexture(bitmapDescriptor);
        baiduMap.addOverlay(startOptions);
        baiduMap.addOverlay(endOptions);
        polylineOverlay = baiduMap.addOverlay(polylineOptions);

        OverlayOptions markerOptions =
                new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapUtil.bmArrowPoint)
                        .position(points.get(points.size() - 1))
                        .rotate((float) CommonUtil.getAngle(points.get(0), points.get(1)));
        mMoveMarker = (Marker) baiduMap.addOverlay(markerOptions);

        animateMapStatus(points);
    }

    public void drawHistoryTrack(List<LatLng> points, SortType sortType, String startTime, String endTime) {
        // 绘制新覆盖物前，清空之前的覆盖物
        if (baiduMap == null) {
            return;
        }

        if (points == null || points.size() == 0) {
            return;
        }

        if (points.size() == 1) {
            OverlayOptions startOptions = new MarkerOptions().position(points.get(0)).title(startTime).icon(BitmapUtil.bmStart)
                    .zIndex(9).draggable(true);
            baiduMap.addOverlay(startOptions);
            animateMapStatus(points.get(0), 18.0f);
            return;
        }

        LatLng startPoint;
        LatLng endPoint;
        if (sortType == SortType.asc) {
            startPoint = points.get(0);
            endPoint = points.get(points.size() - 1);
        } else {
            startPoint = points.get(points.size() - 1);
            endPoint = points.get(0);
        }

        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions()
                .position(startPoint).icon(BitmapUtil.bmStart)
                .title(startTime)
                .zIndex(9)
                .draggable(true);
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions()
                .position(endPoint)
                .title(endTime)
                .icon(bmEnd)
                .zIndex(9)
                .draggable(true);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
        OverlayOptions polylineOptions = new PolylineOptions().width(10).points(points).dottedLine(true).customTexture(bitmapDescriptor);
        baiduMap.addOverlay(startOptions);
        baiduMap.addOverlay(endOptions);
        polylineOverlay = baiduMap.addOverlay(polylineOptions);
    }


    public void animateMapStatus(List<LatLng> points) {
        if (baiduMap == null) {
            return;
        }
        if (null == points || points.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
        baiduMap.animateMapStatus(msUpdate);
    }

    public void animateMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    public void setMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    public void refresh() {
        LatLng mapCenter = baiduMap.getMapStatus().target;
        float mapZoom = baiduMap.getMapStatus().zoom - 1.0f;
        setMapStatus(mapCenter, mapZoom);
    }


}

