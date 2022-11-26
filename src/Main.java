import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(
                "Hello, would you be interested in to taking a political survey?\n" +
                "Please answer 'yes' or 'no'..");
        String response = scanner.nextLine().trim().toLowerCase();
        while (!response.matches("yes|no")) {
            System.out.print("Please answer 'yes' or 'no'..");
            response = scanner.nextLine().trim().toLowerCase();
        }
        if (response.equals("no")) {
            System.out.println("Ok, thank you anyway!");
        } else {
            System.out.print("Great! What is your name? ");
            String name = scanner.nextLine().trim();
            while (name.isBlank()) {
                System.out.print("This name is not valid, please tell me your name..");
                name = scanner.nextLine().trim();
            }
            System.out.printf("Great! Thank you %s, let's get started!\n", name);
            takeSurvey();
            System.out.printf("Thanks for your time, %s. I hope you have a great day!", name);
        }
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
            // At least 3 questions are asked to give some leeway to the participant, but this can be lowered or raised
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
            String plural = numberOfGuesses == 1 ? "try" : "tries";
            System.out.printf("We have correctly guessed that you are in the %s party in %d %s!\n",
                    participant.displayPartyName(knownParty), numberOfGuesses, plural);
        } else {
            knownParty = getParty();
            participant.confirmParty(knownParty);
        }
        // Results get stored for each question here once the party is known
        for (Question question : questionnaire) {
            question.storeResult(knownParty);
        }
        if ((knownParty == 0 && !participant.couldBeDemocrat) ||
                (knownParty == 1 && !participant.couldBeRepublican) ||
                (knownParty == 2 && !participant.couldBeGreen) ||
                (knownParty == 3 && !participant.couldBeLibertarian)) {
            System.out.println("Hmm, it seems you did not give an accurate answer earlier..");
        } else {
            System.out.println("Your results have been logged. Thank you for your participation.");
            saveTally(questionnaire);
        }
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
                for (int i = 0; i < 4; i++) {
                    question.choice1Tally.put(i, 0);
                    question.choice2Tally.put(i, 0);
                    question.choice3Tally.put(i, 0);
                    question.choice4Tally.put(i, 0);
                }
                questionnaire.add(question);
                line = br.readLine();
            }
        } catch (IOException e) {
            questionnaire = loadInternalQuestions();
        }
        File democraticTally = new File("democratic_tally.txt");
        File republicanTally = new File("republican_tally.txt");
        File greenTally = new File("green_tally.txt");
        File libertarianTally = new File("libertarian_tally.txt");
        ArrayList<ArrayList<String>> questionsAnswersTallies = new ArrayList<>();
        try(
                FileReader dr = new FileReader(democraticTally);
                BufferedReader dbr = new BufferedReader(dr);
                FileReader rr = new FileReader(republicanTally);
                BufferedReader rbr = new BufferedReader(rr);
                FileReader gr = new FileReader(greenTally);
                BufferedReader gbr = new BufferedReader(gr);
                FileReader lr = new FileReader(libertarianTally);
                BufferedReader lbr = new BufferedReader(lr);
        ) {
            String dLine = dbr.readLine();
            String rLine = rbr.readLine();
            String gLine = gbr.readLine();
            String lLine = lbr.readLine();
            for (int i = 0; i < 4; i++) { questionsAnswersTallies.add(new ArrayList<>()); }
            while (dLine != null && rLine != null && gLine != null && lLine != null) {
                questionsAnswersTallies.get(0).add(dLine);
                questionsAnswersTallies.get(1).add(rLine);
                questionsAnswersTallies.get(2).add(gLine);
                questionsAnswersTallies.get(3).add(lLine);
                dLine = dbr.readLine();
                rLine = rbr.readLine();
                gLine = gbr.readLine();
                lLine = lbr.readLine();
            }
        } catch (IOException io) {
            return questionnaire;
        }
        for (int i = 0; i <questionnaire.size(); i++) {
            Question question = questionnaire.get(i);
            String[] dTally = questionsAnswersTallies.get(0).get(i).split(" :: ");
            if (Integer.parseInt(dTally[0]) == question.questionID) {
                question.choice1Tally.put(0, Integer.parseInt(dTally[1]));
                question.choice2Tally.put(0, Integer.parseInt(dTally[2]));
                question.choice3Tally.put(0, Integer.parseInt(dTally[3]));
                question.choice4Tally.put(0, Integer.parseInt(dTally[4]));
            }
            String[] rTally = questionsAnswersTallies.get(1).get(i).split(" :: ");
            if (Integer.parseInt(rTally[0]) == question.questionID) {
                question.choice1Tally.put(1, Integer.parseInt(rTally[1]));
                question.choice2Tally.put(1, Integer.parseInt(rTally[2]));
                question.choice3Tally.put(1, Integer.parseInt(rTally[3]));
                question.choice4Tally.put(1, Integer.parseInt(rTally[4]));
            }
            String[] gTally = questionsAnswersTallies.get(2).get(i).split(" :: ");
            if (Integer.parseInt(gTally[0]) == question.questionID) {
                question.choice1Tally.put(2, Integer.parseInt(gTally[1]));
                question.choice2Tally.put(2, Integer.parseInt(gTally[2]));
                question.choice3Tally.put(2, Integer.parseInt(gTally[3]));
                question.choice4Tally.put(2, Integer.parseInt(gTally[4]));
            }
            String[] lTally = questionsAnswersTallies.get(3).get(i).split(" :: ");
            if (Integer.parseInt(lTally[0]) == question.questionID) {
                question.choice1Tally.put(3, Integer.parseInt(lTally[1]));
                question.choice2Tally.put(3, Integer.parseInt(lTally[2]));
                question.choice3Tally.put(3, Integer.parseInt(lTally[3]));
                question.choice4Tally.put(3, Integer.parseInt(lTally[4]));
            }
        }
        return questionnaire;
    }

    /* This function only exists for the purposes to the assignment to prevent errors,
    questions should be loaded from the questions.txt file
     */
    private static ArrayList<Question> loadInternalQuestions() {
        ArrayList<Question> questionnaire = new ArrayList<>();
        ArrayList<String> questions = new ArrayList<>();
        questions.add("1 :: Which thought on gun control best describes your stance? :: The right to own firearms is subject to reasonable regulation :: Ease rules on interstate concealed gun carry :: Restrict police use of guns and all forms of control weapons :: Repeal all gun control laws and regulation of weapons");
        questions.add("2 :: How should we handle the budget & economy? :: Restore the budget discipline of the 1990s :: End executive overreach and impose limits on spending :: Increase affordable housing & protect renter’s rights :: Reduce taxes, spending, and eliminate controls on trade");
        questions.add("3 :: What is your thought on drug laws? :: We should support policies that will reorient our public safety approach toward prevention :: Jail time and school drug testing deters drug use :: It's time for legalization and treatment of abuse :: De-fund war on drugs, and end violent drug cartels");
        questions.add("4 :: Which of these would you say best describes your position on illegal immigration? :: There should be a path for undocumented aliens to earn citizenship :: A porous border puts our nation at risk :: All seeking asylum should be given permanent resident status :: We should eliminate all restrictions on immigration");
        questions.add("5 :: How should health care be handled in America? :: We must provide affordable public option through ACA :: We should repeal ObamaCare :: There should be universal Single-Payer Health Care for everyone in the US :: We should restore and revive a free market health care system");
        questions.add("6 :: Where do you stand on the Environment? :: Climate change poses a real and urgent threat to our economy, our national security, and our children’s health and future :: Policy that supports conservation must equally address economic growth and development :: We need to replace fracking, coal power stations, subsidies to fossil fuels and nuclear with the clean green efficient renewable energy of the future :: I disagree with the notion that capitalism is the cause of global warming and the government has the solution");
        questions.add("7 :: What should be done about the topic of abortion? :: I strongly and unequivocally support Roe v. Wade :: We need to defund Planned Parenthood :: We must offer abortion services and contraception free to women :: Government should be kept out of the matter of abortion");
        questions.add("8 :: How should we help the impoverished and homeless? :: We need to take aggressive steps to increase affordable housing :: I oppose government run welfare programs for the poor, and believe it encourages laziness and dependence on the government :: We must enact a Homeless Bill of Rights to address affordable housing crisis :: We should only help poor via voluntary charitable efforts");
        questions.add("9 :: What is your stance on free trade policies? :: We must pursue a trade policy that puts workers first :: The US must promote open markets and expand free trade. Not only does trade grow the American economy, but it spreads global freedom. :: There must be international trade policies that respect the planet's ecology and peoples' social needs :: Government efforts to control or manage trade are improper");
        questions.add("10 :: How should we handle criminal laws in this country? :: We must end mass incarceration & reform criminal justice :: Criminals behind bars cannot harm the public :: We should focus on crime prevention instead of harsher sentences :: We should repeal all victimless crime laws as in gambling, drugs, sex, etc");
        questions.add("11 :: What thought on Social Security best describes your stance? :: Protecting the promise of Social Security is absolute :: We should make retirement saving options voluntary, portable, & secure :: I oppose the privatization of Social Security :: Replace the Social Security system with a private system");
        for (String line : questions) {
            Question question = new Question(Integer.parseInt(line.split(" :: ")[0]),
                    line.split(" :: ")[1],
                    line.split(" :: ")[2],
                    line.split(" :: ")[3],
                    line.split(" :: ")[4],
                    line.split(" :: ")[5]);
            for (int j = 0; j < 4; j++) {
                question.choice1Tally.put(j, 0);
                question.choice2Tally.put(j, 0);
                question.choice3Tally.put(j, 0);
                question.choice4Tally.put(j, 0);
            }
            questionnaire.add(question);
        }
        return questionnaire;
    }


    private static void saveTally(ArrayList<Question> questionnaire) {
        File democraticTally = new File("democratic_tally.txt");
        File republicanTally = new File("republican_tally.txt");
        File greenTally = new File("green_tally.txt");
        File libertarianTally = new File("libertarian_tally.txt");
        try(
                FileWriter dw = new FileWriter(democraticTally);
                FileWriter rw = new FileWriter(republicanTally);
                FileWriter gw = new FileWriter(greenTally);
                FileWriter lw = new FileWriter(libertarianTally)
        ) {
            for (Question question : questionnaire) {
                dw.write(String.format("%d :: %d :: %d :: %d :: %d\n",
                        question.questionID,
                        question.choice1Tally.get(0),
                        question.choice2Tally.get(0),
                        question.choice3Tally.get(0),
                        question.choice4Tally.get(0)));
                rw.write(String.format("%d :: %d :: %d :: %d :: %d\n",
                        question.questionID,
                        question.choice1Tally.get(1),
                        question.choice2Tally.get(1),
                        question.choice3Tally.get(1),
                        question.choice4Tally.get(1)));
                gw.write(String.format("%d :: %d :: %d :: %d :: %d\n",
                        question.questionID,
                        question.choice1Tally.get(2),
                        question.choice2Tally.get(2),
                        question.choice3Tally.get(2),
                        question.choice4Tally.get(2)));
                lw.write(String.format("%d :: %d :: %d :: %d :: %d\n",
                        question.questionID,
                        question.choice1Tally.get(3),
                        question.choice2Tally.get(3),
                        question.choice3Tally.get(3),
                        question.choice4Tally.get(3)));
            }
        } catch (IOException e) {
            System.out.println("Unable to save the tally");
            throw new RuntimeException(e);
        }
    }

}