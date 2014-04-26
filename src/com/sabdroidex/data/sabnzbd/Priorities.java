package com.sabdroidex.data.sabnzbd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by marc on 22/03/14.
 */
public class Priorities {

    private final static List<String> priorities = new ArrayList<String>();
    static {
        Collections.addAll(priorities, "Default", "Paused", "Low", "Normal", "High");
    }

    public List<String> getPriorities() {
        return priorities;
    }

    public enum Priority {

        DEFAULT(-100),
        PAUSED(-2),
        LOW(-1),
        NORMAL(0),
        HIGH(1);

        private int value;

        private Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }
}
