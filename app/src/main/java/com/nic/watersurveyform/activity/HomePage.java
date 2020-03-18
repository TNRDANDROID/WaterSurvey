package com.nic.watersurveyform.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.watersurveyform.DataBase.DBHelper;
import com.nic.watersurveyform.DataBase.dbData;
import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.adapter.CommonAdapter;
import com.nic.watersurveyform.api.Api;
import com.nic.watersurveyform.api.ApiService;
import com.nic.watersurveyform.api.ServerResponse;
import com.nic.watersurveyform.constant.AppConstant;
import com.nic.watersurveyform.databinding.HomePageBinding;
import com.nic.watersurveyform.dialog.MyDialog;
import com.nic.watersurveyform.pojo.WaterSurveyForm;
import com.nic.watersurveyform.support.ProgressHUD;
import com.nic.watersurveyform.utils.UrlGenerator;
import com.nic.watersurveyform.utils.Utils;
import com.nic.watersurveyform.windowpreferences.WindowPreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomePage extends AppCompatActivity implements MyDialog.myOnClickListener, Api.ServerResponseListener {

    private HomePageBinding homePageBinding;
    public dbData dbData = new dbData(this);
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    Animation smalltobig;
    final Handler handler = new Handler();
    private PrefManager prefManager;
    String pref_Block, pref_district, pref_Village;
    boolean isPanchayatUnion, isMunicipality, isTownPanchayat, isCorporation;
    private ProgressHUD progressHUD;
    private List<WaterSurveyForm> Village = new ArrayList<>();
    private List<WaterSurveyForm> Habitation = new ArrayList<>();
    private List<WaterSurveyForm> Street = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        homePageBinding = DataBindingUtil.setContentView(this, R.layout.home_page);
        homePageBinding.setActivity(this);
        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());



        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        prefManager = new PrefManager(this);
        homePageBinding.homeImg.startAnimation(smalltobig);
        homePageBinding.selectVillageTv.setTranslationX(800);
        homePageBinding.villageLayout.setTranslationX(800);
        homePageBinding.selectHabTv.setTranslationX(800);
        homePageBinding.habLayout.setTranslationX(800);
        homePageBinding.selectStreetTv.setTranslationX(800);
        homePageBinding.streetLayout.setTranslationX(800);
        homePageBinding.saveLayout.setTranslationY(800);
        homePageBinding.syn.setTranslationZ(800);


        homePageBinding.selectVillageTv.setAlpha(0);
        homePageBinding.villageLayout.setAlpha(0);
        homePageBinding.selectHabTv.setAlpha(0);
        homePageBinding.habLayout.setAlpha(0);
        homePageBinding.selectStreetTv.setAlpha(0);
        homePageBinding.streetLayout.setAlpha(0);
        homePageBinding.syn.setAlpha(0);

        homePageBinding.selectVillageTv.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        homePageBinding.villageLayout.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        homePageBinding.selectHabTv.animate().translationX(0).alpha(1).setDuration(1200).setStartDelay(800).start();
        homePageBinding.habLayout.animate().translationX(0).alpha(1).setDuration(1400).setStartDelay(1000).start();
        homePageBinding.selectStreetTv.animate().translationX(0).alpha(1).setDuration(1600).setStartDelay(1200).start();
        homePageBinding.streetLayout.animate().translationX(0).alpha(1).setDuration(1800).setStartDelay(1400).start();
        homePageBinding.saveLayout.animate().translationY(0).alpha(1).setDuration(2000).setStartDelay(1700).start();
        homePageBinding.syn.animate().translationZ(0).alpha(1).setDuration(2200).setStartDelay(1900).start();
//        homePageBinding.syn.setAlpha(1);
//        homePageBinding.syn.startAnimation(AnimationUtils.loadAnimation(HomePage.this, R.anim.text_view_move_right));


        syncButtonVisibility();

