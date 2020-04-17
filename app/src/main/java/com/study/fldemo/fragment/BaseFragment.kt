package com.study.fldemo.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.study.fldemo.utils.NetStateUtils

import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {

    private var ids = android.R.layout.simple_expandable_list_item_1
    protected lateinit var netStateUtils: NetStateUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        onCreateFragment(savedInstanceState)
        netStateUtils = NetStateUtils.getInstance(context)
        return setContent(inflater, ids)
    }

    private fun setContent(inflater: LayoutInflater, ids: Int): View {
        return inflater.inflate(ids, null)
    }

    abstract fun onCreateFragment(state: Bundle?)

    fun setContent(ids: Int) {
        this.ids = ids
    }


}
