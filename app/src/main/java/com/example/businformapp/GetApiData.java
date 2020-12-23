package com.example.businformapp;
import android.os.AsyncTask;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class GetApiData extends AsyncTask<Void,Void,Vector<String>> {

    private static boolean ifrouteId = false, ifrouteName = false, ifrouteTypeCd = false, ifregionName = false, ifdistricCd = false, ifrouteTypeName = false, addtext = false;
    private static String routeId = null, routeName = null, routeTypeCd = null, routeTypeName = null, regionName = null, districCd = null;

    String servicePath = "http://openapi.gbis.go.kr/ws/rest/";
    String serviceName = "";
    String operation = "";
    String serviceKey = "";
    String areaId = "";
    String params = "";
    Vector<String> v = new Vector<>();

    public GetApiData(String[] arr) {
        this.serviceName = arr[0];
        this.operation = arr[1];
        this.serviceKey = arr[2];
        this.areaId = arr[3];
        this.params = arr[4];
    }

    @Override
    protected Vector<String> doInBackground(Void ... strings) {
        URL url = null;
        try {
            url = new URL(servicePath + serviceName + serviceKey + params);
            System.out.println(url);
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
//                            routeView.setText(routeView.getText()+"노선ID : " + routeId + "\n 노선 번호 : " + routeName
//                                    + "\n 노선 유형 : " + routeTypeCd + "\n 노선 유형명 : " + routeTypeName
//                                    + "\n 지역명 : " + regionName + "\n 관할지역 : " + districCd);
                            v.add("노선ID : " + routeId + "\n 노선 번호 : " + routeName
                                    + "\n 노선 유형 : " + routeTypeCd + "\n 노선 유형명 : " + routeTypeName
                                    + "\n 지역명 : " + regionName + "\n 관할지역 : " + districCd);
                            addtext = false;
                        }
                        break; // </regionName> 같이 무시해도 되는 것은 그냥 패스
                }
                parserEvent = parser.next(); // --2. 구리,남양주,서울,포천 --> 이제 TEXT가 되었다.
            }
        } catch (XmlPullParserException e) {
            System.out.println("XmlPullParserException----------------");
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException-----------------");
        } catch (IOException e) {
            System.out.println("IOException--------------------------");
        }
        return v;
    }
}

