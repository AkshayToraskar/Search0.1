package com.ak.search.bluetooth.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ak.search.R;
import com.ak.search.adapter.SurveyAdapter;
import com.ak.search.adapter.SurveyHistoryAdapter;
import com.ak.search.bluetooth.adapter.BtSurveyHistoryAdapter;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Survey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class BtCollectionFragment extends Fragment {

    private List<DataCollection> dataCollectionList;
    @BindView(R.id.rv_data_collection)
    RecyclerView recyclerView;
    private BtSurveyHistoryAdapter mAdapter;
    Realm realm;
    View view;

    public BtCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_bt_collection, container, false);


        ButterKnife.bind(this,view);

        realm=Realm.getDefaultInstance();

        //  usersList = MUser.listAll(MUser.class);

        RealmResults<DataCollection> results = realm.where(DataCollection.class).findAll();

        dataCollectionList=new ArrayList<>();
        dataCollectionList.addAll(results);

        mAdapter = new BtSurveyHistoryAdapter(getContext(), dataCollectionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

}
