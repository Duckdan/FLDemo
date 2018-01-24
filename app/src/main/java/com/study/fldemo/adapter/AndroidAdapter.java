package com.study.fldemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.study.fldemo.R;
import com.study.fldemo.bean.AndroidBean;
import com.study.fldemo.bean.FuLiBean;
import com.study.fldemo.dao.DatabaseBean;
import com.study.fldemo.utils.TimeUtils;
import com.study.fldemo.utils.doubleclick.AntiShake;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/20.
 */

public class AndroidAdapter extends RecyclerView.Adapter<AndroidAdapter.AndroidHoder> {


    private Context context;
    private ArrayList<AndroidBean> first;
    private ArrayList<FuLiBean> second;
    private OnItemClickListener listener;
    protected AntiShake antiShake = new AntiShake();

    public AndroidAdapter(Context context, ArrayList<AndroidBean> first, ArrayList<FuLiBean> second) {
        this.context = context;
        this.first = first;
        this.second = second;
    }

    public void addData(ArrayList<AndroidBean> first, ArrayList<FuLiBean> second, int page) {
        if (first == null || second == null) {
            return;
        }
        if (page == 1) {
            this.first = first;
            this.second = second;
        } else {
            this.first.addAll(first);
            this.second.addAll(second);
        }

    }

    @Override
    public AndroidHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.rv_item_horizontal, parent, false);
        return new AndroidHoder(inflate);
    }

    @Override
    public void onBindViewHolder(AndroidHoder holder, int position) {
        final AndroidBean firstBean = first.get(position);
        final FuLiBean secondBean = second.get(position);
        holder.sdv.setImageURI(secondBean.url);
        holder.tvTitle.setText(firstBean.getDesc());
        holder.tvAuthor.setText(firstBean.getWho());
        holder.tvTime.setText(TimeUtils.utc2Local(firstBean.getPublishedAt()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (antiShake.check(view)) {
                    return;
                }
                if (listener != null) {
                    DatabaseBean bean = new DatabaseBean();
                    bean.set_id(firstBean.get_id());
                    bean.setCreatedAt(firstBean.getCreatedAt());
                    bean.setDesc(firstBean.getDesc());
                    bean.setPublishedAt(firstBean.getPublishedAt());
                    bean.setSource(firstBean.getSource());
                    bean.setType(firstBean.getType());
                    bean.setUrl(firstBean.getUrl());
                    bean.setWho(firstBean.getWho());
                    bean.setImageUrl(secondBean.url);
                    listener.onItemClick(bean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return first.size();
    }

    /**
     * 给控件添加static属性之后，数据复用时出现错位问题？？？
     */
    public class AndroidHoder extends RecyclerView.ViewHolder {

        public SimpleDraweeView sdv;
        public TextView tvTitle;
        public TextView tvAuthor;
        public TextView tvTime;

        public AndroidHoder(View itemView) {
            super(itemView);
            initView();
        }

        private void initView() {
            sdv = (SimpleDraweeView) itemView.findViewById(R.id.sdv);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void setData(int position) {
            final AndroidBean firstBean = first.get(position);
            final FuLiBean secondBean = second.get(position);
            sdv.setImageURI(secondBean.url);
            tvTitle.setText(firstBean.getDesc());
            tvAuthor.setText(firstBean.getWho());
            tvTime.setText(TimeUtils.utc2Local(firstBean.getPublishedAt()));


        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DatabaseBean bean);
    }

}
