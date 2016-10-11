package com.bcgtgjyb.multidextool.tool;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by bigwen on 16/10/11.
 */

public class DexLoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadDex();
    }

    private void initView() {
        RelativeLayout rootLay = new RelativeLayout(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLay.setLayoutParams(params);
        rootLay.setBackgroundColor(Color.parseColor("#000000"));
        TextView textView = new TextView(this);
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setText("正在加载...");
        RelativeLayout.LayoutParams txParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textView.setLayoutParams(txParams);
        rootLay.addView(textView);
        setContentView(rootLay);
    }

    private void loadDex() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                MultiDex.install(DexLoadActivity.this);
                return "";
            }

            @Override
            protected void onPostExecute(Object o) {
                DexLoadUtil.finishDexLoad(getApplication());
                finish();
                System.exit(0);
            }
        };
        asyncTask.execute("");
    }

}
