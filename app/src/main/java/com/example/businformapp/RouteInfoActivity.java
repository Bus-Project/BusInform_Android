package com.example.businformapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class RouteInfoActivity extends AppCompatActivity {
    private TextView routeName, routeTypeName, startStationName, endStationName;
    private RouteStationListAdapter adapter;
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
        routeName.setText(intent.getStringExtra("routeName"));
        routeTypeName.setText(intent.getStringExtra("routeTypeName"));
        String title = intent.getStringExtra("routeName");
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);

        // 노선 정보항목 조회 (상단)
        params += intent.getStringExtra("routeId");
        arr = new String[]{searvicePath, serviceName, operation, serviceKey, params};
        // 기점/종점 정류소명, 평일 기점 첫/막차 시간, 평일 종점 첫/막차 시간, 주말 기점 첫/막차 시간, 주말 종점 첫/막차 시간, 운수업체명, 전화번호
        tags = new String[]{"startStationName", "endStationName", "upFirstTime", "upLastTime", "downFirstTime", "downLastTime",
                "weekUpFirstTime", "weekUpLastTime", "weekDownFirstTime", "weekDownLastTime","companyName", "companyTel"};
        endTag = "busRouteInfoItem";
        ApiParser();
             // 정보 표시
        HashMap<String, String> data = map.get(0);
        startStationName.setText(data.get("startStationName"));
        endStationName.setText(data.get("endStationName"));

        // 노선 정보항목 조회 (하단)
        operation = "station";
        params = "&routeId=";
        params += intent.getStringExtra("routeId");
        arr = new String[]{searvicePath, serviceName, operation, serviceKey, params};
        tags = new String[]{"stationId", "staOrder", "stationName", "mobileNo"};
        endTag = "busRouteStationList";
        ApiParser();

        TripListView();
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

    public void TripListView() {
        RouteStationListAdapter adapter = new RouteStationListAdapter(RouteInfoActivity.this, map);

        ListView listView = (ListView)findViewById(R.id.routeStationListView);
        listView.setAdapter(adapter);

        // 클릭시 상세 정보가 나타남
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~>");
            }
        });
    }

    class RouteStationListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<HashMap<String, String>> array_data;

        private ViewHolder mViewHolder;

        public RouteStationListAdapter(Context mContext, ArrayList<HashMap<String, String>> mapArrayList) {
            this.mContext = mContext;
            this.array_data = mapArrayList;
        }

        @Override
        public int getCount() {
            return array_data.size();
        }

        @Override
        public Object getItem(int position) {
            return array_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.station_name_list_item, parent, false); // 리스트 뷰에 들어갈 아이템.xml 파일
                mViewHolder = new ViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            HashMap<String, String> data = array_data.get(position);
            System.out.println("------------------------------> "+data);
            System.out.println("------------------------------> " + data.get("stationName"));
            System.out.println("------------------------------> " + data.get("mobileNo"));
            mViewHolder.station_name_title.setText(data.get("stationName")); // 받아올 정보
            mViewHolder.station_name_text.setText(data.get("mobileNo"));

            return convertView;
        }

        private class ViewHolder {
            private TextView station_name_title; // xml의 title id
            private TextView station_name_text; // // xml의 text id

            public ViewHolder(View convertView) {
                station_name_title = (TextView) convertView.findViewById(R.id.station_name_title);
                station_name_text = (TextView) convertView.findViewById(R.id.station_name_text);
            }
        }
    }
}