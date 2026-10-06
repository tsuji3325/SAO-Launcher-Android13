package com.tsuji3325.saolauncher;

import android.app.*;
import android.os.*;
import android.provider.Settings;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    private TextView status;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setPadding(48,64,48,48);
        TextView title=new TextView(this); title.setText("SAO Launcher A13"); title.setTextSize(28); root.addView(title);
        TextView info=new TextView(this);
        info.setText("\nAndroid 13+向けテスト版\n\n画面左端の中央付近から右へスワイプするとメニューを表示します。");
        info.setTextSize(17); root.addView(info);
        status=new TextView(this); status.setPadding(0,32,0,24); root.addView(status);
        Button permission=new Button(this); permission.setText("① オーバーレイ権限を許可");
        permission.setOnClickListener(v -> {
            Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            startActivity(i);
        }); root.addView(permission);
        Button start=new Button(this); start.setText("② Swipe Launcherを開始");
        start.setOnClickListener(v -> {
            if(!Settings.canDrawOverlays(this)){ Toast.makeText(this,"先にオーバーレイ権限を許可してください",Toast.LENGTH_LONG).show(); return; }
            Intent i=new Intent(this,SwipeOverlayService.class);
            if(Build.VERSION.SDK_INT>=26) startForegroundService(i); else startService(i);
            updateStatus();
        }); root.addView(start);
        Button stop=new Button(this); stop.setText("停止");
        stop.setOnClickListener(v -> { stopService(new Intent(this,SwipeOverlayService.class)); updateStatus(); }); root.addView(stop);
        setContentView(root);
    }
    @Override protected void onResume(){ super.onResume(); updateStatus(); }
    private void updateStatus(){ if(status!=null) status.setText("オーバーレイ権限: "+(Settings.canDrawOverlays(this)?"許可済み":"未許可")); }
}
