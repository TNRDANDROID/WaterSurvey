package com.nic.watersurveyform.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.databinding.HomePageAdapterBinding;
import com.nic.watersurveyform.pojo.WaterSurveyForm;

import org.json.JSONObject;

import java.util.List;


public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<WaterSurveyForm> pendingListValues;
    static JSONObject dataset = new JSONObject();

    private LayoutInflater layoutInflater;

    public HomePageAdapter(Activity context, List<WaterSurveyForm> pendingListValues) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.pendingListValues = pendingListValues;
    }

    @Override
    public HomePageAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        HomePageAdapterBinding homePageAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.home_page, viewGroup, false);
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
//        holder.pendingAdapterBinding.habName.setText(pendingListValues.get(position).getHabitationName());
//        holder.pendingAdapterBinding.villageName.setText(pendingListValues.get(position).getPvName());
//        holder.pendingAdapterBinding.secId.setText(pendingListValues.get(position).getSeccId());
//        holder.pendingAdapterBinding.name.setText(pendingListValues.get(position).getBeneficiaryName());
//
//        if(image.getCount() > 0) {
//            holder.pendingAdapterBinding.viewOfflineImages.setVisibility(View.VISIBLE);
//        }
//        else {
//            holder.pendingAdapterBinding.viewOfflineImages.setVisibility(View.GONE);
//        }
//
//        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(button_text.equals("Take Photo")) {
//                    if(image.getCount() == 2 ) {
//                        uploadPending(position);
//                    }
//                    else {
//                        new AlertDialog.Builder(context)
//                                .setTitle("Alert")
//                                .setMessage("There's some photos are missing.Please, delete it and enter details once again")
//                                .setIcon(R.mipmap.alert)
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        dialog.cancel();
//                                    }
//                                }).show();
//
//                    }
//                }
//                else if(button_text.equals("Save details")){
//                    uploadPending(position);
//                }
//            }
//        });
//
//        holder.pendingAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePending(position);
//            }
//        });
//
//
//
//        holder.pendingAdapterBinding.viewOfflineImages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewImages(position);
//            }
//        });


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
        return pendingListValues.size();
    }


}
