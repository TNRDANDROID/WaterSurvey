package com.nic.watersurveyform.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.watersurveyform.DataBase.dbData;
import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.adapter.HomePageAdapter;
import com.nic.watersurveyform.api.Api;
import com.nic.watersurveyform.api.ServerResponse;
import com.nic.watersurveyform.databinding.FamilCardDetailsBinding;
import com.nic.watersurveyform.pojo.WaterSurveyForm;
import com.nic.watersurveyform.windowpreferences.WindowPreferencesManager;

import java.util.ArrayList;

public class FamilyCardDetails extends AppCompatActivity implements Api.ServerResponseListener {
    private FamilCardDetailsBinding familCardDetailsBinding;
    public dbData dbData = new dbData(this);
    private HomePageAdapter homePageAdapter;
    private PrefManager prefManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        familCardDetailsBinding = DataBindingUtil.setContentView(this, R.layout.famil_card_details);
        familCardDetailsBinding.setActivity(this);
        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());
        prefManager = new PrefManager(this);


        recyclerView = familCardDetailsBinding.userList;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        new fetchUserList().execute();
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class fetchUserList extends AsyncTask<Void, Void,
            ArrayList<WaterSurveyForm>> {
        @Override
        protected ArrayList<WaterSurveyForm> doInBackground(Void... params) {
            dbData.open();
            ArrayList<WaterSurveyForm> waterSurveys = new ArrayList<>();
            waterSurveys = dbData.getuserHabitationWise(prefManager.getDistrictCode(), prefManager.getBlockCode(), prefManager.getPvCode(), prefManager.getKeyHabCode());
            Log.d("LIST_COUNT", String.valueOf(waterSurveys.size()));
            return waterSurveys;
        }

        @Override
        protected void onPostExecute(ArrayList<WaterSurveyForm> waterSurveyForms) {
            super.onPostExecute(waterSurveyForms);
            homePageAdapter = new HomePageAdapter(FamilyCardDetails.this, waterSurveyForms);
            if (waterSurveyForms.size() > 0) {
                familCardDetailsBinding.userList.setAdapter(homePageAdapter);
            } else {
//                recyclerView.setVisibility(View.GONE);
//                viewServerDataScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
