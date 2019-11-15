package com.acxca.ava.entity;

import java.util.Date;

public class UserWord {
    private String id;
    private String user_id;
    private String word_id;
    private int lang;
    private int phase;
    private boolean finished;
    private Date last_review_time;
    private Date next_review_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWord_id() {
        return word_id;
    }

    public void setWord_id(String word_id) {
        this.word_id = word_id;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Date getLast_review_time() {
        return last_review_time;
    }

    public void setLast_review_time(Date last_review_time) {
        this.last_review_time = last_review_time;
    }

    public Date getNext_review_date() {
        return next_review_date;
    }

    public void setNext_review_date(Date next_review_date) {
        this.next_review_date = next_review_date;
    }
}
