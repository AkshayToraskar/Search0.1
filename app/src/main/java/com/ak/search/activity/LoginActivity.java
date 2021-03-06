package com.ak.search.activity;

/**
 * Login for user
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    public static String LANG;
    ProgressDialog progress;

    public static String USERNAME = "username", ISADMIN = "is_admin";
    private SessionManager sessionManager;
    private Validate validate;
    Realm realm;


    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
            /*Manifest.permission.BLUETOOTH,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE*/};

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        validate = new Validate();
        sessionManager = new SessionManager(this);
        LANG = sessionManager.getLanguage();

        permissionCheck();

        User uAd = realm.where(User.class).equalTo("id", 1).findFirst();
        if (uAd == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    // Add a admin
                    User user = new User();
                    user.setId(1);
                    user.setName("admin");
                    user.setPassword("admin");
                    user.setType(1);
                    realm.copyToRealmOrUpdate(user);
                    Log.v("TAG", "admin added..!");

                }
            });

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

                //validate data
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


                checkLogin();
                break;


        }
    }

    public void permissionCheck() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[0])) {
                //Show Information about why you need the permission
                ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);

            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant storage permission", Toast.LENGTH_LONG).show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[0])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {


            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    // check is login credential is valid or not
    public void checkLogin() {


        //if (user.size() > 0) {
        switch (rgUserType.getCheckedRadioButtonId()) {

            case R.id.rb_admin:
                List<User> admin = realm.where(User.class).equalTo("name", txt_username.getText().toString()).equalTo("password", txt_password.getText().toString()).equalTo("type", 1).findAll();

                if (admin.size() > 0) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sessionManager.setLogin(true, admin.get(0).getName(), admin.get(0).getType(), admin.get(0).getId());
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong credential", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.rb_user:
                List<User> user = realm.where(User.class).equalTo("name", txt_username.getText().toString()).equalTo("password", txt_password.getText().toString()).equalTo("type", 3).findAll();

                if (user.size() > 0) {
                    Intent i = new Intent(this, SelectSurveyActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sessionManager.setLogin(true, user.get(0).getName(), user.get(0).getType(), user.get(0).getId());
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong credential", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rb_superviser:
                List<User> supervisor = realm.where(User.class).equalTo("name", txt_username.getText().toString()).equalTo("password", txt_password.getText().toString()).equalTo("type", 2).findAll();

                if (supervisor.size() > 0) {
                    Intent i1 = new Intent(this, MainActivity.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sessionManager.setLogin(true, supervisor.get(0).getName(), supervisor.get(0).getType(), supervisor.get(0).getId());
                    startActivity(i1);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong credential", Toast.LENGTH_SHORT).show();
                }
                break;
            //  }
        }
    }

    private void proceedAfterPermission() {
        // txtPermissions.setText("We've got all permissions");
        //Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!LANG.equals(sessionManager.getLanguage())) {
            Intent refresh = new Intent(this, LoginActivity.class);
            refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(refresh);
        }


    }
}
