package com.acxca.ava.entity;

public enum Lang {
    EN(1),
    JP(2),
    KR(3),
    FR(4);

    private int id;

    Lang(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
