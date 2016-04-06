package student_player.mytools;

import hus.HusBoardState;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by marcyang on 2016-04-05.
 */
public class Pot_Gain implements Runnable {

    private volatile ArrayList<PotentialTwoStep> pg_list = new ArrayList<PotentialTwoStep>();
    private volatile PotentialOutCome second_pg;
    ArrayList<PotentialOutCome> potentialMoves;
    HusBoardState cloned_board_state;
    int player_id;
    int opponent_id;


    public Pot_Gain(int player_id, int opponent_id, HusBoardState board_state, ArrayList<PotentialOutCome> pg) {
        this.potentialMoves = pg;
        this.player_id = player_id;
        this.cloned_board_state = (HusBoardState) board_state.clone();
        this.opponent_id = opponent_id;
    }

    @Override
    public void run() {



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