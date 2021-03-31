package com.frtelg.cadence.activity;

import java.io.PrintStream;

public class GreetingActivitiesImpl implements GreetingActivities {

    private final PrintStream printStream;

    public GreetingActivitiesImpl(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void sayHi(String name) {
        printStream.println("Hi " + name);
    }
}
