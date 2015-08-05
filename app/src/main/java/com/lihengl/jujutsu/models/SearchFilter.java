package com.lihengl.jujutsu.models;

import java.io.Serializable;

public class SearchFilter implements Serializable {
    private static final long serialVersionUID = 8726708152513193182L;

    public String color;
    public String style;
    public String size;
    public String site;

    public SearchFilter() {
        this.color = "";
        this.style = "";
        this.site = "";
        this.size = "";
    }
}
