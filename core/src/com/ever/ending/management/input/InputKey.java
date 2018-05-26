package com.ever.ending.management.input;

import com.ever.ending.interfaces.control.IController;

public class InputKey {
    public InputKey(int key, IController.GameActions action){
        this.action = action;
        this.setAssignedKey(key);
    }

    public int getAssignedKey() {
        return assignedKey;
    }

    public void setAssignedKey(int assignedKey) {
        this.assignedKey = assignedKey;
    }

    private int assignedKey;

    private final IController.GameActions action;

    public IController.GameActions getAction() {
        return action;
    }
}