package com.infinitestack.contacts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * BaseFragment.java
 * Description :
 * <p>
 * Created by InfiniteStack on 2017/4/29 15:36.
 * Copyright © 2017 InfiniteStack. All rights reserved.
 */

public abstract class BaseFragment extends Fragment {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    protected Activity mActivity;
    protected BackPressedListener mBackPressedListener;

    protected abstract int getLayoutResId();

    protected void parseArgumentsFromBundle(Bundle argBundle) {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            parseArgumentsFromBundle(getArguments());
        }

        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        initView(view);
        initEvent();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
        if (context instanceof BackPressedListener) {
            mBackPressedListener = (BackPressedListener) context;
        }else {
            throw new ClassCastException("Hosting Activity must implement BackPressedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBackPressedListener.setCurrentFragment(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    protected void initView(View view) {
    }

    ;

    protected void initEvent() {
    }

    ;

    public interface BackPressedListener {
        void setCurrentFragment(BaseFragment currentFragment);
    }
}
