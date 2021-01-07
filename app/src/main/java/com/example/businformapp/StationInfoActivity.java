package com.example.businformapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class StationInfoActivity extends AppCompatActivity {
    private TextView stationName, routeName, locationNo1, predictTime1 , locationNo2, predictTime2 ;

    private String servicePath = "http://openapi.gbis.go.kr/ws/rest/";
    private String serviceName = "busstationservice";
    private String serviceName2 = "busrouteservice";
    private String operation = "route";
    private String operation2 = "info";
    private String serviceKey = "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D";
    private String params = "&stationId=";
    private String params2 = "&routeId=";

    private String arr[];
    private String tags[]; // 요청 태그
    private String endTag;
    private ArrayList<HashMap<String, String>> map = new ArrayList<>();
    private ArrayList<HashMap<String, String>> map2 = new ArrayList<>();

    private String stationId;

    String routeId;

    private String x;
    private String y;
    private String markerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_info);

        // 표시할 정보 가져오기
        Intent intent = getIntent();


        String title = intent.getStringExtra("stationName") + " (" + intent.getStringExtra("stationId") + ")";
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);

        stationId = intent.getStringExtra("stationId");
        params += stationId;
        // 노선 정보항목 조회
        arr = new String[]{servicePath, serviceName, operation, serviceKey, params};
        tags = new String[]{"routeId", "routeName", "routeTypeName", "staOrder"};
        endTag = "busRouteList";
        ApiParser();

        BusNumberListView();

        x = intent.getStringExtra("x");
        y = intent.getStringExtra("y");
        markerName = title;
        Button mapStationBtn = findViewById(R.id.mapStationBtn);
        mapStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Kakao Map", "Map View Activity start.");

                Intent intent = new Intent(StationInfoActivity.this, MapViewActivity.class);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("markerName", markerName);
                startActivity(intent);
            }
        });
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

    public void BusNumberListView() {
        StationInfoActivity.BusNumberListAdapter adapter = new StationInfoActivity.BusNumberListAdapter(StationInfoActivity.this, map);

        ListView listView = (ListView)findViewById(R.id.busNumberListView);
        listView.setAdapter(adapter);

        // 클릭시 상세 정보가 나타남
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // BusArrivalInfoActivity로 넘어감
                Intent intent = new Intent(StationInfoActivity.this, BusArrivalInfoActivity.class);
                // 클릭 위치 탐색
                HashMap<String, String> data = map.get(position);
                // putExtra 첫 인자는 식별 태그, 두번째는 다음 엑티비티에 넘길 정보
                intent.putExtra("routeId", data.get("routeId"));
                intent.putExtra("routeName", data.get("routeName"));
                intent.putExtra("staOrder", data.get("staOrder"));
                intent.putExtra("stationId", stationId);
                startActivity(intent);
            }
        });
    }
    class BusNumberListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<HashMap<String, String>> array_data;

        private ViewHolder mViewHolder;

        public BusNumberListAdapter(Context mContext, ArrayList<HashMap<String, String>> mapArrayList) {
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.route_name_list_item, parent, false); // 리스트 뷰에 들어갈 아이템.xml 파일
                mViewHolder = new StationInfoActivity.BusNumberListAdapter.ViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (StationInfoActivity.BusNumberListAdapter.ViewHolder) convertView.getTag();
            }

            HashMap<String, String> data = array_data.get(position);
            System.out.println("------------------------------> "+data);
            System.out.println("------------------------------> " + data.get("routeId"));
            System.out.println("------------------------------> " + data.get("routeName"));
            mViewHolder.route_name_title.setText(data.get("routeName")); // 받아올 정보
            mViewHolder.route_name_text.setText(data.get("routeTypeName"));

            return convertView;
        }

        private class ViewHolder {
            private TextView route_name_title; // xml의 title id
            private TextView route_name_text; // // xml의 text id

            public ViewHolder(View convertView) {
                route_name_title = (TextView) convertView.findViewById(R.id.route_name_title);
                route_name_text = (TextView) convertView.findViewById(R.id.route_name_text);
            }
        }
    }
}