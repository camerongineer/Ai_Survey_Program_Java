import java.io.*;
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
        ArrayList<Question> questionnaire = loadQuestionnaire();
        for (int i = 0; i < questionnaire.size(); i++) {
            Question question = questionnaire.get(i);
            if (partyIsKnown) break;
            int[] answer = question.askQuestionGetAnswer();
            if (participant.couldBeDemocrat) participant.incrementDemocraticPoints(answer[0]);
            if (participant.couldBeRepublican) participant.incrementRepublicanPoints(answer[1]);
            if (participant.couldBeGreen) participant.incrementGreenPoints(answer[2]);
            if (participant.couldBeLibertarian) participant.incrementLibertarianPoints(answer[3]);
            participant.questionsAnswered++;
            if (participant.questionsAnswered >= 3) {
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
            System.out.printf("We have correctly guessed that you are in the %s party in %d %s!\n", participantParty, numberOfGuesses, plural);
        } else {
            knownParty = getParty();
            participant.confirmParty(knownParty);
        }
        for (Question question : questionnaire) {
            question.storeResult(knownParty);
        }
        System.out.println("Your results have been logged. Thank you for your participation.");
        saveTally(questionnaire);
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

    private static ArrayList<Question> loadQuestionnaire() {
        ArrayList<Question> questionnaire = new ArrayList<>() {
            @Override
            public boolean contains(Object o) {
                return super.contains(o);
            }
        };
        File questions = new File("questions.txt");
        try {
            FileReader fr = new FileReader(questions);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                Question question = new Question(Integer.parseInt(line.split(" :: ")[0]),
                        line.split(" :: ")[1],
                        line.split(" :: ")[2],
                        line.split(" :: ")[3],
                        line.split(" :: ")[4],
                        line.split(" :: ")[5]);
                questionnaire.add(question);
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Unable to load Questionnaire");
            throw new RuntimeException(e);
        }
        File tally = new File("tally.txt");
        try {
            FileReader fr = new FileReader(tally);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                int questionID = Integer.parseInt(line.split(" :: ")[0]);
                String tallyStr = line.split(" :: ")[1];
                String[] tallySplit = tallyStr.split(",");
                int count = 0;
                int[][] choiceTally = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
                for (int i = 0; i < choiceTally.length; i++) {
                    for (int j = 0; j < choiceTally[i].length; j++) {
                        choiceTally[i][j] = Integer.parseInt(tallySplit[count++]);
                    }
                }
                for (Question question : questionnaire) {
                    if (question.questionID == questionID) {
                        question.setChoiceTally(choiceTally);
                    }
                }
                line = br.readLine();
            }
        } catch (IOException io) {
            System.out.println("Unable to distribute tally");
            throw new RuntimeException(io);
        }
        return questionnaire;
    }

    private static void saveTally(ArrayList<Question> questionnaire) {
        File tally = new File("tally.txt");
        try(FileWriter fw = new FileWriter(tally)) {
            for (Question question : questionnaire) {
                fw.write(String.format("%d :: %s\n", question.questionID, question.choiceTallyPrintout()));
            }
        } catch (IOException e) {
            System.out.println("Unable to save the tally");
            throw new RuntimeException(e);
        }
    }

}