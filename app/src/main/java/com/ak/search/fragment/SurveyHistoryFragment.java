package com.ak.search.fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.activity.SelectPatientsActivity;
import com.ak.search.activity.SelectSurveyActivity;
import com.ak.search.adapter.SurveyHistoryAdapter;
import com.ak.search.app.SessionManager;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * show supervisor history panel
 */
public class SurveyHistoryFragment extends Fragment {


    @BindView(R.id.btn_login_logout)
    Button btnLoginLogout;

    Dialog dialog;


    View view;


    long surveyId;
    private List<DataCollection> surveyHistory;
    List<DataCollection> surveyHistoryFilter;

    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;

    @BindView(R.id.spnSurveyName)
    Spinner spnSurveyName;

    @BindView(R.id.slidingDrawer)
    SlidingDrawer slidingDrawer;

    public SurveyHistoryAdapter mAdapter;
    ArrayAdapter<String> spnSurveyNameAdapter, spnFiledworkerNameAdapter;
    Realm realm;
    public static int PATIENT_REQUEST = 12;
    List<Survey> lstSurveyData;

    List<User> lstUserData;

    @BindView(R.id.ivArrow)
    ImageView ivArrow;

    @BindView(R.id.tvDate)
    TextView tvDate;

    @BindView(R.id.tvPatient)
    TextView tvPatient;

    @BindView(R.id.spnFieldworker)
    Spinner spnFieldWorker;

    @BindView(R.id.tv_survey_count)
    TextView tvSurveyCount;

    private int mYear, mMonth, mDay, mHour, mMinute;

    Long patientId = (long) 0;
    String selectedDate;


    @BindView(R.id.btnApply)
    Button btnApply;
    @BindView(R.id.btnSelectDate)
    Button btnSelectDate;
    @BindView(R.id.btnSelectPatient)
    Button btnSelectPatient;

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


        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                // slideButton.setBackgroundResource(R.drawable.down_arrow_icon);
                slidingDrawer.setBackgroundResource(R.color.cardview_light_background);

                ivArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));

                recyclerView.setVisibility(View.GONE);
            }
        });

        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {

                recyclerView.setVisibility(View.VISIBLE);
                //  slideButton.setBackgroundResource(R.drawable.upwar_arrow_icon);
                slidingDrawer.setBackgroundColor(Color.TRANSPARENT);
                ivArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
            }
        });

        surveyHistory = new ArrayList<>();
        surveyHistoryFilter = new ArrayList<>();
        surveyHistory.addAll(realm.where(DataCollection.class).findAll());

        tvSurveyCount.setText("" + surveyHistory.size());

        mAdapter = new SurveyHistoryAdapter(getActivity(), surveyHistory, true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);


        lstSurveyData = realm.where(Survey.class).equalTo("nested", false).findAll();

        final String surveyName[] = new String[lstSurveyData.size() + 1];
        surveyName[0] = "All";
        for (int i = 0; i < lstSurveyData.size(); i++) {
            surveyName[i + 1] = lstSurveyData.get(i).getName();
        }


        spnSurveyNameAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, surveyName);
        spnSurveyNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnSurveyName.setAdapter(spnSurveyNameAdapter);


        lstUserData = realm.where(User.class).equalTo("type", 3).findAll();
        final String fieldworkderName[] = new String[lstUserData.size() + 1];
        fieldworkderName[0] = "All";
        for (int i = 0; i < lstUserData.size(); i++) {
            fieldworkderName[i + 1] = lstUserData.get(i).getName();
        }


        spnFiledworkerNameAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, fieldworkderName);
        spnFiledworkerNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnFieldWorker.setAdapter(spnFiledworkerNameAdapter);


        btnLoginLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SelectSurveyActivity.SuperviserLogin == 0) {
                    callLoginDialog();
                } else {
                    callLogoutDialog();
                }
            }
        });


        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter();

                slidingDrawer.close();
            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                selectedDate = dayOfMonth + "." + String.format("%02d", (monthOfYear + 1)) + "." + year;
                                tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        btnSelectPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SelectPatientsActivity.class);
                startActivityForResult(i, PATIENT_REQUEST);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        applyFilter();

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

                        SelectSurveyActivity.SuperviserLogin = user.getId();
                        btnLoginLogout.setText(getString(R.string.logout));
                        //btnSend.setVisibility(View.VISIBLE);


                        mAdapter = new SurveyHistoryAdapter(getContext(), surveyHistory, true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);


                    }
                } else {
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

                        mAdapter = new SurveyHistoryAdapter(getContext(), surveyHistory, false);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PATIENT_REQUEST && resultCode == Activity.RESULT_OK) {

            patientId = (long) data.getExtras().get("data");
            Patients p = realm.where(Patients.class).equalTo("id", patientId).findFirst();
            if (p != null) {
                tvPatient.setText(p.getPatientname());
            }

        }
    }

    public void applyFilter() {


        RealmQuery q = realm.where(DataCollection.class);

        if (spnSurveyName.getSelectedItemPosition() > 0) {
            q = q.equalTo("surveyid", lstSurveyData.get(spnSurveyName.getSelectedItemPosition() - 1).getId());

        }

        if (spnFieldWorker.getSelectedItemPosition() > 0) {
            q = q.equalTo("fieldworkerId", lstUserData.get(spnFieldWorker.getSelectedItemPosition() - 1).getId());

        }

        if (patientId != 0) {
            q = q.equalTo("patients.id", patientId);

        }

        if (selectedDate != null) {
            q = q.beginsWith("timestamp", selectedDate);

        }


        surveyHistory.clear();
        surveyHistory.addAll(q.findAll());
        mAdapter.notifyDataSetChanged();

        tvSurveyCount.setText("" + surveyHistory.size());
    }
}
