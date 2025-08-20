//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.util.Scanner;
class Chat {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ChatterBot[] bots = new ChatterBot[2];

        String[] Bot1_legal_replies = {"say "+ ChatterBot.PLACEHOLDER_FOR_REQUESTED_PHRASE +
                "? okay: " +  ChatterBot.PLACEHOLDER_FOR_REQUESTED_PHRASE
                ,"alright here we go:" + ChatterBot.PLACEHOLDER_FOR_REQUESTED_PHRASE};

        String[] Bot2_legal_replies = {"pfft okay:" + ChatterBot.PLACEHOLDER_FOR_REQUESTED_PHRASE ,
                "whatever you say:" +  ChatterBot.PLACEHOLDER_FOR_REQUESTED_PHRASE};

        String[] Bot1_illegal_replies = {"what" + ChatterBot.PLACEHOLDER_FOR_ILLEGAL_REQUEST,
                "say what I should say" + ChatterBot.PLACEHOLDER_FOR_ILLEGAL_REQUEST};
        String[] Bot2_illegal_replies = {"whaaat" + ChatterBot.PLACEHOLDER_FOR_ILLEGAL_REQUEST,
                "say say" + ChatterBot.PLACEHOLDER_FOR_ILLEGAL_REQUEST};

        bots[0] = new ChatterBot("Ori",Bot1_legal_replies,Bot1_illegal_replies);
        bots[1] = new ChatterBot("Alex",Bot2_legal_replies,Bot2_illegal_replies);

        String statement = "hey";

        int curr_bot = 0;
        while(true) {
            statement = bots[curr_bot].replyTo(statement);
            System.out.println(bots[curr_bot].getName() + ": " + statement);
            String wait = sc.nextLine();
            curr_bot = 1 - curr_bot;
        }



    }
}