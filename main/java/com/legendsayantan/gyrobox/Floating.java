package com.legendsayantan.gyrobox;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.transition.Transition;

import static com.legendsayantan.gyrobox.MainActivity.sharedPreferences;

public class Floating extends Service {
    public static ViewGroup floatView;
    private int LAYOUT_TYPE;
    public static WindowManager.LayoutParams floatWindowLayoutParam,param2;
    public static WindowManager windowManager;
    private static Button closeBtn,startBtn;
    public static int snsvt;
    public static MainActivity main;
    public static Floating floating;
    public static TextView textView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override

    public void onCreate() {

        super.onCreate();
            main=new MainActivity();


        // The screen height and width are calculated, cause

        // the height and width of the floating window is set depending on this


        // To obtain a WindowManager of a different Display,

        // we need a Context for that display, so WINDOW_SERVICE is used

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        // A LayoutInflater instance is created to retrieve the
        // LayoutInflater for the floating_layout xml

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // inflate a new view hierarchy from the floating_layout xml

        floatView = (ViewGroup) inflater.inflate(R.layout.floating, null);

        // The Buttons and the EditText are connected with

        // the corresponding component id used in floating_layout xml file



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // If API Level is more than 26, we need TYPE_APPLICATION_OVERLAY
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        } else {

            // If API Level is lesser than 26, then we can

            // use TYPE_SYSTEM_ERROR,

            // TYPE_SYSTEM_OVERLAY, TYPE_PHONE, TYPE_PRIORITY_PHONE.

            // But these are all

            // deprecated in API 26 and later. Here TYPE_TOAST works best.

            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;

        }



        // Now the Parameter of the floating-window layout is set.

        // 3) Layout_Type is already set.

        // 4) Next Parameter is Window_Flag. Here FLAG_NOT_FOCUSABLE is used. But

        // problem with this flag is key inputs can't be given to the EditText.

        // This problem is solved later.

        // 5) Next parameter is Layout_Format. System chooses a format that supports

        // translucency by PixelFormat.TRANSLUCENT

        floatWindowLayoutParam = new WindowManager.LayoutParams(
                (int) (200),
                (int) (200),
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        param2 = new WindowManager.LayoutParams(
                (int) (100),
                (int) (100),
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        // The Gravity of the Floating Window is set.

        // The Window will appear in the center of the screen

        floatWindowLayoutParam.gravity = Gravity.CENTER;
        param2.gravity=Gravity.START;


        // X and Y value of the window is set

        floatWindowLayoutParam.x = 0;

        floatWindowLayoutParam.y = 0;



        // The ViewGroup that inflates the floating_layout.xml is

        // added to the WindowManager with all the parameters

        windowManager.addView(floatView, floatWindowLayoutParam);






        // The EditText string will be stored

        // in currentDesc while writing

        textView = floatView.findViewById(R.id.textView12);

        // Another feature of the floating window is, the window is movable.

        // The window can be moved at any position on the screen.

        floatView.setOnTouchListener(new View.OnTouchListener() {

            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatWindowLayoutParam;

            double x;

            double y;

            double px;

            double py;



            @Override

            public boolean onTouch(View v, MotionEvent event) {



                switch (event.getAction()) {

                    // When the window will be touched,

                    // the x and y position of that position

                    // will be retrieved

                    case MotionEvent.ACTION_DOWN:

                        x = floatWindowLayoutUpdateParam.x;

                        y = floatWindowLayoutUpdateParam.y;



                        // returns the original raw X

                        // coordinate of this event

                        px = event.getRawX();



                        // returns the original raw Y

                        // coordinate of this event

                        py = event.getRawY();

                        break;

                    // When the window will be dragged around,

                    // it will update the x, y of the Window Layout Parameter

                    case MotionEvent.ACTION_MOVE:

                        floatWindowLayoutUpdateParam.x = (int) ((x + event.getRawX()) - px);

                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("locX",floatWindowLayoutUpdateParam.x);
                        editor.putInt("locY",floatWindowLayoutUpdateParam.y);
                        editor.apply();
                        textView.setText("GyroBox");
                        startBtn.setVisibility(View.VISIBLE);
                        closeBtn.setVisibility(View.VISIBLE);
                        System.out.println(sharedPreferences.getInt("locX",0)+","+sharedPreferences.getInt("locY",0));
                        refreshButtons();

                        // updated parameter is applied to the WindowManager

                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam);

                        break;

                }

                return false;

            }

        });

        startBtn = floatView.findViewById(R.id.button1);
        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                if(MainActivity.accservice) {
                    if (sharedPreferences.getBoolean("service", false)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("service", false);
                        editor.apply();

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !Accessibility.tmrrun) {
                                Accessibility.refreshData();
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("service", true);
                        editor.apply();

                    }
                    refreshButtons();
                }
            }
        });
        closeBtn = floatView.findViewById(R.id.button2);
        closeBtn.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("service",false);
                if(sharedPreferences.getBoolean("ifauto",false))  {
                    editor.putBoolean("auto",false);
                    editor.putBoolean("autoinstant",false);
                    Toast.makeText(getApplicationContext(),"AutoStart For GyroBox was disabled.",Toast.LENGTH_SHORT).show();
                }
                editor.apply();
                stopSelf();

                windowManager.removeView(floatView);
                Accessibility.floatingActive = false;
            }
        });

    }



    // It is called when stopService()

    // method is called in MainActivity

    @Override

    public void onDestroy() {

        super.onDestroy();

        stopSelf();

        // Window is removed from the screen
try {
    windowManager.removeView(floatView);
}catch (Exception e){

}

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void stop(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("service",false);
        editor.apply();

        try {
            windowManager.removeView(floatView);
        }catch (Exception  e){
        }
        Accessibility.floatingActive = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void shrink(){
        try {
            windowManager.removeView(floatView);
        }catch (Exception e){

        }
        windowManager.addView(floatView,param2);
        textView.setText("G");
        startBtn.setVisibility(View.GONE);
        closeBtn.setVisibility(View.GONE);
        Accessibility.floatingActive=true;
    }
    public static void refreshButtons(){
        if(sharedPreferences.getBoolean("service",false)){
            startBtn.setBackgroundResource(R.drawable.ic_action_pause);
        }else {
            startBtn.setBackgroundResource(R.drawable.ic_action_play);
        }
    }

}

