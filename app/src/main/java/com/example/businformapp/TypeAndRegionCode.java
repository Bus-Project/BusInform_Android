package com.example.businformapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.R)
public class TypeAndRegionCode {
    private String idCode;
    private String regionCode;
    private HashMap<String, String> routeTypeCode = new HashMap<String, String>() {{put("11", "직행좌석형시내버스");}};
    private HashMap<String, String> routeRegionCode = new HashMap<String, String>() {{put("01","가평군");put("02","고양시");
            put("03","과천시");put("04","광명시");put("01","가평군");put("01","가평군");put("01","가평군");put("01","가평군");put("01","가평군");
            put("01","가평군");put("01","가평군");put("01","가평군");put("01","가평군");put("01","가평군");put("01","가평군");}};

    public TypeAndRegionCode(String idCode, String regionCode) {
        this.idCode = idCode;
        this.regionCode = regionCode;
    }


}
