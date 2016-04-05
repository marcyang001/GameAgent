package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.*;

import student_player.mytools.MyTools;
import student_player.mytools.PotentialOutCome;
import student_player.mytools.PotentialTwoStep;

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


        final HusBoardState f_board_state = (HusBoardState) board_state.clone();
        final int[] my_pits = pits[player_id];
        final int[] op_pits = pits[opponent_id];
        //the number associated with my_pit[0] is the number of rocks in that specific hole


        HusMove move = null;




        // Get the legal moves for the current board state.
        ArrayList<HusMove> moves = board_state.getLegalMoves();

        //System.out.println("Current enemy rock number: " + MyTools.getMyTotalRocks(op_pits));

        //first move => get the greatest relay
        //randomly choose the first and second move
        if (board_state.getTurnNumber() == 0) {
            move = moves.get(0);
        }
        else {
            if (board_state.getTurnNumber() == 1) {
                List<PotentialOutCome> list_of_attacks = MyTools.ColumnWithLargestSum(my_pits, MyTools.Outcome.LOSS);
                int whichMove = MyTools.randomLegalMove(list_of_attacks.size());

                move = new HusMove(list_of_attacks.get(whichMove).pitToMove, player_id);


            } else {
                //create a new thread, which is responsible for the potential gain

                class Pot_Gain implements Runnable {

                    private volatile ArrayList<PotentialTwoStep> pg_list = new ArrayList<PotentialTwoStep>();
                    private volatile PotentialOutCome second_pg;
                    ArrayList<PotentialOutCome> potentialMoves;

                    public Pot_Gain(ArrayList<PotentialOutCome> pg) {
                        this.potentialMoves = pg;
                    }

                    @Override
                    public void run() {

                        //get the list of largest enemy columns
                        //List<PotentialOutCome> tm_largest = MyTools.ColumnWithLargestSum(op_pits, MyTools.Outcome.GAIN);
                        //find the potential gain of the current step
                        //ArrayList<PotentialOutCome> potentialMoves = MyTools.potentialMoves(tm_largest, my_pits, MyTools.Outcome.GAIN);



                        if (potentialMoves.size() > 0) {
                            //assignment temporarily the move with most gain
                            second_pg = potentialMoves.get(0);
                            System.out.println("Current PIT: " + second_pg.pitToMove);
                            //loop over all the steps of potential gain in order to check
                            // which one has the greatest potential loss after first move
                            // look for two steps ahead
                            for (int i = 0; i < potentialMoves.size(); i++) {
                                //make a move
                                HusMove p_move = new HusMove(potentialMoves.get(i).pitToMove, player_id);

                                //try to see the effect of potential move
                                HusBoardState cloned_board_state = new HusBoardState();
                                cloned_board_state = (HusBoardState) f_board_state.clone();
                                cloned_board_state.move(p_move);

                                //calculate the move with greatest potential loss at the second step
                                List<PotentialOutCome> second_steps = MyTools.ColumnWithLargestSum(cloned_board_state.getPits()[player_id], MyTools.Outcome.LOSS);


                                if (second_steps.size() > 0) {

                                    //PotentialOutCome potentialGainAfterTwoSteps = MyTools.potentialOutCome(second_steps, cloned_board_state.getPits()[player_id], MyTools.Outcome.GAIN);
                                    PotentialOutCome potentialLossAfterTwoSteps = MyTools.potentialOutCome(second_steps, cloned_board_state.getPits()[opponent_id], MyTools.Outcome.LOSS);
                                    //if there exists a great potential loss at the second step, add it to the queue

                                    if (potentialLossAfterTwoSteps != null) {
                                        //System.out.println("GREAT POTENTIAL LOSS AT STEP TWO: pit#" + potentialLossAfterTwoSteps.pitToMove);
                                        PotentialTwoStep p_twoSteps = new PotentialTwoStep(potentialMoves.get(i).pitToMove,potentialLossAfterTwoSteps.rocks, 0);
                                        pg_list.add(p_twoSteps);

                                    }
                                }


                            }
                            //sort the list in ascending order so that the winning move with the least potential loss gets played
                            if (pg_list.size() > 0) {
                                System.out.println("use the move with least potential loss");
                                //sort in ascending order. make sure to get the move with least potential loss after two steps
                                Collections.sort(pg_list, new Comparator<PotentialTwoStep>() {
                                    @Override
                                    public int compare(PotentialTwoStep o1, PotentialTwoStep o2) {
                                        //sort the list in descending order
                                        return o1.potentialLoss - o2.potentialLoss;
                                    }
                                });
                                second_pg = new PotentialOutCome(pg_list.get(0).firstPitToMove, pg_list.get(0).potentialLoss);
                                System.out.println("Changed the PIT: " + second_pg.pitToMove);
                            }
                            else {
                                System.out.println("Choose the current move");
                            }
                        }

                    }

                    public PotentialOutCome getPg() {




                        System.out.println("calling another thread: using the pit # " + second_pg.pitToMove);
                        return second_pg;
                    }
                }


                //get the list of largest enemy columns
                List<PotentialOutCome> tm = MyTools.ColumnWithLargestSum(op_pits, MyTools.Outcome.GAIN);
                //find the potential gain
                ArrayList<PotentialOutCome> pg = MyTools.potentialMoves(tm, my_pits, MyTools.Outcome.GAIN);


                //main thread is responsible for calculating the potential loss
                //get the list of my columns
                List < PotentialOutCome > tm_loss = MyTools.ColumnWithLargestSum(my_pits, MyTools.Outcome.LOSS);
                //find the potential loss
                PotentialOutCome pl = MyTools.potentialOutCome(tm_loss, op_pits, MyTools.Outcome.LOSS);


                Pot_Gain t_gain = null;
                if (pg.size() > 0) {
                    //create a thread to calculate potential gain of two step ahead
                    t_gain = new Pot_Gain(pg);
                    Thread cal_gain = new Thread(t_gain);
                    cal_gain.start();

                    try {
                        cal_gain.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
               // if (pl != null) {
               //     System.out.println("Potential Loss: " + pl.rocks);
               // }

                if (pg.size() > 0 && pl != null) {

                    //when potential gain >= potential loss
                    if (pg.get(0).rocks >= pl.rocks) {


                        int pit_to_play = pg.get(0).rocks;
                        double ratio = MyTools.myRockToOpRockRatio(my_pits, op_pits);
                        if (t_gain != null && (ratio > 0.8 && ratio < 2.0)) {
                            System.out.println("!!!!!use the changed value ");
                            pit_to_play = t_gain.getPg().pitToMove;
                        }

                        //System.out.println("One move gain: " + pg.get(0).pitToMove + "Second step gain: " + pg_two.pitToMove);
                        //attack/ capture
                        move = new HusMove(pit_to_play, player_id);
                        System.out.println("Attack TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pit_to_play);

                    } else {
                        //when potential loss > potential gain
                        //defend:
                        //compare whether to move the inner row or outer row.
                        //sumValue = 7, inner row is 2, outer row is 5, move the outer row
                        move = new HusMove(pl.pitToMove, player_id);
                        System.out.println("Defend TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pl.pitToMove);
                    }

                } else if (pg.size() > 0) {
                    //attack only
                    int pit_to_play = pg.get(0).rocks;
                    double ratio = MyTools.myRockToOpRockRatio(my_pits, op_pits);
                    if (t_gain != null && (ratio > 0.8 && ratio < 2.0)) {
                        System.out.println("!!!!!use the changed value ");
                        pit_to_play = t_gain.getPg().pitToMove;
                    }
                    //attack/ capture
                    move = new HusMove(pit_to_play, player_id);

                    System.out.println("only attack TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pg.get(0).pitToMove);
                } else if (pl != null) {
                    //defend only
                    //move the inner row
                    move = new HusMove(pl.pitToMove, player_id);
                    System.out.println("Only Defend TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pl.pitToMove);

                } else {
                    //no gain no loss

                    //plan two steps ahead

                    int tempMaxPit = -1;

                    int nextPitToMove = -1;

                    if (tm_loss.size()> 0) {

                        for (int i = 0; i < tm_loss.size(); i++) {

                            //System.out.print(tm_loss.get(i).pitToMove + " ");
                            if (MyTools.mintTwoReplays(my_pits, tm_loss.get(i).pitToMove)) {

                                if (tempMaxPit < my_pits[tm_loss.get(i).pitToMove]) {

                                    nextPitToMove = tm_loss.get(i).pitToMove;
                                    tempMaxPit = my_pits[tm_loss.get(i).pitToMove];
                                }
                            }
                        }
                        //System.out.println();
                    }

                    if (nextPitToMove != -1) {

                        System.out.println("Choose pit " + nextPitToMove);
                        move = new HusMove(nextPitToMove, player_id);

                    }else {
                        move = moves.get(MyTools.randomLegalMove(moves.size()));
                    }



                    System.out.println("Random Move");



                    // We can see the effects of a move like this...
                    //HusBoardState cloned_board_state = (HusBoardState) board_state.clone();
                    //cloned_board_state.move(move);




                }
                //check legal move
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