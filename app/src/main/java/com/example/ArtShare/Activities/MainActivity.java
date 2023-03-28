package com.example.ArtShare.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.ArtShare.Fragments.FavouritesFragment;
import com.example.ArtShare.Fragments.HomeFragment;
import com.example.ArtShare.Fragments.SearchFragment;
import com.example.ArtShare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final String TARGET = "target";

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        String target = getIntent().getStringExtra(TARGET);

        if (target != null) {
            if (target.equals("search")) {
                bottomNavigationView.setSelectedItemId(R.id.nav_search);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, new SearchFragment()).commit();
            } else {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, new HomeFragment()).commit();
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container, new HomeFragment()).commit();
        }
    }

    public void showMyAccountActivity(MenuItem item) {
        startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.nav_favourites:
                        selectedFragment = new FavouritesFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_container, selectedFragment).commit();
                }

                return true;
            };
}