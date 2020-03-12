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
    private List<WaterSurveyForm> electionWardNoCalls;
    private Context mContext;
    private String type;


    public CommonAdapter(Context mContext, List<WaterSurveyForm> electionWardNoCalls, String type) {
        this.electionWardNoCalls = electionWardNoCalls;
        this.mContext = mContext;
        this.type = type;
    }


    public int getCount() {
        return electionWardNoCalls.size();
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
        WaterSurveyForm electionWardNoCall = electionWardNoCalls.get(position);

        if (type.equalsIgnoreCase("DistrictList")) {
            tv_type.setText(electionWardNoCall.getDistrictName());
        } else if (type.equalsIgnoreCase("BlockList")) {
            tv_type.setText(electionWardNoCall.getLocalBodyName());
        } else if (type.equalsIgnoreCase("RuralUrbanList")) {
            tv_type.setText(electionWardNoCall.getRuralUrbanName());
        } else if (type.equalsIgnoreCase("MunicipalityList")) {
            tv_type.setText(electionWardNoCall.getLocalBodyName());
        } else if (type.equalsIgnoreCase("TownPanchayatList")) {
            tv_type.setText(electionWardNoCall.getLocalBodyName());
        } else if (type.equalsIgnoreCase("CorporationList")) {
            tv_type.setText(electionWardNoCall.getLocalBodyName());
        }
        return view;
    }
}
