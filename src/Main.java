import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
        takeSurvey();
    }

    public static void takeSurvey() {
        Participant participant = new Participant();
        boolean partyIsKnown = false;
        ArrayList<Question> questionnaire = new ArrayList<>();
        questionnaire.add(new Question("Hi?", "Yes", "No", "Maybe", "So"));
        for (Question question : questionnaire) {
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
                    if (!partyIsKnown) {
                        participant.couldBeDemocrat = false;
                        participant.resetDemocraticPoints();
                    }
                } else if (participant.isLikelyRepublican() && participant.couldBeRepublican) {
                    partyIsKnown = participant.confirmOrDenyParty(1);
                    if (!partyIsKnown) {
                        participant.couldBeRepublican = false;
                        participant.resetRepublicanPoints();
                    }
                } else if (participant.isLikelyGreen() && participant.couldBeGreen) {
                    partyIsKnown = participant.confirmOrDenyParty(2);
                    if (!partyIsKnown) {
                        participant.couldBeGreen = false;
                        participant.resetGreenPoints();
                    }
                } else if (participant.isLikelyLibertarian() && participant.couldBeLibertarian) {
                    partyIsKnown = participant.confirmOrDenyParty(3);
                    if (!partyIsKnown) {
                        participant.couldBeLibertarian = false;
                        participant.resetLibertarianPoints();
                    }
                } else if (!participant.couldBeDemocrat && !participant.couldBeRepublican
                        && !participant.couldBeGreen && !participant.couldBeLibertarian) {
                    System.out.println();
                }
            }
        }
    }

}