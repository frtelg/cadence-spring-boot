package com.frtelg.cadence.workflow;

import com.frtelg.cadence.activity.GreetingActivities;
import com.uber.cadence.workflow.Workflow;

import java.util.Objects;

public class GreetingWorkflowImpl implements GreetingWorkflow {

    private final GreetingActivities greetingActivities = Workflow.newActivityStub(GreetingActivities.class);

    private String name = "Stranger";
    private boolean active = true;

    @Override
    public void greet() {
        while (active) {
            String oldName = name;
            greetingActivities.sayHi(name);
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
