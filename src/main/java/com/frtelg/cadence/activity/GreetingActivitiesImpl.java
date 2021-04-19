package com.frtelg.cadence.activity;

import com.frtelg.cadence.exception.InvalidNumberException;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintStream;

@Slf4j
public class GreetingActivitiesImpl implements GreetingActivities {

    private final PrintStream printStream;
    private int counter = 0;

    public GreetingActivitiesImpl(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void sayHi(String name) {
        log.info("I am going to say Hi");

        // it will fail at the second attempt, triggering a retry
        if (counter == 2 || counter == 3) {
            counter++;
            throw new IllegalStateException("I do not like the number " + counter);
        }

        if (counter == 5) {
            throw new InvalidNumberException(counter);
        }

        printStream.println("Hi " + name);
        counter++;
    }
}
