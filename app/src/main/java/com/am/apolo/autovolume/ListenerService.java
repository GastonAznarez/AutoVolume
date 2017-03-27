package com.am.apolo.autovolume;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by gaston on 06/03/17.
 */

public class ListenerService extends Service {

    static boolean isOn = false;

    @Override
    public void onCreate() { //Se crea el servicio
        Toast.makeText(this,"OnCreate",Toast.LENGTH_SHORT).show();
        isOn = true;

        start();
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) { //Arranca el servicio

        Toast.makeText(this, "OnStartCommand", Toast.LENGTH_SHORT).show();


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    int i;

                    while (isOn) {


                        Log.i("BUCLE ", "n° " + "unico");


                        int amplitude = getAmplitude();

                        // Start recording but don't store data


// Obtain maximum amplitude since last call of getMaxAmplitude()


                        Log.i("AMPLITUDE: ", amplitude + " | | " + getAmplitudeEMA());

                        float sense = (float) (MainActivity.getSense() / 10);

                        MainActivity.hola((int) (amplitude * sense));

                        Thread.sleep(100);

                    }

                } catch (InterruptedException e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        };
        thread.start();

        return START_STICKY;


    }


    @Override
    public void onDestroy() {
        Toast.makeText(this,"OnDestroy",Toast.LENGTH_SHORT).show();
        isOn = false;

        stop();
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

    static final private double EMA_FILTER = 0.6;

    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    public void start() {

        if (mRecorder == null) {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");

            try {
                mRecorder.prepare();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mRecorder.start();
            mEMA = 0.0;
        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public int getAmplitude() {
        if (mRecorder != null) {

         //   long n = (100 / 32768) * mRecorder.getMaxAmplitude();

            long n =(mRecorder.getMaxAmplitude() * 100) / 32768;

            return (int) n;
        }
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}