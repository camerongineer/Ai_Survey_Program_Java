import java.util.Scanner;

public class Question {
    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private int[][] choiceTally = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
    private int answerGiven = -1;


    public Question(String question, String choice1, String choice2, String choice3, String choice4) {
        this.question = question;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
    }

    public int[] askQuestionGetAnswer() {
        String prompt = this + "\n Please select your answer: 'a', 'b', 'c', or 'd'... ";
        System.out.print(prompt);
        while(true) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.matches("repeat")) {
                System.out.print(prompt);
            } else if (!input.matches("[abcd]")) {
                System.out.print("This is not a valid question, please choose: 'a', 'b', 'c', 'd' or 'repeat'... ");
            } else {
                switch (input) {
                    case "a" -> answerGiven = 0;
                    case "b" -> answerGiven = 1;
                    case "c" -> answerGiven = 2;
                    case "d" -> answerGiven = 3;
                }
                return choiceTally[answerGiven];
            }
        }
    }

    @Override
    public String toString() {
        return String.format(
                """
                        
                        %s
                          A. %s
                          B. %s
                          C. %s
                          D. %s
                        """
                , question, choice1, choice2, choice3, choice4);
    }
}
