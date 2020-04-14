package com.study.fldemo.adapter;

import android.content.Context;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.study.fldemo.R;
import com.study.fldemo.bean.FuLiBean;
import com.study.fldemo.utils.doubleclick.AntiShake;

import java.util.List;


/**
 * Created by Administrator on 2017/8/8.
 */

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.MineHolder> {

    private Context mContext;
    private List<FuLiBean> mLists;
    private OnItemClickListener listener;
    protected AntiShake antiShake = new AntiShake();

    public BaseAdapter(Context context, List<FuLiBean> lists) {
        mContext = context;
        mLists = lists;
    }

    @Override
    public MineHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = null;
        RecyclerView recyclerView = (RecyclerView) parent;
        StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
        inflate = View.inflate(mContext, R.layout.rv_item, null);
        return new MineHolder(inflate, viewType);
    }

    @Override
    public void onBindViewHolder(MineHolder holder, int position) {
        if (position < mLists.size()) {
            FuLiBean bean = mLists.get(position);
            holder.setFuLiBean(bean, position);
        }
    }


    @Override
    public int getItemCount() {
        return mLists == null ? 0 : mLists.size();
    }

    public void addData(List<FuLiBean> results, int page) {
        if (results == null)
            return;
        if (page == 1) {
            mLists = results;
        } else {
            mLists.addAll(mLists.size(), results);
        }
    }

    class MineHolder extends RecyclerView.ViewHolder {

        private TextView mTvName;
        private ImageView mSdv;

        public MineHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (antiShake.check(v)) {
                            return;
                        }
                        int position = getAdapterPosition();
                        if (listener != null) {
                            listener.onItemClickListener(position, mLists.get(position));
                        }
                    }
                });
                mTvName = itemView.findViewById(R.id.tv_name);
                mSdv = itemView.findViewById(R.id.sdv);
            }
        }

        public void setFuLiBean(FuLiBean fuLiBean, int position) {


            Glide
                    .with(mContext)
                    .load(fuLiBean.getUrl())
                    //   .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(mSdv);
            mTvName.setText(fuLiBean.getWho());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, FuLiBean fuLiBean);
    }
}
