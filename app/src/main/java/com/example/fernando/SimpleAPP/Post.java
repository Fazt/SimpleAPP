package com.example.fernando.SimpleAPP;

public class Post {

    private String ID;
    private int Created_at;
    private String Author;
    private String Title;
    private String Url;
    private int Flag;


    Post(int created_at, String author, String title, String url, String id, int flag) {
        this.Created_at = created_at;
        this.Author = author;
        this.Title = title;
        this.Url = url;
        this.ID = id;
        this.Flag = flag;
    }

    public int getCreated_at() {
        return Created_at;
    }

    public String getAuthor() {
        return Author;
    }

    public String getTitle() {
        return Title;
    }

    public String getUrl() {
        return Url;
    }

    public String getID() {
        return ID;
    }

    public int getFlag() {
        return Flag;
    }
}
