package com.acxca.ava.entity;


import com.acxca.components.java.util.diff_match_patch;

import java.util.Date;
import java.util.List;

public class ReciteRecord {
    private String id;
    private String user_id;
    private String article_id;
    private String paragraph_id;
    private String split_id;

    private float use_time;
    private float score;
    private String content;
    private Date submit_time;

    private List<diff_match_patch.Diff> diffs;

    public List<diff_match_patch.Diff> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<diff_match_patch.Diff> diffs) {
        this.diffs = diffs;
    }

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

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getParagraph_id() {
        return paragraph_id;
    }

    public void setParagraph_id(String paragraph_id) {
        this.paragraph_id = paragraph_id;
    }

    public String getSplit_id() {
        return split_id;
    }

    public void setSplit_id(String split_id) {
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
