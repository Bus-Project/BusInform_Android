package com.example.businformapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class LocationSearchActivity extends AppCompatActivity {
    private LocationTracker lt;
    private GetApiData getApiData;

    private ArrayList<String []> array_data;
    private ListView mListView;
    private NearStationAdapter mNearStationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        mListView = (ListView) findViewById(R.id.near_station_list);

        lt = new LocationTracker(this);

        array_data = new ArrayList<>();

        APIThread thread = new APIThread();
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Thread", e.toString());
        }
        System.out.println(array_data.size());
        mNearStationAdapter = new NearStationAdapter(LocationSearchActivity.this, array_data);
        mListView.setAdapter(mNearStationAdapter);
    }

    private class APIThread extends Thread {
        public void run() {
            Log.i("API", "X: " + lt.getLong() + ", Y: " + lt.getLat());
            getApiData = new GetApiData("busstationservice", "searcharound", "&x=" + lt.getLong() + "&y=" + lt.getLat());

            boolean isRegionName = false, isStationId = false, isStationName = false;
            String regionName = "", stationId = "", stationName = "";

            XmlPullParser parser = Xml.newPullParser();
            try {
                parser.setInput(getApiData.getData(), null);

                int parserEvent = parser.getEventType();
                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG:
                            String tagName = parser.getName();

                            if (tagName.equals("regionName")) {
                                isRegionName = true;
                            }
                            else if (tagName.equals("stationId")) {
                                isStationId = true;
                            }
                            else if (tagName.equals("stationName")) {
                                isStationName = true;
                            }
                            break;
                        case XmlPullParser.TEXT:
                            if (isRegionName) {
                                regionName = parser.getText();
                                isRegionName = false;
                            }
                            else if (isStationId) {
                                stationId = parser.getText();
                                isStationId = false;
                            }
                            else if (isStationName) {
                                stationName = parser.getText();
                                isStationName = false;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("busStationAroundList")) {
                                array_data.add(new String[] {
                                   regionName, stationId, stationName
                                });
                            }
                            break;
                    }
                    parserEvent = parser.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Parsing", e.toString());
            } catch (NullPointerException e) {
                Log.e("API", e.toString());
            }
        }
    }
}

class NearStationAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<String []> array_data;

    private ViewHolder mViewHolder;

    public NearStationAdapter(Context mContext, ArrayList<String []> array_data) {
        this.mContext = mContext;
        this.array_data = array_data;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.near_station_list_item, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String [] data = array_data.get(position);
        mViewHolder.near_station_title.setText(data[2] + " (" + data[1] + ")");
        mViewHolder.near_station_text.setText(data[0]);

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
