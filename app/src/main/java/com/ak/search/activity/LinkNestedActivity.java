package com.ak.search.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ak.search.R;
import com.ak.search.adapter.LinkNestedSurveyAdapter;
import com.ak.search.adapter.SurveyAdapter;
import com.ak.search.app.AddNestedInfo;
import com.ak.search.realm_model.Survey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class LinkNestedActivity extends AppCompatActivity implements AddNestedInfo {


    private List<Survey> surveysList;
    @BindView(R.id.rv_survey)
    RecyclerView recyclerView;
    public LinkNestedSurveyAdapter mAdapter;
    Realm realm;
    AddNestedInfo addNestedInfo;

    long surveyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_nested);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm = Realm.getDefaultInstance();
        addNestedInfo = this;


        if (getIntent().getExtras() != null) {
            surveyId = getIntent().getLongExtra("surveyId", 0);
        }


        //  usersList = MUser.listAll(MUser.class);

        RealmResults<Survey> results = realm.where(Survey.class).equalTo("nested", true).findAll();
        //surveysList = MSurvey.listAll(MSurvey.class);
        surveysList = new ArrayList<>();
        surveysList.addAll(results);

        mAdapter = new LinkNestedSurveyAdapter(this, surveysList, addNestedInfo, surveyId);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LinkNestedActivity.this, AddSurveyActivity.class);
                intent.putExtra("isNestead",false);
                startActivity(intent);
            }
        });*/
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

    @Override
    protected void onResume() {
        super.onResume();
        surveysList.clear();
        surveysList.addAll(realm.where(Survey.class).equalTo("nested", true).findAll());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void addNestedData(long survId) {
        Intent intent = getIntent();
        intent.putExtra("SURVEYID", survId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void linkNestedData(int pos) {

    }
}
