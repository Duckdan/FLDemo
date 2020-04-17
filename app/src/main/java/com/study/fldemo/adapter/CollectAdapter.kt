package com.study.fldemo.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import com.study.fldemo.R
import com.study.fldemo.dao.DatabaseBean
import com.study.fldemo.utils.TimeUtils
import com.study.fldemo.utils.doubleclick.AntiShake

/**
 * Created by Administrator on 2017/12/29.
 */

class CollectAdapter(private val context: Context, private val lists: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val NORMAL_TYPE = 0
    private val TITLE_TYPE = 1
    private var listener: CollectAdapter.OnItemClickListener? = null
    private var longListener: OnItemLongClickListener? = null
    private var isShow: Boolean = false
    private var isUserDeal = false
    private var antiShake = AntiShake()

    override fun getItemCount(): Int {
        return lists.size
    }


    override fun getItemViewType(position: Int): Int {
        val `object` = lists[position]
        return if (`object` is String) {
            TITLE_TYPE
        } else {
            NORMAL_TYPE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View?
        val inflater = LayoutInflater.from(context)
        return if (viewType == TITLE_TYPE) {
            view = inflater.inflate(R.layout.item_collect_title_layout, parent, false)
            CollectTitleHolder(view)
        } else {
            view = inflater.inflate(R.layout.item_collect_content_layout, parent, false)
            CollectContentHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val o = lists[position]
        if (holder is CollectTitleHolder) {
            val title = o as String
            holder.tvTitle.text = title
        } else if (holder is CollectContentHolder) {
            val bean = o as DatabaseBean

            if (isShow) {
                holder.cb.visibility = View.VISIBLE
            } else {
                holder.cb.visibility = View.GONE
            }

            val check = bean.isCheck()
            holder.cb.isChecked = check

            holder.tvDesc.text = bean.desc
            holder.tvUrl.text = TimeUtils.utc2Local(bean.publishedAt)
            holder.itemView.setOnClickListener(View.OnClickListener { view ->
                if (antiShake.check(view)) {
                    return@OnClickListener
                }
                if (listener != null) {
                    listener!!.onItemClick(bean)
                }
            })

            holder.itemView.setOnLongClickListener {
                if (longListener != null) {
                    var hasChecked = false
                    for (bean in lists) {
                        if (bean is DatabaseBean) {
                            hasChecked = hasChecked or bean.isCheck()
                        }
                    }
                    longListener?.onItemLongClick(bean, hasChecked)
                }
                true
            }
        }
    }

    fun setShowCheckBox(isShow: Boolean) {
        this.isShow = isShow
    }

    fun setIsUserDeal(isUserDeal: Boolean) {
        this.isUserDeal = isUserDeal
    }

    class CollectTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView = itemView.findViewById<View>(R.id.tv_title) as TextView

    }

    class CollectContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvDesc: TextView = itemView.findViewById<View>(R.id.tv_desc) as TextView
        val tvUrl: TextView = itemView.findViewById<View>(R.id.tv_url) as TextView
        val cb: CheckBox = itemView.findViewById<View>(R.id.cb) as CheckBox

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.longListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(bean: DatabaseBean)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(bean: DatabaseBean, hasCheck: Boolean)
    }
}
