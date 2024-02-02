package de.melchers.heat.models;

public class TableData {
    String name;
    int id;
    int totalScore;

    public TableData(String name, int id, int totalScore) {
        this.id = id;
        this.name = name;
        this.totalScore = totalScore;
    }
}