//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadOfflineDistrictListDBValues();
//            }
//        }, 2000);

        villageFilterSpinner(prefManager.getBlockCode());
        homePageBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    pref_Village = Village.get(position).getPvName();
                    prefManager.setVillageListPvName(pref_Village);
                    prefManager.setPvCode(Village.get(position).getPvCode());
                    habitationFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());
                    homePageBinding.streetSpinner.setAdapter(null);
                } else if (position == 0) {
                    homePageBinding.habitationSpinner.setAdapter(null);
                    homePageBinding.streetSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homePageBinding.habitationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setkeyHabCode(Habitation.get(position).getHabCode());

                    if (Utils.isOnline()) {
                        getStreetList();
                    } else {
                        Utils.showAlert(HomePage.this, getResources().getString(R.string.no_internet));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        homePageBinding.streetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setStreetCode(Street.get(position).getStreetCode());


//
//                    new fetchUserList().execute();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
    }


    public void villageFilterSpinner(String filterVillage) {
        Cursor VillageList = null;
        VillageList = db.rawQuery("SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " where dcode = "+prefManager.getDistrictCode()+ " and bcode = '" + filterVillage + "'", null);

        Village.clear();
        WaterSurveyForm villageListValue = new WaterSurveyForm();
        villageListValue.setPvName("Select Village");
        Village.add(villageListValue);
        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    WaterSurveyForm villageList = new WaterSurveyForm();
                    String districtCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String pvname = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME));

                    villageList.setDistictCode(districtCode);
                    villageList.setBlockCode(blockCode);
                    villageList.setPvCode(pvCode);
                    villageList.setPvName(pvname);

                    Village.add(villageList);
                    Log.d("spinnersize", "" + Village.size());
                } while (VillageList.moveToNext());
            }
        }
        homePageBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }


    public void habitationFilterSpinner(String dcode,String bcode, String pvcode) {
        Cursor HABList = null;
        HABList = db.rawQuery("SELECT * FROM " + DBHelper.HABITATION_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' order by habitation_name asc", null);

        Habitation.clear();
        WaterSurveyForm habitationListValue = new WaterSurveyForm();
        habitationListValue.setHabitationName("Select Habitation");
        Habitation.add(habitationListValue);
        if (HABList.getCount() > 0) {
            if (HABList.moveToFirst()) {
                do {
                    WaterSurveyForm habList = new WaterSurveyForm();
                    String districtCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String habCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABB_CODE));
                    String habName = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABITATION_NAME));

                    habList.setDistictCode(districtCode);
                    habList.setBlockCode(blockCode);
                    habList.setPvCode(pvCode);
                    habList.setHabCode(habCode);
                    habList.setHabitationName(habName);

                    Habitation.add(habList);
                    Log.d("spinnersize", "" + Habitation.size());
                } while (HABList.moveToNext());
            }
        }
        homePageBinding.habitationSpinner.setAdapter(new CommonAdapter(this, Habitation, "HabitationList"));
    }

    public void streetFilterSpinner(String dcode, String bcode, String pvcode, String hab_code) {
        Cursor streetList = null;
        String streetQuery = "SELECT * FROM " + DBHelper.STREET_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' and hab_code = '" + hab_code + "' order by street_name_t asc";
//        HABList = db.rawQuery("SELECT * FROM " + DBHelper.STREET_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' and habcode = '" + habcode + "' order by habitation_name asc", null);
        streetList = db.rawQuery(streetQuery, null);
        Log.d("Streetque", "" + streetQuery);
        Street.clear();
        WaterSurveyForm habitationListValue = new WaterSurveyForm();
        habitationListValue.setStreetName("Select Street");
        Street.add(habitationListValue);
        if (streetList.getCount() > 0) {
            if (streetList.moveToFirst()) {
                do {
                    WaterSurveyForm street = new WaterSurveyForm();
                    String districtCode = streetList.getString(streetList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = streetList.getString(streetList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = streetList.getString(streetList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String habCode = streetList.getString(streetList.getColumnIndexOrThrow(AppConstant.HAB_CODE));
                    String streetCode = streetList.getString(streetList.getColumnIndexOrThrow(AppConstant.STREET_CODE));
                    String StreetName = streetList.getString(streetList.getColumnIndexOrThrow(AppConstant.STREET_NAME_TAMIL));

                    street.setDistictCode(districtCode);
                    street.setBlockCode(blockCode);
                    street.setPvCode(pvCode);
                    street.setHabCode(habCode);
                    street.setStreetCode(streetCode);
                    street.setStreetName(StreetName);

                    Street.add(street);
                    Log.d("spinnersize", "" + Street.size());
                } while (streetList.moveToNext());
            }
        }
        homePageBinding.streetSpinner.setAdapter(new CommonAdapter(this, Street, "StreetList"));
      //  syncButtonVisibility();
    }



    public void permissionGrantedMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


        }
    }

    public void getStreetList() {
        try {
            new ApiService(this).makeJSONObjectRequest("StreetList", Api.Method.POST, UrlGenerator.getWaterSurveyMainUrl(), streetListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getWaterSurveyListVillageWise() {
        try {
            new ApiService(this).makeJSONObjectRequest("WaterSurveyVillageWise", Api.Method.POST, UrlGenerator.getWaterSurveyMainUrl(), waterSurveyVillageJson(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject waterSurveyVillageJson() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.WaterSurveyVillageParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("waterSurveyVillageJson", "" + authKey);
        Log.d("daataset", "" + dataSet.toString());
        return dataSet;
    }

    public JSONObject streetListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.streetListDistrictBlockVillageWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("StreetList", "" + authKey);
        return dataSet;
    }

    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<WaterSurveyForm> workImageCount = dbData.getSavedUserDetails("", "", "", "");

        if (workImageCount.size() > 0) {
            homePageBinding.syn.setVisibility(View.VISIBLE);

        } else {
            homePageBinding.syn.setVisibility(View.GONE);

        }
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();

            if ("WaterSurveyVillageWise".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                 new InsertUserVillageWiseTask().execute(jsonObject);
//                Log.d("de",responseObj.getJSONArray(AppConstant.JSON_DATA).getJSONObject(0).getString("name_of_family_head"));
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, "No Record Found!");
                }
                Log.d("WaterSurveyVillageWise", "" + responseDecryptedBlockKey);
            }
            if ("StreetList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new insertStreetTask().execute(jsonObject);
                }
                Log.d("StreetList", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class InsertUserVillageWiseTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            if (Utils.isOnline()) {
                dbData.deleteUserTable();
            }
            ArrayList<WaterSurveyForm> villageUserlist_count = dbData.getuserVillageWise(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());
            if (villageUserlist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        WaterSurveyForm villageListValue = new WaterSurveyForm();
                        try {
                            villageListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            villageListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            villageListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            villageListValue.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HAB_CODE));
                            villageListValue.setEditId(jsonArray.getJSONObject(i).getString(AppConstant.EDIT_ID));
                            villageListValue.setNameOfFamilyHead(jsonArray.getJSONObject(i).getString(AppConstant.NAME_OF_FAMILY_HEAD));
                            villageListValue.setFamilyHeadTitle(jsonArray.getJSONObject(i).getString(AppConstant.FAMILY_HEAD_TITLE));
                            villageListValue.setFatherHusbandName(jsonArray.getJSONObject(i).getString(AppConstant.FATHER_HUSBAND_NAME));
                            villageListValue.setTypeOfId(jsonArray.getJSONObject(i).getString(AppConstant.TYPE_OF_ID));
                            villageListValue.setTypeOfIdNUmber(jsonArray.getJSONObject(i).getString(AppConstant.TYPE_OF_ID_NUMBER));
                            dbData.insertUserVillageWise(villageListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(HomePage.this, "Downloading", true, false, null);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressHUD != null) {
                progressHUD.cancel();
            }
            dataFromServer();

        }


    }

    public class insertStreetTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            if (Utils.isOnline()) {
                dbData.deleteStreetTable();
            }
            ArrayList<WaterSurveyForm> streetlist_count = dbData.getAll_Street();
            if (streetlist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        WaterSurveyForm habListValue = new WaterSurveyForm();
                        try {
                            habListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            habListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            habListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            habListValue.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HAB_CODE));
                            habListValue.setStreetCode(jsonArray.getJSONObject(i).getString(AppConstant.STREET_CODE));
                            habListValue.setStreetName(jsonArray.getJSONObject(i).getString(AppConstant.STREET_NAME_TAMIL));

                            dbData.insertStreet(habListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            streetFilterSpinner(prefManager.getDistrictCode(), prefManager.getBlockCode(), prefManager.getPvCode(), prefManager.getKeyHabCode());
        }

    }

    private void dataFromServer() {
        Intent intent = new Intent(this, FamilyCardDetails.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void openPendingScreen() {
        Intent intent = new Intent(this, PendingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }


    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }

    public void refreshScreenCallApi() {
        if (Utils.isOnline()) {
            deleteRefreshTable();
           // setAnimationView();

        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
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


    public void deleteRefreshTable() {
        dbData.open();
        dbData.deleteAllTables();
    }

    public void closeApplication() {
        if (!Utils.isOnline()) {
            Utils.showAlert(this, "Logging out while offline may leads to loss of data!");
        } else {
            new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
        }
    }

    public void validate() {
        if (Utils.isOnline()) {
            if (!"Select Village".equalsIgnoreCase(Village.get(homePageBinding.villageSpinner.getSelectedItemPosition()).getPvName())) {
                if (!"Select Habitation".equalsIgnoreCase(Habitation.get(homePageBinding.habitationSpinner.getSelectedItemPosition()).getHabitationName())) {
                    if (!"Select Street".equalsIgnoreCase(Street.get(homePageBinding.streetSpinner.getSelectedItemPosition()).getStreetName())) {
                        getWaterSurveyListVillageWise();
                    } else {
                        Utils.showAlert(this, "Select Street!");
                    }
                } else {
                    Utils.showAlert(this, "Select Habitation!");
                }
            } else {
                Utils.showAlert(this, "Select Village!");
            }
        } else {
            dataFromServer();
        }

    }

//    public void setAnimationView() {
//        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
//        rotation.setRepeatCount(Animation.INFINITE);
//        homePageBinding.refresh.startAnimation(rotation);
//    }
//
//    public void clearAnimations() {
//        homePageBinding.refresh.clearAnimation();
//    }

    public void emptyValue(){
        villageFilterSpinner(prefManager.getBlockCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
        emptyValue();
    }
}
