package com.nic.watersurveyform.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.util.Log;

import com.nic.watersurveyform.constant.AppConstant;
import com.nic.watersurveyform.pojo.WaterSurveyForm;

import java.util.ArrayList;


public class dbData {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public dbData(Context context) {
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteReadOnlyDatabaseException e) {

        }

    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }



    /****** VILLAGE TABLE *****/
    public WaterSurveyForm insertVillage(WaterSurveyForm pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.PV_NAME, pmgsySurvey.getPvName());

        long id = db.insert(DBHelper.VILLAGE_TABLE_NAME,null,values);
        Log.d("Inserted_id_village", String.valueOf(id));

        return pmgsySurvey;
    }
    public ArrayList<WaterSurveyForm> getAll_Village(String dcode, String bcode) {

        ArrayList<WaterSurveyForm > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by pvname asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    WaterSurveyForm  card = new WaterSurveyForm ();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public WaterSurveyForm insertHabitation(WaterSurveyForm pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.HABB_CODE, pmgsySurvey.getHabCode());
        values.put(AppConstant.HABITATION_NAME, pmgsySurvey.getHabitationName());

        long id = db.insert(DBHelper.HABITATION_TABLE_NAME,null,values);
        Log.d("Inserted_id_habitation", String.valueOf(id));

        return pmgsySurvey;
    }
    public ArrayList<WaterSurveyForm > getAll_Habitation(String dcode,String bcode) {

        ArrayList<WaterSurveyForm > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.HABITATION_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by habitation_name asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    WaterSurveyForm  card = new WaterSurveyForm ();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABB_CODE)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public WaterSurveyForm insertUserVillageWise(WaterSurveyForm userVillageWiseUser) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, userVillageWiseUser.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, userVillageWiseUser.getBlockCode());
        values.put(AppConstant.PV_CODE, userVillageWiseUser.getPvCode());
        values.put(AppConstant.HAB_CODE, userVillageWiseUser.getHabCode());
        values.put(AppConstant.EDIT_ID, userVillageWiseUser.getEditId());
        values.put(AppConstant.NAME_OF_FAMILY_HEAD, userVillageWiseUser.getNameOfFamilyHead());
        values.put(AppConstant.FAMILY_HEAD_TITLE, userVillageWiseUser.getFamilyHeadTitle());
        values.put(AppConstant.FATHER_HUSBAND_NAME, userVillageWiseUser.getFatherHusbandName());
        values.put(AppConstant.TYPE_OF_ID, userVillageWiseUser.getTypeOfId());
        values.put(AppConstant.TYPE_OF_ID_NUMBER, userVillageWiseUser.getTypeOfIdNUmber());

        long id = db.insert(DBHelper.USER_LIST_VILLAGE_WISE,null,values);
        Log.d("Insert_id_user_village", String.valueOf(id));

        return userVillageWiseUser;
    }

    public ArrayList<WaterSurveyForm> getuserVillageWise(String dcode, String bcode,String pvcode) {

        ArrayList<WaterSurveyForm > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.USER_LIST_VILLAGE_WISE+" where dcode = "+dcode+" and bcode = "+bcode+" and pvcode = "+pvcode+" order by pvcode asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    WaterSurveyForm  card = new WaterSurveyForm ();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setEditId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EDIT_ID)));
                    card.setNameOfFamilyHead(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.NAME_OF_FAMILY_HEAD)));
                    card.setFamilyHeadTitle(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.FAMILY_HEAD_TITLE)));
                    card.setFatherHusbandName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.FATHER_HUSBAND_NAME)));
                    card.setTypeOfId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_ID)));
                    card.setTypeOfIdNUmber(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_ID_NUMBER)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public ArrayList<WaterSurveyForm> getuserHabitationWise(String dcode, String bcode,String pvcode,String hab_code) {

        ArrayList<WaterSurveyForm > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.USER_LIST_VILLAGE_WISE+" where dcode = "+dcode+" and bcode = "+bcode+" and pvcode = "+pvcode+"  and hab_code = "+hab_code+" order by pvcode asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    WaterSurveyForm  card = new WaterSurveyForm ();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setEditId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EDIT_ID)));
                    card.setNameOfFamilyHead(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.NAME_OF_FAMILY_HEAD)));
                    card.setFamilyHeadTitle(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.FAMILY_HEAD_TITLE)));
                    card.setFatherHusbandName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.FATHER_HUSBAND_NAME)));
                    card.setTypeOfId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_ID)));
                    card.setTypeOfIdNUmber(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_ID_NUMBER)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public void deleteAllTables(){
        deleteVillageTable();
        deleteHabitationTable();
    }

    public void deleteVillageTable() {
        db.execSQL("delete from " + DBHelper.VILLAGE_TABLE_NAME);
    }
    public void deleteHabitationTable() {
        db.execSQL("delete from " + DBHelper.HABITATION_TABLE_NAME);
    }


}
