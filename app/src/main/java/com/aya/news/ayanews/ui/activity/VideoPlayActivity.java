package com.aya.news.ayanews.ui.activity;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aya.news.ayanews.R;
import com.aya.news.ayanews.common.ApplicationUtils;
import com.aya.news.ayanews.common.CompressionTools;
import com.aya.news.ayanews.common.DeviceUtils;
import com.aya.news.ayanews.common.ImageUtils;
import com.aya.news.ayanews.common.MediaUtils;
import com.aya.news.ayanews.common.PreferencesUtils;
import com.aya.news.ayanews.common.StringUtils;
import com.aya.news.ayanews.common.ToastUtils;
import com.aya.news.ayanews.config.Const;
import com.aya.news.ayanews.model.VP;
import com.aya.news.ayanews.service.PlayerService;
import com.aya.news.ayanews.ui.view.MediaController;
import com.aya.news.ayanews.ui.view.VideoView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.DataFormatException;

import io.vov.vitamio.utils.FileUtils;
import io.vov.vitamio.widget.OutlineTextView;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;

/**
 * Created by Single on 2015/11/18.
 */
public class VideoPlayActivity extends AppCompatActivity implements MediaController.MediaPlayerControl, VideoView.SurfaceCallback {

    public static final int RESULT_FAILED = -7;

    private static final IntentFilter USER_PRESENT_FILTER = new IntentFilter(Intent.ACTION_USER_PRESENT);
    private static final IntentFilter SCREEN_FILTER = new IntentFilter(Intent.ACTION_SCREEN_ON);
    private static final IntentFilter HEADSET_FILTER = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    private static final IntentFilter BATTERY_FILTER = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private IDanmakuView mDanmakuView;//弹幕控件
    private BaseDanmakuParser mParser;
    private String danmakuPath;
    private String av;
    private String page;
    private boolean isload = false;
    private boolean isfirst = true;
    private View startVideo;
    private TextView startVideoInfo;
    private String startText = "初始化播放器...";
    private ImageView biliAnim;
    private AnimationDrawable anim;


    private boolean mCreated = false;
    private boolean mNeedLock;
    private String mDisplayName;
    private String mBatteryLevel;
    private boolean mFromStart;
    private int mLoopCount;
    private boolean mSaveUri;
    private int mParentId;
    private float mStartPos;
    private boolean mEnd = false;
    private String mSubPath;
    private boolean mSubShown;
    private View mViewRoot;
    private VideoView mVideoView;
    private View mVideoLoadingLayout;
    private TextView mVideoLoadingText;
    private View mSubtitleContainer;
    private OutlineTextView mSubtitleText;
    private ImageView mSubtitleImage;
    private Uri mUri;
    private ScreenReceiver mScreenReceiver;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private UserPresentReceiver mUserPresentReceiver;
    private BatteryReceiver mBatteryReceiver;
    private boolean mIsHWCodec = false;
    private boolean mCloseComplete = false;
    private boolean mHeadsetPlaying = false;
    private boolean mReceiverRegistered = false;

    private MediaController mMediaController;
    private PlayerService vPlayer;
    private ServiceConnection vPlayerServiceConnection;

    private InputStream stream = null;
    private DanmakuContext danmakuContext;
    // private Animation mLoadingAnimation;
    // private View mLoadingProgressView;

    static {
        SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_OFF);
    }

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mRequestQueue = Volley.newRequestQueue(this);
        danmakuContext = new DanmakuContext();
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;

        vPlayerServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                vPlayer = ((PlayerService.LocalBinder) service).getService();
                mServiceConnected = true;
                if (mSurfaceCreated)
                    vPlayerHandler.sendEmptyMessage(OPEN_FILE);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                vPlayer = null;
                mServiceConnected = false;
            }
        };

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        parseIntent(getIntent());
        loadView(R.layout.activity_video_play);
        manageReceivers();

        findViews();

        mCreated = true;
