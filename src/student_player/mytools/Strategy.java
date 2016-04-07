package student_player.mytools;

import hus.HusBoard;
import hus.HusBoardState;
import hus.HusMove;

import java.util.ArrayList;
import java.util.List;

import student_player.mytools.MyTools;
/**
 * Created by marcyang on 2016-04-05.
 */
public class Strategy {


    int m_player_id;
    int m_opponent_id;

    enum PitType{
        MOVABLE,
        UNMOVABLE,
        FRONT,
        BACK,
        ALL,
        CAPTURED,
        UNCAPTURED

    }


    public Strategy(int player_id, int opponent_id) {

        this.m_player_id = player_id;
        this.m_opponent_id = opponent_id;
    }

    /**
     * Min-max algorithm with heuristic function: //heuristic function for minimax = totalpits + current largest capture
     *                                                      + # movable pits after the move - # of unmovable pits
     * This Min max is referenced from the pseudo code in wikipedia
     * @param move
     * @param depth
     * @param maximizingPlayer
     * @param heuristicValue
     * @return
     */


    public int alphabetaMinimax(HusBoardState boardState, HusMove move, int depth, int alpha, int beta, boolean maximizingPlayer, int heuristicValue) {

        //see the effect of each move

        HusBoardState cloned_board_state = (HusBoardState) boardState.clone();
        cloned_board_state.move(move);


        int[] myPits = cloned_board_state.getPits()[m_player_id];
        int[] opPits = cloned_board_state.getPits()[m_opponent_id];

        ArrayList<HusMove> legalMoves = cloned_board_state.getLegalMoves();


        if (depth == 0) {
            return heuristicValue;
        }

        if (cloned_board_state.getWinner() == m_player_id) {
            return Integer.MAX_VALUE;
        }

        if (cloned_board_state.getWinner() == m_opponent_id) {
            return Integer.MIN_VALUE;
        }

        if (maximizingPlayer) {

            List<PotentialOutCome> lg_list = MyTools.ColumnWithLargestSum(opPits, MyTools.Outcome.GAIN);

            //largest number of rocks that I can capture with that move
            PotentialOutCome gainoutcome = MyTools.potentialOutCome(lg_list, myPits, MyTools.Outcome.GAIN);

            int bestValue = getTotalRocks(myPits, PitType.ALL)
                    + gainoutcome.rocks
                    + numOfPitsLeft(myPits, PitType.MOVABLE)
                    - numOfPitsLeft(myPits, PitType.UNMOVABLE);


            for (int i = 0; i < legalMoves.size(); i++) {
                bestValue = Math.max(bestValue, alphabetaMinimax(cloned_board_state, legalMoves.get(i), depth - 1, alpha, beta, false, bestValue));
                alpha = Math.max(alpha, bestValue);
                //beta cut-off
                if (beta <= alpha) {
                    break;
                }
            }

            return bestValue;

        } else {

            List<PotentialOutCome> loss_list = MyTools.ColumnWithLargestSum(myPits, MyTools.Outcome.GAIN);
            //largest number of rocks that I can capture with that move
            PotentialOutCome loss_outcome = MyTools.potentialOutCome(loss_list, opPits, MyTools.Outcome.GAIN);

            int bestValue = getTotalRocks(myPits, PitType.ALL) - loss_outcome.rocks
                    + numOfPitsLeft(myPits, PitType.MOVABLE)
                    - numOfPitsLeft(myPits, PitType.UNMOVABLE);

            for (int i = 0; i < legalMoves.size(); i++) {

                bestValue = Math.min(bestValue, alphabetaMinimax(cloned_board_state, legalMoves.get(i), depth - 1, alpha, beta, true, bestValue));
                beta = Math.min(beta, bestValue);
                //alpha cut-off
                if (beta <= alpha) {
                    break;
                }
            }
            return bestValue;
        }
    }// end of minimax 1

    /**
     * This is for defensive strategy --> if the ratio is be
     * @param move
     * @param depth
     * @param maximizingPlayer
     * @param heuristicValue
     * @return
     */

