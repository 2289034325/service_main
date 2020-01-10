package com.acxca.ava.entity;

import java.util.Date;

public class LearnRecordDetail {
    private String id;
    private String learn_record_id;
    private String user_id;
    private String word_id;
    private int lang;
    private int answer_times;
    private int wrong_times;
    private Date learn_time;

    // 数据库中字段名是user_word.phase，前端起名learn_phase，为了兼容，两者都保留
    private int phase;
    private int learn_phase;
    private Date next_review_date;
    private boolean finished;

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public Date getNext_review_date() {
        return next_review_date;
    }

    public void setNext_review_date(Date next_review_date) {
        this.next_review_date = next_review_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLearn_record_id() {
        return learn_record_id;
    }

    public void setLearn_record_id(String learn_record_id) {
        this.learn_record_id = learn_record_id;
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

    public Date getLearn_time() {
        return learn_time;
    }

    public void setLearn_time(Date learn_time) {
        this.learn_time = learn_time;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
        this.learn_phase = phase;
    }

    public int getLearn_phase() {
        return phase;
    }

    public void setLearn_phase(int learn_phase) {
        this.learn_phase = learn_phase;
        this.phase = learn_phase;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
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
}
