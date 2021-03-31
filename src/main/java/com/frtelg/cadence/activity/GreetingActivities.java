package com.frtelg.cadence.activity;

import com.uber.cadence.activity.ActivityMethod;

public interface GreetingActivities {
    @ActivityMethod(scheduleToCloseTimeoutSeconds = 100)
    void sayHi(String name);
}
