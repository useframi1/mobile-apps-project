package edu.aucegypt.gymwya;

public class Sport {
    private String sportName;
    private boolean isIndividual;

    public Sport(String sportName, boolean isIndividual) {
        this.sportName = sportName;
        this.isIndividual = isIndividual;
    }

    public String getSportName() {
        return sportName;
    }

    public boolean getIsIndividual() {
        return isIndividual;
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