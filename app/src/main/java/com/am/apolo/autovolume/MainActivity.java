package com.am.apolo.autovolume;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    Switch power;
    static SeekBar sense;
    static SeekBar volume;
    static ProgressBar noise;
    Intent servicio;

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        servicio = new Intent(this, ListenerService.class);

        power = (Switch) findViewById(R.id.switchPower);
        sense = (SeekBar) findViewById(R.id.seekBarSense);
        volume = (SeekBar) findViewById(R.id.seekBarVol);
        noise = (ProgressBar) findViewById(R.id.progressBarNoise);
        noise.setMax(100);

        power.setOnCheckedChangeListener(this);

        volume.setOnSeekBarChangeListener(this);

        sense.setMax(50);

        SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver( new Handler() );
        this.getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mSettingsContentObserver );

        permisos();

        setVolume();

    }

    public static double getSense(){

        return sense.getProgress();

    }

    public static void setVolume(){

        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        int volume_level;

        volume_level= am.getStreamVolume(AudioManager.STREAM_MUSIC);

        volume.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volume.setProgress(volume_level);

        volume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
    }


    public void permisos(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?


                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    dialogo();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void dialogo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app can't work whitout this permission. You want exit")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();


                        permisos();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void hola(int a){

        noise.setProgress(a);
    }

    public void onSwitchOn(){

        startService(servicio);


    }

    public void onSwitchOff(){

        stopService(servicio);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (compoundButton == power){
            if (power.isChecked()){
                onSwitchOn();
                Log.i("Switch:", " set On");
            }else{
                onSwitchOff();
                Log.i("Switch:", " set Off");
            }
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        Log.i("OnPRogressCHange", "");

        if (seekBar == volume){
            Log.i("OnPRogressCHange", " Volume");

            AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);

            am.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    i,
                    0);

        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
