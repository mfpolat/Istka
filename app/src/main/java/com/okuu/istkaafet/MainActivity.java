package com.okuu.istkaafet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.okuu.istkaafet.InfoActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.gc.materialdesign.views.Switch;
import com.gc.materialdesign.widgets.SnackBar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements Switch.OnCheckListener {

    private TextView login_activity_service_message_tv;
    private String tcKimlikNo;
    private String passWord;
    private EditText login_dialog_tc_et;
    private EditText login_dialog_pass_et;
    private Switch login_activity_on_off_switch;
    private TextView login_activity_online_tv, login_activity_offline_tv;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setToolbar();
        showLoginDialog();
        showOfflineMap();
        dbHelper = new DBHelper(this);
    }

    private void initViews() {
        login_activity_service_message_tv = (TextView) findViewById(R.id.login_activity_service_message_tv);
        login_activity_online_tv = (TextView) findViewById(R.id.login_activity_online_tv);
        login_activity_online_tv.setSelected(false);
        login_activity_offline_tv = (TextView) findViewById(R.id.login_activity_offline_tv);
        login_activity_offline_tv.setTextIsSelectable(true);
        login_activity_on_off_switch = (Switch) findViewById(R.id.login_activity_on_off_switch);
        login_activity_on_off_switch.setOncheckListener(this);
    }

    private void setToolbar() {
        Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_info_white_36dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    private void showLoginDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.please_login))
                .customView(R.layout.login_dialog, true)
                .positiveColor(R.color.colorAccent)
                .positiveText(getString(R.string.login))
                .theme(Theme.LIGHT)
                .cancelable(false)
                .autoDismiss(false)

                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        tcKimlikNo = login_dialog_tc_et.getText().toString();
                        passWord = login_dialog_pass_et.getText().toString();
                        if (tcKimlikNo.isEmpty() || passWord.isEmpty()) {
                            showWarning(getString(R.string.please_fill_areas));
                        } else {
                            dialog.dismiss();
                            callRegister(tcKimlikNo, passWord);
                        }
                    }
                })
                .build();
        dialog.setCancelable(false);
        login_dialog_tc_et = (EditText) dialog.getCustomView().findViewById(R.id.login_dialog_tc_et);
        login_dialog_pass_et = (EditText) dialog.getCustomView().findViewById(R.id.login_dialog_pass_et);


        dialog.show();

    }

    private void showWarning(String message) {
        SnackBar snackbar = new SnackBar(this, message, "", null);
        snackbar.show();
    }

    private void callRegister(String tcNo, String passWord) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("");
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.REGISTER_BAASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        Service service = restAdapter.create(Service.class);
        service.register(tcNo, passWord, new Callback<RegisterResponse>() {
            @Override
            public void success(RegisterResponse registerResponse, Response response) {
                progressDialog.hide();
                if (registerResponse == null) {
                    showWarning(getString(R.string.general_error));
                    showLoginDialog();
                    return;
                }
                String accessToken = registerResponse.getAccess_token();
                int id = registerResponse.getDoctor_id();
                callDoktorInfo(id, accessToken);
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.hide();
            }
        });

    }

    private void callDoktorInfo(int id, String accessToken) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("");
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVICE_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        Service service = restAdapter.create(Service.class);
        final int token = Integer.parseInt(accessToken);
        service.doctors(id, token, new Callback<Doctor>() {
            @Override
            public void success(Doctor doctor, Response response) {
                progressDialog.dismiss();
                String info = doctor.getName() + " " + doctor.getLastname() + " ";
                login_activity_service_message_tv.setText(login_activity_service_message_tv.getText().toString() + " " + info);
                getHospitals(token);
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                showWarning(getString(R.string.general_error));
                int a = 0;
            }
        });
    }

    private void getHospitals(int accessToken) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("");
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVICE_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        Service service = restAdapter.create(Service.class);
        service.getHospitals(accessToken, new Callback<HospitalsResponse>() {
            @Override
            public void success(HospitalsResponse hospitalsResponse, Response response) {
                saveHospitals(hospitalsResponse);
                progressDialog.hide();
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.hide();
                showWarning(getString(R.string.general_error));
            }
        });
    }

    private void saveHospitals(HospitalsResponse hospitalsResponse) {
        for (Hospital hospital : hospitalsResponse.getHospitals()) {
            dbHelper.insertHospital(hospital);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = new Intent(MainActivity.this, AfisActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_logout) {
            showLoginDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheck(boolean isSelected) {
        if (isSelected) {
            login_activity_online_tv.setSelected(true);
            login_activity_offline_tv.setSelected(false);
            showOnlineMap();
        } else {
            login_activity_offline_tv.setSelected(true);
            login_activity_online_tv.setSelected(false);
            showOfflineMap();
        }
    }

    private void showOfflineMap() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        OfflineMapFragment offlineMapFragment = OfflineMapFragment.newInstance();
        fragmentTransaction.replace(R.id.login_activity_container_fl, offlineMapFragment);
        fragmentTransaction.commit();
    }

    private void showOnlineMap() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        OnlineMapFragment onlineMapFragment = OnlineMapFragment.newInstance();
        fragmentTransaction.replace(R.id.login_activity_container_fl, onlineMapFragment);
        fragmentTransaction.commit();
    }
}

