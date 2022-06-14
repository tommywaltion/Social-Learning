package com.example.testapp;

public class PostModel {

    int id, likes, post_image;
    String name, time, title, creator_profile;

    public PostModel(int id, int likes, String creator_profile, int post_image, String name, String time, String title ) {
        this.id = id;
        this.likes = likes;
        this.creator_profile = creator_profile;
        this.post_image = post_image;
        this.name = name;
        this.time = time;
        this.title = title;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLikes() {
        return this.likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getCreator_profile() {
        return this.creator_profile;
    }

    public void setCreator_profile(String creator_profile) {
        this.creator_profile = creator_profile;
    }

    public int getPost_image() {
        return this.post_image;
    }

    public void setPost_image(int post_image) {
        this.post_image = post_image;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
