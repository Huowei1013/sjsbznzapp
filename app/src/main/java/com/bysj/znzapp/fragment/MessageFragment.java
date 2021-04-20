package com.bysj.znzapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beagle.component.activity.BaseCompatFragment;
import com.beagle.component.adapter.MyBaseAdapter;
import com.beagle.component.app.MvpManager;
import com.beagle.component.app.MvpPresenter;
import com.bysj.znzapp.R;
import com.bysj.znzapp.adapter.MessageAdapter;
import com.bysj.znzapp.bean.response.MessageGroup;
import com.bysj.znzapp.persenter.fragment.MessageFragmentPresenter;
import com.bysj.znzapp.views.ClearEditText;
import com.thirdsdks.filedeal.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MessageFragment extends BaseCompatFragment {

    private View rootView;
    @BindView(R.id.lv_message_list)
    ListView listView;
    @BindView(R.id.cet_search_text)
    ClearEditText search;
    private MyBaseAdapter myBaseAdapter;
    private List<MessageGroup> listMessage;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listMessage = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        listView = (ListView) rootView.findViewById(R.id.lv_message_list);
        listView.setDivider(null);
//        final ClearEditText search = (ClearEditText) view.findViewById(R.id.cet_search_text);

        search.setHint("搜索");
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        search.setImeActionLabel("请输入关键词搜索", EditorInfo.IME_ACTION_SEARCH);
        search.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        search.setSingleLine(false);
        search.setMaxLines(5);
        search.setHorizontallyScrolling(false);
        myBaseAdapter = new MessageAdapter(getActivity(), listMessage, R.layout.item_message);
        listView.setAdapter(myBaseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageGroup dataBean = (MessageGroup) myBaseAdapter.getItem((int) id);
                if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_DYMANIC)) {
//                    Intent i1 = new Intent(getActivity(), MessageDynamicActivity.class);
//                    i1.putExtra("bType", TYPE_DYMANIC);
//                    startActivity(i1);
                } else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_EVENT)) {
//                    Intent i4 = new Intent(getActivity(), MessageEventActivity.class);
//                    i4.putExtra("bType", TYPE_EVENT);
//                    startActivity(i4);
                } else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_EXP)) {
//                    Intent i2 = new Intent(getActivity(), MessageExpActivity.class);
//                    i2.putExtra("bType", TYPE_EXP);
//                    startActivity(i2);
                } else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_NOTICE)) {
//                    Intent i = new Intent(getActivity(), MessageNoticeActivity.class);
//                    i.putExtra("bType", TYPE_NOTICE);
//                    startActivity(i);
                } else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_CHECK)) {
//                    Intent i3 = new Intent(getActivity(), MessageTourActivity.class);
//                    i3.putExtra("bType", TYPE_CHECK);
//                    startActivity(i3);
                } else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_WORKLOG)) {
//                    Intent i5 = new Intent(getActivity(), MessageWorkLogActivity.class);
//                    i5.putExtra("bType", TYPE_WORKLOG);
//                    startActivity(i5);
                } else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_WORK_COUNT_WARN)) {

                } else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_WORK_WARN)) {
//                    Intent i5 = new Intent(getActivity(), MessageWorkWarnActivity.class);
//                    i5.putExtra("bType", TYPE_WORK_WARN);
//                    startActivity(i5);
                }else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_IMPORTANT_WORK)) {
//                    Intent i = new Intent(getActivity(), MessageWorkImportantActivity.class);
//                    i.putExtra("bType", TYPE_IMPORTANT_WORK);
//                    startActivity(i);
                }else if (TextUtils.equals(dataBean.getType(), MessageAdapter.TYPE_TASK)) {
//                    Intent i = new Intent(getActivity(), MessageTaskActivity.class);
//                    i.putExtra("bType", TYPE_TASK);
//                    startActivity(i);
                }
            }
        });
    }

    @OnEditorAction(R.id.cet_search_text)
    boolean OnEditorAction(TextView v, int actionId, KeyEvent event){
        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                && event.getAction() == KeyEvent.ACTION_DOWN)) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
            if (search.getText().toString().isEmpty()) {
                ToastUtil.showToast(context, "请输入关键词搜索");
            } else {
//                        Intent intent = new Intent(getActivity(), MessageSearchActivity.class);
//                        intent.putExtra("keyWords", search.getText().toString());
//                        startActivity(intent);
//                        search.setText("");
            }
            return true;
        }
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        doRefresh();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    // 刷新
    private void doRefresh() {
        if (findPresenter() != null) {
            findPresenter().getData();
        }
    }

    // 刷新
    public void refresh() {
        doRefresh();
    }

    // 更新
    public void update(List<MessageGroup> lists) {
        if (myBaseAdapter != null) {
            List<MessageGroup> result = new ArrayList<>();
            if (lists != null && lists.size() > 0) {
                Collections.sort(lists);
                for (int i = 0; i < lists.size(); i++) {
                    MessageGroup messageGroup = lists.get(i);
                    String type = messageGroup.getType();
                    if (TextUtils.isEmpty(type)) {
                        continue;
                    }
                    if (type.equalsIgnoreCase(MessageAdapter.TYPE_WORK_WARN)
                            || type.equalsIgnoreCase(MessageAdapter.TYPE_NOTICE)
                            || type.equalsIgnoreCase(MessageAdapter.TYPE_WORKLOG)
                            || type.equalsIgnoreCase(MessageAdapter.TYPE_EXP)
                            || type.equalsIgnoreCase(MessageAdapter.TYPE_CHECK)
                            || type.equalsIgnoreCase(MessageAdapter.TYPE_DYMANIC)
                            || type.equalsIgnoreCase(MessageAdapter.TYPE_EVENT)
                            || type.equalsIgnoreCase(MessageAdapter.TYPE_WORK_COUNT_WARN)
                            || type.equals(MessageAdapter.TYPE_IMPORTANT_WORK)
                            || type.equals(MessageAdapter.TYPE_TASK)) {
                        result.add(messageGroup);
                    }
                }
            } else {
                setEmptyView();
            }
            this.listMessage.clear();
            this.listMessage.addAll(result);
            myBaseAdapter.notifyDataSetChanged();
        }
    }

    // 全部已读
    public void markAll() {
        if (findPresenter() != null) {
            findPresenter().markAll();
        }
    }

    // 展示无消息
    private void setEmptyView() {
        final View view1 = (View) listView.getParent().getParent();
        View mEmptyView = View.inflate(context, R.layout.layout_empty, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, view1.getHeight());
        ((ViewGroup) listView.getParent()).addView(mEmptyView, layoutParams);
        listView.setEmptyView(mEmptyView);
    }

    // 绑定Presenter
    private MessageFragmentPresenter findPresenter() {
        MvpPresenter presenter = MvpManager.findPresenter(this);
        if (presenter == null) {
            MvpManager.bindPresenter(this, "com.bysj.znzapp.persenter.fragment.MessageFragmentPresenter");
            presenter = MvpManager.findPresenter(this);
        }
        if (presenter instanceof MessageFragmentPresenter) {
            return (MessageFragmentPresenter) presenter;
        }
        return null;
    }
}
