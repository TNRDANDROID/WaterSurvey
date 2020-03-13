package com.nic.watersurveyform.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.watersurveyform.DataBase.DBHelper;
import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.api.Api;
import com.nic.watersurveyform.api.ApiService;
import com.nic.watersurveyform.api.ServerResponse;
import com.nic.watersurveyform.constant.AppConstant;
import com.nic.watersurveyform.databinding.WaterConnectionBinding;
import com.nic.watersurveyform.dialog.MyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.nic.watersurveyform.DataBase.dbData;
import com.nic.watersurveyform.pojo.WaterSurveyForm;
import com.nic.watersurveyform.support.ProgressHUD;
import com.nic.watersurveyform.utils.UrlGenerator;
import com.nic.watersurveyform.utils.Utils;

import static com.nic.watersurveyform.utils.Utils.*;

public class WaterConnection extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private WaterConnectionBinding waterConnectionBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private List<WaterSurveyForm> Village = new ArrayList<>();
    private List<WaterSurveyForm> Habitation = new ArrayList<>();
    String lastInsertedID;
    String isAlive = "", isLegal = "", isMigrated = "";
    private ProgressHUD progressHUD;


    String pref_Village;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        waterConnectionBinding = DataBindingUtil.setContentView(this, R.layout.water_connection);
        waterConnectionBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }

        waterConnectionBinding.available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean Y) {
                if(Y) {
                    waterConnectionBinding.notAvailable.setChecked(false);
                    waterConnectionBinding.approvedTv.setVisibility(View.VISIBLE);
                    waterConnectionBinding.approvalLayout.setVisibility(View.VISIBLE);
                }
                else {
                    waterConnectionBinding.approvedTv.setVisibility(View.GONE);
                    waterConnectionBinding.approvalLayout.setVisibility(View.GONE);
                }

            }
        });

        waterConnectionBinding.notAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean N) {
                if(N) {
                    waterConnectionBinding.available.setChecked(false);
                    waterConnectionBinding.approvedTv.setVisibility(View.GONE);
                    waterConnectionBinding.approvalLayout.setVisibility(View.GONE);
                    waterConnectionBinding.ifApproved.setChecked(false);
                    waterConnectionBinding.schemeTv.setVisibility(View.GONE);
                    waterConnectionBinding.schemeLayout.setVisibility(View.GONE);
                }

            }
        });
        waterConnectionBinding.ifApproved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean Y) {
                if(Y) {
                    waterConnectionBinding.notApproved.setChecked(false);
                    waterConnectionBinding.schemeTv.setVisibility(View.VISIBLE);
                    waterConnectionBinding.schemeLayout.setVisibility(View.VISIBLE);
                }
                else {
                    waterConnectionBinding.schemeTv.setVisibility(View.GONE);
                    waterConnectionBinding.schemeLayout.setVisibility(View.GONE);
                }

            }
        });

        waterConnectionBinding.notApproved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean N) {
                if(N) {
                    waterConnectionBinding.ifApproved.setChecked(false);
                    waterConnectionBinding.schemeTv.setVisibility(View.GONE);
                    waterConnectionBinding.schemeLayout.setVisibility(View.GONE);
                }

            }
        });
        syncButtonVisibility();
    }


    public void validateYesNo() {
        if (isAlive.equalsIgnoreCase("Y")) {
            isLegal = "";
        }
        if (isAlive.equalsIgnoreCase("N") && isLegal.equalsIgnoreCase("N")) {
            isMigrated = "";
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void logout() {
//        dbData.open();
//        ArrayList<WaterSurveyForm> ImageCount = dbData.getSavedPMAYDetails();
//        if (!isOnline()) {
//            showAlert(this, "Logging out while offline may leads to loss of data!");
//        } else {
//            if (!(ImageCount.size() > 0)) {
//                closeApplication();
//            } else {
//                showAlert(this, "Sync all the data before logout!");
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
    }


    public void viewServerData() {
//        homeScreenBinding.viewServerData.setVisibility(View.VISIBLE);
//        homeScreenBinding.viewServerData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isOnline()) {
//                    getPMAYList();
//                } else {
//                    showAlert(HomePage.this, "Your Internet seems to be Offline.Data can be viewed only in Online mode.");
//                }
//            }
//        });
    }

    public void getPMAYList() {
//        try {
//            new ApiService(this).makeJSONObjectRequest("PMAYList", Api.Method.POST, UrlGenerator.getPMAYListUrl(), pmayListJsonParams(), "not cache", this);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

  //  public JSONObject pmayListJsonParams() throws JSONException {
//        String authKey = encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.pmayListJsonParams().toString());
//        JSONObject dataSet = new JSONObject();
//        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
//        dataSet.put(AppConstant.DATA_CONTENT, authKey);
//        Log.d("PMAYList", "" + authKey);
//        return dataSet;
  //  }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("PMAYList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                  //  new InsertPMAYTask().execute(jsonObject);
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
                    showAlert(this,"No Record Found!");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//
//    private void dataFromServer() {
//        Intent intent = new Intent(this, ViewServerDataScreen.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//    }

    @Override
    public void OnError(VolleyError volleyError) {

    }



    public void closeApplication() {
        new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }

//    public void validateFields() {
//        if (!"Select Village".equalsIgnoreCase(Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvName())) {
//            if (!"Select Habitation".equalsIgnoreCase(Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabitationName())) {
//                if (!homeScreenBinding.name.getText().toString().isEmpty()) {
//                    if (!homeScreenBinding.fatherName.getText().toString().isEmpty()) {
//                        if (!homeScreenBinding.seccId.getText().toString().isEmpty()) {
//                            if (isValidMobile(homeScreenBinding.seccId.getText().toString())) {
//                                if ((homeScreenBinding.aliveYes.isChecked()) || homeScreenBinding.aliveNo.isChecked()) {
//                                    if (isAlive.equalsIgnoreCase("N")) {
//                                        if ((homeScreenBinding.legalYes.isChecked()) || homeScreenBinding.legalNo.isChecked()) {
//                                            checkLegalYesNo();
//                                        } else {
//                                            showAlert(this, "Check the beneficiary legal heir is available or not!");
//                                        }
//                                    } else {
//                                        checkLegalYesNo();
//                                    }
//                                } else {
//                                    showAlert(this, "Check the beneficiary is alive or not!");
//                                }
//
//                            } else {
//                                showAlert(this, "Seec Id Must be 7 Digit!");
//                            }
//                        } else {
//                            showAlert(this, "Enter the  Seec Id!");
//                        }
//                    } else {
//                        showAlert(this, "Enter the Father/Husband Name!");
//                    }
//                } else {
//                    showAlert(this, "Enter the Beneficiary Name!");
//                }
//            } else {
//                showAlert(this, "Select Habitation!");
//            }
//        } else {
//            showAlert(this, "Select Village!");
//        }
//
//    }

    public void checkLegalYesNo() {
//        if ((isLegal.equalsIgnoreCase("N"))) {
//            takePhoto(homeScreenBinding.takePhotoTv.getText().toString());
//        } else {
//            if ((homeScreenBinding.migYes.isChecked()) || homeScreenBinding.migNo.isChecked()) {
//                takePhoto(homeScreenBinding.takePhotoTv.getText().toString());
//            } else {
//                showAlert(this, "Check the beneficiary is Migrated or not!");
//            }
//        }
    }


    public void syncButtonVisibility() {
//        dbData.open();
//        ArrayList<PMAYSurvey> workImageCount = dbData.getSavedPMAYDetails();
//
//        if (workImageCount.size() > 0) {
//            homeScreenBinding.synData.setVisibility(View.VISIBLE);
//        }else {
//            homeScreenBinding.synData.setVisibility(View.GONE);
//        }
    }

    public void emptyValue() {
//        homeScreenBinding.villageSpinner.setSelection(0);
//        homeScreenBinding.habitationSpinner.setSelection(0);
//        homeScreenBinding.fatherName.setText("");
//        homeScreenBinding.name.setText("");
//        homeScreenBinding.seccId.setText("");
//        homeScreenBinding.aliveYes.setChecked(false);
//        homeScreenBinding.aliveNo.setChecked(false);
//        homeScreenBinding.legalYes.setChecked(false);
//        homeScreenBinding.legalNo.setChecked(false);
//        homeScreenBinding.migYes.setChecked(false);
//        homeScreenBinding.migNo.setChecked(false);
        isLegal = "";
        isAlive = "";
        isMigrated = "";

    }

    public void openPendingScreen() {
//        Intent intent = new Intent(this, PendingScreen.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


//    public class InsertPMAYTask extends AsyncTask<JSONObject, Void, Void> {
//
//        @Override
//        protected Void doInBackground(JSONObject... params) {
//            dbData.deletePMAYTable();
//            dbData.open();
//            ArrayList<PMAYSurvey> all_pmayListCount = dbData.getAll_PMAYList("","");
//            if (all_pmayListCount.size() <= 0) {
//                if (params.length > 0) {
//                    JSONArray jsonArray = new JSONArray();
//                    try {
//                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        PMAYSurvey pmaySurvey = new PMAYSurvey();
//                        try {
//                            pmaySurvey.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
//                            pmaySurvey.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HAB_CODE));
//                            pmaySurvey.setBeneficiaryName(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_NAME));
//                            pmaySurvey.setSeccId(jsonArray.getJSONObject(i).getString(AppConstant.SECC_ID));
//                            pmaySurvey.setHabitationName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));
//                            pmaySurvey.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));
//                            pmaySurvey.setPersonAlive(jsonArray.getJSONObject(i).getString(AppConstant.PERSON_ALIVE));
//                            pmaySurvey.setIsLegel(jsonArray.getJSONObject(i).getString(AppConstant.LEGAL_HEIR_AVAILABLE));
//                            pmaySurvey.setIsMigrated(jsonArray.getJSONObject(i).getString(AppConstant.PERSON_MIGRATED));
//
//                            dbData.insertPMAY(pmaySurvey);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//
//            }
//            return null;
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressHUD = ProgressHUD.show(HomePage.this, "Downloading", true, false, null);
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if(progressHUD!=null){
//                progressHUD.cancel();
//            }
//            dataFromServer();
//
//        }
//    }

}
