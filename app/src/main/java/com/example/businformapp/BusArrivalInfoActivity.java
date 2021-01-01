package com.example.businformapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class BusArrivalInfoActivity extends AppCompatActivity{
    private TextView route_Name, locationNo1, predictTime1 , locationNo2, predictTime2;

    private String searvicePath = "http://openapi.gbis.go.kr/ws/rest/";
    private String serviceName = "busarrivalservice";
    private String operation = "";
    private String serviceKey = "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D&stationId=";
    private String params = "&staOrder=";

    private String arr[];
    private String tags[]; // 요청 태그
    private String endTag;
    private ArrayList<HashMap<String, String>> map = new ArrayList<>();

    private String routeId;
    private String routeName;
    private String stationId;
    private String staOrder;
    private String newServiceKey;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busarrival_info);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ApiParser();
                BusInformView();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        Intent intent = getIntent();

        //텍스트 뷰
        route_Name = (TextView)findViewById(R.id.routeName);
        locationNo1 = (TextView)findViewById(R.id.locationNo1);
        predictTime1 = (TextView)findViewById(R.id.predictTime1);
        locationNo2 = (TextView)findViewById(R.id.locationNo2);
        predictTime2 = (TextView)findViewById(R.id.predictTime2);

        String title = intent.getStringExtra("routeName");
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title+"번 도착정보");

        routeId = intent.getStringExtra("routeId");
        staOrder = intent.getStringExtra("staOrder");
        System.out.println(staOrder);
        stationId = intent.getStringExtra("stationId");

        params += staOrder;
        newServiceKey = serviceKey + stationId + "&routeId=" + routeId;
        System.out.println(newServiceKey);

        arr = new String[]{searvicePath, serviceName, operation, newServiceKey, params};
        tags = new String[]{"locationNo1", "predictTime1", "locationNo2", "predictTime2"};
        endTag = "busArrivalItem";

        ApiParser();
        BusInformView();


    }

    public void ApiParser() {
        map.clear();
        try {
            map = new GetApiData(arr, tags, endTag).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map);
    }

    public void BusInformView() {
        try {
            HashMap<String, String> data = map.get(0);
            //정보 화면에 출력
            locationNo1.setText(data.get("locationNo1")+"정류장 전");
            predictTime1.setText(data.get("predictTime1")+"분 후 도착예정");
            locationNo2.setText(data.get("locationNo2")+"정류장 전");
            predictTime2.setText(data.get("predictTime2")+"분 후 도착예정");
        }
        catch(IndexOutOfBoundsException e) {
            TextView title1 = (TextView)findViewById(R.id.trip_route);
            title1.setText("도착 정보 없음");
            locationNo1.setText("정보 없음");
            locationNo2.setText("정보 없음");
            predictTime1.setText("");
            predictTime2.setText("");
        }
    }

}
