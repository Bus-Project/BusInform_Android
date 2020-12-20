package com.example.businformapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment2 extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override  // MainActivity에서의 onCreate 메소드는 Fragment에서는 onCreateView 메소드에 작성합니다.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment에서는 MainActivity에서의 setContentView(R.layout.xml파일명) 대신 inflater가 존재합니다.
        // inflater는 xml로 정의된 view (또는 menu 등)를 실제 객체화 시키는 용도
        // 또한 setContentView의 부재로 findViewById(R.id.id명)은 바로 사용할 수 없고 앞에 getView 를 붙여주거나 inflater된 View의 변수명을 붙여주도록 합니다.
        // findViewById 은 xml 레이아웃에 정의되어있는 뷰를 가져오는 메소드 (참조 : https://yongku.tistory.com/entry/안드로이드-스튜디오Android-Studio-findViewById )
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        return rootView;
    }

}
