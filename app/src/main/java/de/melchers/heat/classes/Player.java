package de.melchers.heat.classes;

public class Player {
    int lastPlacement = 0;
    int totalScore = 0;
    String name;
    int seasons;
    public Player(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastPlacement() {
        return lastPlacement;
    }

    public void setLastPlacement(int lastPlacement) {
        this.lastPlacement = lastPlacement;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getSeasons() {
        return seasons;
    }

    public void setSeasons(int seasons) {
        this.seasons = seasons;
    }
}
