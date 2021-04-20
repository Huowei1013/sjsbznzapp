package com.bysj.znzapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beagle.component.activity.RefreshFragment;
import com.beagle.component.adapter.ViewHolder;
import com.beagle.component.app.MvpManager;
import com.beagle.okhttp.callback.Response;
import com.bysj.znzapp.R;
import com.bysj.znzapp.activity.TodoDetailActivity;
import com.bysj.znzapp.adapter.BannerAdapter;
import com.bysj.znzapp.bean.response.BannerListInfo;
import com.bysj.znzapp.bean.response.MyReportInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;
import com.bysj.znzapp.persenter.fragment.MainFragmentPresenter;
import com.bumptech.glide.Glide;
import com.thirdsdks.videoplay.StringUtil;
import com.thirdsdks.xbanner.XBanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ApplicationFragment extends RefreshFragment<MyReportInfo, MyReportInfo.RowsBean> {

    private String ID = "";

    @Override
    protected void initRequestParams(Map<String, String> map) {
        map.put("page", super.getmPage() + "");
        map.put("limit", super.getmRow() + "");
        map.put("status", "");
        map.put("state", "4");// 所有的状态
    }

    @NonNull
    @Override
    protected RefreshConfig refreshViewConfig() {
        return new RefreshConfig(HttpConfig.getEventList, UserInfoProvide.getUserInfo().getToken(), R.layout.item_my_report).bindClass(MyReportInfo.class);
    }

    @Override
    protected void bindItemData(ViewHolder viewHolder, MyReportInfo.RowsBean rowsBean, int i) {
        TextView content = viewHolder.getView(R.id.tv_content);
        content.setText(rowsBean.getEventId());
        TextView date = viewHolder.getView(R.id.date);
        date.setText(rowsBean.getEventCreateTime());
        TextView tv_detail = viewHolder.getView(R.id.tv_detail);
        tv_detail.setText("事件描述：" + rowsBean.getEventContent());
        TextView tv_grid = viewHolder.getView(R.id.tv_grid);
//        tv_grid.setText("所属地区：" + StringUtil.notNull(rowsBean.getGridName()));
    }

    @Override
    protected List<MyReportInfo.RowsBean> transfromList(Response<MyReportInfo> response) {
        MyReportInfo myBao = response.getData();
        if (myBao != null) {
            return myBao.getRows();
        } else {
            return null;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getmListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyReportInfo.RowsBean row = (MyReportInfo.RowsBean) parent.getAdapter().getItem(position);
                TodoDetailActivity.startActivity(context, row.getId());
            }
        });
    }
}
