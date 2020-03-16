package com.nic.watersurveyform.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nic.watersurveyform.R;
import com.nic.watersurveyform.pojo.WaterSurveyForm;

import java.util.List;

/**
 * Created by shanmugapriyan on 25/05/16.
 */
public class CommonAdapter extends BaseAdapter {
    private List<WaterSurveyForm> waterSurveyFormList;
    private Context mContext;
    private String type;


    public CommonAdapter(Context mContext, List<WaterSurveyForm> waterSurveyFormList, String type) {
        this.waterSurveyFormList = waterSurveyFormList;
        this.mContext = mContext;
        this.type = type;
    }


    public int getCount() {
        return waterSurveyFormList.size();
    }


    public Object getItem(int position) {
        return position;
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.spinner_drop_down_item, parent, false);
//        TextView tv_type = (TextView) view.findViewById(R.id.tv_spinner_item);
        View view = inflater.inflate(R.layout.spinner_value, parent, false);
        TextView tv_type = (TextView) view.findViewById(R.id.spinner_list_value);
        WaterSurveyForm waterSurveyForm = waterSurveyFormList.get(position);

        if (type.equalsIgnoreCase("VillageList")) {
            tv_type.setText(waterSurveyForm.getPvName());
        } else if (type.equalsIgnoreCase("HabitationList")) {
            tv_type.setText(waterSurveyForm.getHabitationName());
        } else if (type.equalsIgnoreCase("StreetList")) {
            tv_type.setText(waterSurveyForm.getStreetName());
        } else if (type.equalsIgnoreCase("SchemeList")) {
            tv_type.setText(waterSurveyForm.getSchemeName());
        }
        return view;
    }
}
