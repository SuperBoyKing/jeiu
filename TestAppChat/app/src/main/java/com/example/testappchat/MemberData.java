package com.example.testappchat;

public class MemberData {

    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Add on empty constructor so we can later parse JSON into MemberData using Jackson
    public MemberData() {

    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
