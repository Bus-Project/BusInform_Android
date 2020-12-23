package com.example.businformapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class Fragment1 extends Fragment {

    private String searvicePath = "http://openapi.gbis.go.kr/ws/rest/";
    private String serviceName = "busrouteservice";
    private String operation = "";
    private String serviceKey = "?serviceKey=2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D";
    private String areaId = "";
    private String params = "&keyword=11";
    String arr[] = {serviceName, operation, serviceKey, areaId, params};
    private Vector<String> routeNameList = new Vector<>();

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

        GetApiData task = new GetApiData(arr);
        try {
            routeNameList = task.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment1, null);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,routeNameList);

        ListView listView = (ListView)view.findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                onItemClick() 함수에서 ListView 아이템인 TextView의 텍스트를 가져오려면
                parent.getItemAtPosition() 함수를 사용하면 됩니다.
                 */
                String strText = (String) parent.getItemAtPosition(position);
            }
        });
        return view;
    }
}