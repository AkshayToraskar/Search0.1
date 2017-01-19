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
import com.ak.search.bluetooth.adapter.BtUsersAdapter;
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
public class BtLoginFragment extends Fragment {

    private List<User> usersList;
    @BindView(R.id.rv_user)
    RecyclerView recyclerView;
    private BtUsersAdapter mAdapter;
    Realm realm;
    View view;

    public BtLoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_bt_login, container, false);

        ButterKnife.bind(this,view);

        realm=Realm.getDefaultInstance();

        //  usersList = MUser.listAll(MUser.class);

        RealmResults<User> results = realm.where(User.class).findAll();

        usersList=new ArrayList<>();
        usersList.addAll(results);

        mAdapter = new BtUsersAdapter(getContext(), usersList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);



        return  view;
    }

}
