//Simple TicTacToe game based on Hyperskill.org coding school project
//Begin game with Input commands: start user easy

import java.util.Scanner;
import java.util.Random;
class player{
    String playerType;
    private char mark;
    char markOther;

    public player(String playerType, char mark) {
        this.playerType = playerType;
        this.mark = mark;
        this.markOther = this.mark=='X'?'O':'X'; //Other players mark
    }

    String getPlayerType() {return playerType;}
    void setPlayerType(String playerType) {this.playerType = playerType; }
    char getMark() {return mark; }
    char getMarkOther() { return markOther;}
}

//This is my second project on the path to learn Java.  My first project with some minor principals of object oriented design.
public class Main {
    enum stateType { //Possible states the game can be in. GNF = Game Not Finished
        START, GNF, IMPOSSIBLE, EXIT, DRAW, XWIN, OWIN
    }
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        char[] board = new char[9];
        boolean playing = true;
        player p1= new player("easy",'X');
        player p2= new player("easy",'O');
        String[] userCmd = new String[3];
        stateType state = stateType.START;
        int turn=0;

        while (playing) {
            switch (state) {
                case START:
                    userCmd = newGame();
                    board = "         ".toCharArray();
                    turn = 0;
                    if ("start".equals(userCmd[0])){
                        state = stateType.GNF;
                        System.out.println("Set state to GNF");
                        p1.setPlayerType(userCmd[1]);
                        p2.setPlayerType(userCmd[2]);
                        displayBoard(board);
                    }
                    else {state = stateType.EXIT;}
                    break;
                case GNF:

                    if (turn%2==0){ //even turn count means X's turn
                        board = makeAmove(board,p1,turn);
                    }
                    else{
                        board = makeAmove(board,p2,turn);
                    }
                    turn++;
                    displayBoard(board);
                    break;
                case DRAW:
                    System.out.println("Game was a draw.");
                    state = stateType.START;
                    break;
                case EXIT:
                    System.out.println("Thanks for playing.");
                    playing = false;
                    break;
                case XWIN:
                    System.out.println("X Wins.");
                    state = stateType.START;
                    break;
                case OWIN:
                    System.out.println("O Wins.");
                    state = stateType.START;
                    break;
                default:  //Also IMPOSSIBLE state
                    System.out.println("Game experienced a problem.");
                    state = stateType.START;
            }

            if (state == stateType.START||state == stateType.EXIT) {
                //If Start then do NOT update state and go to beginning of loop
                }
            else {//Else update the state
                state = checkState(board, turn);
            }
        }
    }

    private static stateType checkState(char[] bd, int turnC) {
        //inRows represents all eight 3-in-a-row options for winning
        int[][] inRows = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
        String testRow;
        boolean xwin = false;
        boolean owin = false;
        /* for each 3inRow combo labeled 'tri' */
        for (int[] tri:inRows){
            testRow= String.valueOf(bd[tri[0]])+String.valueOf(bd[tri[1]])+String.valueOf(bd[tri[2]]);
            if ("XXX".equals(testRow)) {
                xwin = true;
            } else if (testRow.equals("OOO")){
                owin = true;
            }
        }
        if (xwin&&owin){
             return stateType.IMPOSSIBLE;}
        else if (xwin){
            return stateType.XWIN;}
        else if (owin){
             return stateType.OWIN;}
        else if (turnC>=9){
            return stateType.DRAW;}
        else{
            return stateType.GNF;}
    }

    private static void displayBoard(char[] board) {
        System.out.println("---------");
        System.out.println("| "+ board[0]+" "+board[1]+" "+board[2]+" |");
        System.out.println("| "+ board[3]+" "+board[4]+" "+board[5]+" |");
        System.out.println("| "+ board[6]+" "+board[7]+" "+board[8]+" |");
        System.out.println("---------");
    }

    private static char[] makeAmove(char[] bd,player play,int turnC){

        switch(play.playerType){
            case "user":
                return moveUser(bd,play.getMark());
             case "easy":
                return moveAIeasy(bd,play);
            case "medium":
                return moveAImedium(bd,play,turnC);
            case"hard":
                return moveAIhard(bd,play.getMark(),turnC);
            default:
            //False case that is impossible
                return  "error".toCharArray();

        }
    }

    private static char[] moveAIhard(char[] bd, char pMark, int turnC) {

        char[] possibleBd;
        int possibleScore;
        int bestScore=-11; //initialized to worse than worse score
        int spot = 0;//spot of space
        int bestSpot=0;
        for (char open : bd) {
            possibleBd = bd.clone();
            if (open == ' ') {
                possibleBd[spot] = pMark;
                possibleScore = minimax(possibleBd, pMark, turnC,true);
                if (possibleScore > bestScore) {
                    bestScore = possibleScore;
                    bestSpot = spot;
            }
            }
            spot++;
        }

        bd[bestSpot]=pMark;
        return bd;
    }
    private static int minimax(char[] bd, char pMark, int turnC, boolean isMax){
        stateType result = checkState(bd,turnC);
        if (result==stateType.OWIN||result==stateType.XWIN){
            return isMax?10-turnC:-10+turnC; //If we are maximizing then score as 10 otherwise -10
            //subtracting turnC means we "discount" the win if it is made after more turns.
        }
        else if (result == stateType.DRAW){
            return 0;
        }
        else{ //If that move made in moveAIhard didn't end the game, give iterate other player moves.
            char[] nextBd = moveAIhard(bd,pMark=='X'?'O':'X',turnC++);
            return minimax(nextBd,pMark=='X'?'O':'X',turnC,!isMax);
        }
    }

    private static char[] moveAIeasy(char[] bd, player play) {
            Random random = new Random();
            int move = random.nextInt(9);
            if (Character.isWhitespace(bd[move])){
                System.out.println("Making move level \""+play.getPlayerType()+"\"");
                bd[move] = play.getMark();
                return bd;
            }
            else{ //Try again
                return moveAIeasy(bd,play);
            }


    }

    private static char[] moveAImedium(char[] bd, player p, int turnC){
        if(turnC<3||turnC==8) { //If it is early or the last move
            return moveAIeasy(bd, p);
        }

        char[] checkerBd;
        int spot=0;//spot of space
        //Check if opponent can win or you can win
        for (char mark:bd){
            checkerBd = bd.clone();
            if (mark==' '){ //If empty, try the your mark there
                checkerBd[spot]=p.getMark();
                //If moving your AI mark here wins, move here
                if ((checkState(checkerBd,0)==stateType.OWIN)||(checkState(checkerBd,0)==stateType.XWIN)){
                    bd[spot]=p.getMark();
                    System.out.println("Making move level \""+p.getPlayerType()+"\" to win");
                    return bd;
                }
                //If moving the opponet here wins, block here.
                checkerBd[spot]=p.getMarkOther(); //Opposite players mark
                if ((checkState(checkerBd,0)==stateType.OWIN)||(checkState(checkerBd,0)==stateType.XWIN)){
                    bd[spot]=p.getMark();
                    System.out.println("Making move level \""+p.getPlayerType()+"\" to block");
                    return bd;
                }

                //else try the next ' ' on the board.
            }
            spot++;
        }





    return moveAIeasy(bd,p);

    }

    private static char[] moveUser(char[] bd, char mark) {
        String coords = "";
        boolean goodInput = false;
        int playRow;
        int playCol;
        int newMove;

        while (!goodInput) {
            System.out.print("Enter the coordinates:");
            coords = scanner.nextLine();
            try {
                playCol = Integer.parseInt(coords.substring(0,1));
                playRow = Integer.parseInt(coords.substring(2,3));
            }
            catch (NumberFormatException e)
            {
                System.out.println("You should enter numbers!");
                continue;
            }
            if (playRow<0||playRow>3||playCol>3||playCol<0){
                System.out.println("Input Col Row for next move: ei. 1 3");
                continue;
            }
            newMove = (3 - playRow) * 3 + playCol - 1;
            if (Character.isWhitespace(bd[newMove])){
                bd[newMove] = mark;
                goodInput = true;
            }
            else{
                System.out.println("This cell is occupied! Choose another one!");
            }
        }
        return bd;
    }

    private static  String[] newGame() {

            String uInput;
            String[] inA= new String[3] ;
            String command="";
            String play1="";
            String play2="";
            boolean needInput = true;
            do {
                System.out.print("Input commands: ");
                uInput = scanner.nextLine(); //expecting 'start user easy/med/hard'
                try {
                    command = uInput.split(" ")[0];
                    play1 = uInput.split(" ")[1];
                    play2 = uInput.split(" ")[2];
                } catch (ArrayIndexOutOfBoundsException exception ) {
                    //try again.
                }
                if ("exit".equals(command)){
                    inA[0] = "exit";
                    break; //inA
                }
                else if ("start".equals(command) ) {
                    if ("user".equals(play1) || "easy".equals(play1) || "medium".equals(play1)|| "hard".equals(play1)){
                        if ("user".equals(play2) || "easy".equals(play2) || "medium".equals(play2)|| "hard".equals(play2)){
                            needInput = false;
                                inA[0]=command;
                                inA[1]=play1;
                                inA[2]=play2;
                            break;
                        }
                    }
                }
                else {
                    System.out.println("Bad Input- try (start/exit) user/easy/medium user/easy/medium");
                }
            }
            while(needInput);

            return inA;
    }

}

