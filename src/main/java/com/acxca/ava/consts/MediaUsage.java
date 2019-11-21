package com.acxca.ava.consts;

public enum MediaUsage {
    ORIGIN(1,"origin"),
    EXPLAIN(2,"explain");

    private int value;
    private String name;

    MediaUsage(int value,String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName(){
        return name;
    }

    public static MediaUsage fromValue(int value){
        switch(value) {
            case 1:
                return ORIGIN;
            case 2:
                return EXPLAIN;
        }
        return null;
    }
}
