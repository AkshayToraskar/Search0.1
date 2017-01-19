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
import com.ak.search.adapter.PatientAdapter;
import com.ak.search.adapter.UsersAdapter;
import com.ak.search.bluetooth.adapter.BtPatientAdapter;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class BtPatientFragment extends Fragment {

    private List<Patients> patientsList;
    @BindView(R.id.rv_patient)
    RecyclerView recyclerView;
    private BtPatientAdapter mAdapter;
    Realm realm;
    View view;

    public BtPatientFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_bt_patient, container, false);
        ButterKnife.bind(this,view);

        realm=Realm.getDefaultInstance();

        //  usersList = MUser.listAll(MUser.class);

        RealmResults<Patients> results = realm.where(Patients.class).findAll();

        patientsList=new ArrayList<>();
        patientsList.addAll(results);

        mAdapter = new BtPatientAdapter(getContext(), patientsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        return view;
    }

}
