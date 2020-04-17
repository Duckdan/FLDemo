package com.study.fldemo.adapter

import android.content.Context

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.study.fldemo.R
import com.study.fldemo.bean.AndroidBean
import com.study.fldemo.bean.FuLiBean
import com.study.fldemo.dao.DatabaseBean
import com.study.fldemo.utils.TimeUtils
import com.study.fldemo.utils.doubleclick.AntiShake

import java.util.ArrayList

/**
 * Created by Administrator on 2017/9/20.
 */

class AndroidAdapter(private val context: Context, private var first: ArrayList<AndroidBean>, private var second: ArrayList<FuLiBean>) : RecyclerView.Adapter<AndroidAdapter.AndroidHoder>() {
    private var listener: OnItemClickListener? = null
    protected var antiShake = AntiShake()

    fun addData(first: ArrayList<AndroidBean>?, second: ArrayList<FuLiBean>?, page: Int) {
        if (first == null || second == null) {
            return
        }
        if (page == 1) {
            this.first = first
            this.second = second
        } else {
            this.first.addAll(first)
            this.second.addAll(second)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AndroidHoder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.rv_item_horizontal, parent, false)
        return AndroidHoder(inflate)
    }

    override fun onBindViewHolder(holder: AndroidHoder, position: Int) {
        val firstBean = first[position]
        val secondBean = second[position]
        holder.setData(position)

        holder.itemView.setOnClickListener(View.OnClickListener { view ->
            if (antiShake.check(view)) {
                return@OnClickListener
            }
            if (listener != null) {
                val bean = DatabaseBean()
                bean._id = firstBean._id
                bean.createdAt = firstBean.createdAt
                bean.desc = firstBean.desc
                bean.publishedAt = firstBean.publishedAt
                bean.source = firstBean.source
                bean.type = firstBean.type
                bean.url = firstBean.url
                bean.who = firstBean.who
                bean.imageUrl = secondBean.url
                listener!!.onItemClick(bean)
            }
        })
    }

    override fun getItemCount(): Int {
        return first?.size ?: 0
    }

    /**
     * 给控件添加static属性之后，数据复用时出现错位问题？？？
     */
    inner class AndroidHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var sdv: ImageView
        lateinit var tvTitle: TextView
        lateinit var tvAuthor: TextView
        lateinit var tvTime: TextView

        init {
            initView()
        }

        private fun initView() {
            sdv = itemView.findViewById(R.id.sdv)
            tvTitle = itemView.findViewById(R.id.tv_title)
            tvAuthor = itemView.findViewById(R.id.tv_author)
            tvTime = itemView.findViewById(R.id.tv_time)
        }

        fun setData(position: Int) {
            val firstBean = first!![position]
            val secondBean = second!![position]
            Glide
                    .with(context)
                    .load(secondBean.url)
                    .apply(RequestOptions.placeholderOf(R.drawable.header1))
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .thumbnail(loadTransform(context, R.drawable.header1))
                    .into(sdv)
            tvTitle.text = firstBean.desc
            tvAuthor.text = firstBean.who
            tvTime.text = TimeUtils.utc2Local(firstBean.publishedAt)


        }

        private fun loadTransform(context: Context, @DrawableRes placeholderId: Int): RequestBuilder<Drawable> {

            return Glide.with(context)
                    .load(placeholderId)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))

        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(bean: DatabaseBean)
    }

}
