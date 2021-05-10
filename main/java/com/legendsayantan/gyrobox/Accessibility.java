package com.legendsayantan.gyrobox;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static com.legendsayantan.gyrobox.MainActivity.autostart;
import static com.legendsayantan.gyrobox.MainActivity.progX;
import static com.legendsayantan.gyrobox.MainActivity.progY;
import static com.legendsayantan.gyrobox.MainActivity.progZ;
import static com.legendsayantan.gyrobox.MainActivity.sharedPreferences;
import static com.legendsayantan.gyrobox.MainActivity.start;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Accessibility extends AccessibilityService {
    static MainActivity main;

    public static TimerTask time;
    public static Timer tmr = new Timer();
    static Boolean swipeLock = false, tmrrun = false, stopfornow = false;
    public static Boolean floatingActive = false, auto = false;

    public static int successful = 1, unsuccessful = 1;
    public static SensorManager gyro;
    public static SensorEventListener gyrolistener;
    public static ArrayList<Integer> axis = new ArrayList<>(3);
    public static ArrayList<Integer> prevaxis = new ArrayList<>(3);
    String foregroundTaskPackageName;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("accessibility", false);
        editor.apply();
    }

    @Override
    protected void onServiceConnected() {
        Toast.makeText(getApplicationContext(), "Service Enabled Successfully", Toast.LENGTH_LONG).show();
        MainActivity.accservice = true;
        main = new MainActivity();
        startActivity(getPackageManager().getLaunchIntentForPackage("com.legendsayantan.gyrobox"));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("accessibility", true);
        editor.apply();

        prevaxis.add(0);
        prevaxis.add(0);
        prevaxis.add(0);
        axis.add(0);
        axis.add(0);
        axis.add(0);

        gyro = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        gyrolistener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent _param1) {
                float[] _rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(_rotationMatrix, _param1.values);
                float[] _remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(_rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, _remappedRotationMatrix);
                float[] _orientations = new float[3];
                SensorManager.getOrientation(_remappedRotationMatrix, _orientations);
                for (int _i = 0; _i < 3; _i++) {
                    _orientations[_i] = (float) (Math.toDegrees(_orientations[_i]));
                }
                final double _x = _orientations[0];
                final double _y = _orientations[1];
                final double _z = _orientations[2];

                registerGyro();
                axis.add(0, (int) _x);
                axis.add(1, (int) _y);
                axis.add(2, (int) _z);
                if (!tmrrun) {
                    Accessibility.timerAction();
                }
                if (start) {
                    try {
                        progX.setProgress((int) (axis.get(0) + 180));
                        progY.setProgress((int) (axis.get(1) + 180));
                        progZ.setProgress((int) (axis.get(2) + 180));
                    } catch (Exception e) {
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        gyro.registerListener(gyrolistener, gyro.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);

        super.onServiceConnected();
    }

    public void swipe(int direction) {
        if (!swipeLock) {
            System.out.println("Swipe in direction " + direction);
        /*
        direction 1 = up
        direction 2 = right
        direction 3 = down
        direction 4 = left
        */
            if (sharedPreferences.getBoolean("accessibility", false)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        Boolean bool = true;
                        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
                        final int middleX = metrics.widthPixels / 2;
                        final int middleY = metrics.heightPixels / 2;
                        int leftSideOfScreen = sharedPreferences.getInt("locX", 0) - 100;
                        int rightSizeOfScreen = sharedPreferences.getInt("locX", 0) + (sharedPreferences.getInt("rad", 1) / 2);
                        int bottomofScreen;
                        int topofScreen;
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            topofScreen = sharedPreferences.getInt("locY", 0) + 200 + (sharedPreferences.getInt("rad", 1) / 2);
                            bottomofScreen = sharedPreferences.getInt("locY", 0) - 100;
                        } else {
                            topofScreen = sharedPreferences.getInt("locY", 0) - 50;
                            bottomofScreen = sharedPreferences.getInt("locY", 0) + 100 + (sharedPreferences.getInt("rad", 1) / 4);
                        }


                        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                        Path path = new Path();
                        if (leftSideOfScreen + middleX < 0) leftSideOfScreen = -(middleX);
                        if (topofScreen + middleY < 0) topofScreen = -(middleY);
                        if (rightSizeOfScreen + middleX < sharedPreferences.getInt("rad", 1))
                            rightSizeOfScreen = -(middleX) + sharedPreferences.getInt("rad", 1);
                        if (bottomofScreen + middleY < sharedPreferences.getInt("rad", 1))
                            bottomofScreen = -(middleY) + sharedPreferences.getInt("rad", 1);
                        if ((sharedPreferences.getInt("locY", 0) + middleY) < 0) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("locY", -(middleY) + sharedPreferences.getInt("rad", 200));
                            editor.apply();
                        }
                        if ((sharedPreferences.getInt("locX", 0) + middleX) < 0) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("locX", -(middleX) + sharedPreferences.getInt("rad", 200));
                            editor.apply();
                        }

                        switch (direction) {
                            case 1:
                                path.moveTo(sharedPreferences.getInt("locX", 0) + middleX, bottomofScreen + middleY);
                                path.lineTo(sharedPreferences.getInt("locX", 0) + middleX, topofScreen + middleY);
                                break;
                            case 2:
                                path.moveTo(leftSideOfScreen + middleX, sharedPreferences.getInt("locY", 0) + middleY);
                                path.lineTo(rightSizeOfScreen + middleX + 200, sharedPreferences.getInt("locY", 0) + middleY);
                                break;
                            case 3:
                                path.moveTo(sharedPreferences.getInt("locX", 0) + middleX, topofScreen + middleY);
                                path.lineTo(sharedPreferences.getInt("locX", 0) + middleX, bottomofScreen + middleY);
                                break;
                            case 4:
                                path.moveTo(rightSizeOfScreen + middleX + 200, sharedPreferences.getInt("locY", 0) + middleY);
                                path.lineTo(leftSideOfScreen + middleX, sharedPreferences.getInt("locY", 0) + middleY);
                                break;
                            default:
                                bool = false;
                                break;
                        }
                        if (bool) {
                            swipeLock = true;
                            Floating.shrink();
                            GestureDescription.StrokeDescription strokeDescription = new GestureDescription.StrokeDescription(path, 0, period());

                            System.out.println("moving point" + Integer.valueOf(leftSideOfScreen + middleX) + "," + Integer.valueOf(sharedPreferences.getInt("locY", 0) + middleY) + "to" + Integer.valueOf(rightSizeOfScreen + middleX) + "," + Integer.valueOf(sharedPreferences.getInt("locY", 0) + middleY));
                            System.out.println(middleX + "," + middleY);
                            gestureBuilder.addStroke(strokeDescription);
                            dispatchGesture(gestureBuilder.build(), new AccessibilityService.GestureResultCallback() {
                                @Override
                                public void onCompleted(GestureDescription gestureDescription) {
                                    System.out.println("Swipe Successful");
                                    swipeLock = false;
                                    successful++;
                                    updateAccuracy();
                                    refreshData();
                                    super.onCompleted(gestureDescription);
                                }

                                @Override
                                public void onCancelled(GestureDescription gestureDescription) {
                                    System.out.println("Swipe Unsuccessful");
                                    swipeLock = false;
                                    unsuccessful++;
                                    updateAccuracy();
                                    refreshData();
                                    super.onCancelled(gestureDescription);
                                }
                            }, null);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } else System.out.println(swipeLock);
    }

    public void registerGyro() {
        if (sharedPreferences.getBoolean("kill", false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("kill", false);
            editor.apply();
            disableSelf();
        }
        double buff = 1000 / sharedPreferences.getInt("sens", 100);
        int x = (prevaxis.get(0) - axis.get(0));
        int y = (prevaxis.get(1) - axis.get(1));
        int z = (prevaxis.get(2) - axis.get(2));
        int dir = 0;
        double rad = 0;
        autoStart();
        if (sharedPreferences.getBoolean("service", false) && tmrrun) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (!sharedPreferences.getBoolean("off", false)) {
                    if (y < -buff) {
                        rad = (-buff / y);
                        dir = 1;
                    }
                    if (y > buff) {
                        rad = (y / buff);
                        dir = 3;
                    }
                }
                if (!sharedPreferences.getBoolean("off2", false)) {
                    if (z < -buff) {
                        rad = (-buff / z);
                        dir = 4;
                    }
                    if (z > buff) {
                        rad = (z / buff);
                        dir = 2;
                    }
                }
                if (sharedPreferences.getBoolean("trigger", false)) {
                    if (x > buff) {
                        rad = (-buff / z);
                        dir = 4;
                    }
                    if (x < -buff) {
                        rad = (z / buff);
                        dir = 2;
                    }
                }
            } else {
                if (!sharedPreferences.getBoolean("off", false)) {
                    if (y < -buff) {
                        rad = (-buff / y);
                        dir = 3;
                    }
                    if (y > buff) {
                        rad = (y / buff);
                        dir = 1;
                    }
                }
                if (!sharedPreferences.getBoolean("off2", false)) {
                    if (x < -buff) {
                        rad = (-buff / x);
                        dir = 2;
                    }
                    if (x > buff) {
                        rad = (x / buff);
                        dir = 4;
                    }
                }
                if (sharedPreferences.getBoolean("trigger", false)) {
                    if (z > buff) {
                        rad = (-buff / x);
                        dir = 2;
                    }
                    if (z < -buff) {
                        rad = (x / buff);
                        dir = 4;
                    }
                }
            }
            if (dir != 0) {
                if (sharedPreferences.getBoolean("autorad", false)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rad", (int) (2 * rad * sharedPreferences.getInt("autoradval", 100)));
                    editor.apply();
                }
                if (sharedPreferences.getBoolean("invert", false)) {
                    if (dir == 1) dir = 3;
                    else if (dir == 3) dir = 1;
                }
                if (sharedPreferences.getBoolean("invert2", false)) {
                    if (dir == 2) dir = 4;
                    else if (dir == 4) dir = 2;
                }
                System.out.println(dir);
                swipe(dir);
            }
        }
    }

    public static void updateAccuracy() {
        try {
            MainActivity.accuracy.setProgress((successful * 100 / (successful + unsuccessful)));
            System.out.println(successful + " Success rate " + (successful * 100 / (successful + unsuccessful)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void refreshData() {
        if (sharedPreferences.getBoolean("mode", false)) {
            prevaxis.set(0, axis.get(0));
            prevaxis.set(1, axis.get(1));
            prevaxis.set(2, axis.get(2));
        }
    }


    public static void timerAction() {
        time = new TimerTask() {
            @Override
            public void run() {
                refreshData();

                tmrrun = true;

            }
        };
        tmr.scheduleAtFixedRate(time, 0, period());
    }

    public void autoStart() {
        if (sharedPreferences.getBoolean("auto", false) && autostart) {
            printForegroundTask();

            if (sharedPreferences.getString("apps", "").contains(foregroundTaskPackageName)) {
                System.out.println(foregroundTaskPackageName);

                auto = true;
                if (stopfornow) {
                    System.out.println("Stopfornow called");
                    Floating.stop();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("service", false);
                    editor.putBoolean("auto", false);
                    editor.apply();
                } else {

                    if (!floatingActive) {
                        startService(new Intent(Accessibility.this, Floating.class));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("ifauto", true);
                        editor.apply();
                        Floating.snsvt = MainActivity.snsvt.getProgress();
                        floatingActive = true;
                        System.out.println(Floating.snsvt);
                        if (sharedPreferences.getBoolean("autoinstant", false)) {
                            editor.putBoolean("service", true);
                            editor.apply();
                            try {
                                Floating.refreshButtons();
                            } catch (Exception e) {

                            }
                        }
                        Accessibility.stopfornow = false;
                    }
                }
            } else {
                if (sharedPreferences.getBoolean("ifauto", false)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("service", false);
                    editor.apply();
                    stopService(new Intent(Accessibility.this, Floating.class));
                    floatingActive = false;
                }
            }
        }
    }

    private void printForegroundTask() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    foregroundTaskPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            foregroundTaskPackageName = tasks.get(0).processName;
        }
    }

    public static int period() {
        return (10000/sharedPreferences.getInt("speed",33));
    }
}

