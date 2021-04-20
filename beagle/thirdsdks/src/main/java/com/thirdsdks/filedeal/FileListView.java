package com.thirdsdks.filedeal;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thirdsdks.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaotao on 2020/12/30 19:04.
 * 文件View
 */

public class FileListView extends ListView implements AdapterView.OnItemClickListener{

    private List<FilesBean> mData = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private String fileDir = "";
    private String fileServer = "";
    private boolean isAutoOpenFile = false;
    private FileDownUtil mDownUtils;

    public FileListView(Context context) {
        this(context,null);
    }

    public FileListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    public FileListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            fileDir = context.getExternalFilesDir(null).getAbsolutePath();
        } else {
            fileDir = context.getFilesDir().getAbsolutePath();
        }
        mAdapter = new ArrayAdapter<>(context, R.layout.z_item_text);
        this.setAdapter(mAdapter);
        this.setOnItemClickListener(this);
        mDownUtils = new FileDownUtil(context);
    }

    /**
     * 设置是否下载完成后自动打开文件
     * 默认 false
     * <p></>
     * @param isAuto boolen
     * @return this
     */
    public FileListView setIsAutoOpenFile(boolean isAuto){
        this.isAutoOpenFile = isAuto;
        return this;
    }

    /**
     * 设置文件服务器地址
     * <p></>
     * @param fileServer fileServer
     * @return this
     */
    public FileListView setFileServer(String fileServer){
        this.fileServer = fileServer;
        return this;
    }

    /**
     * 添加数据
     * <p></>
     * @param data 数据
     * @return this
     */
    public FileListView setData(List<FilesBean> data){
        this.mData = data;
        this.mAdapter.clear();
        for (FilesBean b : data){
            mAdapter.add(b.getFileName());
        }
        this.mAdapter.notifyDataSetChanged();
        return this;
    }

    /**
     * 获取Adapter
     * <p></p>
     * @return Adapter
     */
    public ArrayAdapter<String> getAdapter(){
        return this.mAdapter;
    }

    /**
     * 获取数据数量
     * <p></p>
     * @return size
     */
    public int getDataSize(){
        if (this.mData != null) return this.mData.size();
        return 0;
    }

    /**
     * 点击每项
     * <p></>
     * @param parent parent
     * @param view view
     * @param position position
     * @param id id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (fileServer.equals("")) throw new NullPointerException("请用setFileServer方法设置文件服务器地址");
        try {
            final FilesBean fileBean = mData.get(position);
            File fileNew = new File(fileDir + File.separator + fileBean.getFileName());
            final List<String> downLoadRank = new ArrayList<>();
            if (!fileNew.exists()) {
                ToastUtil.showToast(getContext(),fileBean.getFileName()+"正在下载...");
                downLoadRank.add(fileBean.getFileName());
                mDownUtils.url(fileServer+fileBean.getVisitPath()).fileName(fileBean.getFileName())
                        .listener(new FileDownUtil.OnCompleteListener(){

                            @Override
                            public void onSuccess(File resource) {
                                ToastUtil.showToast(getContext(),fileBean.getFileName()+"已下载");
                                downLoadRank.remove(fileBean.getFileName());
                                resource.setReadable(true,false);
                                if (isAutoOpenFile) OpenFile.openFile(getContext(),resource);
                            }

                            @Override
                            public void onFailed() {
                                ToastUtil.showToast(getContext(),fileBean.getFileName()+"下载失败");
                                downLoadRank.remove(fileBean.getFileName());
                            }

                        }).download();

            }else if (downLoadRank.contains(fileBean.getFileName())){
                ToastUtil.showToast(getContext(),fileBean.getFileName()+"正在下载....");
            }else {
                //打开文件
                fileNew.setReadable(true,false);
                OpenFile.openFile(getContext(),fileNew);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 布局拉伸
     * <p></>
     * @param widthMeasureSpec widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
