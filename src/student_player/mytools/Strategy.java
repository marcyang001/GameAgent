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


    HusBoardState m_board_state;
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


    public Strategy(HusBoardState board_state, int player_id, int opponent_id) {
        this.m_board_state = (HusBoardState) board_state.clone();
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


    public int minimax(HusMove move, int depth, boolean maximizingPlayer, int heuristicValue) {



        try {
            if (m_board_state.move(move)) {
                if (depth == 0) {
                    return heuristicValue;
                }

                if (m_board_state.getWinner() == m_player_id) {
                    return Integer.MAX_VALUE;
                }

                if (m_board_state.getWinner() == m_opponent_id) {
                    return Integer.MIN_VALUE;
                }

                if (maximizingPlayer) {
                    int bestValue;


                    List<PotentialOutCome> lg_list = MyTools.ColumnWithLargestSum(m_board_state.getPits()[m_opponent_id], MyTools.Outcome.GAIN);

                    //largest number of rocks that I can capture with that move
                    PotentialOutCome outcome = MyTools.potentialOutCome(lg_list, m_board_state.getPits()[m_player_id], MyTools.Outcome.GAIN);

                    //heuristic function
                    bestValue = getTotalRocks(m_board_state.getPits()[m_player_id], PitType.ALL) + outcome.rocks +
                            numOfPitsLeft(m_board_state.getPits()[m_player_id], PitType.MOVABLE) -  numOfPitsLeft(m_board_state.getPits()[m_player_id], PitType.UNMOVABLE);


                    for (int i = 0; i < m_board_state.getLegalMoves().size(); i++) {
                        int v = minimax(m_board_state.getLegalMoves().get(i), depth - 1, false, bestValue);
                        bestValue = Math.max(bestValue, v);
                    }

                    //System.out.println(" max best value " + bestValue);
                    return bestValue;
                } else {

                    int bestValue;
                    List<PotentialOutCome> loss_list = MyTools.ColumnWithLargestSum(m_board_state.getPits()[m_player_id], MyTools.Outcome.GAIN);

                    //biggest loss that I can have with that move
                    PotentialOutCome outcome = MyTools.potentialOutCome(loss_list, m_board_state.getPits()[m_opponent_id], MyTools.Outcome.GAIN);
                    bestValue = getTotalRocks(m_board_state.getPits()[m_player_id], PitType.ALL) - outcome.rocks
                            - numOfPitsLeft(m_board_state.getPits()[m_player_id], PitType.MOVABLE) + numOfPitsLeft(m_board_state.getPits()[m_player_id], PitType.UNMOVABLE);


                    for (int i = 0; i < m_board_state.getLegalMoves().size(); i++) {
                        int v = minimax(m_board_state.getLegalMoves().get(i), depth - 1, true, bestValue);
                        bestValue = Math.min(bestValue, v);
                    }
                    //System.out.println("min best value " + bestValue);

                    return bestValue;
                }
            } else {
                return 0;
            }

        } catch (IllegalArgumentException e) {

            return 0;

        }

    }//end of minmax 1

    public int minimaxDefensive(HusMove move, int depth, boolean maximizingPlayer, int heuristicValue) {

        int[] myPits = m_board_state.getPits()[m_player_id];
        int[] opPits = m_board_state.getPits()[m_opponent_id];

        ArrayList<HusMove> legalMoves = m_board_state.getLegalMoves();

        if (depth == 0) {
            return heuristicValue;
        }

        if (m_board_state.getWinner() == m_player_id) {
            return Integer.MAX_VALUE;
        }

        if (m_board_state.getWinner() == m_opponent_id) {
            return Integer.MIN_VALUE;
        }
        if (maximizingPlayer) {
            // new heuristic function designed for defensive strategy
            int bestValue = getTotalRocks(m_board_state.getPits()[m_player_id], PitType.ALL)
                    + numOfPitsLeft(m_board_state.getPits()[m_player_id], PitType.UNMOVABLE)
                    + rocksCapturedOrNot(myPits, opPits, PitType.CAPTURED);
            //+ numOfPitsLeft(m_board_state.getPits()[m_player_id], PitType.MOVABLE)

            for (int i = 0; i<legalMoves.size(); i++) {
                int v = minimaxDefensive(legalMoves.get(i), depth - 1, false, bestValue);
                bestValue = Math.max(bestValue, v);
            }
            return bestValue;
        }
        else {
            int bestValue = getTotalRocks(m_board_state.getPits()[m_player_id], PitType.ALL)
                    - rocksCapturedOrNot(myPits, opPits, PitType.UNCAPTURED)
                    - numOfPitsLeft(m_board_state.getPits()[m_player_id], PitType.MOVABLE);
                    //- numOfPitsLeft(m_board_state.getPits()[m_player_id], PitType.UNMOVABLE);

            for (int i = 0; i < legalMoves.size(); i++) {
                int v = minimaxDefensive(legalMoves.get(i), depth-1, true, bestValue);
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
