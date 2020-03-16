package com.nic.watersurveyform.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WaterSurveyForm";
    private static final int DATABASE_VERSION = 1;


    public static final String VILLAGE_TABLE_NAME = "villageTable";
    public static final String HABITATION_TABLE_NAME = "habitaionTable";
    public static final String SCHEME_TABLE_NAME = "schemeTable";
    public static final String STREET_TABLE_NAME = "streetTable";
    public static final String USER_LIST_VILLAGE_WISE = "userlistVillageWise";
    public static final String  SAVE_WATER_CONN_DETAILS = "saveWaterConnectionDetails";
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + VILLAGE_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "pvname TEXT)");

        db.execSQL("CREATE TABLE " + SCHEME_TABLE_NAME + " ("
                + "id TEXT," +
                "name TEXT)");

        db.execSQL("CREATE TABLE " + HABITATION_TABLE_NAME + " ("
                + "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "habitation_code TEXT," +
                "habitation_name TEXT)");

        db.execSQL("CREATE TABLE " + STREET_TABLE_NAME + " ("
                + "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "street_code TEXT," +
                "street_name_t TEXT)");

        db.execSQL("CREATE TABLE " + USER_LIST_VILLAGE_WISE + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "hab_code INTEGER," +
                "edit_id INTEGER," +
                "name_of_family_head TEXT," +
                "family_head_title TEXT," +
                "father_husband_name TEXT," +
                "type_of_id TEXT," +
                "type_of_id_number TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_WATER_CONN_DETAILS + " ("
                +"pvcode TEXT," +
                "hab_code TEXT," +
                "street_code TEXT," +
                "edit_id TEXT," +
                "water_conn_available TEXT," +
                "is_approved TEXT," +
                "scheme_id TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HABITATION_TABLE_NAME);
            onCreate(db);
        }
    }


}
