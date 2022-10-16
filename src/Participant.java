public class Participant {
    private int confirmedParty = -1;

    public int questionsAnswered = 0;
    private int democraticPoints = 0;
    private int republicanPoints = 0;
    private int greenPoints = 0;
    private int libertarianPoints = 0;

    public Participant() {}

    public void incrementDemocraticPoints(int points) { democraticPoints += points; }
    public void incrementRepublicanPoints(int points) { republicanPoints += points; }
    public void incrementGreenPoints(int points) { greenPoints += points; }
    public void incrementLibertarianPoints(int points) { libertarianPoints += points; }

    public boolean hasConfirmedParty() { return confirmedParty != -1; }

    public void setConfirmedParty(int partyType) { confirmedParty = partyType; }

    public boolean isLikelyDemocrat() {
        return democraticPoints > (republicanPoints + greenPoints + libertarianPoints);
    }

    public boolean isLikelyRepublican() {
        return republicanPoints > (democraticPoints + greenPoints + libertarianPoints);
    }

    public boolean isLikelyGreen() {
        return greenPoints > (democraticPoints + republicanPoints + libertarianPoints);
    }

    public boolean isLikelyLibertarian() {
        return libertarianPoints > (democraticPoints + republicanPoints + greenPoints);
    }
}
