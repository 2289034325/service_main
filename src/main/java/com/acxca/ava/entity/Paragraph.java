package com.acxca.ava.entity;


import java.util.ArrayList;
import java.util.List;

public class Paragraph {
    private String id;
    private String performer;
    private String text;
    private String translation;
    private String article_id;
    private int index;
    private List<ParagraphSplit> splits;

    public Paragraph(){
        this.splits = new ArrayList<>();
    }

    public Paragraph(String article_id, String performer,String text,String translation){
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public List<ParagraphSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ParagraphSplit> splits) {
        this.splits = splits;
    }
}
