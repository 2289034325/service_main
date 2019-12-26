package com.acxca.ava.entity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WritingArticle
{
    private String id;
    private int lang;
    private String source;
    private String title;
    private String description;
    private Date insert_time;
    private List<Paragraph> paragraphs;
    private List<ReciteRecord> histories;
    private boolean deleted;

    public WritingArticle(){
        this.paragraphs = new ArrayList<>();
        this.histories = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ReciteRecord> getHistories() {
        return histories;
    }

    public void setHistories(List<ReciteRecord> histories) {
        this.histories = histories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Date insert_time) {
        this.insert_time = insert_time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
