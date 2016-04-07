package student_player;

import hus.HusBoard;
import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.*;

import student_player.mytools.MyTools;
import student_player.mytools.PotentialOutCome;
import student_player.mytools.Strategy;


/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {

    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260531701"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state){

        long startTime = System.currentTimeMillis();

        // Get the contents of the pits so we can use it to make decisions.
        int[][] pits = board_state.getPits();

        int[] my_pits = pits[player_id];
        int[] op_pits = pits[opponent_id];
        //the number associated with my_pit[0] is the number of rocks in that specific hole




        HusMove move = null;


        // Get the legal moves for the current board state.
        ArrayList<HusMove> moves = board_state.getLegalMoves();


        /**
         * this object is for min max strategy as well as defensive minmax 
         */
        Strategy strategy = new Strategy(player_id, opponent_id);

        //HusBoardState clone_board = (HusBoardState) board_state.clone();

        //strategy.minimaxDefensive(board_state, moves.get(0), 2, true, 0);


        //first move => get the greatest relay
        if (player_id == board_state.firstPlayer() && board_state.getTurnNumber() == 0) {
            move = moves.get(0);
        }
        else {

            //apply minmax when you already know a move of the opponent
            if (board_state.getTurnNumber() == 0) {

                int pitTOPlay = moves.get(0).getPit();
                System.out.println("random pit :" + pitTOPlay);

                int bestValue = -100000;
                for (int i = 0; i < moves.size(); i++) {

                    int alpha = Integer.MIN_VALUE;
                    int beta = Integer.MAX_VALUE;

                    int possibleHeuristics = MyTools.getTotalRocks(my_pits) + MyTools.possibleCapture(my_pits, op_pits, moves.get(i).getPit());
                    int heuristic = strategy.alphabetaMinimax(board_state, moves.get(i), 2, alpha, beta,true, possibleHeuristics);
                    if (heuristic > bestValue) {
                        pitTOPlay = moves.get(i).getPit();
                        bestValue = heuristic;
                    }
                }

                System.out.println("Pit chosen by minimax: " + pitTOPlay);
                move = new HusMove(pitTOPlay, player_id);

            }
            else {
                //create a new thread, which is responsible for the potential gain


                //get the list of largest enemy columns
                List<PotentialOutCome> tm = MyTools.ColumnWithLargestSum(op_pits, MyTools.Outcome.GAIN);
                //find the potential gain
                PotentialOutCome pg = MyTools.potentialOutCome(tm, my_pits, MyTools.Outcome.GAIN);


                //main thread is responsible for calculating the potential loss
                //get the list of my columns
                List<PotentialOutCome> tm_loss = MyTools.ColumnWithLargestSum(my_pits, MyTools.Outcome.LOSS);
                //find the potential loss
                PotentialOutCome pl = MyTools.potentialOutCome(tm_loss, op_pits, MyTools.Outcome.LOSS);

                /**
                 * this ratio determines which strategy to use
                 * this is the ratio of my rocks / opponent's rocks
                 *
                 */


                double ratio = MyTools.myRockToOpRockRatio(my_pits, op_pits);


                if ((ratio >= 0.7 && ratio <= 2.0)) {
                    System.out.println(" 0.8 <=RATIO <= 2.0");
                    int pit_to_play = moves.get(MyTools.randomLegalMove(moves.size())).getPit();


                    System.out.println("random pit :" + pit_to_play);
                    int depth;
                    if (board_state.getTurnNumber() < 30) {
                        depth = 4;
                    }
                    else {
                        depth = 8;
                        System.out.println("Depth is 8");
                    }
                    int bestValue = -100000;

                    for (int i = 0; i < moves.size(); i++) {

                        int alpha = Integer.MIN_VALUE;
                        int beta = Integer.MAX_VALUE;
                        int possibleHeuristic = MyTools.getTotalRocks(my_pits) + MyTools.possibleCapture(my_pits, op_pits, moves.get(i).getPit());


                        int heuristic = strategy.alphabetaMinimax(board_state, moves.get(i), depth, alpha, beta, true, possibleHeuristic);

                        if (heuristic > bestValue) {
                            pit_to_play = moves.get(i).getPit();
                            bestValue = heuristic;
                        }
                    }

                    move = new HusMove(pit_to_play, player_id);
                    System.out.println("Pit chosen by minimax: " + pit_to_play);

                }

                // ratio of 36 / 60 at least
                else if (ratio >= 0.6 && ratio < 0.7) {

                    move = moves.get(MyTools.randomLegalMove(moves.size()));
                    int pit_to_play = move.getPit();

                    //int possibleHeuristics = MyTools.getTotalRocks(my_pits);
                    int depth;
                    if (board_state.getTurnNumber() < 30) {
                        depth = 8;
                    }
                    else if (board_state.getTurnNumber() >= 30 && board_state.getTurnNumber() <= 50) {
                        depth = 6;
                    }
                    else {
                        depth = 4;
                    }
                    int bestValue = -100000;
                    //implement minmax with new heuristic function
                    for (int i = 0; i < moves.size(); i++) {

                        int heuristics = strategy.minimaxDefensive(board_state, moves.get(i),depth,false, 0);

                        //get the maximum value of heuristics from all the moves
                        if (heuristics > bestValue) {
                            pit_to_play = moves.get(i).getPit();
                            //loop back and compare
                            bestValue = heuristics;
                        }
                    }
                    System.out.println("Defensive Min max!!!!! ");
                    move = new HusMove(pit_to_play, player_id);

                }

                /**
                 * when my player is dominating the random agent or he is losing so badly,
                 * it is better to use the greedy method for the next best move
                 */
                else {

                    System.out.println("RATIO: " + ratio);
                    move = moves.get(MyTools.randomLegalMove(moves.size()));

                    if (pg.pitToMove != 1 && pl.pitToMove != -1) {

                        //when potential gain >= potential loss
                        if (pg.rocks >= pl.rocks) {


                            int pit_to_play = pg.pitToMove;

                            //attack/ capture
                            move = new HusMove(pit_to_play, player_id);
                            System.out.println("ATTACK TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pit_to_play);

                        } else {
                            //when potential loss > potential gain
                            //defend:
                            //compare whether to move the inner row or outer row.
                            //sumValue = 7, inner row is 2, outer row is 5, move the outer row
                            move = new HusMove(pl.pitToMove, player_id);
                            System.out.println("Defend TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pl.pitToMove);
                        }

                    }

                    else if (pg.pitToMove != -1 && pl.pitToMove == -1) {
                        //attack only
                        int pit_to_play = pg.pitToMove;

                        move = new HusMove(pit_to_play, player_id);

                        System.out.println("ONLY attack TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pg.pitToMove);
                    }
                    else if (pl.pitToMove != -1) {
                        //defend only
                        //move the inner row
                        move = new HusMove(pl.pitToMove, player_id);
                        System.out.println("Only Defend TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pl.pitToMove);

                    } else {
                        //no gain no loss
                        //try to fill the blank pits

                        int tempMaxPit = -1;

                        int nextPitToMove = -1;


                        for (int i = 0; i < moves.size(); i++) {
                            if (MyTools.mintTwoReplays(my_pits, moves.get(i).getPit())) {
                                if (tempMaxPit < my_pits[moves.get(i).getPit()]) {
                                    nextPitToMove = moves.get(i).getPit();
                                    tempMaxPit = my_pits[moves.get(i).getPit()];
                                }
                            }
                        }

                        if (nextPitToMove != -1) {

                            System.out.println("Choose pit " + nextPitToMove);
                            move = new HusMove(nextPitToMove, player_id);

                        } else {
                            move = moves.get(MyTools.randomLegalMove(moves.size()));
                        }

                        System.out.println("Random Move");

                    }
                }

                //check legal move just in case of error (testing purposes)
                if (board_state.isLegal(move)) {
                    System.out.println("Legal move");
                    return move;
                } else {
                    System.out.println("Not legal move");
                    move = moves.get(MyTools.randomLegalMove(moves.size()));
                }

            }
        }

        long endTime   = System.currentTimeMillis();
        long totalTime = (long) ((endTime - startTime)/1000.0);


        if (totalTime > 2.0) {
            System.out.println("Move time out ");
            board_state.gameOver();
        }

        // But since this is a placeholder algorithm, we won't act on that information.
        return move;
    }



}