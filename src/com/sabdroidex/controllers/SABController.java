package com.sabdroidex.controllers;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public abstract class SABController {

    public enum MESSAGE {
        UPDATE("Update"), ERROR("Error"), EMPTY("");

        private String message;
        private MESSAGE(String message) {
            this.message = message;
        }
    }

    /**
     * Sends a message to the calling {@link Activity} to update it's status bar
     * 
     * @param messageHandler
     *            The message handler to be notified
     * @param text
     *            The text to write in the message
     */
    public static void sendUpdateMessageStatus(final Handler messageHandler, final String text) {
        
        final Message message = new Message();
        message.setTarget(messageHandler);
        message.what = MESSAGE.UPDATE.hashCode();
        message.obj = text;
        message.sendToTarget();
    }
}
