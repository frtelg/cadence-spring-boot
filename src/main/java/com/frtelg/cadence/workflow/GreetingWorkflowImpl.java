package com.frtelg.cadence.workflow;

import com.frtelg.cadence.activity.GreetingActivities;
import com.frtelg.cadence.exception.InvalidNumberException;
import com.google.common.base.Throwables;
import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.ActivityFailureException;
import com.uber.cadence.workflow.Workflow;

import java.util.Objects;

import static com.frtelg.cadence.config.CadenceConfiguration.ACTIVITY_OPTIONS;

public class GreetingWorkflowImpl implements GreetingWorkflow {

    private final GreetingActivities greetingActivities = Workflow.newActivityStub(GreetingActivities.class, ACTIVITY_OPTIONS);

    private String name = "Stranger";
    private boolean active = true;

    @Override
    public void greet() {
        while (active) {
            String oldName = name;

            try {
                greetingActivities.sayHi(name);
            } catch (ActivityFailureException e) {
                if (Throwables.getRootCause(e) instanceof InvalidNumberException) {
                    terminate();
                } else {
                    throw e;
                }
            }

            Workflow.await(() -> !Objects.equals(oldName, name) || !active);
        }
    }

    @Override
    public void changeName(String name) {
        this.name = name;
    }

    @Override
    public void terminate() {
        this.active = false;
    }

    @Override
    public String getCurrentName() {
        return this.name;
    }
}
