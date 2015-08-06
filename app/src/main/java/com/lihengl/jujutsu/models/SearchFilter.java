package com.lihengl.jujutsu.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchFilter implements Serializable {
    private static final long serialVersionUID = 8726708152513193182L;

    private int selectedIndex;

    public String title;
    public String name;
    public ArrayList<String> options;

    public SearchFilter(String displayTitle, String queryName) {
        this.name = queryName;
        this.selectedIndex = 0;
        this.title = displayTitle;

        this.options = new ArrayList<>();
        this.options.add("");
    }

    public void select(int index) {
        this.selectedIndex = index;
    }

    public String value() {
        return this.options.get(this.selectedIndex);
    }

}
