package com.ak.search.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ak.search.R;
import com.ak.search.adapter.FieldworkerStatusAdapter;
import com.ak.search.adapter.GetSurveyAdapter;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class FieldworkerStatusActivity extends AppCompatActivity {

    private List<User> usersList;
    @BindView(R.id.rv_users)
    RecyclerView recyclerView;
    public FieldworkerStatusAdapter mAdapter;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fieldworker_status);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fieldworker status");
        usersList = realm.where(User.class).equalTo("type", 3).findAll();


        mAdapter = new FieldworkerStatusAdapter(this, usersList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
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
