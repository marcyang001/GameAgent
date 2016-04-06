package student_player;

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
        // Get the contents of the pits so we can use it to make decisions.
        int[][] pits = board_state.getPits();



        int[] my_pits = pits[player_id];
        int[] op_pits = pits[opponent_id];
        //the number associated with my_pit[0] is the number of rocks in that specific hole


        HusMove move = null;


        // Get the legal moves for the current board state.
        ArrayList<HusMove> moves = board_state.getLegalMoves();

        Strategy strategy = new Strategy(board_state, player_id, opponent_id);


        //System.out.println("Current enemy rock number: " + MyTools.getMyTotalRocks(op_pits));

        //first move => get the greatest relay
        if (player_id == board_state.firstPlayer() && board_state.getTurnNumber() == 0) {
            move = moves.get(0);
        }
        else {

            //apply minmax
            if (board_state.getTurnNumber() == 0) {
                System.out.println("Turn 1 !!!!!!!!");

                int pitTOPlay = MyTools.randomLegalMove(moves.size());
                //System.out.println("random pit :" + pitTOPlay);

                int bestValue = -100000;
                for (int i = 0; i < moves.size(); i++) {

                    int heuristic = strategy.minimax(moves.get(0),3,true,0);

                    if (heuristic > bestValue) {
                        pitTOPlay = moves.get(0).getPit();
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
                ArrayList<PotentialOutCome> pg = MyTools.potentialMoves(tm, my_pits, MyTools.Outcome.GAIN);


                //main thread is responsible for calculating the potential loss
                //get the list of my columns
                List<PotentialOutCome> tm_loss = MyTools.ColumnWithLargestSum(my_pits, MyTools.Outcome.LOSS);
                //find the potential loss
                PotentialOutCome pl = MyTools.potentialOutCome(tm_loss, op_pits, MyTools.Outcome.LOSS);

                double ratio = MyTools.myRockToOpRockRatio(my_pits, op_pits);

                if ((ratio >= 0.8 && ratio <= 2.0)) {
                    System.out.println(" 0.8 <=RATIO <= 2.0");
                    int pit_to_play = MyTools.randomLegalMove(moves.size());
                    if (pg.size() > 0) {
                        pit_to_play = pg.get(0).pitToMove;
                    }
                    else if (pl.pitToMove != -1){
                        pit_to_play = pl.pitToMove;
                    }

                    System.out.println("random pit :" + pit_to_play);

                    int bestValue = -100000;
                    for (int i = 0; i < moves.size(); i++) {

                        int heuristic = strategy.minimax(moves.get(0), 5, true, 0);

                        if (heuristic > bestValue) {
                            pit_to_play = moves.get(i).getPit();
                        }
                    }

                    move = new HusMove(pit_to_play, player_id);
                    System.out.println("Pit chosen by minimax: " + pit_to_play);

                }
                else if (ratio > 2.0) {
                    System.out.println("RaTIO > 2.0");
                    move = moves.get(MyTools.randomLegalMove(moves.size()));

                    if (pg.size() > 0 && pl.pitToMove != -1) {

                        //when potential gain >= potential loss
                        if (pg.get(0).rocks >= pl.rocks) {


                            int pit_to_play = pg.get(0).pitToMove;

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
                    else if (pg.size() > 0) {
                        //attack only
                        int pit_to_play = pg.get(0).pitToMove;

                        move = new HusMove(pit_to_play, player_id);

                        System.out.println("ONLY attack TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pg.get(0).pitToMove);
                    } else if (pl.pitToMove != -1) {
                        //defend only
                        //move the inner row
                        move = new HusMove(pl.pitToMove, player_id);
                        System.out.println("Only Defend TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pl.pitToMove);

                    } else {
                        //no gain no loss
                        //plan two steps ahead

                        int tempMaxPit = -1;

                        int nextPitToMove = -1;

                        if (tm_loss.size() > 0) {

                            for (int i = 0; i < tm_loss.size(); i++) {
                                if (MyTools.mintTwoReplays(my_pits, tm_loss.get(i).pitToMove)) {

                                    if (tempMaxPit < my_pits[tm_loss.get(i).pitToMove]) {

                                        nextPitToMove = tm_loss.get(i).pitToMove;
                                        tempMaxPit = my_pits[tm_loss.get(i).pitToMove];
                                    }
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
                //ratio < 0.8
                else  {
                    System.out.println("RaTIO < 0.8");
                    //defensive strategy
                    int pit_to_play = MyTools.randomLegalMove(moves.size());

                    if (pl.pitToMove != -1){
                        pit_to_play = pl.pitToMove;
                    }
                    else if (pg.size() > 0) {
                        pit_to_play = pg.get(0).pitToMove;
                    }
                    int bestValue = -100000;
                    for (int i = 0; i < moves.size(); i++) {

                        int heuristic = strategy.minimax(moves.get(0), 3, true, 0);

                        if (heuristic > bestValue) {
                            pit_to_play = moves.get(i).getPit();
                        }
                    }

                    move = new HusMove(pit_to_play, player_id);


                    //move = moves.get(MyTools.randomLegalMove(moves.size()));
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

        // But since this is a placeholder algorithm, we won't act on that information.
        return move;
    }



}