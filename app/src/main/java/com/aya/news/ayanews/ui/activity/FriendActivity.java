package com.aya.news.ayanews.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.config.Urls;
import com.aya.news.ayanews.okhttp.OkHttpPostRequest;
import com.aya.news.ayanews.okhttp.ResultCallback;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Single on 2015/11/20.
 */
public class FriendActivity extends AppCompatActivity {
    private EditText editText;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        editText = (EditText) findViewById(R.id.edit_text);
        data = editText.getText().toString();
        findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Urls.JPUSH_BASE_URL;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("platform", "all");
                    jsonObject.put("audience", "all");
                    JSONObject notificationObj = new JSONObject();
                    JSONObject androidObj = new JSONObject();
                    androidObj.put("alert", "今天强降雪，注意安全");
                    notificationObj.put("android", androidObj);
                    jsonObject.put("notification", notificationObj);
                    new OkHttpPostRequest.Builder()
                            .url(url)
                            .addHeader("Authorization", Urls.BASE_64)
                            .addHeader("Content-Type", "application/json")
                            .addParams("",jsonObject.toString())
                            .post(new ResultCallback<String>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    Log.d("aya", e.getMessage().toString());
                                }

                                @Override
                                public void onResponse(String response) {
                                    Log.d("aya", response);
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
