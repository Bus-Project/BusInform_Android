package com.example.businformapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class StationInfoActivity extends AppCompatActivity {
    private TextView stationName, locationNo1, predictTime1 , plateNo1, locationNo2, predictTime2 , plateNo2 ;

    private String servicePath = "http://openapi.gbis.go.kr/ws/rest/";
    private String serviceName = "busarrivalservice";
    private String operation = "station";
    private String serviceKey = "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D";
    private String params = "&stationId=";

    private String arr[];
    private String tags[]; // 요청 태그
    private String endTag;
    private ArrayList<HashMap<String, String>> map = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_info);

        // 표시할 정보 가져오기
        Intent intent = getIntent();

        // 텍스트뷰 객체화 (activity_route_info.xml)
        stationName = (TextView)findViewById(R.id.station_name);
        locationNo1 = (TextView)findViewById(R.id.locationNo1);
        predictTime1 = (TextView)findViewById(R.id.predictTime1);
        locationNo2 = (TextView)findViewById(R.id.locationNo2);
        predictTime2 = (TextView)findViewById(R.id.predictTime2);

        String title = intent.getStringExtra("stationName") + " (" + intent.getStringExtra("stationId") + ")";
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);

        // putExtra에서 식별태그로 넣어주었던 값을 전달 받아 사용
        stationName.setText(intent.getStringExtra("stationName"));

        params += intent.getStringExtra("stationId");
        // 노선 정보항목 조회
        arr = new String[]{servicePath, serviceName, operation, serviceKey, params};
        //
        tags = new String[]{"locationNo1", "predictTime1", "plateNo1", "locationNo2", "predictTime2", "plateNo2"};
        endTag = "busArrivalList";
        try {
            map = new GetApiData(arr, tags, endTag).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map);
        // 정보 표시
        try {
            HashMap<String, String> data = map.get(0);
            locationNo1.setText(data.get("locationNo1")+"정류장 전");
            predictTime1.setText(data.get("predictTime1")+"분 후 도착예정");
            locationNo2.setText(data.get("locationNo2")+"정류장 전");
            predictTime2.setText(data.get("predictTime2")+"분 후 도착예정");
        }
        catch(IndexOutOfBoundsException e) {
            TextView title1 = (TextView)findViewById(R.id.trip_route);
            title1.setText("도착 정보 없음");
            locationNo1.setText("없음");
            locationNo2.setText("");
            predictTime1.setText("");
            predictTime2.setText("");
        }
    }
}