package com.acxca.ava.entity;

public class UserWordStat {
    private int lang;
    private int total_count;
    private int notstart_count;
    private int learning_count;
    private int finished_count;

    public UserWordStat(int lang,int total_count,int notstart_count,int learning_count,int finished_count){
        this.lang = lang;
        this.total_count = total_count;
        this.notstart_count = notstart_count;
        this.learning_count = learning_count;
        this.finished_count = finished_count;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getNotstart_count() {
        return notstart_count;
    }

    public void setNotstart_count(int notstart_count) {
        this.notstart_count = notstart_count;
    }

    public int getLearning_count() {
        return learning_count;
    }

    public void setLearning_count(int learning_count) {
        this.learning_count = learning_count;
    }

    public int getFinished_count() {
        return finished_count;
    }

    public void setFinished_count(int finished_count) {
        this.finished_count = finished_count;
    }
}
