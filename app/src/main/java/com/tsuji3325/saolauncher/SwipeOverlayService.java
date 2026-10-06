package com.tsuji3325.saolauncher;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class SwipeOverlayService extends Service {
    private WindowManager wm;
    private View edge, menu;
    private float downX, downY;
    private static final String CHANNEL="sao_overlay";

    @Override public void onCreate(){
        super.onCreate();
        createChannel();
        Notification n=new Notification.Builder(this,CHANNEL).setContentTitle("SAO Launcher A13")
                .setContentText("Swipe launcher is active").setSmallIcon(android.R.drawable.ic_menu_compass).build();
        startForeground(1001,n);
        wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        showEdge();
    }
    private void showEdge(){
        TextView v=new TextView(this);
        v.setBackgroundColor(Color.argb(20,0,180,255));
        v.setOnTouchListener((view,e)->{
            if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getRawX();downY=e.getRawY();return true;}
            if(e.getAction()==MotionEvent.ACTION_UP){
                float dx=e.getRawX()-downX, dy=Math.abs(e.getRawY()-downY);
                if(dx>90 && dx>dy) showMenu();
                return true;
            }
            return true;
        });
        edge=v;
        WindowManager.LayoutParams p=params(dp(28),dp(420));
        p.gravity=Gravity.START|Gravity.CENTER_VERTICAL;
        wm.addView(edge,p);
    }
    private void showMenu(){
        if(menu!=null)return;
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(22),dp(18),dp(22),dp(18));
        GradientDrawable bg=new GradientDrawable(); bg.setColor(Color.argb(235,245,245,245)); bg.setCornerRadius(dp(10)); bg.setStroke(dp(2),Color.DKGRAY); box.setBackground(bg);
        TextView title=new TextView(this); title.setText("MENU"); title.setTextSize(24); title.setTextColor(Color.DKGRAY); box.addView(title);
        String[] labels={"Apps","Settings","Profile","Close"};
        for(String s:labels){
            Button b=new Button(this); b.setText(s); box.addView(b,new LinearLayout.LayoutParams(dp(210),dp(58)));
            if(s.equals("Apps")) b.setOnClickListener(v->openApps());
            else if(s.equals("Settings")) b.setOnClickListener(v->openSettings());
            else if(s.equals("Close")) b.setOnClickListener(v->hideMenu());
        }
        menu=box;
        WindowManager.LayoutParams p=params(dp(260),WindowManager.LayoutParams.WRAP_CONTENT);
        p.gravity=Gravity.START|Gravity.CENTER_VERTICAL; p.x=dp(35);
        wm.addView(menu,p);
    }
    private void openApps(){
        Intent i=new Intent(Intent.ACTION_MAIN); i.addCategory(Intent.CATEGORY_APP_MARKET); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{startActivity(i);}catch(Exception e){openSettings();}
        hideMenu();
    }
    private void openSettings(){ Intent i=new Intent(this,MainActivity.class); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i); hideMenu(); }
    private void hideMenu(){ if(menu!=null){wm.removeView(menu);menu=null;} }
    private WindowManager.LayoutParams params(int w,int h){
        return new WindowManager.LayoutParams(w,h,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);
    }
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    private void createChannel(){ if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel(CHANNEL,"SAO Launcher",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);} }
    @Override public void onDestroy(){hideMenu(); if(edge!=null){wm.removeView(edge);edge=null;} super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}
