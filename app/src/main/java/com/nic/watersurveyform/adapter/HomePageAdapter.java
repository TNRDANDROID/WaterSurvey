package com.nic.watersurveyform.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.activity.WaterConnection;
import com.nic.watersurveyform.databinding.HomePageAdapterBinding;
import com.nic.watersurveyform.pojo.WaterSurveyForm;

import org.json.JSONObject;

import java.util.List;


public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<WaterSurveyForm> userListValues;
    static JSONObject dataset = new JSONObject();

    private LayoutInflater layoutInflater;

    public HomePageAdapter(Activity context, List<WaterSurveyForm> userListValues) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.userListValues = userListValues;
    }

    @Override
    public HomePageAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        HomePageAdapterBinding homePageAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.home_page_adapter, viewGroup, false);
        return new HomePageAdapter.MyViewHolder(homePageAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private HomePageAdapterBinding homePageAdapterBinding;

        public MyViewHolder(HomePageAdapterBinding Binding) {
            super(Binding.getRoot());
            homePageAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.homePageAdapterBinding.nameofFamilyHead.setText(userListValues.get(position).getNameOfFamilyHead());
        holder.homePageAdapterBinding.fatherHusbandName.setText(userListValues.get(position).getFatherHusbandName());
        holder.homePageAdapterBinding.idCardType.setText(userListValues.get(position).getTypeOfId()+ " ("+userListValues.get(position).getTypeOfIdNUmber()+")");


        holder.homePageAdapterBinding.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWaterSurvey(position);
            }
        });
    }

    public void openWaterSurvey(int pos) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, WaterConnection.class);
//        intent.putExtra(AppConstant.PV_CODE, serverDataListValuesFiltered.get(pos).getPvCode());
//        intent.putExtra(AppConstant.HAB_CODE, serverDataListValuesFiltered.get(pos).getHabCode());
//        intent.putExtra(AppConstant.SECC_ID, serverDataListValuesFiltered.get(pos).getSeccId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


//    public void deletePending(int position) {
//        String pmay_id = pendingListValues.get(position).getPmayId();
//
//        int sdsm = db.delete(DBHelper.SAVE_PMAY_DETAILS, "id = ? ", new String[]{pmay_id});
//        int sdsm1 = db.delete(DBHelper.SAVE_PMAY_IMAGES, "pmay_id = ? ", new String[]{pmay_id});
//        pendingListValues.remove(position);
//        notifyItemRemoved(position);
//        notifyItemChanged(position, pendingListValues.size());
//        Log.d("sdsm", String.valueOf(sdsm));
//    }
//
//    public void uploadPending(int position) {
//        dataset = new JSONObject();
//        String dcode = pendingListValues.get(position).getDistictCode();
//        String bcode = pendingListValues.get(position).getBlockCode();
//        String pvcode = pendingListValues.get(position).getPvCode();
//        String habcode = pendingListValues.get(position).getHabCode();
//        String beneficiary_name = pendingListValues.get(position).getBeneficiaryName();
//        String father_name = pendingListValues.get(position).getFatherName();
//        String secc_id = pendingListValues.get(position).getSeccId();
//        String person_alive = pendingListValues.get(position).getPersonAlive();
//        String legal_heir_available = pendingListValues.get(position).getIsLegel();
//        String person_migrated = pendingListValues.get(position).getIsMigrated();
//        String button_text = pendingListValues.get(position).getButtonText();
//
//        String pmay_id = pendingListValues.get(position).getPmayId();
//        prefManager.setKeyDeletePosition(position);
//        prefManager.setKeyDeleteId(pmay_id);
//
//        try {
//            dataset.put(AppConstant.KEY_SERVICE_ID,AppConstant.PMAY_SOURCE_SAVE);
//            dataset.put(AppConstant.PV_CODE, pvcode);
//            dataset.put(AppConstant.HAB_CODE, habcode);
//            dataset.put(AppConstant.BENEFICIARY_NAME, beneficiary_name);
//            dataset.put(AppConstant.BENEFICIARY_FATHER_NAME, father_name);
//            dataset.put(AppConstant.PERSON_ALIVE, person_alive);
//            dataset.put(AppConstant.LEGAL_HEIR_AVAILABLE, legal_heir_available);
//            dataset.put(AppConstant.PERSON_MIGRATED, person_migrated);
//            dataset.put(AppConstant.SECC_ID, secc_id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JSONArray imageArray = new JSONArray();
//
//        if(button_text.equals("Take Photo")){
//            String image_sql = "Select * from " + DBHelper.SAVE_PMAY_IMAGES + " where pmay_id =" + pmay_id ;
//            Log.d("sql", image_sql);
//            Cursor image = db.rawQuery(image_sql, null);
//
//            if (image.getCount() > 0) {
//                if (image.moveToFirst()) {
//                    do {
//                        String latitude = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_LATITUDE));
//                        String longitude = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE));
//                        String images = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
//                        String type_of_photo = image.getString(image.getColumnIndexOrThrow(AppConstant.TYPE_OF_PHOTO));
//
//                        JSONObject imageJson = new JSONObject();
//
//                        try {
//                            imageJson.put(AppConstant.TYPE_OF_PHOTO,type_of_photo);
//                            imageJson.put(AppConstant.KEY_LATITUDE,latitude);
//                            imageJson.put(AppConstant.KEY_LONGITUDE,longitude);
//                            imageJson.put(AppConstant.KEY_IMAGE,images.trim());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        imageArray.put(imageJson);
//
//                    } while (image.moveToNext());
//                }
//            }
//        }
//
//        try {
//            dataset.put("images", imageArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if (Utils.isOnline()) {
//            ((PendingScreen)context).savePMAYImagesJsonParams(dataset);
//        } else {
//            Utils.showAlert(context, "Turn On Mobile Data To Upload");
//        }
//
//    }

    @Override
    public int getItemCount() {
        return userListValues.size();
    }


}
