import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        takeSurvey();
    }

    public static void takeSurvey() {
        Participant participant = new Participant();
        boolean partyIsKnown = false;
        int knownParty = -1;
        int numberOfGuesses = 0;
        ArrayList<Question> questionnaire = new ArrayList<>();
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        for (int i = 0; i < questionnaire.size(); i++) {
            Question question = questionnaire.get(i);
            if (partyIsKnown) break;
            int[] answer = question.askQuestionGetAnswer();
            if (participant.couldBeDemocrat) participant.incrementDemocraticPoints(answer[0]);
            if (participant.couldBeRepublican) participant.incrementRepublicanPoints(answer[1]);
            if (participant.couldBeGreen) participant.incrementGreenPoints(answer[2]);
            if (participant.couldBeLibertarian) participant.incrementLibertarianPoints(answer[3]);
            participant.questionsAnswered++;
            if (participant.questionsAnswered >= 5) {
                if (participant.isLikelyDemocrat() && participant.couldBeDemocrat) {
                    partyIsKnown = participant.confirmOrDenyParty(0);
                    numberOfGuesses++;
                    if (!partyIsKnown) {
                        participant.couldBeDemocrat = false;
                        participant.resetDemocraticPoints();
                    } else {
                        knownParty = 0;
                    }
                } else if (participant.isLikelyRepublican() && participant.couldBeRepublican) {
                    partyIsKnown = participant.confirmOrDenyParty(1);
                    numberOfGuesses++;
                    if (!partyIsKnown) {
                        participant.couldBeRepublican = false;
                        participant.resetRepublicanPoints();
                    } else {
                        knownParty = 1;
                    }
                } else if (participant.isLikelyGreen() && participant.couldBeGreen) {
                    partyIsKnown = participant.confirmOrDenyParty(2);
                    numberOfGuesses++;
                    if (!partyIsKnown) {
                        participant.couldBeGreen = false;
                        participant.resetGreenPoints();
                    } else {
                        knownParty = 2;
                    }
                } else if (participant.isLikelyLibertarian() && participant.couldBeLibertarian) {
                    partyIsKnown = participant.confirmOrDenyParty(3);
                    numberOfGuesses++;
                    if (!partyIsKnown) {
                        participant.couldBeLibertarian = false;
                        participant.resetLibertarianPoints();
                    } else {
                        knownParty = 3;
                    }
                } else if (!participant.couldBeDemocrat && !participant.couldBeRepublican
                        && !participant.couldBeGreen && !participant.couldBeLibertarian) {
                    System.out.println("You are not a member of any of the parties we have surveyed, so we could not guess.");
                    return;
                }
            }
        }
        if (knownParty != -1) {
            String participantParty = knownParty == 0 ? "Democratic" : knownParty == 1 ? "Republican" : knownParty == 2 ? "Green" : "Libertarian";
            String plural = numberOfGuesses == 1 ? "try" : "tries";
            System.out.printf("We have correctly guessed that you are in the %s in %d party in %s!\n", participantParty, numberOfGuesses, plural);
        } else {
            knownParty = getParty();
            participant.confirmParty(knownParty);
        }
        for (Question question : questionnaire) {
            question.storeResult(knownParty);
        }
        System.out.println("Your results have been logged. Thank you for your participation.");
    }

    private static int getParty() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(
                """
                        What is your political party?\s
                          A: Democratic Party
                          B: Republican Party
                          C: Green Party
                          D: Libertarian Party
                        """);
        String input = scanner.nextLine().trim().toLowerCase();
        while(!input.matches("[abcd]")) {
            System.out.print("please choose: 'a', 'b', 'c', 'd'...");
            input = scanner.nextLine().trim().toLowerCase();
        }
        return input.matches("a") ? 0 : input.matches("b") ? 1 : input.matches("c") ? 2 : 3;
    }



}