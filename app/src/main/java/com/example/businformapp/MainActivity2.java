package com.example.businformapp;

import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private int tabPosition;

    Fragment fragment1, fragment2;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
//        ViewPager viewPager = findViewById(R.id.view_pager);
//        viewPager.setAdapter(sectionsPagerAdapter);
//        TabLayout tabs = findViewById(R.id.tabs);
//        tabs.setupWithViewPager(viewPager);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();

        getSupportFragmentManager().beginTransaction().add(R.id.frame, fragment1).commit(); // 초기화면 설정

        TabLayout tabs = findViewById(R.id.tabs);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                if (position == 0) {
                    selected = fragment1;
                } else if (position == 1) {
                    selected = fragment2;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit(); // 탭 전환
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("Tab: " + tabPosition + "\t" + query);
                bundle.putString("query", query);
                if (tabPosition == 0) {
                    fragment1 = new Fragment1();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment1).commit(); // 탭 전환
                    fragment1.setArguments(bundle);
                }
                else
                    fragment2 = new Fragment2();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment2).commit(); // 탭 전환
                fragment2.setArguments(bundle);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bundle.putString("query", newText);
                if (tabPosition == 0) {
                    fragment1 = new Fragment1();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment1).commit(); // 탭 전환
                    fragment1.setArguments(bundle);
                }
                else
                    fragment2 = new Fragment2();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment2).commit(); // 탭 전환
                fragment2.setArguments(bundle);
                return true;
            }
        });

    }
}