package com.ak.search.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ak.search.R;
import com.ak.search.adapter.GetSurveyAdapter;
import com.ak.search.realm_model.Survey;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {


    private List<Survey> surveysList;
    @BindView(R.id.rv_survey)
    RecyclerView recyclerView;



    public GetSurveyAdapter mAdapter;
    Realm realm;


    public UserFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();

        surveysList = realm.where(Survey.class).equalTo("nested",false).findAll();




        boolean isPhone = getActivity().getResources().getBoolean(R.bool.is_phone);

        mAdapter = new GetSurveyAdapter(getContext(), surveysList, getActivity());
        GridLayoutManager mLayoutManager;
        if (isPhone) {
           mLayoutManager = new GridLayoutManager(getContext(), 2);

        } else {
          mLayoutManager = new GridLayoutManager(getContext(), 3);

        }

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);





        return view;
    }

}
