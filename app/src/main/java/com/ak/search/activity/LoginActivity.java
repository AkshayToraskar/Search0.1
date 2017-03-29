package com.ak.search.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ak.search.*;
import com.ak.search.app.ApiClient;
import com.ak.search.app.ApiInterface;
import com.ak.search.app.SessionManager;
import com.ak.search.app.Validate;
import com.ak.search.bluetooth.BluetoothActivity;
import com.ak.search.bluetooth.BluetoothClientActivity;
import com.ak.search.model.Login;
import com.ak.search.realm_model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.txt_username)
    EditText txt_username;

    @BindView(R.id.txt_password)
    EditText txt_password;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rb_admin)
    RadioButton rbAdmin;
    @BindView(R.id.rb_superviser)
    RadioButton rbSupervisor;
    @BindView(R.id.rb_user)
    RadioButton rbUser;

    @BindView(R.id.rg_user_type)
    RadioGroup rgUserType;

    ProgressDialog progress;

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
            if (sessionManager.getLoginType() == 3) {
                Intent i = new Intent(this, SelectSurveyActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }

    }


    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.btnLoginSubmit:

                View view1 = this.getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }


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

                if (rbAdmin.isChecked()) {

                    if (isNetworkAvailable()) {
                        checkAdminLogin();

                        progress = new ProgressDialog(this);
                        progress.setMessage("Please Wait");
                        progress.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Active Internet !", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    checkOtherLogin();
                }


                break;

            case R.id.btn_bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;

            case R.id.btn_bluetooth_client:
                startActivity(new Intent(this, BluetoothClientActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    public void checkAdminLogin() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<Login> call = apiService.getLogin(txt_username.getText().toString(), txt_password.getText().toString());
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                Login login = response.body();
                Log.d("asdf", "Name of user: " + login.getError_message());
                progress.dismiss();

                if (!login.isError()) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sessionManager.setLogin(true, login.getUser().getName(), login.getUser().getType(), login.getUser().getId());
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong credential", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                // Log error here since request failed
                progress.dismiss();
                Log.e("asdf", t.toString());
                Toast.makeText(getApplicationContext(), "Internal server error..!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkOtherLogin() {

        List<User> user = realm.where(User.class).equalTo("name", txt_username.getText().toString()).equalTo("password", txt_password.getText().toString()).findAll();

        if (user.size() > 0) {
            switch (rgUserType.getCheckedRadioButtonId()) {
                case R.id.rb_user:
                    Intent i = new Intent(this, SelectSurveyActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sessionManager.setLogin(true, user.get(0).getName(), user.get(0).getType(), user.get(0).getId());
                    startActivity(i);
                    break;

                case R.id.rb_superviser:
                    Intent i1 = new Intent(this, MainActivity.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sessionManager.setLogin(true, user.get(0).getName(), user.get(0).getType(), user.get(0).getId());
                    startActivity(i1);
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Wrong credential", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
