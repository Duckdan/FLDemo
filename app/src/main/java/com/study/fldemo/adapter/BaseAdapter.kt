package com.study.fldemo.adapter

import android.content.Context
import android.net.Uri

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.study.fldemo.R
import com.study.fldemo.bean.FuLiBean
import com.study.fldemo.utils.doubleclick.AntiShake


/**
 * Created by Administrator on 2017/8/8.
 */

class BaseAdapter(private val mContext: Context, private var mLists: MutableList<FuLiBean>) : RecyclerView.Adapter<BaseAdapter.MineHolder>() {
    private var listener: OnItemClickListener? = null
    private var antiShake = AntiShake()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MineHolder {


        var inflate = View.inflate(mContext, R.layout.rv_item, null)
        return MineHolder(inflate, viewType)
    }

    override fun onBindViewHolder(holder: MineHolder, position: Int) {
        if (position < mLists.size) {
            val bean = mLists[position]
            holder.setFuLiBean(bean, position)
        }
    }


    override fun getItemCount(): Int {
        return mLists.size
    }

    fun addData(results: MutableList<FuLiBean>?, page: Int) {
        if (results == null)
            return
        if (page == 1) {
            mLists = results
        } else {
            mLists.addAll(mLists.size, results)
        }
    }

    inner class MineHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        private lateinit var mTvName: TextView
        private lateinit var mSdv: ImageView

        init {
            if (viewType == 0) {
                itemView.setOnClickListener(View.OnClickListener { v ->
                    if (antiShake.check(v)) {
                        return@OnClickListener
                    }
                    val position = adapterPosition
                    if (listener != null) {
                        listener?.onItemClickListener(position, mLists[position])
                    }
                })
                mTvName = itemView.findViewById(R.id.tv_name)
                mSdv = itemView.findViewById(R.id.sdv)
            }
        }

        fun setFuLiBean(fuLiBean: FuLiBean, position: Int) {


            Glide
                    .with(mContext)
                    .load(fuLiBean.url)
                    .into(mSdv)
            mTvName.text = fuLiBean.who
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClickListener(position: Int, fuLiBean: FuLiBean)
    }
}
