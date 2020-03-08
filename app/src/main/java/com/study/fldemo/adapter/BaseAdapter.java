package com.study.fldemo.adapter;

import android.content.Context;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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
//        if (viewType == 1) {
//            inflate = View.inflate(mContext, R.layout.rv_footer, null);
//
//        } else {
        inflate = View.inflate(mContext, R.layout.rv_item, null);
        // sglm.setSpanCount(2);
//        }
        return new MineHolder(inflate, viewType);
    }

    @Override
    public void onBindViewHolder(MineHolder holder, int position) {
        if (position < mLists.size()) {
            FuLiBean bean = mLists.get(position);
            holder.setFuLiBean(bean, position);
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == mLists.size()) {
//            return 1;
//        } else {
//            return 0;
//        }
//    }

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
        private SimpleDraweeView mSdv;

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
//                        Toast.makeText(mContext, "点击了" + position, Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onItemClickListener(position, mLists.get(position));
                        }
                    }
                });
                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                mSdv = (SimpleDraweeView) itemView.findViewById(R.id.sdv);
            }
        }

        public void setFuLiBean(FuLiBean fuLiBean, int position) {
            ViewGroup.LayoutParams layoutParams = mSdv.getLayoutParams();
//            layoutParams.width = 300;
            if (position % 2 == 0) {
                layoutParams.height = 500;
            } else {
                layoutParams.height = 600;
            }
            Uri uri = Uri.parse(fuLiBean.url);
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .build();
            DraweeController draweeController = Fresco
                    .newDraweeControllerBuilder()
                    .setUri(uri)
                    .setImageRequest(imageRequest)
                    .setOldController(mSdv.getController())
                    .build();
            mSdv.setController(draweeController);
//            mSdv.setImageURI(fuLiBean.url);
            mTvName.setText(fuLiBean.who);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, FuLiBean fuLiBean);
    }
}
