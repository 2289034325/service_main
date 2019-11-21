package com.acxca.ava.entity;

import java.util.Date;

public class UserWordStat {
    private int lang;
    private int total_count;
    private int notstart_count;
    private int learning_count;
    private int finished_count;
    private int needreview_count;
    private Date last_learn_time;
    private int last_learn_count;

    public UserWordStat(int lang,int total_count,int notstart_count,int learning_count,int finished_count,int needreview_count){
        this.lang = lang;
        this.total_count = total_count;
        this.notstart_count = notstart_count;
        this.learning_count = learning_count;
        this.finished_count = finished_count;
        this.needreview_count = needreview_count;
    }

    public Date getLast_learn_time() {
        return last_learn_time;
    }

    public void setLast_learn_time(Date last_learn_time) {
        this.last_learn_time = last_learn_time;
    }

    public int getLast_learn_count() {
        return last_learn_count;
    }

    public void setLast_learn_count(int last_learn_count) {
        this.last_learn_count = last_learn_count;
    }

    public int getNeedreview_count() {
        return needreview_count;
    }

    public void setNeedreview_count(int needreview_count) {
        this.needreview_count = needreview_count;
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
