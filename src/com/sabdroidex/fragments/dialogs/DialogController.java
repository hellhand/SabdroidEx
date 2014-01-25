package com.sabdroidex.fragments.dialogs;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Marc on 27/12/13.
 */
public class DialogController {

    public static final void callHandler(Handler handler) {
        callHandler(handler, null, null);
    }

    public static final void callHandler(Handler handler, Integer what, Object o) {
        Message message = new Message();
        message.obj = o;
        message.what = what;
        message.setTarget(handler);
        message.sendToTarget();
    }
}
