package com.legendsayantan.gyrobox;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;

import static com.legendsayantan.gyrobox.Accessibility.gyro;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "";
    public static boolean overlay, accservice, backservice,start,autostart;
    private final ArrayList<Double> axis = new ArrayList<>();
    public static SensorManager gyroX;
    public static ProgressBar progX,progY,progZ,accuracy;
    public static SharedPreferences sharedPreferences;
    public static CheckBox checkbox,checkbox2,checkrad;
    public static RadioButton simple,adjust,rotate,tilt;
    public static ArrayList<ApplicationInfo> Apps;
    public static ArrayList<Integer> enabledApps ;
    public static ArrayList<Integer> disabledApps ;
    public static ArrayList<String> enableString = new ArrayList<>();
    public static ArrayList<String> disableString = new ArrayList<>();
    String buff;
    ScrollView ansbox;
                ListView enable,disable,questions;
                TabLayout tabs;
                ConstraintLayout cont,cons,info;
                public Intent extLink;

    static SeekBar snsvt;
    SeekBar rad,speed;
    SeekBar seekautorad;
    Intent perms;
    static TextView sens,forceStop,ans;

    Switch accswitch, ovlswitch, backswitch,switchinvert,switchinvert2,switchoff,switchoff2;

    View warn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            accswitch = findViewById(R.id.switchas);
            ovlswitch = findViewById(R.id.switcho);
            backswitch = findViewById(R.id.switch3);

            accservice = overlay = backservice = start = autostart = false;
            sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            perms = new Intent();
            warn = findViewById(R.id.warn);


                sensorCheck();

            check(accswitch);
            Accessibility.updateAccuracy();
        }else{
            Toast.makeText(getApplicationContext(),"This App is Compatible with Android 7 or later versions only.",Toast.LENGTH_LONG).show();
            finish();
        }

        }
    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);

        accswitch = findViewById(R.id.switchas);
        ovlswitch = findViewById(R.id.switcho);
        backswitch = findViewById(R.id.switch3);
        accservice = overlay = backservice = false;
        perms = new Intent();
        warn = findViewById(R.id.warn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sensorCheck();
            check(accswitch);
            Accessibility.updateAccuracy();
        }

    }

    public void getOverlayPerm() {
        checkOverlay();
        if (!overlay) {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            Toast.makeText(getApplicationContext(), "Find GyroBox and enable overlays", Toast.LENGTH_LONG).show();
        }
        checkOverlay();
    }

    public void checkOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            overlay = Settings.canDrawOverlays(getApplicationContext());
        } else overlay = true;
    }

    public void getAccessibility() {
        Toast.makeText(getApplicationContext(), "Enable Accessibility Service for GyroBox.", Toast.LENGTH_LONG).show();
        perms.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        perms.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(perms);
        finish();
    }

    public void checkService() {
        accservice = isAccessibilitySettingsOn(getApplicationContext());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sensorCheck() {
        try {
            if (gyroX.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {

                warn.setVisibility(View.GONE);
                System.out.println(gyroX.getDefaultSensor(Sensor.TYPE_GYROSCOPE).toString());
            }else warn.setVisibility(View.VISIBLE);
        } catch (Exception e){
        }

    }

    public void getBackService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    public void checkBackService() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        String packageName = getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            backservice = pm.isIgnoringBatteryOptimizations(packageName);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void check(View view) {
        checkService();
        checkOverlay();
        checkBackService();
        Switch v = (Switch) view;
        if (view != null && v.isChecked()) {
            if (view.getId() == R.id.switchas) getAccessibility();

            if (view.getId() == R.id.switcho) getOverlayPerm();

            if (view.getId() == R.id.switch3) getBackService();

        }
        accswitch.setChecked(accservice);
        ovlswitch.setChecked(overlay);
        backswitch.setChecked(backservice);
        if (accservice && overlay && backservice){
            start();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void start(){
        if (accservice && overlay && backservice){
            setContentView(R.layout.activity_home);
            sens = findViewById(R.id.senstext);
            forceStop=findViewById(R.id.stoptext);
            progX=findViewById(R.id.progressBarX);
            progY=findViewById(R.id.progressBarY);
            progZ=findViewById(R.id.progressBarZ);
            accuracy=findViewById(R.id.accuracy);
            checkrad = findViewById(R.id.checkrad);
            simple=findViewById(R.id.simple);
            adjust=findViewById(R.id.adjust);
            rotate=findViewById(R.id.rotate);
            tilt=findViewById(R.id.tilt);
            checkbox=findViewById(R.id.checkBox);
            checkbox2=findViewById(R.id.checkBox2);
            switchinvert=findViewById(R.id.switchinvert);
            switchinvert2=findViewById(R.id.switchinvert2);
            switchoff=findViewById(R.id.switchoff);
            switchoff2=findViewById(R.id.switchoff2);
            tabs=findViewById(R.id.tabLayout);
            enable=findViewById(R.id.listenable);
            disable=findViewById(R.id.listdisable);
            speed=findViewById(R.id.seekspeed);
        cont=findViewById(R.id.home);
        cons=findViewById(R.id.cons);
        info=findViewById(R.id.info);
        cont.setVisibility(View.VISIBLE);
        cons.setVisibility(View.GONE);
        info.setVisibility(View.GONE);
            Apps= new ArrayList<>(1000);
            enabledApps = new ArrayList<>(1000);
            disabledApps=new ArrayList<>(1000);
            try {
                sens.setText(gyro.getDefaultSensor(Sensor.TYPE_GYROSCOPE).toString());
            }catch (Exception e){

            }
            snsvt = findViewById(R.id.seeksns);
            rad = findViewById(R.id.seekrad);
            seekautorad = findViewById(R.id.seekautorad);
            tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if(tab.getPosition()==0){
                        cont.setVisibility(View.VISIBLE);
                    }if(tab.getPosition()==1){
                        cons.setVisibility(View.VISIBLE);
                    }if(tab.getPosition()==2){
                        info.setVisibility(View.VISIBLE);
                        faqConfig();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    if(tab.getPosition()==0){
                        cont.setVisibility(View.GONE);
                    }if(tab.getPosition()==1){
                        cons.setVisibility(View.GONE);
                    }if(tab.getPosition()==2){
                        info.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            snsvt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("sens",seekBar.getProgress());
                    editor.apply();
                    System.out.println("sensitivity changed."+sharedPreferences.getInt("sens",500));
                }
            });
            forceStop.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("kill",true);
                    editor.apply();
                    stopService(new Intent(MainActivity.this, Floating.class));
                    finishAffinity();
                    finish();
                    return false;
                }
            });
            rad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rad",seekBar.getProgress());
                    editor.apply();
                    System.out.println("radius changed."+sharedPreferences.getInt("rad",200));
                }
            });
            seekautorad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("autoradval",seekBar.getProgress());
                    editor.apply();
                }
            });
            simple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("mode",!isChecked);
                        editor.apply();
                        adjust.setChecked(!isChecked);
                }
            });
            adjust.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("mode",isChecked);
                        editor.apply();
                        simple.setChecked(!isChecked);
                }
            });
            rotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("trigger",!isChecked);
                        editor.apply();
                        tilt.setChecked(!isChecked);
                }
            });
            tilt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("trigger",isChecked);
                        editor.apply();
                        rotate.setChecked(!isChecked);
                }
            });
            checkrad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("autorad",isChecked);
                        editor.apply();
                }
            });
            switchinvert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("invert",isChecked);
                        editor.apply();
                }
            });
            switchinvert2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("invert2", isChecked);
                    editor.apply();
                }
            });
            switchoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("off",isChecked);
                    editor.apply();
                }
            });
            switchoff2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("off2", isChecked);
                    editor.apply();
                }
            });
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("auto",isChecked);
                        editor.apply();
                        if(!isAccessGranted()) {
                            checkbox.setChecked(false);
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, 0);
                        }
                        if(!isChecked)checkbox2.setChecked(isChecked);
                }
            });
            checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("autoinstant",isChecked);
                    editor.apply();
                    if(!isAccessGranted()) {
                        checkbox2.setChecked(false);
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 0);
                    }
                    if(isChecked)checkbox.setChecked(true);
                }
            });
            speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("speed",seekBar.getProgress());
                    editor.apply();
                }
            });

            snsvt.setProgress(sharedPreferences.getInt("sens", 100));
            seekautorad.setProgress(sharedPreferences.getInt("autoradval",100));
            rad.setProgress(sharedPreferences.getInt("rad",0));
            if(!sharedPreferences.getBoolean("mode",true)){
                simple.setChecked(true);
            }else adjust.setChecked(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                checkbox2.setChecked((isAccessGranted()&&sharedPreferences.getBoolean("autoinstant",false)));
                checkbox.setChecked((isAccessGranted()&&sharedPreferences.getBoolean("auto",false)));

            }else Toast.makeText(getApplicationContext(),"Your device is not supported",Toast.LENGTH_LONG).show();
            checkrad.setChecked(sharedPreferences.getBoolean("autorad",false));
            switchinvert.setChecked(sharedPreferences.getBoolean("invert",false));
            switchinvert2.setChecked(sharedPreferences.getBoolean("invert2",false));
            switchoff.setChecked(sharedPreferences.getBoolean("off",false));
            switchoff2.setChecked(sharedPreferences.getBoolean("off2",false));
            speed.setProgress(sharedPreferences.getInt("speed",33));
        if(sharedPreferences.getBoolean("trigger",false)){
            tilt.setChecked(true);
        }else rotate.setChecked(true);

            start = true;
            refreshApps(true);

        }
    }



    public void close(View view) {
        finishAffinity();
        finish();
    }
    // To check if service is enabled
    public boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + Accessibility.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        accservice = true;
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        accservice = false;
        return false;
    }
    public boolean isFloatingWindowWorking(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Floating.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializeLogic(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("ifauto",false);
        editor.apply();
        startService(new Intent(MainActivity.this,Floating.class));
        Floating.snsvt = snsvt.getProgress();
        System.out.println(Floating.snsvt);
        Accessibility.stopfornow=false;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View view) {
        initializeLogic();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            autostart = (mode == AppOpsManager.MODE_ALLOWED);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void social(View view) {
        extLink=new Intent(Intent.ACTION_VIEW);
        switch(view.getId()){
            case R.id.fb:
                extLink.setData(Uri.parse("https://www.facebook.com/LegendSayantan"));
                break;
            case R.id.ig:
                extLink.setData(Uri.parse("https://www.instagram.com/LegendSayantan"));
                break;
            case R.id.git:
                extLink.setData(Uri.parse("https://github.com/legendsayantan"));
                break;
            case R.id.reddit:
                extLink.setData(Uri.parse("https://www.reddit.com/user/LegendSayantan"));
                break;
        }
        startActivity(extLink);
    }

    public void changeTab(View view) {
        tabs.selectTab(tabs.getTabAt(1));
    }

    public void refreshApps(boolean b) {
        Apps.clear();
        enabledApps.clear();
        disabledApps.clear();
        enableString.clear();
        disableString.clear();
        final PackageManager pm = getPackageManager();
        Apps = (ArrayList<ApplicationInfo>) pm.getInstalledApplications(PackageManager.GET_META_DATA);

        if(sharedPreferences.getString("apps","").equals("")){
            for(ApplicationInfo applicationInfo : Apps){
                disabledApps.add(Apps.indexOf(applicationInfo));
            }
        }else for(ApplicationInfo applicationInfo : Apps ){
            if(sharedPreferences.getString("apps","").contains(applicationInfo.packageName)){
                enabledApps.add(Apps.indexOf(applicationInfo));
            }else{
                if(!disabledApps.contains(Apps.indexOf(applicationInfo))){
                    disabledApps.add(Apps.indexOf(applicationInfo));
                }
            }
        }
        enabledApps.removeAll(Collections.singleton(null));
        disabledApps.removeAll(Collections.singleton(null));

        for(int i : enabledApps){
            enableString.add((String) Apps.get(i).loadLabel(pm));
        }
        for(int j : disabledApps){
            disableString.add((String) Apps.get(j).loadLabel(pm));
        }
        enableString.remove("Android System");

        enable.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,enableString));
        disable.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, disableString));
        if(b) {
            enable.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("apps",sharedPreferences.getString("apps","").replace(Apps.get(enabledApps.get(position)).packageName+" ",""));
                    editor.apply();
                    System.out.println(sharedPreferences.getString("apps","null"));
                    Toast.makeText(getApplicationContext(),"App removed",Toast.LENGTH_SHORT).show();

                    refreshApps(false);
                    return false;
                }
            });
            disable.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("apps",sharedPreferences.getString("apps","")+Apps.get(disabledApps.get(position)).packageName+" ");
                    editor.apply();
                    System.out.println(sharedPreferences.getString("apps","null"));
                    Toast.makeText(getApplicationContext(),"App added",Toast.LENGTH_SHORT).show();

                    refreshApps(false);
                    return false;
                }
            });
        }


    }
    public void faqConfig(){
        ansbox=findViewById(R.id.ansbox);
        ansbox.setVisibility(View.GONE);
        ArrayList<String> questionsText = new ArrayList<>();
        questionsText.add("What is GyroBox?");
        questionsText.add("Why does it need accessibiity permissions?");
        questionsText.add("Why does it need to override Battery Optimisations?");
        questionsText.add("Why does it need usage access?");
        questionsText.add("Why is GyroBox not working?");
        questionsText.add("Usage of 'Sensitivity' slider?");
        questionsText.add("Usage of 'Configure Apps' or 'Apps' tab?");
        questionsText.add("Usage of 'Swipe Radius' slider?");
        questionsText.add("Usage of 'Auto Radius'?");
        questionsText.add("Usage of 'Speed' slider?");
        questionsText.add("What is 'Trigger Preference'?");
        questionsText.add("What is 'Mode Selector'?");
        questionsText.add("What is 'Invert Sensor'?");
        questionsText.add("What is 'Turn Off Sensor'?");
        questionsText.add("What is 'Success Rate'?");


        ArrayList<String> answersText = new ArrayList<>();
        answersText.add("GyroBox is an app designed to give you more convenient controls over your phone , through your phone's sensor." +
                "\nThis App processes your devices's various sensors data and helps you to navigate through your phone or specific apps." +
                "\nGyroBox also offers wide customisations over its controls, automatic actions and intensity of those actions to fit your needs."+
                "\nThis is an open-source project on github.com/legendsayantan/gyrobox");
        answersText.add("GyroBox needs Accessibility Service to perform gestures on screen on behalf of the user.." +
                "\nExcept this permission , functionalities of GyroBox would be corrupted and it won't work." +
                "\nGyroBox does not collect any of your personal information, every action done on your device, remains in your device." +
                "\nThis is an open-source project on github.com/legendsayantan/gyrobox");
        answersText.add("Android System prevents apps from working in background to save more battery, This method breaks some features of this app." +
                "\nGyroBox needs to be active always in the background in order to provide you features and keep it's services alive." +
                "\nAlthough, GyroBox doesnot do heavy processing in the background to highly impact phone's performance, It only collects device sensor information from background. ");
        answersText.add("To use App AutoStart , GyroBox needs to know what apps are you using at any moment." +
                "\nTo access info about the current running app, this permission must be accessed.");
        answersText.add("Your device needs to be minimum Android 7 Nougat (API Level 24) or latest in order to run GyroBox." +
                "\nIf your device is latest but still GyroBox does not work,try clearing App data and re-enabling permissions.");
        answersText.add("GyroBox performs gestures whenever your device sensor state changes." +
                "\nTo adjust how much sensor state change would matter to perform a single gesture, adjust sensitivity." +
                "\n\nUsage:" +
                "\nset low sensitivity to perform gestures when high angular rotations happen," +
                "\nset high sensitivity to perform gestures at very little angular rotations of the device.");
        answersText.add("GyroBox can automatically start or stop when you are using a specific app," +
                "\nJust go to apps section and enable autostart, then enable the apps you want to use GyroBox with." +
                "\nLong press on an enabled app on the list to disable it , and long press on an disabled app on the list to enable it.");
        answersText.add("You can customise the length of gestures performed on screen," +
                "\nJust drag the swipe radius slider according to your preferred size." +
                "\n(minimum value = 60px , maximum value= 600px)");
        answersText.add("Auto radius determines automatically how much length of a gesture should be performed." +
                "\nIt analyzes your device movement impact and calculates the radius value depending on the auto radius factor value.");
        answersText.add("Choose how fast gestures occur, set the Speed slider to match your expected speed for convenient navigation." +
                "\n(for lower end devices, it is recommended not to set speed more than 50%)");
        answersText.add("Do you feel easy to rotate your phone around or to tilt your phone for gestures?" +
                "\nGyroBox works in both modes , but you can still set your preference to get more accuracy...");
        answersText.add("GyroBox has two modes of operation:" +
                "\n1.Simple Mode-" +
                "\nSimple mode keeps performing gestures continuously until you restore phone to its original rotation state, This mode can perform auto scrolls or auto swipes without any user interaction.." +
                "\nselect Simple Mode, Start GyroBox and rotate your device once to keep performing corresponding gesture until you rotate back the device to its prior rotation." +
                "\n2.Adjust Mode" +
                "\nAdjust mode performs one gesture for every rotation change.This mode performs the corresponding gesture when device rotated, and performs another opposite gesture when device is restored to its prior state." +
                "\nselect Adjust Mode,start GyroBox and rotate your device once towards any direction to perform that gesture action once, rotate back to prior rotation to perform a reverse action." +
                "\n\n(when using simple mode, it is recommended to reduce sensitivity and speed. Simple mode uses more resources from your device, this may have temporary performance impact on your device)");
        answersText.add("You can use invert sensor options to perform reversed gestures on that same device movement." +
                "\nLeft-Right or Up-Down gestures can be applied this setting independently.");
        answersText.add("You can turn off sensor gestures for specific directions." +
                "\nLeft-Right or Up-Down gestures can be applied this setting independently." +
                "\nturning off gestures for a specific direction also allows you to get more accuracy for the other direction. ");
        answersText.add("GyroBox Shows you if it could perform all the gestures perfectly." +
                "\nIf it could perform all the past gestures successively, you will see Success Rate to go high.." +
                "\nIf it was interrupted during a gesture or any other accessibility services are preventing GyroBox to work properly, you will see Success Rate to go low..");

        questions = findViewById(R.id.question);
        ans=findViewById(R.id.ans);

        questions.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,questionsText));
        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ansbox.setVisibility(View.VISIBLE);
                ans.setText(answersText.get(position));
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }
}