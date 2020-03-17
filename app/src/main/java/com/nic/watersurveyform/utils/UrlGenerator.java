package com.nic.watersurveyform.utils;


import com.nic.watersurveyform.R;
import com.nic.watersurveyform.application.NICApplication;

/**
 * Created by Achanthi Sundar  on 21/03/16.
 */
public class UrlGenerator {



    public static String getLoginUrl() {
        return NICApplication.getAppString(R.string.LOGIN_URL);
    }

    public static String getServicesListUrl() {
        return NICApplication.getAppString(R.string.BASE_SERVICES_URL);
    }

    public static String getWaterSurveyMainUrl() {
        return NICApplication.getAppString(R.string.WATER_SURVEY_MAIN_URL);
    }

    public static String getTnrdHostName() {
        return NICApplication.getAppString(R.string.TNRD_HOST_NAME);
    }

}
