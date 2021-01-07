package com.example.businformapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class Fragment2 extends Fragment {
    private SearchView mSearchView;
    private ListView mListView;
    private ArrayList<HashMap<String, String>> busStationList;
    private String params = "&keyword=";
    private Bundle bundle = new Bundle();
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Nullable
    @Override // MainActivity에서의 onCreate 메소드는 Fragment에서는 onCreateView 메소드에 작성합니다.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            bundle = getArguments();
            String arg = bundle.getString("query");
            System.out.println("———————— >> " + arg);
            params  += arg;
        }
        catch (NullPointerException e) {
            System.out.println("정보 없음");
        }

        String[] arr = {
                "http://openapi.gbis.go.kr/ws/rest/",
                "busstationservice",
                "",
                "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D",
                params
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

                JsonDataManager jsonManager = new JsonDataManager(requireContext());
                JSONArray jsArray = jsonManager.getData("Station");

                if (jsArray != null) {
                    Log.i("JSON", "Loaded: " + jsArray.toString());

                    boolean hasName = false;
                    for (int i = 0; i < jsArray.length(); i++) {
                        try {
                            JSONObject obj = jsArray.getJSONObject(i);
                            if (obj.get("stationId").equals(data.get("stationId"))) {
                                hasName = true;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!hasName) {
                        jsArray.put(new JSONObject(data));
                        jsonManager.setData(jsArray, "Station");
                    }
                }
                else {
                    jsArray = new JSONArray();
                    jsArray.put(new JSONObject(data));
                    jsonManager.setData(jsArray, "Station");
                }
                Log.i("JSON", "Saved: " + data.toString());

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
            convertView = LayoutInflater.from(applicationContext).inflate(R.layout.station_name_list_item2, parent, false);
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