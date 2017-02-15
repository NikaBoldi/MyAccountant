package com.mikeos.demo.myaccountant.ui.fragment;

import android.appwidget.AppWidgetProviderInfo;
import android.database.Cursor;
import android.view.View;
import android.widget.CursorAdapter;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mikeos.demo.myaccountant.mvp.presenter.BaseDbListPresenter;
import com.mikeos.demo.myaccountant.mvp.presenter.ClientListPresenter;
import com.mikeos.demo.myaccountant.ui.adapter.ClientAdapter;

/**
 * Created on 15.02.17.
 */

public class ClientListFragment extends BaseListFragment {

    public static ClientListFragment getInstance(){
        return new ClientListFragment();
    }

    @InjectPresenter
    ClientListPresenter presenter;

    @Override
    protected BaseDbListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected CursorAdapter initAdapter() {
        return new ClientAdapter(getActivity(), null);
    }

    @Override
    public void moveToAdd() {

    }

    @Override
    public void onItemSelected(View v, int position, Cursor cursor, long id) {

    }
}