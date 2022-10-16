import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
        takeSurvey();
    }

    public static void takeSurvey() {
        Participant participant = new Participant();
        ArrayList<Question> questionnaire = new ArrayList<>();
        for (Question question : questionnaire) {
            int[] answer = question.askQuestionGetAnswer();
            participant.incrementDemocraticPoints(answer[0]);
            participant.incrementRepublicanPoints(answer[1]);
            participant.incrementGreenPoints(answer[2]);
            participant.incrementLibertarianPoints(answer[3]);
            participant.questionsAnswered++;
            if (participant.questionsAnswered >= 5) {
                //TODO
            }
        }

    }

}