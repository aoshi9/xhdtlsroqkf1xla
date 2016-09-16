package com.gimbal.android.skccJeju;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 준형 on 2016-09-07.
 */
public class DBHelper extends SQLiteOpenHelper {
    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성 (SHOP_PRODUCT, 매장판매물품 관리 table)
        // 추가 테이블 필요시 이 부분에 추가
        //db.execSQL("CREATE TABLE SHOP_ITEM (SHOP_ID TEXT, ITEM TEXT, PRICE INTEGER, PRIMARY KEY(SHOP_ID, ITEM));");
        //db.execSQL("CREATE TABLE SHOP_ITEM (SHOP_ID TEXT, ITEM TEXT, PRICE INTEGER);");
        db.execSQL("CREATE TABLE SOT_BEACON_INFO (BC_NO TEXT, PLACE_SE_CD TEXT, BC_PLACE_NM TEXT, LATITUDE DOUBLE, " +
                 "LONGITUDE DOUBLE, URL TEXT, EVENT TEXT, EVENT_IMG TEXT, TEL_NO TEXT, ADDR TEXT, VERSION TEXT);");
        db.execSQL("CREATE TABLE SOT_BEACON_INFO_ITEM (BC_NO TEXT, ITEM_NO TEXT, ITEM_NM TEXT, ITEM_PRICE INTEGER, ITEM_DESC TEXT, BASKET_YN TEXT, ITEM_EVENT TEXT);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // SOT_BEACON_INFO 테이블의 select 작업 함수
    public String shopItemSelect(String shop_id, String item, int price) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM SHOP_ITEM", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)
                    + " : "
                    + cursor.getString(1)
                    + " | "
                    + cursor.getInt(2)
                    + "원 "
                    + "\n";
        }

