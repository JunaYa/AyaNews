package com.aya.news.ayanews.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.config.Const;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Single on 2015/11/18.
 */
public class VideoPlay2Activity extends AppCompatActivity {
    private VideoView videoView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play2);
        url = getIntent().getStringExtra(Const.ARG_VIDEO_URL);
        videoView = (VideoView) findViewById(R.id.video_view);
        if (url != null && !url.equals("")) {
            videoView.setVideoPath(url);
            videoView.setMediaController(new MediaController(this));
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setPlaybackSpeed(1.0f);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

}
