package com.nic.watersurveyform.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.watersurveyform.DataBase.DBHelper;
import com.nic.watersurveyform.DataBase.dbData;
import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.api.Api;
import com.nic.watersurveyform.api.ApiService;
import com.nic.watersurveyform.api.ServerResponse;
import com.nic.watersurveyform.constant.AppConstant;
import com.nic.watersurveyform.databinding.LoginScreenBinding;
import com.nic.watersurveyform.pojo.WaterSurveyForm;
import com.nic.watersurveyform.support.ProgressHUD;
import com.nic.watersurveyform.utils.UrlGenerator;
import com.nic.watersurveyform.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    private String randString;

    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    JSONObject jsonObject;

    private PrefManager prefManager;
    private ProgressHUD progressHUD;
    private int setPType;

    public LoginScreenBinding loginScreenBinding;
    public dbData dbData = new dbData(this);
    Animation stb2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        loginScreenBinding = DataBindingUtil.setContentView(this, R.layout.login_screen);
        loginScreenBinding.setActivity(this);
        loginScreenBinding.scrollView.setVerticalScrollBarEnabled(false);
        loginScreenBinding.scrollView.isSmoothScrollingEnabled();
//        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
//        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());
        intializeUI();


    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        stb2 = AnimationUtils.loadAnimation(this, R.anim.stb2);
        loginScreenBinding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        loginScreenBinding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    checkLoginScreen();
                }
                return false;
            }
        });

        loginScreenBinding.password.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf"));
        loginScreenBinding.rd.setTranslationY(400);
        loginScreenBinding.and.setTranslationY(400);
        loginScreenBinding.dpt.setTranslationY(400);
        loginScreenBinding.tvVersionNumber.setTranslationY(400);
        loginScreenBinding.nicName.setTranslationY(400);
        loginScreenBinding.btnBuy.setTranslationY(400);

        loginScreenBinding.ivItemOne.setTranslationX(800);
        loginScreenBinding.ivItemTwo.setTranslationX(800);


        loginScreenBinding.btnBuy.setAlpha(0);

        loginScreenBinding.rd.setAlpha(0);
        loginScreenBinding.and.setAlpha(0);
        loginScreenBinding.dpt.setAlpha(0);
        loginScreenBinding.tvVersionNumber.setAlpha(0);
        loginScreenBinding.nicName.setAlpha(0);

        loginScreenBinding.ivItemOne.setAlpha(0);
        loginScreenBinding.ivItemTwo.setAlpha(0);


        loginScreenBinding.btnBuy.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();

        loginScreenBinding.rd.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        loginScreenBinding.and.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        loginScreenBinding.dpt.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        loginScreenBinding.tvVersionNumber.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        loginScreenBinding.nicName.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(100).start();

        loginScreenBinding.ivItemOne.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(200).start();
        loginScreenBinding.ivItemTwo.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();

        loginScreenBinding.ivIlls.startAnimation(stb2);
        randString = Utils.randomChar();


        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            loginScreenBinding.tvVersionNumber.setText("Version" + " " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }

    public boolean validate() {
        boolean valid = true;
        String username = loginScreenBinding.username.getText().toString().trim();
        prefManager.setUserName(username);
        String password = loginScreenBinding.password.getText().toString().trim();


        if (username.isEmpty()) {
            valid = false;
            Utils.showAlert(this, "Please enter the username");
        } else if (password.isEmpty()) {
            valid = false;
            Utils.showAlert(this, "Please enter the password");
        }
        return valid;
    }

    public void checkLoginScreen() {
        final String username = loginScreenBinding.username.getText().toString().trim();
        final String password = loginScreenBinding.password.getText().toString().trim();
        prefManager.setUserPassword(password);

        if (Utils.isOnline()) {
            if (!validate())
                return;
            else if (prefManager.getUserName().length() > 0 && password.length() > 0) {
                new ApiService(this).makeRequest("LoginScreen", Api.Method.POST, UrlGenerator.getLoginUrl(), loginParams(), "not cache", this);
            } else {
                Utils.showAlert(this, "Please enter your username and password!");
            }
        } else {
            //Utils.showAlert(this, getResources().getString(R.string.no_internet));
            AlertDialog.Builder ab = new AlertDialog.Builder(
                    LoginScreen.this);
            ab.setMessage("Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..");
            ab.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            Intent I = new Intent(
                                    android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(I);
                        }
                    });
            ab.setNegativeButton("Continue With Off-Line",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            offline_mode(username, password);
                        }
                    });
            ab.show();
        }
    }


    public Map<String, String> loginParams() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstant.KEY_SERVICE_ID, "login");


        String random = Utils.randomChar();

        params.put(AppConstant.USER_LOGIN_KEY, random);
        Log.d("randchar", "" + random);

        params.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        Log.d("user", "" + loginScreenBinding.username.getText().toString().trim());

        String encryptUserPass = Utils.md5(loginScreenBinding.password.getText().toString().trim());
        prefManager.setEncryptPass(encryptUserPass);
        Log.d("md5", "" + encryptUserPass);

        String userPass = encryptUserPass.concat(random);
        Log.d("userpass", "" + userPass);
        String sha256 = Utils.getSHA(userPass);
        Log.d("sha", "" + sha256);

        params.put(AppConstant.KEY_USER_PASSWORD, sha256);


        Log.d("user", "" + loginScreenBinding.username.getText().toString().trim());

        Log.d("params", "" + params);
        return params;
    }

    //The method for opening the registration page and another processes or checks for registering


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;

            if ("LoginScreen".equals(urlType)) {
                status = loginResponse.getString(AppConstant.KEY_STATUS);
                response = loginResponse.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")) {
                    if (response.equals("LOGIN_SUCCESS")) {
                        String key = loginResponse.getString(AppConstant.KEY_USER);
                        String user_data = loginResponse.getString(AppConstant.USER_DATA);
                        String decryptedKey = Utils.decrypt(prefManager.getEncryptPass(), key);
                        String userDataDecrypt = Utils.decrypt(prefManager.getEncryptPass(), user_data);
                        Log.d("userdatadecry", "" + userDataDecrypt);
                        jsonObject = new JSONObject(userDataDecrypt);
                        prefManager.setDistrictCode(jsonObject.get(AppConstant.DISTRICT_CODE));
                        prefManager.setBlockCode(jsonObject.get(AppConstant.BLOCK_CODE));
                        prefManager.setPvCode(jsonObject.get(AppConstant.PV_CODE));
                        prefManager.setDistrictName(jsonObject.get(AppConstant.DISTRICT_NAME));
                        prefManager.setBlockName(jsonObject.get(AppConstant.BLOCK_NAME));
                        prefManager.setDesignation(jsonObject.get(AppConstant.DESIG_NAME));
                        prefManager.setName(String.valueOf(jsonObject.get(AppConstant.DESIG_NAME)));
                        Log.d("userdata", "" + prefManager.getDistrictCode() + prefManager.getBlockCode() + prefManager.getPvCode() + prefManager.getDistrictName() + prefManager.getBlockName() + prefManager.getName());
                        prefManager.setUserPassKey(decryptedKey);
                        getVillageList();
                        getHabList();
                        getSchemeList();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showHomeScreen();
                            }
                        }, 1000);

                    } else {
                        if (response.equals("LOGIN_FAILED")) {
                            Utils.showAlert(this, "Invalid UserName Or Password");
                        }
                    }
                }

            }
            if ("VillageList".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertVillageTask().execute(jsonObject);
                }
                Log.d("VillageList", "" + responseDecryptedBlockKey);
            }
            if ("HabitationList".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertHabTask().execute(jsonObject);
                }
                Log.d("HabitationList", "" + responseDecryptedBlockKey);
            }
            if ("SchemeList".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertSchemeTask().execute(jsonObject);
                }
                Log.d("SchemeList", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getHabList() {
        try {
            new ApiService(this).makeJSONObjectRequest("HabitationList", Api.Method.POST, UrlGenerator.getServicesListUrl(), habitationListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSchemeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SchemeList", Api.Method.POST, UrlGenerator.getWaterSurveyMainUrl(), schemeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject villageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.villageListDistrictBlockWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + authKey);
        return dataSet;
    }


    public JSONObject habitationListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.HabitationListDistrictBlockVillageWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("HabitationList", "" + authKey);
        return dataSet;
    }
    public JSONObject schemeListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.SchemeListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("SchemeList", "" + authKey);
        return dataSet;
    }


    @Override
    public void OnError(VolleyError volleyError) {
        Utils.showAlert(this, "Login Again");
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        showHomeScreen();
//    }

    public class InsertVillageTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<WaterSurveyForm> villagelist_count = dbData.getAll_Village(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (villagelist_count.size() <= 0) {
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
                            villageListValue.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));

                            dbData.insertVillage(villageListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }

    public class InsertHabTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<WaterSurveyForm> hablist_count = dbData.getAll_Habitation(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (hablist_count.size() <= 0) {
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
                            habListValue.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HABB_CODE));
                            habListValue.setHabitationName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));

                            dbData.insertHabitation(habListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;


        }
    }

    public class InsertSchemeTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<WaterSurveyForm> schemelistcount = dbData.getAllScheme();
            if (schemelistcount.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        WaterSurveyForm schemeListValue = new WaterSurveyForm();
                        try {
                            schemeListValue.setSchemeID(jsonArray.getJSONObject(i).getString("id"));
                            schemeListValue.setSchemeName(jsonArray.getJSONObject(i).getString("name"));

                            dbData.insertScheme(schemeListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;


        }
    }


    private void showHomeScreen() {
        Intent intent = new Intent(LoginScreen.this, HomePage.class);
        intent.putExtra("Home", "Login");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void offline_mode(String name, String pass) {
        String userName = prefManager.getUserName();
        String password = prefManager.getUserPassword();
        if (name.equals(userName) && pass.equals(password)) {
            showHomeScreen();
        } else {
            Utils.showAlert(this, "No data available for offline. Please Turn On Your Network");
        }
    }
}