        return result;
    }

    // 생성된 테이블의 insert 작업 함수
    public void shopItemInsert(String shop_id, String item, int price) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        Log.v("tempLog  :  ", "Data : " + shop_id + " " + item + " " + price);
        db.execSQL("INSERT INTO SHOP_ITEM VALUES('" + shop_id  + "', '" + item + "', " + price + ");");
        db.close();
    }

    // 생성된 테이블의 update 작업 함수
    public void shopItemUpdate(String shop_id, String item, int price) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE SHOP_ITEM SET price=" + price + " WHERE SHOP_ID='" + shop_id + "' AND ITEM='" + item + "';");
        db.close();
    }

    // 생성된 테이블의 delete 작업 함수
    public void shopItemDelete(String shop_id, String item) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM SHOP_ITEM WHERE SHOP_ID='" + shop_id + "' AND ITEM='" + item + "';");
        db.close();
    }

    //시장 번호에 따른 필요 정보를 불러오는 select함수
    public String shopItemSelectByPlaceSeCd(String placeCode) {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        /*String myQuery = "SELECT B.BC_PLACE_NM, B.LATITUDE, B.LONGITUDE, B.URL, I.ITEM_NM, I.ITEM_PRICE " +
                " FROM SOT_BEACON_INFO B INNER JOIN SOT_BEACON_INFO_ITEM I " +
                " ON B.BC_NO = I.BC_NO " +
                " WHERE B.PLACE_SE_CD = ? ;";
        Cursor cursor = db.rawQuery(myQuery, new String[] {"1"});*/

        Cursor cursor = db.rawQuery("SELECT B.BC_NO, BC_PLACE_NM, LATITUDE, LONGITUDE, URL, ITEM_NM, ITEM_PRICE " +
                " FROM SOT_BEACON_INFO B INNER JOIN SOT_BEACON_INFO_ITEM I " +
                " ON B.BC_NO = I.BC_NO " +
                " WHERE B.PLACE_SE_CD = '" + placeCode + "'; " , null);

        //inside Cursor 아예 실행이 안됨
        if(cursor.getCount()!=0) {
            if(cursor.moveToFirst()){
                do{
                    result += cursor.getString(cursor.getColumnIndex("BC_NO"))
                            + "!"
                            + cursor.getString(cursor.getColumnIndex("BC_PLACE_NM"))
                            + "!"
                            + cursor.getDouble(cursor.getColumnIndex("LATITUDE"))
                            + "!"
                            + cursor.getDouble(cursor.getColumnIndex("LONGITUDE"))
                            + "!"
                            + cursor.getString(cursor.getColumnIndex("URL"))
                            + "!"
                            + cursor.getString(cursor.getColumnIndex("ITEM_NM"))
                            + "!"
                            + cursor.getInt(cursor.getColumnIndex("ITEM_PRICE"))
                            + "!";
                }while(cursor.moveToNext());
            }
        }
        return result;
    }

    //SOT_BEACON_INFO 테이블 INSERT문
    public long SotBeaconInfoInsert(String beaconNumber, String marketCode, String shopName, double latitude, double longitude, String url, String event, String eventImg, String phoneNumber, String address, String version) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BC_NO", beaconNumber);
        values.put("PLACE_SE_CD", marketCode);
        values.put("BC_PLACE_NM", shopName) ;
        values.put("LATITUDE", latitude);
        values.put("LONGITUDE", longitude);
        values.put("URL", url);
        values.put("EVENT", event);
        values.put("EVENT_IMG", eventImg);
        values.put("TEL_NO", phoneNumber);
        values.put("ADDR", address);
        values.put("VERSION", version);
        return db.insert("SOT_BEACON_INFO", null, values);

        /*  db.execSQL("INSERT INTO SOT_BEACON_INFO (BC_NO, PLACE_SE_CD, BC_PLACE_NM, LATITUDE, LONGITUDE, URL, EVENT, EVENT_IMG, TEL_NO, ADDR, VERSION) " +
                "VALUES ('" + beaconNumber + "', '" + marketCode + "', '" + shopName + "', " + latitude + ", " + longitude + ", '" + url +
                "', '" + event + "', '" + eventImg + "', '" + phoneNumber + "', '" + address + "', '" + version + "' ); ");
        db.close();*/
    }

    //SOT_BEACON_INFO_ITEM 테이블 INSERT문 & BasketYN은 Default로 N
    public long SotBeaconInfoItemInsert(String beaconNumber, String itemNumber, String itemName, int itemPrice, String itemDiscount, String itemEvent) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BC_NO", beaconNumber);
        values.put("ITEM_NO", itemNumber);
        values.put("ITEM_NM", itemName) ;
        values.put("ITEM_PRICE", itemPrice);
        values.put("ITEM_DESC", itemDiscount);
        values.put("BASKET_YN", "N");
        values.put("ITEM_EVENT", itemEvent);
        return db.insert("SOT_BEACON_INFO_ITEM", null, values);

       /*
        db.execSQL("INSERT INTO SOT_BEACON_INFO_ITEM (BC_NO, ITEM_NO, ITEM_NM, ITEM_PRICE, ITEM_DESC, BASKET_YN, ITEM_EVENT) " +
                   " VALUES ('" + beaconNumber + "', '" + itemNumber + "', '" + itemName + "', " + itemPrice + ", '" + itemDiscount + "', 'N', '" + itemEvent + "' );");
        db.close();*/
    }

    // SOT_BEACON_INFO_ITEM 테이블의 BASKET_YN -> Y
    public void SotBeaconInfoItemBasketY(String beaconNumber, String itemName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE SOT_BEACON_INFO_ITEM SET BASKET_YN= 'Y' WHERE BC_NO = '" + beaconNumber + "' AND ITEN_NM = '" + itemName + "';");
        db.close();
    }

    // SOT_BEACON_INFO_ITEM 테이블의 BASKET_YN -> N
    public void SotBeaconInfoItemBasketN(String beaconNumber, String itemName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE SOT_BEACON_INFO_ITEM SET BASKET_YN= 'N' WHERE BC_NO = '" + beaconNumber + "' AND ITEN_NM = '" + itemName + "';");
        db.close();
    }

}
