package com.nic.watersurveyform.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.nic.watersurveyform.adapter.PendingAdapter;
import com.nic.watersurveyform.api.Api;
import com.nic.watersurveyform.api.ApiService;
import com.nic.watersurveyform.api.ServerResponse;
import com.nic.watersurveyform.constant.AppConstant;
import com.nic.watersurveyform.databinding.PendingScreenBinding;
import com.nic.watersurveyform.pojo.WaterSurveyForm;
import com.nic.watersurveyform.utils.UrlGenerator;
import com.nic.watersurveyform.utils.Utils;
import com.nic.watersurveyform.windowpreferences.WindowPreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PendingScreen extends AppCompatActivity implements Api.ServerResponseListener {
    public PendingScreenBinding pendingScreenBinding;
    private RecyclerView recyclerView;
    private PendingAdapter pendingAdapter;
    private PrefManager prefManager;
    public com.nic.watersurveyform.DataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private Activity context;
    JSONObject dataset = new JSONObject();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pendingScreenBinding = DataBindingUtil.setContentView(this, R.layout.pending_screen);
        pendingScreenBinding.setActivity(this);
        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());
        context = this;
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView = pendingScreenBinding.pendingList;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        new fetchPendingtask().execute();
    }


    public class fetchPendingtask extends AsyncTask<Void, Void,
            ArrayList<WaterSurveyForm>> {
        @Override
        protected ArrayList<WaterSurveyForm> doInBackground(Void... params) {
            dbData.open();
            ArrayList<WaterSurveyForm> pmaySurveys = new ArrayList<>();
            pmaySurveys = dbData.getSavedUserDetails( "", "", "", "");
            Log.d("PMAY_COUNT", String.valueOf(pmaySurveys.size()));
            return pmaySurveys;
        }

        @Override
        protected void onPostExecute(ArrayList<WaterSurveyForm> pmaySurveys) {
            super.onPostExecute(pmaySurveys);
            recyclerView.setVisibility(View.VISIBLE);
            pendingAdapter = new PendingAdapter(PendingScreen.this, pmaySurveys);
            recyclerView.setAdapter(pendingAdapter);
        }
    }


    public class toUploadTask extends AsyncTask<WaterSurveyForm, Void,
            JSONObject> {
        @Override
        protected JSONObject doInBackground(WaterSurveyForm... realTimeValue) {
            dbData.open();
            JSONArray track_data = new JSONArray();
            ArrayList<WaterSurveyForm> assets = dbData.getSavedUserDetails("upload", realTimeValue[0].getPvCode(), realTimeValue[0].getHabCode(), realTimeValue[0].getEditId());

            if (assets.size() > 0) {
                for (int i = 0; i < assets.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(AppConstant.PV_CODE, assets.get(i).getPvCode());
                        jsonObject.put(AppConstant.HAB_CODE, assets.get(i).getHabCode());
                        jsonObject.put(AppConstant.STREET_CODE,assets.get(i).getStreetCode());
                        jsonObject.put(AppConstant.EDIT_ID, assets.get(i).getEditId());
                        jsonObject.put(AppConstant.SCHEME_ID, assets.get(i).getSchemeID());
                        jsonObject.put(AppConstant.WATER_CONNECTION_AVAILABLE, assets.get(i).getWaterConnAvailable());
                        jsonObject.put(AppConstant.WATER_CONNECTION_APPROVED, assets.get(i).getWaterConnApproved());
                        track_data.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                dataset = new JSONObject();

                try {
                    dataset.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_BNA_SANITATION_WATER_CONN_SAVE);
                    dataset.put(AppConstant.KEY_TRACK_DATA, track_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return dataset;
        }

        @Override
        protected void onPostExecute(JSONObject dataset) {
            super.onPostExecute(dataset);
            syncData();
        }
    }

    public void syncData() {
        try {
            new ApiService(this).makeJSONObjectRequest("saveUserDetails", Api.Method.POST, UrlGenerator.getWaterSurveyMainUrl(), saveListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject saveListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), dataset.toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("dataset",dataset.toString());
        Log.d("saveUserDetails", "" + authKey);
        return dataSet;
    }




    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("saveUserDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Utils.showAlert(this, "Uploaded");
                    db.delete(DBHelper.SAVE_WATER_CONN_DETAILS, "edit_id = ?", new String[]{prefManager.getKeyDeleteId()});
                    new fetchPendingtask().execute();
                    pendingAdapter.notifyDataSetChanged();
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")) {
                    Toasty.error(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
                    db.delete(DBHelper.SAVE_WATER_CONN_DETAILS, "edit_id = ?", new String[]{prefManager.getKeyDeleteId()});

                    new fetchPendingtask().execute();
                    pendingAdapter.notifyDataSetChanged();
                }
                Log.d("saved_response", "" + responseDecryptedBlockKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
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
}
