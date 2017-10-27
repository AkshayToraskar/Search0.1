package com.ak.search.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ak.search.R;
import com.ak.search.adapter.GetSurveyAdapter;
import com.ak.search.realm_model.Survey;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class CollectSurveyActivity extends AppCompatActivity {

    private List<Survey> surveysList;
    @BindView(R.id.rv_survey)
    RecyclerView recyclerView;


    public GetSurveyAdapter mAdapter;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_survey);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Survey");
        surveysList = realm.where(Survey.class).equalTo("nested", false).findAll();

        boolean isPhone = getResources().getBoolean(R.bool.is_phone);

        mAdapter = new GetSurveyAdapter(this, surveysList, this);
        GridLayoutManager mLayoutManager;
        if (isPhone) {
            mLayoutManager = new GridLayoutManager(this, 2);

        } else {
            mLayoutManager = new GridLayoutManager(this, 3);

        }

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
