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
    private HashMap<String, String> routeRegionCode = new HashMap<String, String>() {{put("1","가평군");put("2","고양시");
            put("3","과천시");put("4","광명시");put("5","광주시");put("6","구리시");put("7","군포시");put("8","김포시");put("9","남양주시");
            put("10","동두천시");put("11","부천시");put("12","성남시");put("13","수원시");put("14","시흥시");put("15","안산시");put("16","안성시");
            put("17","안양시");put("18","양주시");put("19","양평군");put("20","여주군");put("21","연천군");put("22","오산시");put("23","용인시");
            put("24","의왕시");put("25","의정부시");put("26","이천시");put("27","파주시");put("28","평택시");put("29","포천시");put("30","하남시");
            put("31","화성시");put("32","서울특별시");put("33","인천광역시");}};

    public TypeAndRegionCode(String idCode, String regionCode) {
        this.idCode = idCode;
        this.regionCode = regionCode;
    }

    public String getRouteType() {
        String result = routeTypeCode.get(idCode);
        return result;
    }

    public String getRegionName() {
        String result = routeRegionCode.get(regionCode);
        return result;
    }

}
