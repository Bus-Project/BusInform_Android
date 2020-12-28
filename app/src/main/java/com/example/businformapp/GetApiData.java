package com.example.businformapp;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class GetApiData extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
    private final String servicePath, serviceName, operation, serviceKey, params;
    private final String [] tags;
    private final String endTag;
    private boolean [] isElements;

    private HashMap<String, String> map = new HashMap<>();
    private ArrayList<HashMap<String, String>> mapArrayList = new ArrayList<>();

    public GetApiData(String[] arr, String[] tags, String endTag) {
        this.servicePath = arr[0];
        this.serviceName = arr[1];
        this.operation = arr[2];
        this.serviceKey = arr[3];
        this.params = arr[4];

        this.tags = tags;
        this.endTag = endTag;

        isElements = new boolean[tags.length];
        for (boolean e: isElements) {
            e = false;
        }
    }

    private void xmlParse(URL url) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();

                        for (int i = 0; i < tags.length; i++) {
                            if (tagName.equals(tags[i])) {
                                isElements[i] = true;
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        for (int i = 0; i < isElements.length; i++) {
                            if (isElements[i]) {
                                map.put(tags[i], parser.getText());
                                isElements[i] = false;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(endTag)) {
                            mapArrayList.add(new HashMap<>(map));
                            map.clear();
                        }
                        break;
                }
                parserEvent = parser.next();
            }

        } catch (IOException e) {
            Log.e("URL", e.toString());
        } catch (XmlPullParserException e) {
            Log.e("Parsing", e.toString());
        }
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void ... v) {
        URL url = null;
        try {
            if (operation.isEmpty()) {
                url = new URL(servicePath + serviceName + '?' + serviceKey + params);
            }
            else {
                url = new URL(servicePath + serviceName + '/' + operation + '?' + serviceKey + params);
            }
            xmlParse(url);
        } catch (MalformedURLException e) {
            Log.e("URL", e.toString());
        }

        return mapArrayList;
    }
}
