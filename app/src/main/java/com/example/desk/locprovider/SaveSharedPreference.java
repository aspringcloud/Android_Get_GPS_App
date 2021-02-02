package com.example.desk.locprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String PREF_CAR_NUMBER = "carnumber";

    static SharedPreferences getSharedPreference(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 차 정보 저장
    public static void setCarNumber(Context ctx, String carNumber) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString(PREF_CAR_NUMBER, carNumber);
        editor.commit();
    }

    // 차 정보 가져오기
    public static String getCarNumber(Context ctx) {
        return getSharedPreference(ctx).getString(PREF_CAR_NUMBER,"1146");
    }

    // 로그아웃
    public static void clearCarNumber(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.clear();
        editor.commit();
    }
}
