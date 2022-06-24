package com.example.NoteMe;

import java.util.HashMap;
import java.util.Map;

public class Comment  {
    private String id, name, nd;
    public Comment() {
    }

    public Comment(String id, String name, String nd) {
        this.id = id;
        this.name = name;
        this.nd = nd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNd() {
        return nd;
    }

    public void setNd(String nd) {
        this.nd = nd;
    }



    public Map<String, Object> toMap() {
        HashMap<String, Object> resuft = new HashMap<>();
        resuft.put("name", name);
        resuft.put("nd", nd);
        return resuft;
    }
}

