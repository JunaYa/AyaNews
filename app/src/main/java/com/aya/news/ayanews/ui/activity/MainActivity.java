package com.aya.news.ayanews.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.base.BaseActivity;
import com.aya.news.ayanews.ui.fragment_main.DiscoveryFragment;
import com.aya.news.ayanews.ui.fragment_main.NewsFragment;
import com.aya.news.ayanews.ui.fragment_main.PcFragment;
import com.aya.news.ayanews.ui.fragment_main.ReadFragment;
import com.aya.news.ayanews.ui.fragment_main.VaFragment;

/**
 * Created by Single on 2015/10/22.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG_NEWS = "news";
    private static final String TAG_READ = "read";
    private static final String TAG_VA = "va";
    private static final String TAG_DISCOVERY = "discovery";
    private static final String TAG_PC = "pc";

    private FragmentTabHost tabHost;
    private RadioButton tabNews, tabVa, tabDiscovery, tabRead, tabPc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        tabHost.setup(this, getSupportFragmentManager(), R.id.container);

        tabHost.addTab(tabHost.newTabSpec(TAG_NEWS).setIndicator(TAG_NEWS), NewsFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(TAG_READ).setIndicator(TAG_READ), ReadFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(TAG_VA).setIndicator(TAG_VA), VaFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(TAG_DISCOVERY).setIndicator(TAG_DISCOVERY), DiscoveryFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(TAG_PC).setIndicator(TAG_PC), PcFragment.class, null);

        tabHost.setOnTabChangedListener(new  TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                refreshTabBtn();
            }
        });

        tabNews = (RadioButton) findViewById(R.id.tab_news);
        tabRead = (RadioButton) findViewById(R.id.tab_read);
        tabVa = (RadioButton) findViewById(R.id.tab_va);
        tabDiscovery = (RadioButton) findViewById(R.id.tab_discovery);
        tabPc = (RadioButton) findViewById(R.id.tab_pc);

        tabNews.setOnClickListener(this);
        tabRead.setOnClickListener(this);
        tabVa.setOnClickListener(this);
        tabDiscovery.setOnClickListener(this);
        tabPc.setOnClickListener(this);
    }

    private void refreshTabBtn() {
        switch (tabHost.getCurrentTab()) {
            case 0: {
                tabNews.setChecked(true);
                break;
            }
            case 1: {
                tabRead.setChecked(true);
                break;
            }
            case 2: {
                tabVa.setChecked(true);
                break;
            }
            case 3: {
                tabDiscovery.setChecked(true);
                break;
            }
            case 4: {
                tabDiscovery.setChecked(true);
                break;
            }
            case 5: {
                tabPc.setChecked(true);
                break;
            }
        }
    }

    private boolean isExit = false;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isExit = false;
        }
    };

    public void setCurrentTabByTag(String tag) {
        tabHost.setCurrentTabByTag(tag);
    }


    @Override
    public void onBackPressed() {
        if (tabHost.getCurrentTab() != 0) {
            tabHost.setCurrentTab(0);
        } else if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再按一次退出AyaNews", Toast.LENGTH_LONG).show();
            handler.postDelayed(runnable, 2000);
        } else {
            handler.removeCallbacks(runnable);
            System.exit(0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_news:
                tabHost.setCurrentTab(0);
                break;
            case R.id.tab_read:
                tabHost.setCurrentTab(1);
                break;
            case R.id.tab_va:
                tabHost.setCurrentTab(2);
                break;
            case R.id.tab_discovery:
                tabHost.setCurrentTab(3);
                break;
            case R.id.tab_pc:
                tabHost.setCurrentTab(4);
                break;

        }
    }
}
