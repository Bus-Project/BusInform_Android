package com.example.businformapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
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
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.businformapp.RouteInfoActivity.RouteStationListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class BusArrivalInfoActivity extends AppCompatActivity{
    private TextView route_Name, locationNo1, predictTime1 , locationNo2, predictTime2;

    private String searvicePath = "http://openapi.gbis.go.kr/ws/rest/";
    private String serviceName = "busarrivalservice";
    private String serviceName2 = "busrouteservice";
    private String serviceName3 = "buslocationservice";
    private String operation = "";
    private String operation2 = "station";
    private String serviceKey = "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D&stationId=";
    private String serviceKey2 = "serviceKey=69LFDh7FMch46%2FjRKfryua60biumVeNfOOox2jZadZGZSEPIplk5OLP8qPi0eHvWL5RGjDoiUkwvj96w58mYzw%3D%3D";
    private String params = "&staOrder=";
    private String params2 = "&routeId=";

    private String arr[];
    private String arr2[];
    private String arr3[];
    private String tags[]; // 요청 태그
    private String tags2[]; // 요청 태그
    private String tags3[]; // 요청 태그
    private String endTag;
    private String endTag2;
    private String endTag3;

    private ArrayList<HashMap<String, String>> map = new ArrayList<>();
    private ArrayList<HashMap<String, String>> map2 = new ArrayList<>();
    private ArrayList<HashMap<String, String>> map3 = new ArrayList<>();
    private ArrayList<ArrayList<HashMap<String, String>>>maps = new ArrayList<>();


    private String routeId;
    private String routeName;
    private String stationId;
    private String staOrder;
    private String newServiceKey;

    int location1;
    int location2;
    int predict1;
    int predict2;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busarrival_info);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "정보 업데이트 완료", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ApiParser();
                BusInformView();
                ApiParser2();
                ApiParser3();
                TripListView();
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

        params2 += routeId;

        // 버스 도착정보
        arr = new String[]{searvicePath, serviceName, operation, newServiceKey, params};
        tags = new String[]{"locationNo1", "predictTime1", "locationNo2", "predictTime2"};
        endTag = "busArrivalItem";
        // 경유 정류장 목록
        arr2 = new String[]{searvicePath, serviceName2, operation2, serviceKey, params2};
        tags2 = new String[]{"stationId", "staOrder", "stationName", "mobileNo"};
        endTag2 = "busRouteStationList";
        // 버스 위치 정보
        arr3 = new String[]{searvicePath, serviceName3, operation, serviceKey2, params2};
        tags3 = new String[]{"stationId", "stationSeq"};
        endTag3 = "busLocationList";

        // 도착 정보 파싱 & 텍스트뷰로 출력
        ApiParser();
        BusInformView();

        maps.clear();
        // 경유 정류장 파싱 & 리스트 띄우기
        ApiParser2();
        // 버스 위치 파싱 & 이미지뷰로 표시
        ApiParser3();
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

    public void ApiParser2(){
//        map2.clear();
        try {
            map2 = new GetApiData(arr2, tags2, endTag2).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map2);
        maps.add(map2);
    }

    public void ApiParser3() {
 //       map3.clear();
        try {
            map3 = new GetApiData(arr3, tags3, endTag3).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map3);
        maps.add(map3);
    }

    public void BusInformView() {
        try {
            HashMap<String, String> data = map.get(0);
            //정보 값 비교
            location1 = Integer.parseInt(data.get("locationNo1"));
            location2 = Integer.parseInt(data.get("locationNo2"));
            predict1 = Integer.parseInt(data.get("predictTime1"));
            predict2 = Integer.parseInt(data.get("predictTime2"));

            //정보 화면에 출력
            if ( location1 <= 0) { locationNo1.setText("아직 도착 정보가 없습니다."); }
            else{ locationNo1.setText(data.get("locationNo1")+"정류장 전"); }

            if ( predict1 <= 0) predictTime1.setText("아직 도착 정보가 없습니다.");
            else{ predictTime1.setText(data.get("predictTime1")+"분 후 도착예정");}

            if ( location2 <= 0) locationNo2.setText("아직 도착 정보가 없습니다.");
            else{ locationNo2.setText(data.get("locationNo2")+"정류장 전");}

            if ( predict2 <= 0) { predictTime2.setText("아직 도착 정보가 없습니다."); }
            else{ predictTime2.setText(data.get("predictTime2")+"분 후 도착예정"); }
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

    public void TripListView() {
        RouteStationListAdapter adapter = new RouteStationListAdapter(BusArrivalInfoActivity.this, maps);

        ListView listView = (ListView)findViewById(R.id.routeStationListView2);
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
        private ArrayList<ArrayList<HashMap<String, String>>> array_data;
        private ArrayList<HashMap<String, String>> arr1;
        private ArrayList<HashMap<String, String>> arr2;
        private Vector<String> stationSeqVector = new Vector<>();
        private HashMap<String, String> locationData;
        private long getPositon = 0;

        private ViewHolder mViewHolder;

        public RouteStationListAdapter(Context mContext, ArrayList<ArrayList<HashMap<String, String>>> mapArrayList) {
            this.mContext = mContext;
            this.array_data = mapArrayList;
            this.arr1 = array_data.get(0);
            this.arr2 = array_data.get(1);

            for (int i = 0; i < arr2.size(); i++) {
                locationData = arr2.get(i);
                String stationSeq = locationData.get("stationSeq");
                stationSeqVector.add(stationSeq);
            }
        }

        @Override
        public int getCount() {
            return arr1.size();
        }

        @Override
        public Object getItem(int position) { // 어댑터가 관리하는 Data의 Item의 Positon을 반환
            return arr1.get(position);
        }

        @Override
        public long getItemId(int position) { // 어댑터가 관리하는 Data의 Item의 Positon의 ID를 반환
            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        // position은 아이템의 인덱스, convertView는 인덱스에 해당하는 뷰 객체, ViewGroup은 convertView를 포함하는 부모 컨테이너 객체
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.station_name_list_item, parent, false); // 리스트 뷰에 들어갈 아이템.xml 파일
                mViewHolder = new ViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            HashMap<String, String> stationData = arr1.get(position);

            getPositon = getItemId(position); // 아이템 인덱스
            String strPosition = String.valueOf(getPositon);

            mViewHolder.route_image.setImageResource(R.drawable.route_null);

            for (int i = 0; i < stationSeqVector.size(); i++) { // 정류소 순번과 인덱스 대조
                String stationSeq = stationSeqVector.get(i);
                if (stationSeq.equals(strPosition))
                    mViewHolder.route_image.setImageResource(R.drawable.route_pass);
            }
            mViewHolder.station_name_title.setText(stationData.get("stationName"));
            mViewHolder.station_name_text.setText(stationData.get("mobileNo"));

            return convertView;
        }

        private class ViewHolder {
            private TextView station_name_title; // xml의 title id
            private TextView station_name_text; // // xml의 text id
            private ImageView route_image;

            public ViewHolder(View convertView) {
                station_name_title = (TextView) convertView.findViewById(R.id.station_name_title);
                station_name_text = (TextView) convertView.findViewById(R.id.station_name_text);
                route_image = (ImageView) convertView.findViewById(R.id.route_image);
            }
        }
    }

}
