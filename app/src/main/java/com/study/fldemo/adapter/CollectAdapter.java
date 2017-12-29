package com.study.fldemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.fldemo.R;
import com.study.fldemo.dao.DatabaseBean;
import com.study.fldemo.utils.TimeUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/12/29.
 */

public class CollectAdapter extends RecyclerView.Adapter {
    private final int NORMAL_TYPE = 0;
    private final int TITLE_TYPE = 1;

    private final Context context;
    private final List<Object> lists;
    private CollectAdapter.OnItemClickListener listener;

    public CollectAdapter(Context context, List<Object> lists) {
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }


    @Override
    public int getItemViewType(int position) {
        Object object = lists.get(position);
        if (object instanceof String) {
            return TITLE_TYPE;
        } else {
            return NORMAL_TYPE;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TITLE_TYPE) {
            view = inflater.inflate(R.layout.item_collect_title_layout, parent, false);
            return new CollectTitleHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_collect_content_layout, parent, false);
            return new CollectContentHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object o = lists.get(position);
        if (holder instanceof CollectTitleHolder) {
            String title = (String) o;
            ((CollectTitleHolder) holder).tvTitle.setText(title);
        } else if (holder instanceof CollectContentHolder) {
            final DatabaseBean bean = (DatabaseBean) o;
            ((CollectContentHolder) holder).tvDesc.setText(bean.getDesc());
            ((CollectContentHolder) holder).tvUrl.setText(TimeUtils.utc2Local(bean.getPublishedAt()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onItemClick(bean);
                    }
                }
            });
        }
    }


    public static class CollectTitleHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        public CollectTitleHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    public static class CollectContentHolder extends RecyclerView.ViewHolder {

        private final TextView tvDesc;
        private final TextView tvUrl;

        public CollectContentHolder(View itemView) {
            super(itemView);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            tvUrl = (TextView) itemView.findViewById(R.id.tv_url);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DatabaseBean bean);
    }

}
