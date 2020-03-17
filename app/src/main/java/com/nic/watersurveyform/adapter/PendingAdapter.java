package com.nic.watersurveyform.adapter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.watersurveyform.DataBase.DBHelper;
import com.nic.watersurveyform.DataBase.dbData;
import com.nic.watersurveyform.R;
import com.nic.watersurveyform.Session.PrefManager;
import com.nic.watersurveyform.activity.PendingScreen;
import com.nic.watersurveyform.databinding.PendingAdapterBinding;
import com.nic.watersurveyform.pojo.WaterSurveyForm;
import com.nic.watersurveyform.utils.Utils;

import org.json.JSONObject;

import java.util.List;


public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<WaterSurveyForm> pendingListValues;
    static JSONObject dataset = new JSONObject();
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    JSONObject datasetActivity = new JSONObject();
    private dbData dbData;

    private LayoutInflater layoutInflater;

    public PendingAdapter(Activity context, List<WaterSurveyForm> pendingListValues) {
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.pendingListValues = pendingListValues;
    }

    @Override
    public PendingAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        PendingAdapterBinding pendingAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.pending_adapter, viewGroup, false);
        return new PendingAdapter.MyViewHolder(pendingAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private PendingAdapterBinding pendingAdapterBinding;

        public MyViewHolder(PendingAdapterBinding Binding) {
            super(Binding.getRoot());
            pendingAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.pendingAdapterBinding.nameofFamilyHead.setText(pendingListValues.get(position).getNameOfFamilyHead());
        holder.pendingAdapterBinding.isConnecAvailable.setText(pendingListValues.get(position).getWaterConnAvailable());

        if (pendingListValues.get(position).getWaterConnAvailable().equalsIgnoreCase("Y")) {

            holder.pendingAdapterBinding.approvedLayout.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.isConnApproved.setText(pendingListValues.get(position).getWaterConnApproved());
            holder.pendingAdapterBinding.schemeView.setVisibility(View.VISIBLE);
        } else {
            holder.pendingAdapterBinding.approvedLayout.setVisibility(View.GONE);
            holder.pendingAdapterBinding.schemeView.setVisibility(View.GONE);
            holder.pendingAdapterBinding.approvedView.setVisibility(View.GONE);
        }

        if (pendingListValues.get(position).getWaterConnApproved().equalsIgnoreCase("Y")) {
            holder.pendingAdapterBinding.schemeLayout.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.schemeName.setText(pendingListValues.get(position).getSchemeName());
        } else {
            holder.pendingAdapterBinding.schemeLayout.setVisibility(View.GONE);
            holder.pendingAdapterBinding.schemeView.setVisibility(View.GONE);
        }

        final String dcode = pendingListValues.get(position).getDistictCode();
        final String bcode = pendingListValues.get(position).getBlockCode();
        final String pvcode = pendingListValues.get(position).getPvCode();
        final String habcode = pendingListValues.get(position).getHabCode();
        final String editid = pendingListValues.get(position).getEditId();
        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline()) {
                    WaterSurveyForm waterSurveyForm = new WaterSurveyForm();
                    waterSurveyForm.setDistictCode(dcode);
                    waterSurveyForm.setBlockCode(bcode);
                    waterSurveyForm.setPvCode(pvcode);
                    waterSurveyForm.setHabCode(habcode);
                    waterSurveyForm.setEditId(editid);
                    prefManager.setDistrictCode(dcode);
                    prefManager.setBlockCode(bcode);
                    prefManager.setPvCode(pvcode);
                    prefManager.setKeyDeleteId(String.valueOf(editid));

                    ((PendingScreen) context).new toUploadTask().execute(waterSurveyForm);
                } else {
                    Activity activity = (Activity) context;
                    Utils.showAlert(activity, "Turn On Mobile Data To Synchronize!");
                }
            }
        });

        holder.pendingAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePending(position);
            }
        });


    }


    public void deletePending(int position) {
        String editid = pendingListValues.get(position).getEditId();

        int sdsm = db.delete(DBHelper.SAVE_WATER_CONN_DETAILS, "edit_id = ? ", new String[]{editid});

        pendingListValues.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());
        Log.d("sdsm", String.valueOf(sdsm));
    }


    @Override
    public int getItemCount() {
        return pendingListValues.size();
    }


}
