package com.example.businformapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Intent;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Fragment1 extends Fragment {
    private SearchView mSearchView;
    private ListView mListView;
    private RouteNameAdapter routeNameAdapter;

    private EditText edit;

    private String searvicePath = "http://openapi.gbis.go.kr/ws/rest/";
    private String serviceName = "busrouteservice";
    private String operation = "";
    private String serviceKey = "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D";
    private String areaId = "";
    private String params = "&keyword=";
    //    private String arr[];
//    private String tags[];
    String endTag = "";

    ArrayList<HashMap<String, String>> mapArrayList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override // MainActivity에서의 onCreate 메소드는 Fragment에서는 onCreateView 메소드에 작성합니다.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment에서는 MainActivity에서의 setContentView(R.layout.xml파일명) 대신 inflater가 존재합니다.
        // inflater는 xml로 정의된 view (또는 menu 등)를 실제 객체화 시키는 용도
        // 또한 setContentView의 부재로 findViewById(R.id.id명)은 바로 사용할 수 없고 앞에 getView 를 붙여주거나 inflater된 View의 변수명을 붙여주도록 합니다.
        // findViewById 은 xml 레이아웃에 정의되어있는 뷰를 가져오는 메소드 (참조 : https://yongku.tistory.com/entry/안드로이드-스튜디오Android-Studio-findViewById )

//        params += "11";
//        arr = new String[]{searvicePath, serviceName, operation, serviceKey, params};
        String[] arr = {
                "http://openapi.gbis.go.kr/ws/rest/",
                "busrouteservice",
                "",
                "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D",
                "&keyword=12"
        };
        String tags[] = {"routeId", "routeName", "routeTypeName", "districtCd"}; // 요청 태그
        endTag = "busRouteList";

        try {
            mapArrayList = new GetApiData(arr, tags, endTag).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        View view = inflater.inflate(R.layout.fragment1, null);

        setListView(view);
        return view;
    }
    public void setListView(View view) {
        // Fragment에서는 .this 대신 getActivity().getApplicationContext() 을 사용합니다.
        RouteNameAdapter adapter = new RouteNameAdapter(getActivity().getApplicationContext(), mapArrayList);

        ListView listView = (ListView)view.findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        // 클릭시 상세 정보가 나타남
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // RouteInfoActivity로 넘어감
                Intent intent = new Intent(getActivity().getApplicationContext(), RouteInfoActivity.class);
                // 클릭 위치 탐색
                HashMap<String, String> data = mapArrayList.get(position);
                // putExtra 첫 인자는 식별 태그, 두번째는 다음 엑티비티에 넘길 정보
                intent.putExtra("routeId", data.get("routeId"));
                intent.putExtra("routeName", data.get("routeName"));
                intent.putExtra("routeTypeName", data.get("routeTypeName"));
                intent.putExtra("districtCd", data.get("districtCd"));
                startActivity(intent);
            }
        });
    }
}

class RouteNameAdapter extends BaseAdapter {
    private Context applicationContext;
    private ArrayList<HashMap<String, String>> array_data;

    private ViewHolder mViewHolder;

    public RouteNameAdapter(Context applicationContext, ArrayList<HashMap<String, String>> mapArrayList) {
        this.applicationContext = applicationContext;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(applicationContext).inflate(R.layout.route_name_list_item, parent, false); // 리스트 뷰에 들어갈 아이템.xml 파일
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> data = array_data.get(position);
        mViewHolder.route_name_title.setText(data.get("routeName")); // 받아올 정보
        if (data.get("districtCd").equals("2"))
            mViewHolder.route_name_text.setText("경기도 " + data.get("routeTypeName"));

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