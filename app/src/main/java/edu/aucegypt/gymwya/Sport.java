package edu.aucegypt.gymwya;

public class Sport {
    private String sportName;
    private boolean isIndividual;
    private SportIcon sportIcon;

    public Sport(String sportName, boolean isIndividual, SportIcon sportIcon) {
        this.sportName = sportName;
        this.isIndividual = isIndividual;
        this.sportIcon = sportIcon;
    }

    public String getSportName() {
        return sportName;
    }

    public int getSportImage() {
        return sportIcon.id;
    }

    public boolean getIsIndividual() {
        return isIndividual;
    }

    public static class SportIcon {
        public int id;
        public boolean isPressed;

        public SportIcon(int id, boolean isPressed) {
            this.id = id;
            this.isPressed = isPressed;
        }
    }
}