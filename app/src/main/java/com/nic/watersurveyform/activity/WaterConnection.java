package com.nic.watersurveyform.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.watersurveyform.DataBase.DBHelper;
import com.nic.watersurveyform.DataBase.dbData;
import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.adapter.CommonAdapter;
import com.nic.watersurveyform.api.Api;
import com.nic.watersurveyform.api.ServerResponse;
import com.nic.watersurveyform.constant.AppConstant;
import com.nic.watersurveyform.databinding.WaterConnectionScreenBinding;
import com.nic.watersurveyform.dialog.MyDialog;
import com.nic.watersurveyform.pojo.WaterSurveyForm;
import com.nic.watersurveyform.support.ProgressHUD;
import com.nic.watersurveyform.utils.Utils;
import com.nic.watersurveyform.windowpreferences.WindowPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.nic.watersurveyform.utils.Utils.decrypt;
import static com.nic.watersurveyform.utils.Utils.showAlert;

public class WaterConnection extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    //    private WaterConnectionBinding waterConnectionBinding;
    private WaterConnectionScreenBinding waterConnectionScreenBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private List<WaterSurveyForm> Village = new ArrayList<>();
    private List<WaterSurveyForm> Habitation = new ArrayList<>();
    String lastInsertedID;
    String isWaterConnection = "", isApproved = "", isScheme = "";
    private ProgressHUD progressHUD;
    private List<WaterSurveyForm> Scheme = new ArrayList<>();
    JSONObject datasetActivity = new JSONObject();


    String pref_Village;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        waterConnectionScreenBinding = DataBindingUtil.setContentView(this, R.layout.water_connection_screen);
        waterConnectionScreenBinding.setActivity(this);
        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());
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

        waterConnectionScreenBinding.available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean Y) {
                if (Y) {
                    isWaterConnection = "Y";
                    waterConnectionScreenBinding.notAvailable.setChecked(false);
                    waterConnectionScreenBinding.ifApproved.setChecked(false);
                    waterConnectionScreenBinding.notApproved.setChecked(false);
                    waterConnectionScreenBinding.approvedTv.setVisibility(View.VISIBLE);
                    waterConnectionScreenBinding.approvalLayout.setVisibility(View.VISIBLE);
                } else {
                    waterConnectionScreenBinding.approvedTv.setVisibility(View.GONE);
                    waterConnectionScreenBinding.approvalLayout.setVisibility(View.GONE);
                }

            }
        });

        waterConnectionScreenBinding.notAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean N) {
                if (N) {
                    isWaterConnection = "N";
                    waterConnectionScreenBinding.available.setChecked(false);
                    waterConnectionScreenBinding.approvedTv.setVisibility(View.GONE);
                    waterConnectionScreenBinding.approvalLayout.setVisibility(View.GONE);
                    waterConnectionScreenBinding.ifApproved.setChecked(false);
                    waterConnectionScreenBinding.notApproved.setChecked(false);
                    waterConnectionScreenBinding.ifApproved.setChecked(false);
                    waterConnectionScreenBinding.schemeTv.setVisibility(View.GONE);
                    waterConnectionScreenBinding.schemeLayout.setVisibility(View.GONE);
                }

            }
        });
        waterConnectionScreenBinding.ifApproved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean Y) {
                if (Y) {
                    isApproved = "Y";
                    waterConnectionScreenBinding.notApproved.setChecked(false);
                    waterConnectionScreenBinding.schemeTv.setVisibility(View.VISIBLE);
                    waterConnectionScreenBinding.schemeLayout.setVisibility(View.VISIBLE);
                    schemeFilterSpinner();
                } else {
                    waterConnectionScreenBinding.schemeTv.setVisibility(View.GONE);
                    waterConnectionScreenBinding.schemeLayout.setVisibility(View.GONE);
                }

            }
        });

        waterConnectionScreenBinding.notApproved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean N) {
                if (N) {
                    isApproved = "N";
                    waterConnectionScreenBinding.ifApproved.setChecked(false);
                    waterConnectionScreenBinding.schemeTv.setVisibility(View.GONE);
                    waterConnectionScreenBinding.schemeLayout.setVisibility(View.GONE);
                }

            }
        });

        waterConnectionScreenBinding.schemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    isScheme = "Y";
                    prefManager.setSchemeId(Scheme.get(position).getSchemeID());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
        waterConnectionScreenBinding.nameOfFamilyHead.setText(getIntent().getStringExtra(AppConstant.NAME_OF_FAMILY_HEAD));
        waterConnectionScreenBinding.familyHeadTitle.setText(getIntent().getStringExtra(AppConstant.FAMILY_HEAD_TITLE));
        waterConnectionScreenBinding.fatherHusbandName.setText(getIntent().getStringExtra(AppConstant.FATHER_HUSBAND_NAME));


    }


    public void schemeFilterSpinner() {
        Cursor whichScheme = null;
        String schemeQuery = "SELECT * FROM " + DBHelper.SCHEME_TABLE_NAME + " order by scheme_name asc";
//        HABList = db.rawQuery("SELECT * FROM " + DBHelper.STREET_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' and habcode = '" + habcode + "' order by habitation_name asc", null);
        whichScheme = db.rawQuery(schemeQuery, null);
        Log.d("Streetque", "" + schemeQuery);
        Scheme.clear();
        WaterSurveyForm habitationListValue = new WaterSurveyForm();
        habitationListValue.setSchemeName("Select Scheme");
        Scheme.add(habitationListValue);
        if (whichScheme.getCount() > 0) {
            if (whichScheme.moveToFirst()) {
                do {
                    WaterSurveyForm scheme = new WaterSurveyForm();
                    String schemeID = whichScheme.getString(whichScheme.getColumnIndexOrThrow(AppConstant.SCHEME_ID));
                    String schemeName = whichScheme.getString(whichScheme.getColumnIndexOrThrow(AppConstant.SCHEME_NAME));

                    scheme.setSchemeID(schemeID);
                    scheme.setSchemeName(schemeName);


                    Scheme.add(scheme);
                    Log.d("spinnersize", "" + Scheme.size());
                } while (whichScheme.moveToNext());
            }
        }
        waterConnectionScreenBinding.schemeSpinner.setAdapter(new CommonAdapter(this, Scheme, "SchemeList"));
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

    }


    public void validate() {
        if ((waterConnectionScreenBinding.available.isChecked()) || (waterConnectionScreenBinding.notAvailable.isChecked())) {
            if (isWaterConnection.equalsIgnoreCase("Y")) {
                if ((waterConnectionScreenBinding.ifApproved.isChecked()) || (waterConnectionScreenBinding.notApproved.isChecked())) {
                    if (isApproved.equalsIgnoreCase("Y")) {
                        if (!"Select Scheme".equalsIgnoreCase(Scheme.get(waterConnectionScreenBinding.schemeSpinner.getSelectedItemPosition()).getSchemeName())) {
                            insertUserDetails();
                        } else {
                            Utils.showAlert(this, "Select Scheme!");
                        }
                    } else {
                        insertUserDetails();
                    }
                } else {
                    Utils.showAlert(this, "ஆம் ’ எனில் அனுமதி பெற்ற குடிநீர் இணைப்பா என்பதை சரிபார்க்கவும்?");
                }
            } else {
                insertUserDetails();
            }
        } else {
            Utils.showAlert(this, "வீட்டுக்கான குடிநீர் இணைப்பு பெறப்பட்டுள்ளதா என்பதை சரிபார்க்கவும்?");
        }
    }

    public void validateYesNo() {

    }

    public void insertUserDetails() {
        dbData.open();
        String edit_id = getIntent().getStringExtra(AppConstant.EDIT_ID);

        try {
            ContentValues values = new ContentValues();
            values.put(AppConstant.EDIT_ID, edit_id);
            values.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
            values.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
            values.put(AppConstant.PV_CODE, getIntent().getStringExtra(AppConstant.PV_CODE));
            values.put(AppConstant.HAB_CODE, getIntent().getStringExtra(AppConstant.HAB_CODE));
            values.put(AppConstant.STREET_CODE, prefManager.getStreetCode());
            values.put(AppConstant.NAME_OF_FAMILY_HEAD, waterConnectionScreenBinding.nameOfFamilyHead.getText().toString());
            if (isScheme.equalsIgnoreCase("Y")) {
                values.put(AppConstant.SCHEME_ID, prefManager.getSchemeId());
                values.put(AppConstant.SCHEME_NAME, Scheme.get(waterConnectionScreenBinding.schemeSpinner.getSelectedItemPosition()).getSchemeName());
            } else {
                values.put(AppConstant.SCHEME_ID, "");
                values.put(AppConstant.SCHEME_NAME, "");
            }
            values.put(AppConstant.WATER_CONNECTION_AVAILABLE, isWaterConnection);
            values.put(AppConstant.WATER_CONNECTION_APPROVED, isApproved);
            Cursor checkAvailableEditId = db.rawQuery("select * from "+DBHelper.SAVE_WATER_CONN_DETAILS+" where edit_id='"+edit_id+"'",null);
            if(checkAvailableEditId.getCount()>0){
                Utils.showAlert(this,"Entry Already exits in local database");
            }else {
                long id = db.insert(DBHelper.SAVE_WATER_CONN_DETAILS, null, values);

                if (id > 0) {
                    Toasty.success(this, "Success!", Toast.LENGTH_LONG, true).show();
                    super.onBackPressed();
                    overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("saveWaterConnection".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Utils.showAlert(this, "Uploaded");
                    db.delete(DBHelper.SAVE_WATER_CONN_DETAILS, "edit_id = ?", new String[]{prefManager.getKeyDeleteId()});
//                     new fetchPendingtask().execute();
//                    pendingAdapter.notifyDataSetChanged();
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")) {
                    showAlert(this, "No Record Found!");
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


    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