//        startText = startText + "【完成】\n解析视频地址...【完成】\n全舰弹幕填装...";
//        startVideoInfo.setText(startText);
        mUri = Uri.parse(getIntent().getStringExtra(Const.ARG_VIDEO_URL));
//        new VideoViewInitTask().execute();
    }

    private void findViews() {
        mDanmakuView = (IDanmakuView) findViewById(R.id.sv_danmaku);
        startVideo = findViewById(R.id.video_start);
        startVideo.setVisibility(View.VISIBLE);
        startVideoInfo = (TextView) findViewById(R.id.video_start_info);
        biliAnim = (ImageView) findViewById(R.id.bili_anim);
        anim = (AnimationDrawable) biliAnim.getBackground();
        anim.start();
        danmakuContext.setDanmakuStyle(0, 3).setDuplicateMergingEnabled(false);
    }

    private BaseDanmakuParser createParser(InputStream stream) {
        if (stream == null) {
            return new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    private BaseDanmakuParser createParser(String uri) {

        if (uri == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://github.com/hongyangAndroid")
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                String htmlStr = response.body().string();
                try {
                    stream = new ByteArrayInputStream(CompressionTools.decompressXML(htmlStr.getBytes()));
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    private void attachMediaController() {
        if (mMediaController != null) {
            mNeedLock = mMediaController.isLocked();
            mMediaController.release();
        }
        mMediaController = new MediaController(this, mNeedLock);
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(mVideoView.getRootView());
        setFileName();
        setBatteryLevel();
    }

    private void setFileName() {
        if (mUri != null) {
            String name = null;
            if (mUri.getScheme() == null || mUri.getScheme().equals("file"))
                name = FileUtils.getName(mUri.toString());
            else
                name = mUri.getLastPathSegment();
            if (name == null)
                name = "null";
            if (mDisplayName == null)
                mDisplayName = name;
            mMediaController.setFileName(mDisplayName);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mCreated)
            return;
//		Intent serviceIntent = new Intent(this, PlayerService.class);
//		serviceIntent.putExtra("isHWCodec", mIsHWCodec);
//		bindService(serviceIntent, vPlayerServiceConnection,
//				Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mCreated)
            return;
        if (isInitialized()) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (!keyguardManager.inKeyguardRestrictedInputMode()) {
                startPlayer();
            }
        } else {
            if (mCloseComplete) {
                reOpen();
            }
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mCreated)
            return;
        if (isInitialized()) {
            //savePosition();
            if (vPlayer != null && vPlayer.isPlaying()) {
                stopPlayer();
            }
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mCreated)
            return;
        if (isInitialized()) {
            vPlayer.releaseSurface();
        }
        if (mServiceConnected) {
            unbindService(vPlayerServiceConnection);
            mServiceConnected = false;
        }
//        if (mDanmakuView != null) {
//            // dont forget release!
//            mDanmakuView.release();
//            mDanmakuView = null;
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mCreated)
            return;
        manageReceivers();
        if (isInitialized() && !vPlayer.isPlaying())
            release();
        if (mMediaController != null)
            mMediaController.release();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (isInitialized()) {
            setVideoLayout();
            attachMediaController();
        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",
                "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    private void loadView(int id) {
        setContentView(id);
        getWindow().setBackgroundDrawable(null);
        mViewRoot = findViewById(R.id.video_root);
        mVideoView = (VideoView) findViewById(R.id.video);
        mVideoView.initialize(this, this, mIsHWCodec);
        mSubtitleContainer = findViewById(R.id.subtitle_container);
        mSubtitleText = (OutlineTextView) findViewById(R.id.subtitle_text);
        mSubtitleImage = (ImageView) findViewById(R.id.subtitle_image);
        mVideoLoadingText = (TextView) findViewById(R.id.video_loading_text);
        mVideoLoadingLayout = findViewById(R.id.video_loading);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void parseIntent(Intent i) {
        av = i.getStringExtra("av");
        page = i.getStringExtra("page");
        mNeedLock = i.getBooleanExtra("lockScreen", false);
        mDisplayName = i.getStringExtra("displayName");
        mFromStart = i.getBooleanExtra("fromStart", false);
        mSaveUri = i.getBooleanExtra("saveUri", true);
        mStartPos = i.getFloatExtra("startPosition", -1.0f);
        mLoopCount = i.getIntExtra("loopCount", 1);
        mParentId = i.getIntExtra("parentId", 0);
        mSubPath = i.getStringExtra("subPath");
        mSubShown = i.getBooleanExtra("subShown", true);
        mIsHWCodec = i.getBooleanExtra("hwCodec", false);
    }

    private void manageReceivers() {
        if (!mReceiverRegistered) {
            mScreenReceiver = new ScreenReceiver();
            registerReceiver(mScreenReceiver, SCREEN_FILTER);
            mUserPresentReceiver = new UserPresentReceiver();
            registerReceiver(mUserPresentReceiver, USER_PRESENT_FILTER);
            mBatteryReceiver = new BatteryReceiver();
            registerReceiver(mBatteryReceiver, BATTERY_FILTER);
            mHeadsetPlugReceiver = new HeadsetPlugReceiver();
            registerReceiver(mHeadsetPlugReceiver, HEADSET_FILTER);
            mReceiverRegistered = true;
        } else {
            try {
                if (mScreenReceiver != null)
                    unregisterReceiver(mScreenReceiver);
                if (mUserPresentReceiver != null)
                    unregisterReceiver(mUserPresentReceiver);
                if (mHeadsetPlugReceiver != null)
                    unregisterReceiver(mHeadsetPlugReceiver);
                if (mBatteryReceiver != null)
                    unregisterReceiver(mBatteryReceiver);
            } catch (IllegalArgumentException e) {
            }
            mReceiverRegistered = false;
        }
    }

    private void mDisplayName() {
        if (mUri != null) {
            String name = null;
            if (mUri.getScheme() == null || mUri.getScheme().equals("file"))
                name = FileUtils.getName(mUri.toString());
            else
                name = mUri.getLastPathSegment();
            if (name == null)
                name = "null";
            if (mDisplayName == null)
                mDisplayName = name;
            mMediaController.setFileName(mDisplayName);
        }
    }

    private void applyResult(int resultCode) {
        vPlayerHandler.removeMessages(BUFFER_PROGRESS);
        Intent i = new Intent();
        i.putExtra("filePath", mUri.toString());
        if (isInitialized()) {
            i.putExtra("position", (double) vPlayer.getCurrentPosition() / vPlayer.getDuration());
            i.putExtra("duration", vPlayer.getDuration());
        }
        switch (resultCode) {
            case RESULT_FAILED:
                ToastUtils.show(this, R.string.video_cannot_play);
                break;
            case RESULT_CANCELED:
            case RESULT_OK:
                break;
        }
        setResult(resultCode, i);
    }

    private void resultFinish(int resultCode) {
        applyResult(resultCode);
        if (DeviceUtils.hasICS() && resultCode != RESULT_FAILED) {
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            finish();
        }
    }

    private void release() {
        if (vPlayer != null) {
            if (DeviceUtils.hasICS()) {
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                vPlayer.release();
                vPlayer.releaseContext();
            }
        }
    }

    private void reOpen(Uri path, String name, boolean fromStart) {
        if (isInitialized()) {
            //savePosition();
            vPlayer.release();
            vPlayer.releaseContext();
        }
        Intent i = getIntent();
        if (mMediaController != null) {
            i.putExtra("lockScreen", mMediaController.isLocked());
            i.putExtra("startPosition", PreferencesUtils.getFloat(this, mUri + VP.SESSION_LAST_POSITION_SUFIX, 7.7f));
            i.putExtra("fromStart", fromStart);
            i.putExtra("displayName", name);
            i.setData(path);
            parseIntent(i);
            mUri = path;
        }
        if (mViewRoot != null)
            mViewRoot.invalidate();
        if (mOpened != null)
            mOpened.set(false);
    }

    public void reOpen() {
        reOpen(mUri, mDisplayName, false);
    }

    protected void startPlayer() {
        if (isInitialized() && mScreenReceiver.screenOn
                && !vPlayer.isBuffering() && isload) {
            if (!vPlayer.isPlaying()) {
                vPlayer.start();
                if (mDanmakuView.isPaused())
                    mDanmakuView.resume();
                else
                    mDanmakuView.start();
            }
        }
    }

    protected void stopPlayer() {
        if (isInitialized()) {
            vPlayer.stop();
            if (mDanmakuView != null) {
                if (mDanmakuView.isPrepared() && mDanmakuView.isShown()) ;
                mDanmakuView.pause();
            }
        }
    }

    private void setBatteryLevel() {
        if (mMediaController != null)
            mMediaController.setBatteryLevel(mBatteryLevel);
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int percent = scale > 0 ? level * 100 / scale : 0;
            if (percent > 100)
                percent = 100;
            mBatteryLevel = String.valueOf(percent) + "%";
            setBatteryLevel();
        }
    }

    private class UserPresentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRootActivity()) {
                startPlayer();
            }
        }
    }

    private boolean isRootActivity() {
        return ApplicationUtils.isTopActivity(getApplicationContext(),
                getClass().getName());
    }

    public class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("state")) {
                int state = intent.getIntExtra("state", -1);
                if (state == 0) {
                    mHeadsetPlaying = isPlaying();
                    stopPlayer();
                } else if (state == 1) {
                    if (mHeadsetPlaying)
                        startPlayer();
                }
            }
        }
    }

    private class ScreenReceiver extends BroadcastReceiver {
        private boolean screenOn = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screenOn = false;
                stopPlayer();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                screenOn = true;
            }
        }
    }

    private void loadVPlayerPrefs() {
        if (!isInitialized())
            return;
        vPlayer.setBuffer(VP.DEFAULT_BUF_SIZE);
        vPlayer.setVideoQuality(VP.DEFAULT_VIDEO_QUALITY);
        vPlayer.setDeinterlace(VP.DEFAULT_DEINTERLACE);
        vPlayer.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME);
        vPlayer.setSubEncoding(VP.DEFAULT_SUB_ENCODING);
        MarginLayoutParams lp = (MarginLayoutParams) mSubtitleContainer
                .getLayoutParams();
        lp.bottomMargin = (int) VP.DEFAULT_SUB_POS;
        mSubtitleContainer.setLayoutParams(lp);
        vPlayer.setSubShown(mSubShown);
        setTextViewStyle(mSubtitleText);
        if (!TextUtils.isEmpty(mSubPath))
            vPlayer.setSubPath(mSubPath);
        if (mVideoView != null && isInitialized())
            setVideoLayout();
    }

    private void setTextViewStyle(OutlineTextView v) {
        v.setTextColor(VP.DEFAULT_SUB_COLOR);
        v.setTypeface(VP.getTypeface(VP.DEFAULT_TYPEFACE_INT),
                VP.DEFAULT_SUB_STYLE);
        v.setShadowLayer(VP.DEFAULT_SUB_SHADOWRADIUS, 0, 0,
                VP.DEFAULT_SUB_SHADOWCOLOR);
    }

    private boolean isInitialized() {
        return (mCreated && vPlayer != null && vPlayer.isInitialized());
    }

    private Handler mSubHandler = new Handler() {
        Bundle data;
        String text;
        byte[] pixels;
        int width = 0, height = 0;
        Bitmap bm = null;
        int oldType = SUBTITLE_TEXT;

        @Override
        public void handleMessage(Message msg) {
            data = msg.getData();
            switch (msg.what) {
                case SUBTITLE_TEXT:
                    if (oldType != SUBTITLE_TEXT) {
                        mSubtitleImage.setVisibility(View.GONE);
                        mSubtitleText.setVisibility(View.VISIBLE);
                        oldType = SUBTITLE_TEXT;
                    }
                    text = data.getString(VP.SUB_TEXT_KEY);
                    mSubtitleText.setText(text == null ? "" : text.trim());
                    break;
                case SUBTITLE_BITMAP:
                    if (oldType != SUBTITLE_BITMAP) {
                        mSubtitleText.setVisibility(View.GONE);
                        mSubtitleImage.setVisibility(View.VISIBLE);
                        oldType = SUBTITLE_BITMAP;
                    }
                    pixels = data.getByteArray(VP.SUB_PIXELS_KEY);
                    if (bm == null || width != data.getInt(VP.SUB_WIDTH_KEY)
                            || height != data.getInt(VP.SUB_HEIGHT_KEY)) {
                        width = data.getInt(VP.SUB_WIDTH_KEY);
                        height = data.getInt(VP.SUB_HEIGHT_KEY);
                        bm = Bitmap.createBitmap(width, height,
                                Bitmap.Config.ARGB_8888);
                    }
                    if (pixels != null)
                        bm.copyPixelsFromBuffer(ByteBuffer.wrap(pixels));
                    mSubtitleImage.setImageBitmap(bm);
                    break;
            }
        }
    };

    private AtomicBoolean mOpened = new AtomicBoolean(Boolean.FALSE);
    private boolean mSurfaceCreated = false;
    private boolean mServiceConnected = false;
    private Object mOpenLock = new Object();
    private static final int OPEN_FILE = 0;
    private static final int OPEN_START = 1;
    private static final int OPEN_SUCCESS = 2;
    private static final int OPEN_FAILED = 3;
    private static final int HW_FAILED = 4;
    private static final int LOAD_PREFS = 5;
    private static final int BUFFER_START = 11;
    private static final int BUFFER_PROGRESS = 12;
    private static final int BUFFER_COMPLETE = 13;
    private static final int CLOSE_START = 21;
    private static final int CLOSE_COMPLETE = 22;
    private static final int SUBTITLE_TEXT = 0;
    private static final int SUBTITLE_BITMAP = 1;
    private Handler vPlayerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_FILE:
                    synchronized (mOpenLock) {
                        if (!mOpened.get() && vPlayer != null) {
                            mOpened.set(true);
                            vPlayer.setVPlayerListener(vPlayerListener);
                            if (vPlayer.isInitialized())
                                mUri = vPlayer.getUri();

                            if (mVideoView != null)
                                vPlayer.setDisplay(mVideoView.getHolder());
                            if (mUri != null)
                                vPlayer.initialize(mUri, mDisplayName, mSaveUri, getStartPosition(), vPlayerListener, mParentId, mIsHWCodec);
                        }
                    }
                    break;
                case OPEN_START:
                    mVideoLoadingText.setText(R.string.video_layout_loading);
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    break;
                case OPEN_SUCCESS:
                    loadVPlayerPrefs();
                    setVideoLoadingLayoutVisibility(View.GONE);
                    setVideoLayout();
                    vPlayer.start();
                    if (mDanmakuView.isPaused())
                        mDanmakuView.resume();
                    else
                        mDanmakuView.start();
                    attachMediaController();
                    break;
                case OPEN_FAILED:
                    resultFinish(RESULT_FAILED);
                    break;
                case BUFFER_START:
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000);
                    break;
                case BUFFER_PROGRESS:
                    if (vPlayer.getBufferProgress() >= 100) {
                        setVideoLoadingLayoutVisibility(View.GONE);
                    } else {
                        mVideoLoadingText.setText(getString(
                                R.string.video_layout_buffering_progress,
                                vPlayer.getBufferProgress()));
                        vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS,
                                1000);
                        stopPlayer();
                    }
                    break;
                case BUFFER_COMPLETE:
                    setVideoLoadingLayoutVisibility(View.GONE);
                    vPlayerHandler.removeMessages(BUFFER_PROGRESS);
                    break;
                case CLOSE_START:
                    mVideoLoadingText.setText(R.string.closing_file);
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    break;
                case CLOSE_COMPLETE:
                    mCloseComplete = true;
                    break;
                case HW_FAILED:
                    if (mVideoView != null) {
                        mVideoView.setVisibility(View.GONE);
                        mVideoView.setVisibility(View.VISIBLE);
                        mVideoView.initialize(VideoPlayActivity.this, VideoPlayActivity.this, false);
                    }
                    break;
                case LOAD_PREFS:
                    loadVPlayerPrefs();
                    break;
            }
        }
    };

    private void setVideoLoadingLayoutVisibility(int visibility) {
        if (mVideoLoadingLayout != null) {
            mVideoLoadingLayout.setVisibility(visibility);
        }
    }

    private PlayerService.VPlayerListener vPlayerListener = new PlayerService.VPlayerListener() {
        @Override
        public void onHWRenderFailed() {
            if (Build.VERSION.SDK_INT < 11 && mIsHWCodec) {
                vPlayerHandler.sendEmptyMessage(HW_FAILED);
                vPlayerHandler.sendEmptyMessageDelayed(HW_FAILED, 200);
            }
        }

        @Override
        public void onSubChanged(String sub) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString(VP.SUB_TEXT_KEY, sub);
            msg.setData(b);
            msg.what = SUBTITLE_TEXT;
            mSubHandler.sendMessage(msg);
        }

        @Override
        public void onSubChanged(byte[] pixels, int width, int height) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putByteArray(VP.SUB_PIXELS_KEY, pixels);
            b.putInt(VP.SUB_WIDTH_KEY, width);
            b.putInt(VP.SUB_HEIGHT_KEY, height);
            msg.setData(b);
            msg.what = SUBTITLE_BITMAP;
            mSubHandler.sendMessage(msg);
        }

        @Override
        public void onOpenStart() {
            vPlayerHandler.sendEmptyMessage(OPEN_START);
        }

        @Override
        public void onOpenSuccess() {
            vPlayerHandler.sendEmptyMessage(OPEN_SUCCESS);
        }

        @Override
        public void onOpenFailed() {
            vPlayerHandler.sendEmptyMessage(OPEN_FAILED);
        }

        @Override
        public void onBufferStart() {
            vPlayerHandler.sendEmptyMessage(BUFFER_START);
            stopPlayer();
        }

        @Override
        public void onBufferComplete() {
            if (isfirst) {
                startVideo.setVisibility(View.GONE);
                anim.stop();
                isfirst = false;
            }
            vPlayerHandler.sendEmptyMessage(BUFFER_COMPLETE);
            if (vPlayer != null && !vPlayer.needResume())
                startPlayer();
        }

        @Override
        public void onPlaybackComplete() {
            mEnd = true;
            if (mLoopCount == 0 || mLoopCount-- > 1) {
                vPlayer.start();
                if (mDanmakuView.isPaused())
                    mDanmakuView.resume();
                else
                    mDanmakuView.start();
                vPlayer.seekTo(0);
            } else {
                resultFinish(RESULT_OK);
            }
        }

        @Override
        public void onCloseStart() {
            vPlayerHandler.sendEmptyMessage(CLOSE_START);
        }

        @Override
        public void onCloseComplete() {
            vPlayerHandler.sendEmptyMessage(CLOSE_COMPLETE);
        }

        @Override
        public void onVideoSizeChanged(int width, int height) {
            if (mVideoView != null) {
                setVideoLayout();
            }
        }

        @Override
        public void onDownloadRateChanged(int kbPerSec) {
            if (!MediaUtils.isNative(mUri.toString())
                    && mMediaController != null) {
                mMediaController.setDownloadRate(String.format("%dKB/s",
                        kbPerSec));
            }
        }

    };

    private int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;

    private void setVideoLayout() {
        mVideoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO,
                vPlayer.getVideoWidth(), vPlayer.getVideoHeight(),
                vPlayer.getVideoAspectRatio());
    }

    private void savePosition() {
        if (vPlayer != null && mUri != null) {
            PreferencesUtils.putString(this, mUri.toString(),
                    StringUtils.generateTime((int) (0.5 + vPlayer.getCurrentPosition())) + " / " + StringUtils.generateTime(vPlayer.getDuration()));
            if (mEnd)
                PreferencesUtils.putFloat(this, mUri + VP.SESSION_LAST_POSITION_SUFIX, 1.0f);
            else
                PreferencesUtils.putFloat(this, mUri + VP.SESSION_LAST_POSITION_SUFIX,
                        (float) (vPlayer.getCurrentPosition() / (double) vPlayer
                                .getDuration()));
        }
    }

    private float getStartPosition() {
        if (mFromStart)
            return 1.1f;
        if (mStartPos <= 0.0f || mStartPos >= 1.0f)
            return PreferencesUtils.getFloat(this, mUri + VP.SESSION_LAST_POSITION_SUFIX, 7.7f);
        return mStartPos;
    }

    @Override
    public int getBufferPercentage() {
        if (isInitialized())
            return (int) (vPlayer.getBufferProgress() * 100);
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (isInitialized())
            return vPlayer.getCurrentPosition();
        return (long) (getStartPosition() * vPlayer.getDuration());
    }

    @Override
    public long getDuration() {
        if (isInitialized())
            return vPlayer.getDuration();
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (isInitialized())
            return vPlayer.isPlaying();
        return false;
    }

    @Override
    public void pause() {
        if (isInitialized())
            vPlayer.stop();
        mDanmakuView.pause();
    }

    @Override
    public void seekTo(long arg0) {
        if (isInitialized())
            vPlayer.seekTo((float) ((double) arg0 / vPlayer.getDuration()));
        mDanmakuView.seekTo(arg0);
        mDanmakuView.pause();
    }

    @Override
    public void start() {
        if (isInitialized())
            vPlayer.start();
        if (mDanmakuView.isPaused())
            mDanmakuView.resume();
        else
            mDanmakuView.start();
    }

    @Override
    public void previous() {
    }

    @Override
    public void next() {
    }

    private static final int VIDEO_MAXIMUM_HEIGHT = 2048;
    private static final int VIDEO_MAXIMUM_WIDTH = 2048;

    @Override
    public float scale(float scaleFactor) {
        float userRatio = VP.DEFAULT_ASPECT_RATIO;
        int videoWidth = vPlayer.getVideoWidth();
        int videoHeight = vPlayer.getVideoHeight();
        float videoRatio = vPlayer.getVideoAspectRatio();
        float currentRatio = mVideoView.mVideoHeight / (float) videoHeight;

        currentRatio += (scaleFactor - 1);
        if (videoWidth * currentRatio >= VIDEO_MAXIMUM_WIDTH)
            currentRatio = VIDEO_MAXIMUM_WIDTH / (float) videoWidth;

        if (videoHeight * currentRatio >= VIDEO_MAXIMUM_HEIGHT)
            currentRatio = VIDEO_MAXIMUM_HEIGHT / (float) videoHeight;

        if (currentRatio < 0.5f)
            currentRatio = 0.5f;

        mVideoView.mVideoHeight = (int) (videoHeight * currentRatio);
        mVideoView.setVideoLayout(mVideoMode, userRatio, videoWidth,
                videoHeight, videoRatio);
        return currentRatio;
    }

    @Override
    public void snapshot() {
        if (!com.aya.news.ayanews.common.FileUtils.sdAvailable()) {
            ToastUtils.show(this, R.string.file_explorer_sdcard_not_available);
        } else {
            Uri imgUri = null;
            Bitmap bitmap = vPlayer.getCurrentFrame();
            if (bitmap != null) {
                File screenshotsDirectory = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + VP.SNAP_SHOT_PATH);
                if (!screenshotsDirectory.exists()) {
                    screenshotsDirectory.mkdirs();
                }

                File savePath = new File(
                        screenshotsDirectory.getPath()
                                + "/"
                                + new SimpleDateFormat("yyyyMMddHHmmss")
                                .format(new Date()) + ".jpg");
                if (ImageUtils.saveBitmap(savePath.getPath(), bitmap)) {
                    imgUri = Uri.fromFile(savePath);
                }
            }
            if (imgUri != null) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        imgUri));
                ToastUtils.show(this, getString(R.string.video_screenshot_save_in, imgUri.getPath()));
            } else {
                ToastUtils.show(this, R.string.video_screenshot_failed);
            }
        }
    }

    @Override
    public void toggleVideoMode(int mode) {
        mVideoMode = mode;
        setVideoLayout();
    }

    @Override
    public void showMenu() {

    }

    @Override
    public void stop() {
        onBackPressed();
    }

    @Override
    public long goForward() {
        return 0;
    }

    @Override
    public long goBack() {
        return 0;
    }

    @Override
    public void removeLoadingView() {
        mVideoLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
        mSurfaceCreated = true;
        if (mServiceConnected)
            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
        if (vPlayer != null)
            vPlayer.setDisplay(holder);
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width,
                                 int height) {
        if (vPlayer != null) {
            setVideoLayout();
        }
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
        if (vPlayer != null && vPlayer.isInitialized()) {
            if (vPlayer.isPlaying()) {
                vPlayer.stop();
                vPlayer.setState(PlayerService.STATE_NEED_RESUME);
            }
            vPlayer.releaseSurface();
            if (vPlayer.needResume()) {
                vPlayer.start();
                if (mDanmakuView.isPaused())
                    mDanmakuView.resume();
                else
                    mDanmakuView.start();
            }

        }
    }

    @Override
    public void setDanmakushow(boolean isShow) {
        // TODO Auto-generated method stub
        if (mDanmakuView != null) {
            if (isShow) {
                mDanmakuView.show();
            } else {
                mDanmakuView.hide();
            }
        }
    }

    private class VideoViewInitTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            JsonObjectRequest videopathjson = new JsonObjectRequest("http://www.bilibili.com/m/html5?aid=" + av + "&page=" + page, null,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                                danmakuPath = response.getString("cid").toString();
                                mUri = Uri.parse(response.getString("src").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            mRequestQueue.add(videopathjson);
            mParser = createParser(danmakuPath);
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mParser != null) {
                mDanmakuView.prepare(mParser, new DanmakuContext());
                mDanmakuView.showFPS(false);
                mDanmakuView.enableDanmakuDrawingCache(false);
            } else {
                startText = startText + "【失败】\n视频缓冲中...";
                startVideoInfo.setText(startText);
            }
            isload = true;
            Intent serviceIntent = new Intent(getApplicationContext(), PlayerService.class);
            serviceIntent.putExtra("isHWCodec", mIsHWCodec);
            bindService(serviceIntent, vPlayerServiceConnection, Context.BIND_AUTO_CREATE);
            startText = startText + "【完成】\n视频缓冲中...";
            startVideoInfo.setText(startText);
        }
    }

    /*退出的间隔时间 */
    private static final long EXIT_INTERVAL_TIME = 2000;
    private long touchTime = 0;

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= EXIT_INTERVAL_TIME) {
                Toast.makeText(VideoPlayActivity.this, "再按一下退出喵(｡･ω･｡)~", Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
                finish();
                if (mDanmakuView != null)
                    mDanmakuView.release();
            }

            return false;
        } else {
            return true;
        }
    }

}
