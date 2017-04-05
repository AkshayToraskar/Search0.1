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
import com.ak.search.app.DataSelection;
import com.ak.search.bluetooth.adapters.BtPatientAdapter;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.TransferModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class BtPatientFragment extends Fragment implements DataSelection {

    public static List<Patients> patientsList;
    @BindView(R.id.rv_patient)
    RecyclerView recyclerView;
    private BtPatientAdapter mAdapter;
    Realm realm;
    View view;
    CollectDataInfo collectDataInfo;
    @BindView(R.id.cb_select_all)
    CheckBox cbSelectAll;
    TransferModel transferModel;
    public static DataSelection dataSelection;

    public BtPatientFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bt_patient, container, false);
        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();
        transferModel = new TransferModel();
        //  usersList = MUser.listAll(MUser.class);
        dataSelection = this;
        RealmResults<Patients> results = realm.where(Patients.class).findAll();

        patientsList = new ArrayList<>();
        patientsList.addAll(results);

        mAdapter = new BtPatientAdapter(collectDataInfo, getContext(), patientsList);
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


                transferModel.setPatientsList(patientsList);
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

    @Override
    public void checkAllData(Boolean bool) {

    }
}
