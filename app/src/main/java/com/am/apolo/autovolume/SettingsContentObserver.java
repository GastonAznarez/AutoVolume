package com.am.apolo.autovolume;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * Created by gaston on 08/03/17.
 */

public class SettingsContentObserver extends ContentObserver {



    public SettingsContentObserver(Handler handler) {
        super(handler);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        MainActivity.setVolume();
    }
}