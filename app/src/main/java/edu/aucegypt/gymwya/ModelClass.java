package edu.aucegypt.gymwya;

public class ModelClass {
    private String sportName;
    private int sportImage;

    private boolean isIndividual;

    public ModelClass(String sportName, int sportImage, boolean isIndividual) {
        this.sportName = sportName;
        this.sportImage = sportImage;
        this.isIndividual = isIndividual;
    }

    public String getSportName() {
        return sportName;
    }

    public int getSportImage() {
        return sportImage;
    }

    public boolean getIsIndividual() {return isIndividual;}
}
