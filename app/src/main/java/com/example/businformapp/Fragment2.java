package com.example.businformapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class Fragment2 extends Fragment {

    private ArrayList<HashMap<String, String>> busStationList;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Nullable
    @Override // MainActivity에서의 onCreate 메소드는 Fragment에서는 onCreateView 메소드에 작성합니다.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment에서는 MainActivity에서의 setContentView(R.layout.xml파일명) 대신 inflater가 존재합니다.
        // inflater는 xml로 정의된 view (또는 menu 등)를 실제 객체화 시키는 용도
        // 또한 setContentView의 부재로 findViewById(R.id.id명)은 바로 사용할 수 없고 앞에 getView 를 붙여주거나 inflater된 View의 변수명을 붙여주도록 합니다.
        // findViewById 은 xml 레이아웃에 정의되어있는 뷰를 가져오는 메소드 (참조 : https://yongku.tistory.com/entry/안드로이드-스튜디오Android-Studio-findViewById )
        String[] arr = {
                "http://openapi.gbis.go.kr/ws/rest/",
                "busstationservice",
                "",
                "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D",
                "&keyword=12"
        };
        String[] tags = {
                "stationId",
                "stationName",
                "regionName"
        };
        String endTag = "busStationList";

        try {
            busStationList = new GetApiData(arr, tags, endTag).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment2, null);
        setListView(view);
        return view;
    }

    public void setListView(View view){
        StationNameAdapter adapter = new StationNameAdapter(getActivity().getApplicationContext() ,busStationList);

        ListView listView = (ListView)view.findViewById(R.id.listView2);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // StationInfoActivity로 넘어감
                Intent intent = new Intent(getActivity().getApplicationContext(), StationInfoActivity.class);
                // 클릭 위치 탐색
                HashMap<String, String> data = busStationList.get(position);
                // putExtra 첫 인자는 식별 태그, 두번째는 다음 엑티비티에 넘길 정보
                intent.putExtra("stationId", data.get("stationId"));
                intent.putExtra("stationName", data.get("stationName"));
                intent.putExtra("regionName", data.get("regionName"));
                startActivity(intent);
            }
        });
    }
}
class StationNameAdapter extends BaseAdapter {
    private Context applicationContext;
    private ArrayList<HashMap<String, String>> array_data;

    private ViewHolder mViewHolder;

    public StationNameAdapter(Context applicationContext, ArrayList<HashMap<String, String>> busStationList) {
        this.applicationContext = applicationContext;
        this.array_data = busStationList;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(applicationContext).inflate(R.layout.station_name_list_item, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> data = array_data.get(position);
        mViewHolder.station_name_title.setText(data.get("stationName")); // 받아올 정보
        mViewHolder.station_name_text.setText(data.get("regionName"));

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