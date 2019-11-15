package com.acxca.ava.entity;


import java.util.ArrayList;
import java.util.List;

public class Paragraph {
    private int id;
    private String performer;
    private String text;
    private String translation;
    private int article_id;
    private Integer insert_after;
    private List<ParagraphSplit> splits;

    public Paragraph(){
        this.splits = new ArrayList<>();
    }

    public Paragraph(int article_id, String performer,String text,String translation){
        this.article_id=article_id;
        this.performer = performer;
        this.text = text;
        this.translation = translation;

        this.splits = new ArrayList<>();
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public Integer getInsert_after() {
        return insert_after;
    }

    public void setInsert_after(Integer insert_after) {
        this.insert_after = insert_after;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public List<ParagraphSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ParagraphSplit> splits) {
        this.splits = splits;
    }
}
