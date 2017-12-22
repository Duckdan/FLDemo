package com.study.fldemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.fldemo.utils.NetStateUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    private int ids = android.R.layout.simple_expandable_list_item_1;
    protected Context context;
    protected NetStateUtils netStateUtils;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onCreateFragment(savedInstanceState);
        context = getActivity();
        netStateUtils = NetStateUtils.getInstance(context);
        View view = setContent(inflater, ids);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private View setContent(LayoutInflater inflater, int ids) {
        return inflater.inflate(ids, null);
    }

    public abstract void onCreateFragment(Bundle state);

    public void setContent(int ids) {
        this.ids = ids;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        unbinder.unbind();
    }

}