    public int minimaxDefensive(HusBoardState boardState,  HusMove move, int depth, boolean maximizingPlayer, int heuristicValue) {

        HusBoardState cloned_board_state = (HusBoardState) boardState.clone();

        cloned_board_state.move(move);

        int[] myPits = cloned_board_state.getPits()[m_player_id];
        int[] opPits = cloned_board_state.getPits()[m_opponent_id];

        ArrayList<HusMove> legalMoves = cloned_board_state.getLegalMoves();

        if (depth == 0) {
            return heuristicValue;
        }

        if (cloned_board_state.getWinner() == m_player_id) {
            return Integer.MAX_VALUE;
        }

        if (cloned_board_state.getWinner() == m_opponent_id) {
            return Integer.MIN_VALUE;
        }
        if (maximizingPlayer) {
            // new heuristic function designed for defensive strategy
            int bestValue = getTotalRocks(myPits, PitType.ALL)
                    + numOfPitsLeft(myPits, PitType.UNMOVABLE)
                    + rocksCapturedOrNot(myPits, opPits, PitType.CAPTURED);


            for (int i = 0; i<legalMoves.size(); i++) {

                int v = minimaxDefensive(cloned_board_state, legalMoves.get(i), depth - 1, false, bestValue);
                bestValue = Math.max(bestValue, v);
            }
            return bestValue;
        }
        else {
            int bestValue = getTotalRocks(myPits, PitType.ALL)
                    - rocksCapturedOrNot(myPits, opPits, PitType.UNCAPTURED)
                    - numOfPitsLeft(myPits, PitType.MOVABLE);


            for (int i = 0; i < legalMoves.size(); i++) {
                int v = minimaxDefensive(cloned_board_state, legalMoves.get(i), depth-1, true, bestValue);
                bestValue = Math.min(bestValue, v);
            }
            return bestValue;
        }

    }


    /**
     *   check the number of pits left that are still movable or unmovable, which can show
     *   how close to losing/ winning
     * @param pits
     * @param type
     * @return
     */


    public int numOfPitsLeft(int[] pits, PitType type) {
        int numpits = 0;

        for (int i = 0; i < pits.length/2; i++) {

            if (type == PitType.MOVABLE) {
                if (pits[i] >1 || pits[pits.length- 1 - i] > 1) {
                    numpits = numpits + 1;
                }
            }//Unmovable pits
            else {
                if (pits[i] <= 1 || pits[pits.length- 1 - i] <= 1) {
                    numpits = numpits + 1;
                }
            }
        }

        return numpits;
    }

    /**
     * Calculate ALL the rocks that a player has
     * Calculate just the inner/front row
     * Calculate just the outer/back row ==>defensive strategy
     * @param pits
     * @param type
     * @return
     */

    public int getTotalRocks(int[] pits, PitType type) {

        int my_totalRocks = 0;
        for (int i = 0; i < pits.length/2; i++) {
            if (type == PitType.ALL) {
                my_totalRocks = my_totalRocks + pits[i] + pits[pits.length - 1 - i];
            }
            else if (type == PitType.FRONT) {
                my_totalRocks = my_totalRocks + pits[pits.length - 1 - i];
            }
            else if (type == PitType.BACK) {
                my_totalRocks = my_totalRocks + pits[i];
            }
            else {
                //should never get here
            }
        }
        return my_totalRocks;
    }


    public int rocksCapturedOrNot(int[] pits, int[]oppits, PitType type) {

        int numRocks = 0;

        for (int i = 0; i< pits.length/2; i++) {
            int enemy_pit = (pits.length - 1 - (pits.length-1-i)) + pits.length / 2;

            if (type == PitType.UNCAPTURED) {

                //enemy's pit --> check if enemy's pit is empty
                if (pits[(pits.length-1-i)] != 0 && oppits[enemy_pit] == 0) {
                    numRocks = numRocks + pits[i] + pits[(pits.length-1-i)];
                }
            }
            else if (type == PitType.CAPTURED) {
                //if my front pits are available for capture
                //check if the enemy can capture my pits
                if ((pits.length-1-i) != 0) {
                    for (int j = 2; j <= enemy_pit; j++) {
                        // temp variable for backtrack enemy pit
                        int tempPit = enemy_pit - j;
                        //if the number of rocks is equal to the number of moves required to that pit
                        if (oppits[tempPit] == j && oppits[enemy_pit] != 0) {
                            numRocks = numRocks + pits[i] + pits[(pits.length - 1 - i)];
                        }
                    }
                }
            }
            else {
                //should never get here
            }


        }



        return numRocks;
    }






}//end of class
