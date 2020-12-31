package com.example.businformapp.ui.home;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.businformapp.R;
import com.example.businformapp.RouteInfoActivity;
import com.example.businformapp.StationInfoActivity;
import com.example.businformapp.TypeAndRegionCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ArrayList<HashMap<String, String>> arrayData = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arrayData2 = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setListView(root);

        return root;
    }

    public void setListView(View view) {
        HomeAdapter adapter1 = new HomeAdapter(requireActivity().getApplicationContext(), arrayData, "Route");
        HomeAdapter adapter2 = new HomeAdapter(requireActivity().getApplicationContext(), arrayData2, "Station");

        ListView listView1 = view.findViewById(R.id.home_route_list);
        listView1.setAdapter(adapter1);
        ListView listView2 = view.findViewById(R.id.home_station_list);
        listView2.setAdapter(adapter2);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(requireContext().getApplicationContext(), RouteInfoActivity.class);
                HashMap<String, String> data = arrayData.get(position);

                for (Map.Entry<String, String> e: data.entrySet()) {
                    intent.putExtra(e.getKey(), e.getValue());
                }
                startActivity(intent);
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(requireContext().getApplicationContext(), StationInfoActivity.class);
                HashMap<String, String> data = arrayData2.get(position);

                for (Map.Entry<String, String> e: data.entrySet()) {
                    intent.putExtra(e.getKey(), e.getValue());
                }
                startActivity(intent);
            }
        });
    }
}

class HomeAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<HashMap<String, String>> arrayData;
    private final String mode;

    private ViewHolder mViewHolder;

    public HomeAdapter(Context mContext, ArrayList<HashMap<String, String>> arrayData, String mode) {
        this.mContext = mContext;
        this.arrayData = arrayData;
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return arrayData.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.home_frag_list_item, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> data = arrayData.get(position);
        if (mode.equals("Route")) {
            mViewHolder.home_frag_title.setText(data.get("routeName"));

            String idCode = data.get("routeTypeCd");
            String regionCode = data.get("districtCd");

            TypeAndRegionCode typeAndRegionCode = new TypeAndRegionCode(idCode, regionCode);
            regionCode = typeAndRegionCode.getRegionName();
            idCode = typeAndRegionCode.getRouteType();

            mViewHolder.home_frag_text.setText(regionCode + " " + idCode);
        } else {
            mViewHolder.home_frag_title.setText(data.get("stationName"));
            mViewHolder.home_frag_text.setText(data.get("regionName"));
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView home_frag_title;
        private TextView home_frag_text;

        public ViewHolder(View convertView) {
            home_frag_title = convertView.findViewById(R.id.home_frag_title);
            home_frag_text = convertView.findViewById(R.id.home_frag_text);
        }
    }
}
