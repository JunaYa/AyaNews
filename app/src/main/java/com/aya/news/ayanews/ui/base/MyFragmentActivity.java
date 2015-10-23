package com.aya.news.ayanews.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.aya.news.ayanews.R;

/**
 * Created by Single on 2015/10/23.
 */
public class MyFragmentActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fragment);

        try{
            Fragment fragment = (Fragment)Class.forName(getIntent().getStringExtra("RootFragment")).newInstance();
            Bundle args = getIntent().getExtras();
            if(args != null)  fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getFragments().size() > 0){
            Fragment fragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (fragment instanceof BaseFragment){
                if (((BaseFragment)fragment).onBackPressed()){
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    public void startFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.side_in_from_right,R.anim.side_out_from_left,R.anim.side_in_from_left,R.anim.side_out_from_right);
        transaction.replace(R.id.container,fragment);
        transaction.commit();
    }

    public void startFragment(String fragmentName, Bundle args) {
        Intent intent = new Intent(this, MyFragmentActivity.class);
        intent.putExtra("RootFragment", fragmentName);
        if (args != null) intent.putExtras(args);
        startActivity(intent);
    }
}
