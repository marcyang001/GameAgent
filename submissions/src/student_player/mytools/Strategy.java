package student_player.mytools;


import hus.HusBoardState;
import hus.HusMove;

import java.util.ArrayList;


/**
 * Created by marcyang on 2016-04-05.
 */
public class Strategy {


    int m_player_id;
    int m_opponent_id;

    enum PitType{
        MOVABLE,
        CAPTURED,
        UNCAPTURED

    }


    public Strategy(int player_id, int opponent_id) {

        this.m_player_id = player_id;
        this.m_opponent_id = opponent_id;
    }

    /**
     * Alpha beta prunning algorithm with heuristic function: //heuristic function for minimax = totalpits + current largest capture
     *                                                      + # movable pits after the move - # of unmovable pits
     * The pseudo code og alpha-beta pruning is referenced from the pseudo code in wikipedia
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

            PackagePit pitPack = getTotalRocks(myPits);


            int bestValue = pitPack.my_totalRocks
                    + rocksCapturedOrNot(opPits, myPits, PitType.CAPTURED)
                    + pitPack.my_movableRocks
                    - pitPack.my_unmovableRocks;


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

            PackagePit pitPack = getTotalRocks(myPits);

            int bestValue = pitPack.my_totalRocks
                    - rocksCapturedOrNot(myPits, opPits, PitType.CAPTURED)
                    + pitPack.my_movableRocks
                    - pitPack.my_unmovableRocks;

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
     * This is for defensive strategy --> if the ratio is between [0.6, 0.7)
     * @param move
     * @param depth
     * @param maximizingPlayer
     * @param heuristicValue
     * @return
     */

    public int alphaBetaDefensive(HusBoardState boardState,  HusMove move, int depth, int alpha, int beta, boolean maximizingPlayer, int heuristicValue) {

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

            PackagePit pitInfo = getTotalRocks(myPits);
            // new heuristic function designed for defensive strategy
            int bestValue = pitInfo.my_totalRocks
                    + rocksCapturedOrNot(myPits, opPits, PitType.UNCAPTURED)
                    + pitInfo.my_movableRocks
                    + pitInfo.my_backRocks;


            for (int i = 0; i<legalMoves.size(); i++) {

                bestValue = Math.max(bestValue, alphaBetaDefensive(cloned_board_state, legalMoves.get(i), depth - 1, alpha, beta, false, bestValue));
                alpha = Math.max(alpha, bestValue);
                //beta cut-off
                if (beta <= alpha) {
                    break;
                }
            }
            return bestValue;
        }
        else {

            PackagePit pitInfo = getTotalRocks(myPits);

            int bestValue = pitInfo.my_totalRocks
                    - pitInfo.my_unmovableRocks
                    - rocksCapturedOrNot(myPits, opPits, PitType.CAPTURED)
                    - pitInfo.my_frontRocks;

            for (int i = 0; i < legalMoves.size(); i++) {
                bestValue = Math.min(bestValue, alphaBetaDefensive(cloned_board_state, legalMoves.get(i), depth - 1, alpha, beta, true, bestValue));
                beta = Math.min(beta, bestValue);
                //alpha cut-off
                if (beta <= alpha) {
                    break;
                }
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
     * @return a package that has some pit information
     */

    public PackagePit getTotalRocks(int[] pits) {

        int my_totalRocks = 0;
        int my_frontRocks = 0;
        int my_backRocks = 0;

        int my_movableRocks = 0;
        int my_unmovableRocks = 0;

        for (int i = 0; i < pits.length/2; i++) {



            my_totalRocks = my_totalRocks + pits[i] + pits[pits.length - 1 - i];



            my_frontRocks = my_frontRocks + pits[pits.length - 1 - i];


            my_backRocks = my_backRocks + pits[i];




            if (pits[i] >1 || pits[pits.length- 1 - i] > 1) {
                my_movableRocks = my_movableRocks + 1;
            }


            if (pits[i] <= 1 || pits[pits.length- 1 - i] <= 1) {
                my_unmovableRocks = my_unmovableRocks + 1;
            }



        }


        PackagePit pitpack = new PackagePit(my_totalRocks,my_frontRocks, my_backRocks,my_movableRocks,my_unmovableRocks);

        return pitpack;
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
