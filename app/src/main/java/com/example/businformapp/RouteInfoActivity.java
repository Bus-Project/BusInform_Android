package com.example.businformapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class RouteInfoActivity extends AppCompatActivity {
    private TextView routeName, routeTypeName, startStationName, endStationName;

    private String searvicePath = "http://openapi.gbis.go.kr/ws/rest/";
    private String serviceName = "busrouteservice";
    private String operation = "info";
    private String serviceKey = "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D";
    private String params = "&routeId=";

    private String arr[];
    private String tags[]; // 요청 태그
    private String endTag;
    private ArrayList<HashMap<String, String>> map = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_info);

        // 표시할 정보 가져오기
        Intent intent = getIntent();

        // 텍스트뷰 객체화 (activity_route_info.xml)
        routeName = (TextView)findViewById(R.id.route_name);
        routeTypeName = (TextView)findViewById(R.id.route_type_name);
        startStationName = (TextView)findViewById(R.id.start_station_name);
        endStationName = (TextView)findViewById(R.id.end_station_name);

        // putExtra에서 식별태그로 넣어주었던 값을 전달 받아 사용
        String title = intent.getStringExtra("routeName");
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);

        routeName.setText(intent.getStringExtra("routeName"));
        routeTypeName.setText(intent.getStringExtra("routeTypeName"));

        params += intent.getStringExtra("routeId");
        // 노선 정보항목 조회
        arr = new String[]{searvicePath, serviceName, operation, serviceKey, params};
        // 기점/종점 정류소명, 평일 기점 첫/막차 시간, 평일 종점 첫/막차 시간, 주말 기점 첫/막차 시간, 주말 종점 첫/막차 시간, 운수업체명, 전화번호
        tags = new String[]{"startStationName", "endStationName", "upFirstTime", "upLastTime", "downFirstTime", "downLastTime",
                "weekUpFirstTime", "weekUpLastTime", "weekDownFirstTime", "weekDownLastTime","companyName", "companyTel"};
        endTag = "busRouteInfoItem";
        try {
            map = new GetApiData(arr, tags, endTag).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map);
        // 정보 표시
        HashMap<String, String> data = map.get(0);
        startStationName.setText(data.get("startStationName"));
        endStationName.setText(data.get("endStationName"));
    }
}