package com.ever.ending.management;

public class DeltaTime {

    public DeltaTime(int ms){
        this.ms = ms;
    }

    private final int ms;

    public int getMillis(){
        return this.ms;
    }
}
