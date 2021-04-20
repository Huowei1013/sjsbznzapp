package com.bysj.znzapp.persenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.beagle.component.app.MvpPresenter;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.activity.TodoDetailActivity;
import com.bysj.znzapp.bean.response.MyReportInfo;
import com.bysj.znzapp.bean.response.TodoDetailInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/15 14:27
 * @Describe:
 * @Version: 1.0.0
 */
public class TodoDetailActivityPresenter extends MvpPresenter {

    private TodoDetailActivity todoDetailActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        todoDetailActivity = (TodoDetailActivity) activity;
    }

    // 获取详情
    public void getTodoDetail(int eventNum) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", eventNum+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        todoDetailActivity.showMyDialog();
        OkHttpUtils.postString()
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .url(HttpConfig.getEventDetail)
                .addHeader("Authorization", "Bearer " + UserInfoProvide.getUserInfo().getToken())
                .content(jsonObject.toString())
                .build()
                .execute(new ResponseCallBack<MyReportInfo.RowsBean>(MyReportInfo.RowsBean.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        todoDetailActivity.closeMyDialog();
                    }

                    @Override
                    public void onResponse(Response<MyReportInfo.RowsBean> response, int id) {
                        if (TextUtils.equals(response.getSuccess(), "1")) {
                            MyReportInfo.RowsBean todoDetailInfo = response.getData();
                            if (todoDetailInfo != null) {
//                                todoDetailActivity.setNo(todoDetailInfo.getNo());// 事件编号
//                                todoDetailActivity.setReply(todoDetailInfo, todoDetailInfo.getNo());// 前序处理流程
//                                todoDetailActivity.setFrom(todoDetailInfo.getEventSource());// 事件来源
//                                todoDetailActivity.setType(todoDetailInfo.getEventTypeText());// 事件类型
//                                todoDetailActivity.setGrid(todoDetailInfo.getGridName());// 所属网格
//                                todoDetailActivity.setFactPerson(todoDetailInfo.getReporterName());// 上报人
//                                todoDetailActivity.setTel(todoDetailInfo.getReporterTel());// 上报人电话
//                                todoDetailActivity.setDate(todoDetailInfo.getStartDate());// 上报时间
//                                todoDetailActivity.setPlace(todoDetailInfo.getEventAddress());// 事件地址
//                                todoDetailActivity.setContent(todoDetailInfo.getDescription());// 事件描述
//                                todoDetailActivity.setListOrgs(todoDetailInfo.getInvolvedOrgs());// 设置涉事企业
//                                todoDetailActivity.setListPlaces(todoDetailInfo.getInvolvedPlaces());// 设置涉事场所
//                                todoDetailActivity.setListPersons(todoDetailInfo.getInvolvedPersons());// 设置涉事人员
                                todoDetailActivity.setEvent(todoDetailInfo);
                            }
                        }
                        todoDetailActivity.closeMyDialog();
                    }
                });
    }

    // 转成百度的坐标系
    public LatLng convert(LatLng source) {
        CoordinateConverter coordinateConverter = new CoordinateConverter();
        coordinateConverter.from(CoordinateConverter.CoordType.GPS);
        coordinateConverter.coord(source);
        return coordinateConverter.convert();
    }

    /* 以下为空函数 */
    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
