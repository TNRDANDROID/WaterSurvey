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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.watersurveyform.DataBase.DBHelper;
import com.nic.watersurveyform.DataBase.dbData;
import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.adapter.CommonAdapter;
import com.nic.watersurveyform.adapter.HomePageAdapter;
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
    private RecyclerView recyclerView;
    private HomePageAdapter homePageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        homePageBinding = DataBindingUtil.setContentView(this, R.layout.home_page);
        homePageBinding.setActivity(this);
        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());

        recyclerView = homePageBinding.userList;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        homePageBinding.recyclerTitle.setVisibility(View.GONE);
        homePageBinding.userList.setVisibility(View.GONE);
        homePageBinding.notFoundTv.setVisibility(View.GONE);

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


        homePageBinding.selectVillageTv.setAlpha(0);
        homePageBinding.villageLayout.setAlpha(0);
        homePageBinding.selectHabTv.setAlpha(0);
        homePageBinding.habLayout.setAlpha(0);
        homePageBinding.selectStreetTv.setAlpha(0);
        homePageBinding.streetLayout.setAlpha(0);

        homePageBinding.selectVillageTv.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        homePageBinding.villageLayout.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        homePageBinding.selectHabTv.animate().translationX(0).alpha(1).setDuration(1200).setStartDelay(800).start();
        homePageBinding.habLayout.animate().translationX(0).alpha(1).setDuration(1400).setStartDelay(1000).start();
        homePageBinding.selectStreetTv.animate().translationX(0).alpha(1).setDuration(1600).setStartDelay(1200).start();
        homePageBinding.streetLayout.animate().translationX(0).alpha(1).setDuration(1800).setStartDelay(1400).start();

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
                    homePageBinding.recyclerTitle.setVisibility(View.GONE);
                    homePageBinding.userList.setVisibility(View.GONE);
                    homePageBinding.notFoundTv.setVisibility(View.GONE);
                    pref_Village = Village.get(position).getPvName();
                    prefManager.setVillageListPvName(pref_Village);
                    prefManager.setPvCode(Village.get(position).getPvCode());
                    getWaterSurveyListVillageWise();
                    habitationFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                homePageBinding.recyclerTitle.setVisibility(View.GONE);
                homePageBinding.userList.setVisibility(View.GONE);
                homePageBinding.notFoundTv.setVisibility(View.GONE);
            }
        });

        homePageBinding.habitationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setkeyHabCode(Habitation.get(position).getHabCode());
                    homePageBinding.recyclerTitle.setVisibility(View.VISIBLE);
                    homePageBinding.userList.setVisibility(View.VISIBLE);
                    homePageBinding.notFoundTv.setVisibility(View.GONE);
                    new fetchUserList().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                homePageBinding.recyclerTitle.setVisibility(View.GONE);
                homePageBinding.userList.setVisibility(View.GONE);
                homePageBinding.notFoundTv.setVisibility(View.GONE);
            }
        });

    }

    public class fetchUserList extends AsyncTask<Void, Void,
            ArrayList<WaterSurveyForm>> {
        @Override
        protected ArrayList<WaterSurveyForm> doInBackground(Void... params) {
            dbData.open();
            ArrayList<WaterSurveyForm> waterSurveys = new ArrayList<>();
            waterSurveys = dbData.getuserHabitationWise(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),prefManager.getKeyHabCode());
            Log.d("LIST_COUNT", String.valueOf(waterSurveys.size()));
            return waterSurveys;
        }

        @Override
        protected void onPostExecute(ArrayList<WaterSurveyForm> waterSurveyForms) {
            super.onPostExecute(waterSurveyForms);
            recyclerView.setVisibility(View.VISIBLE);
            homePageAdapter = new HomePageAdapter(HomePage.this, waterSurveyForms);
            recyclerView.setAdapter(homePageAdapter);
        }
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



    public void permissionGrantedMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


        }
    }



    public void getWaterSurveyListVillageWise() {
        try {
            new ApiService(this).makeJSONObjectRequest("WaterSurveyVillageWise", Api.Method.POST, UrlGenerator.getWaterSurveyVillageUrl(), waterSurveyVillageJson(), "not cache", this);
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
                Log.d("de",responseObj.getJSONArray(AppConstant.JSON_DATA).getJSONObject(0).getString("name_of_family_head"));
                }
                Log.d("WaterSurveyVillageWise", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class InsertUserVillageWiseTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
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


    public void closeApplication() {
        if(!Utils.isOnline()) {
            Utils.showAlert(this,"Logging out while offline may leads to loss of data!");
        }else {
            new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
        }
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

//    public void setAnimationView() {
//        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
//        rotation.setRepeatCount(Animation.INFINITE);
//        homePageBinding.refresh.startAnimation(rotation);
//    }
//
//    public void clearAnimations() {
//        homePageBinding.refresh.clearAnimation();
//    }
}
