package com.example.businformapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.URL;

public class Fragment1 extends Fragment {
    TextView routeView;

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
        View view = inflater.inflate(R.layout.fragment1, null);

        StrictMode.enableDefaults();
        // 파싱 데이터
        routeView = (TextView) view.findViewById(R.id.result_bus);

        boolean ifrouteId = false, ifrouteName = false, ifrouteTypeCd = false, ifregionName = false, ifdistricCd = false, ifrouteTypeName = false, addtext = false;
        String routeId = null, routeName = null, routeTypeCd = null, routeTypeName = null, regionName = null, districCd = null;

        String urlpath = "http://openapi.gbis.go.kr/ws/rest/busrouteservice?serviceKey=";
        String key = "2LGrVBKRbUxVD5dXYkOPLb9Sar7XnzXiJ4REz2%2FS60MTHKOjsVBL7ZL6wKMrBomsdEVmDHmH9xW7J2hvtgllxA%3D%3D";
        String target = "&keyword=11";

        try {
            URL url = new URL(urlpath + key + target);
            InputStream ins = url.openStream();

            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();


            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG: // 처음이라 <response> 태그를 파싱
                        // 두번째로는 <comMsgHeader>
                        // 세번째로는 <errMsg>
                        // -1. <regionName>
                        if (parser.getName().equals("routeId")) {
                            System.out.println("---------출력" + parser.getName());
                            ifrouteId = true;
                        }
                        if (parser.getName().equals("routeName")) {
                            ifrouteName = true;
                        }
                        if (parser.getName().equals("routeTypeCd")) {
                            ifrouteTypeCd = true;
                        }
                        if (parser.getName().equals("routeTypeName")) {
                            ifrouteTypeName = true;
                        }
                        if (parser.getName().equals("regionName")) { // --1. <regionName> 태그를 만났으므로
                            ifregionName = true; // --1. 텍스트 파싱을 위해 true로 변경
                        }
                        if (parser.getName().equals("districCd")) {
                            ifdistricCd = true;
                        }
                        break; // <response> 이므로 패스. <comMsgHeader>이므로 패스, <errMsg>이므로 패스
                    // ~~~~ if 문에 있는 태그가 나올 때까지 반복

                    case XmlPullParser.TEXT: //parser가 내용에 접근함 --1. <regionName>은 TEXT가 아니므로 패스
                        // --2. 구리,남양주,서울,포천 의 TEXT가 들어왔으므로 작업 수행
                        if (ifrouteId) {
                            routeId = parser.getText();
                            ifrouteId = false;
                        }
                        if (ifrouteName) {
                            routeName = parser.getText();
                            ifrouteName = false;
                        }
                        if (ifrouteTypeCd) {
                            routeTypeCd = parser.getText();
                            ifrouteTypeCd = false;
                        }
                        if (ifrouteTypeName) {
                            routeTypeName = parser.getText();
                            ifrouteTypeName = false;
                        }
                        if (ifregionName) {  // --3. 전과정에서 true로 변경했으므로 작업 진행
                            regionName = parser.getText();
                            // --3. <regionName>구리,남양주,서울,포천</regionName> 였으므로 구리,남양주,서울,포천 파싱
                            ifregionName = false; // 다음 파싱을 위해 false로 변경
                        }
                        if (ifdistricCd) {
                            districCd = parser.getText();
                            ifdistricCd = false;
                        }
                        break;

                    case XmlPullParser.END_TAG: // </regionName> 처럼 닫기 태그를 만날 때
                        if (parser.getName().equals("busRouteList")) { // </busRouteList> 이라면
                            routeView.setText(routeView.getText()+"노선ID : " + routeId + "\n 노선 번호 : " + routeName
                                    + "\n 노선 유형 : " + routeTypeCd + "\n 노선 유형명 : " + routeTypeName
                                    + "\n 지역명 : " + regionName + "\n 관할지역 : " + districCd);
                            addtext = false;
                        }
                        break; // </regionName> 같이 무시해도 되는 것은 그냥 패스
                }
                parserEvent = parser.next(); // --2. 구리,남양주,서울,포천 --> 이제 TEXT가 되었다.
            }
        } catch (Exception e) {
            routeView.setText("에러가..났습니다…");
        }

        return view;
    }
}