package com.acxca.ava.entity;

import java.util.Date;
import java.util.List;

public class LearnRecord {
    private String id;
    private String user_id;
    private int lang;
    private int word_count;
    private int answer_times;
    private int wrong_times;
    private Date start_time;
    private Date end_time;
    private List<LearnRecordWord> detail;

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

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public int getAnswer_times() {
        return answer_times;
    }

    public void setAnswer_times(int answer_times) {
        this.answer_times = answer_times;
    }

    public int getWrong_times() {
        return wrong_times;
    }

    public void setWrong_times(int wrong_times) {
        this.wrong_times = wrong_times;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public List<LearnRecordWord> getDetail() {
        return detail;
    }

    public void setDetail(List<LearnRecordWord> detail) {
        this.detail = detail;
    }
}
