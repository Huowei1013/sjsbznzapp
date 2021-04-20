package com.bysj.znzapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.bysj.znzapp.R;
import com.bysj.znzapp.config.HttpConfig;
import com.thirdsdks.webview.BrowserActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 19:05
 * @Describe:
 * @Version: 1.0.0
 */
public class BannerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<String> strings = new ArrayList<>();

    public BannerAdapter(Context context,List<String> words){
        this.context = context;
        this.strings = words;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_general, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerHolder) {
            ((RecyclerHolder) holder).tv_text.setText(strings.get(position));
            if (((RecyclerHolder) holder).tv_text.getText().equals("抗旱监测")) {
                ((RecyclerHolder) holder).iv_image.setImageDrawable(context.getResources().getDrawable(R.mipmap.general_default));
                ((RecyclerHolder) holder).ll_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BrowserActivity.startBrowserActivity(context, "抗旱监测", HttpConfig.xiangshanWaterServiceUrl, 0, false);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }
    // 数据显示适配器辅助类
    private class RecyclerHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_container;
        private ImageView iv_image;
        private TextView tv_text;

        public RecyclerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            ll_container = (LinearLayout) itemView.findViewById(R.id.ll_container);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }
}

