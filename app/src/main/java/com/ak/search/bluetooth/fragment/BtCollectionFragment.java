package com.ak.search.bluetooth.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ak.search.R;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.bluetooth.adapters.BtSurveyHistoryAdapter;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.TransferModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 *
 * fragment to display collected data
 *
 */
public class BtCollectionFragment extends Fragment{

    public static List<DataCollection> dataCollectionList;
    @BindView(R.id.rv_data_collection)
    RecyclerView recyclerView;
    @BindView(R.id.cb_select_all)
    CheckBox cbSelectAll;
    private BtSurveyHistoryAdapter mAdapter;
    Realm realm;
    View view;
    CollectDataInfo collectDataInfo;
    TransferModel transferModel;


    public BtCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bt_collection, container, false);
        transferModel = new TransferModel();


        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();


        RealmResults<DataCollection> results = realm.where(DataCollection.class).findAll();

        dataCollectionList = new ArrayList<>();
        dataCollectionList.addAll(results);

        mAdapter = new BtSurveyHistoryAdapter(collectDataInfo, getContext(), dataCollectionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(mAdapter);

        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    mAdapter.selectAll(b);
                    transferModel.setDataCollectionList(dataCollectionList);
                    transferModel.setName(String.valueOf(b));
                    collectDataInfo.collectData(transferModel);


            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            collectDataInfo = (CollectDataInfo) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }


}
