package com.example.tatthood;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayDeque;
import java.util.Deque;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Deque<Integer> integerDeque = new ArrayDeque<>(5);
    boolean flag = true;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //variables assignment
        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //add Home fragment in deque list
        integerDeque.push(R.id.bn_home);

        //Load home fragment
        loadFragment(new Home());

        //set home as default fragment
        bottomNavigationView.setSelectedItemId(R.id.bn_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //get item selected item id
                int id = item.getItemId();
                // Check condition
                if(integerDeque.contains(id)) {
                    //When deque List contains selected id
                    //Check condition
                    if(id == R.id.bn_home){
                        //When selected id is equal to home fragment id
                        //Check condition
                        if(integerDeque.size() !=1){
                            //When deque list size is not equal to 1
                            //Check condition
                            if (flag) {
                                //When flag value is true
                                // add home fragment in deque list
                                integerDeque.addFirst(R.id.bn_home);
                                //set flag is equal to false
                                flag = false ;
                            }
                        }
                    }
                    // Remove selected id from deque list
                    integerDeque.remove(id);
                }
                //push selected id in deque list
                integerDeque.push(id);
                //Load Fragment
                loadFragment(getFragment(item.getItemId()));
                //return true
                return true;
            }
        });

    }

    private Fragment getFragment(int itemId) {
        switch (itemId) {
            case R.id.bn_gallery:
                //set checked gallery fragment
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                // return gallery fragment
                return new Gallery();

            case R.id.bn_home:
                //set checked gallery fragment
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                // return gallery fragment
                return new Home();


            case R.id.bn_market:
                //set checked gallery fragment
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                // return gallery fragment
                return new Market();

            case R.id.bn_notifications:
                //set checked gallery fragment
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                // return gallery fragment
                return new Notifications();

            case R.id.bn_hood_map:
                //set checked gallery fragment
                bottomNavigationView.getMenu().getItem(4).setChecked(true);
                // return gallery fragment
                return new Hood_map();
        }
        // set checked default home fragment
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        return  new Home();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment,fragment,fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        integerDeque.pop();
        // check condition
        if(!integerDeque.isEmpty()){
            //when deque list is not empty
            // load fragment
            loadFragment(getFragment(integerDeque.peek()));
        } else {
            //when deque list is empty
            //finish activity
            finish();
        }
    }
}