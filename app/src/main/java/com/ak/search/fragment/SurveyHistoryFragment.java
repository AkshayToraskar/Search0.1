package com.ak.search.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.activity.SelectSurveyActivity;
import com.ak.search.adapter.SurveyHistoryAdapter;
import com.ak.search.app.SessionManager;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyHistoryFragment extends Fragment {


    private List<DataCollection> surveyHistory;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;
    @BindView(R.id.btn_login_logout)
    Button btnLoginLogout;
    /*@BindView(R.id.btn_send)
    Button btnSend;*/
    Dialog dialog;
   /* @BindView(R.id.spnSurveyName)
    Spinner spnSurveyName;*/

    public SurveyHistoryAdapter mAdapter;
    //ArrayAdapter<String> spnSurveyNameAdapter;

    View view;
    Realm realm;

    public SurveyHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_survey_history, container, false);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this, view);

        /*spnSurveyNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, surveyName);
        spnSurveyNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnSurveyName.setAdapter(spnSurveyNameAdapter);*/

        surveyHistory=new ArrayList<>();

        surveyHistory.addAll(realm.where(DataCollection.class).equalTo("fieldworkerId",SelectSurveyActivity.sessionManager.getUserId()).findAll());



        mAdapter = new SurveyHistoryAdapter(getContext(), surveyHistory,false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);


        btnLoginLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SelectSurveyActivity.SuperviserLogin==0){
                    callLoginDialog();
                }
                else{
                    callLogoutDialog();
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        surveyHistory.clear();
        // List<MUser> results = ;

        surveyHistory.addAll(realm.where(DataCollection.class).equalTo("fieldworkerId",SelectSurveyActivity.sessionManager.getUserId()).findAll());
        mAdapter.notifyDataSetChanged();

    }

    private void callLoginDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_login);
        dialog.setCancelable(false);
        dialog.setTitle("Supervisor Login");
        Button login = (Button) dialog.findViewById(R.id.btn_login);

        final EditText txt_username = (EditText) dialog.findViewById(R.id.et_username);
        final EditText txt_password = (EditText) dialog.findViewById(R.id.et_password);
        dialog.show();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = realm.where(User.class).equalTo("name", txt_username.getText().toString()).equalTo("password", txt_password.getText().toString()).findFirst();


                if (user != null) {
                    if (user.getType() == 2) {
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

                        SelectSurveyActivity.SuperviserLogin=user.getId();
                        btnLoginLogout.setText(getString(R.string.logout));
                        //btnSend.setVisibility(View.VISIBLE);


                        mAdapter = new SurveyHistoryAdapter(getContext(), surveyHistory,true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);


                    }
                }
                else{
                    Toast.makeText(getContext(), "Unsuccess", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public void callLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.sure_logout))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SelectSurveyActivity.SuperviserLogin = 0;
                        btnLoginLogout.setText(getString(R.string.supervisor_login));
                        //btnSend.setVisibility(GONE);

                        mAdapter = new SurveyHistoryAdapter(getContext(), surveyHistory,false);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to logout
                    }
                })
                .show();
    }
}
