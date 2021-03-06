package com.mikeos.demo.myaccountant.ui.fragment;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.mikeos.demo.myaccountant.R;
import com.mikeos.demo.myaccountant.databinding.BaseListFragmentLayoutBinding;
import com.mikeos.demo.myaccountant.mvp.presenter.base.BaseDbListPresenter;
import com.mikeos.demo.myaccountant.mvp.view.DBListView;
import com.mikeos.demo.myaccountant.utils.DialogsHelper;

/**
 * Created on 15.02.17.
 */

public abstract class BaseListFragment extends BaseFragment implements DBListView {

    private CursorAdapter adapter;
    private BaseListFragmentLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_list_fragment_layout, null);
        binding = DataBindingUtil.bind(view);

        adapter = initAdapter();
        binding.list.setAdapter(adapter);
        binding.list.setOnItemClickListener(
                (adapterView, view1, i, l) -> getPresenter().selectedItem(view1, i, adapter.getCursor(), l));
        binding.list.setOnItemLongClickListener((parent, view1, position, id) -> {
            onLongClick(id);
            return true;
        });
        binding.fab.setOnClickListener(view1 -> getPresenter().onAddClicked());

        View root;
        CustomViewData customViewData = customView();
        if (customViewData != null) {
            root = inflater.inflate(customViewData.layout, null);
            ViewGroup forList = (ViewGroup) root.findViewById(customViewData.containerId);
            forList.addView(view);
        } else {
            root = view;
        }
        return root;
    }

    /**
     * Provide the custom layout id and id of {@link ViewGroup} which should contains base list layout
     */
    protected CustomViewData customView() {
        return null;
    }

    protected abstract BaseDbListPresenter getPresenter();

    protected abstract CursorAdapter initAdapter();

    @Override
    public void showData(Cursor data) {
        adapter.swapCursor(data);
    }

    public void onLongClick(long id) {
        DialogsHelper.getBuilder(getActivity()).setMessage("Remove item?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> getPresenter().remove(id))
                .setNegativeButton(android.R.string.cancel, null).show();

    }

    @Override
    public void deleteFailed(String msg) {
        DialogsHelper.showErrorDialog(getActivity(), msg);
    }

    /*
    static
     */

    public static class CustomViewData {
        @LayoutRes
        int layout;
        @IdRes
        int containerId;

        public CustomViewData(@LayoutRes int layout, @IdRes int containerId) {
            this.layout = layout;
            this.containerId = containerId;
        }
    }
}
