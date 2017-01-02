package com.ak.search.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ak.search.*;
import com.ak.search.app.SessionManager;
import com.ak.search.app.Validate;
import com.ak.search.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.txt_username)
    EditText txt_username;

    @BindView(R.id.txt_password)
    EditText txt_password;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static String USERNAME = "username", ISADMIN = "is_admin";
    private SessionManager sessionManager;
    private Validate validate;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);

        validate = new Validate();
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

    }


    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.btnLoginSubmit:

                if (validate.validateString(txt_username.getText().toString())) {
                    txt_username.setError("Enter Username");
                    return;
                } else {
                    txt_username.setError(null);
                }
                if (validate.validateString(txt_password.getText().toString())) {
                    txt_password.setError("Enter Password");
                    return;
                } else {
                    txt_username.setError(null);
                }


                if (txt_username.getText().toString().equalsIgnoreCase("admin") && txt_password.getText().toString().equalsIgnoreCase("admin")) {
                    Intent i = new Intent(this, MainActivity.class);
                    //  i.putExtra(USERNAME, txt_username.getText().toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sessionManager.setLogin(true, "admin", 1);
                    //  i.putExtra(ISADMIN, true);
                    startActivity(i);
                } else {

                    // List<User> user = User.find(User.class, "name = ?", txt_username.getText().toString());

                    boolean loginStatus = false, isAdmin = false;

                    String username = "";
                    int type = 0;

                    List<User> user = realm.where(User.class).equalTo("name", txt_username.getText().toString()).findAll();


                    for (int j = 0; j < user.size(); j++) {
                        if (user.get(j).getPassword().equals(txt_password.getText().toString())) {
                            loginStatus = true;
                            username = user.get(j).getName();
                            type = user.get(j).getType();
                        }
                    }

                    if (type == 1) {
                        isAdmin = true;
                    }


                    if (loginStatus) {
                        Intent i = new Intent(this, MainActivity.class);
                        // i.putExtra(USERNAME, username);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sessionManager.setLogin(true, username, type);
                        i.putExtra(ISADMIN, isAdmin);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong credential", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
