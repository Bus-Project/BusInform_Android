package com.example.businformapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LocationSearchActivity extends AppCompatActivity {
    private LocationTracker lt;

    private ArrayList<HashMap<String, String>> stationData;
    private ListView mListView;
    private NearStationAdapter mNearStationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        mListView = (ListView) findViewById(R.id.near_station_list);

        lt = new LocationTracker(this);
        Log.i("Location", "X: " + lt.getLong() + ", Y: " + lt.getLat());

        stationData = new ArrayList<>();

        String[] arr = {
                "http://openapi.gbis.go.kr/ws/rest/",
                "busstationservice",
                "searcharound",
                "serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D",
                "&x=" + lt.getLong() + "&y=" + lt.getLat()
        };
        String[] tags = {
                "regionName",
                "stationId",
                "stationName"
        };
        String endTag = "busStationAroundList";

        try {
            stationData = new GetApiData(arr, tags, endTag).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("API", "Array size: " + stationData.size());

        mNearStationAdapter = new NearStationAdapter(LocationSearchActivity.this, stationData);
        mListView.setAdapter(mNearStationAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ListView", position + "번 선택");

                Intent intent = new Intent(LocationSearchActivity.this, StationInfoActivity.class);

                HashMap<String, String> data = stationData.get(position);

                JsonDataManager jsonManager = new JsonDataManager(LocationSearchActivity.this);
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

                for (Map.Entry<String, String> e: data.entrySet()) {
                    intent.putExtra(e.getKey(), e.getValue());
                }

                startActivity(intent);
            }
        });
    }
}

class NearStationAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<HashMap<String, String>> stationData;

    private ViewHolder mViewHolder;

    public NearStationAdapter(Context mContext, ArrayList<HashMap<String, String>> stationData) {
        this.mContext = mContext;
        this.stationData = stationData;
    }

    @Override
    public int getCount() {
        return stationData.size();
    }

    @Override
    public Object getItem(int position) {
        return stationData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.near_station_list_item, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> data = stationData.get(position);
        mViewHolder.near_station_title.setText(data.get("stationName") + " (" + data.get("stationId") + ")");
        mViewHolder.near_station_text.setText(data.get("regionName"));

        return convertView;
    }

    private class ViewHolder {
        private TextView near_station_title;
        private TextView near_station_text;

        public ViewHolder(View convertView) {
            near_station_title = (TextView) convertView.findViewById(R.id.near_station_title);
            near_station_text = (TextView) convertView.findViewById(R.id.near_station_text);
        }
    }
}
