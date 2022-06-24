package com.example.NoteMe;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id, name, ngaysinh, nd, linkhinhanh;
    public User() {
    }

    public User(String id, String name, String ngaysinh, String nd, String linkhinhanh) {
        this.id = id;
        this.name = name;
        this.ngaysinh = ngaysinh;
        this.nd = nd;
        this.linkhinhanh = linkhinhanh;
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

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getNd() {
        return nd;
    }

    public void setNd(String nd) {
        this.nd = nd;
    }

    public String getLinkhinhanh() {
        return linkhinhanh;
    }

    public void setLinkhinhanh(String linkhinhanh) {
        this.linkhinhanh = linkhinhanh;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> resuft = new HashMap<>();
        resuft.put("name", name);
        resuft.put("ngaysinh", ngaysinh);
        resuft.put("nd", nd);
        resuft.put("linkhinhanh", linkhinhanh);
        return resuft;
    }
}
