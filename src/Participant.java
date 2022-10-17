import java.util.Scanner;

public class Participant {
    private int confirmedParty = -1;
    public int questionsAnswered = 0;
    private int democraticPoints = 0;
    private int republicanPoints = 0;
    private int greenPoints = 0;
    private int libertarianPoints = 0;
    public boolean couldBeDemocrat = true;
    public boolean couldBeRepublican = true;
    public boolean couldBeGreen = true;
    public boolean couldBeLibertarian = true;

    public Participant() {}

    public void incrementDemocraticPoints(int points) { democraticPoints += points; }
    public void incrementRepublicanPoints(int points) { republicanPoints += points; }
    public void incrementGreenPoints(int points) { greenPoints += points; }
    public void incrementLibertarianPoints(int points) { libertarianPoints += points; }
    public void resetDemocraticPoints() { democraticPoints = 0; }
    public void resetRepublicanPoints() { republicanPoints = 0; }
    public void resetGreenPoints() { greenPoints = 0; }
    public void resetLibertarianPoints() { libertarianPoints = 0; }

    public void confirmParty(int party) { confirmedParty = party; }

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

    public boolean confirmOrDenyParty(int partyGuess) {
        String partyName = partyGuess == 0 ? "Democratic" : partyGuess == 1 ? "Republican" : partyGuess == 2 ? "Green" : "Libertarian";
        Scanner scanner = new Scanner(System.in);
        System.out.printf("The surveyor would like to know if you are in the %s party\n" +
                "Please answer 'yes' or 'no'... ", partyName);
        String input = scanner.nextLine().trim().toLowerCase();
        while (!input.matches(("yes|no"))) {
            System.out.print("The surveyor does not understand, please answer 'yes' or 'no'... ");
            input = scanner.nextLine().trim().toLowerCase();
        }
        if (input.matches("yes")) {
            confirmedParty = partyGuess;
            return true;
        } else {
            return false;
        }
    }
}
