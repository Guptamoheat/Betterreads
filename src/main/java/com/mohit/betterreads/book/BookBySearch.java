package com.mohit.betterreads.book;


import java.util.List;

public class BookBySearch {
    //DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    private String title;
    private List<String> covers;
    private String key;
    private List<AuthorId> authors;
    
    // private LocalDate created;

    private Description description;
    
    public Description getDescription() {
        return description;
    }
    public void setDescription(Description description) {
        this.description = description;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<String> getCovers() {
        return covers;
    }
    public void setCovers(List<String> covers) {
        this.covers = covers;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public List<AuthorId> getAuthors() {
        return authors;
    }
    public void setAuthors(List<AuthorId> authors) {
        this.authors = authors;
    }
    // public LocalDate getCreated() {
    //     return created;
    // }
    // public void setCreated(LocalDate created) {
    //     this.created = created;
    // }
    @Override
    public String toString() {
        return "BookBySearch [title=" + title + ", covers=" + covers + ", key=" + key + ", author=" + authors + "]";
    }

    

    

}
