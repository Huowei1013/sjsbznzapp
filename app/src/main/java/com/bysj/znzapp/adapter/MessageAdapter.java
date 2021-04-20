package com.bysj.znzapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beagle.component.adapter.MyBaseAdapter;
import com.beagle.component.adapter.ViewHolder;

import com.bysj.znzapp.R;
import com.bysj.znzapp.bean.response.MessageGroup;
import com.bysj.znzapp.views.BadgeView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/23 11:15
 * @Describe:
 * @Version: 1.0.0
 */
public class MessageAdapter extends MyBaseAdapter<MessageGroup> {
    public static final String TYPE_DYMANIC = "workdynamic";
    public static final String TYPE_EXP = "workexperience";
    public static final String TYPE_NOTICE = "announcement";
    public static final String TYPE_EVENT = "event";
    public static final String TYPE_WORKLOG = "worklog";
    public static final String TYPE_CHECK = "check";
    public static final String TYPE_WORK_COUNT_WARN = "workCountWarn";
    public static final String TYPE_WORK_WARN = "workWarn";
    public static final String TYPE_IMPORTANT_WORK = "workImportant";
    public static final String TYPE_TASK = "task";
    private final Context context;
    private final List<MessageGroup> list;
    LinearLayout mItemLayout;
    ImageView mIvTour;
    TextView mTvTitle;
    TextView mTvIntr;
    TextView mTvTime;
    BadgeView mTvNum;

    public MessageAdapter(Context context, List<MessageGroup> list, int layoutId) {
        super(context, list, layoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ButterKnife.bind(this, convertView);
        return super.getView(position, convertView, parent);
    }

    @Override
    public void bindData(ViewHolder viewHolder, MessageGroup messageGroup, int i) {
        View convertView = viewHolder.getConvertView();
        mItemLayout = (LinearLayout) convertView.findViewById(R.id.ll_message_item);
        mIvTour = (ImageView) convertView.findViewById(R.id.iv_tour);
        mTvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        mTvIntr = (TextView) convertView.findViewById(R.id.tv_intr);
        mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
        mTvNum = (BadgeView) convertView.findViewById(R.id.tv_num);
        mTvNum.setText(messageGroup.getUnReadCount() > 99 ? "99+" : messageGroup.getUnReadCount() + "");
        mTvIntr.setText(messageGroup.getContent());
        mTvTime.setText(messageGroup.getCreateDate());
        if (TextUtils.equals(messageGroup.getType(), TYPE_DYMANIC)) {
            mTvTitle.setText("部门动态");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_department_dynamic));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_EVENT)) {
            mTvTitle.setText("事件提醒");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_event_notify));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_EXP)) {
            mTvTitle.setText("经验交流");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_exp_contacts));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_NOTICE)) {
            mTvTitle.setText("通知公告");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_notice));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_CHECK)) {
            mTvTitle.setText("平安检查");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_tour));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_WORKLOG)) {
            mTvTitle.setText("工作日志");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.general_work_log));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_WORK_COUNT_WARN)) {
            mTvTitle.setText("智能提醒");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_work_warn));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_WORK_WARN)) {
            mTvTitle.setText("工作消息");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_auto_warn));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_IMPORTANT_WORK)) {
            mTvTitle.setText("重要工作");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_notice));
        } else if (TextUtils.equals(messageGroup.getType(), TYPE_TASK)) {
            mTvTitle.setText("任务提醒");
            mIvTour.setImageDrawable(context.getResources().getDrawable(R.mipmap.message_event_notify));
        }
    }
}
