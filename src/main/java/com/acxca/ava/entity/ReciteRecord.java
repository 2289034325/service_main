package com.acxca.ava.entity;


import java.util.Date;

public class ReciteRecord {
    private int id;
    private int article_id;
    private int paragraph_id;
    private int split_id;

    private float use_time;
    private float score;
    private String content;
    private Date submit_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public int getParagraph_id() {
        return paragraph_id;
    }

    public void setParagraph_id(int paragraph_id) {
        this.paragraph_id = paragraph_id;
    }

    public int getSplit_id() {
        return split_id;
    }

    public void setSplit_id(int split_id) {
        this.split_id = split_id;
    }

    public float getUse_time() {
        return use_time;
    }

    public void setUse_time(float use_time) {
        this.use_time = use_time;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(Date submit_time) {
        this.submit_time = submit_time;
    }
}
