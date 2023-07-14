package edu.aucegypt.gymwya;

public class Sport {
    String sportName;
    boolean isIndividual;
    SportIcon sportIcon;

    public Sport(String sportName, boolean isIndividual, SportIcon sportIcon) {
        this.sportName = sportName;
        this.isIndividual = isIndividual;
        this.sportIcon = sportIcon;
    }

    public static class SportIcon {
        public int id;
        public boolean isPressed;

        public SportIcon(int id) {
            this.id = id;
            this.isPressed = false;
        }
    }
}