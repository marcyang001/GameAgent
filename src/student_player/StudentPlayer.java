package student_player;

import hus.HusBoard;
import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.*;

import student_player.mytools.MyTools;
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


        /**
         * this object is for min max strategy as well as defensive minmax 
         */
        Strategy strategy = new Strategy(player_id, opponent_id);

        //HusBoardState clone_board = (HusBoardState) board_state.clone();

        //strategy.minimaxDefensive(board_state, moves.get(0), 2, true, 0);


        /**
         * first move and first player => get the greatest relay
         */

        if (player_id == board_state.firstPlayer() && board_state.getTurnNumber() == 0) {
            move = moves.get(0);
        }
        else {

            /**
             * when the player is played second, choose a good move based on one opponent move
             */

            //apply minmax when you already know a move of the opponent
            if (board_state.getTurnNumber() == 0) {

                int pitTOPlay = moves.get(0).getPit();
                System.out.println("random pit :" + pitTOPlay);

                int bestValue = -100000;
                for (int i = 0; i < moves.size(); i++) {

                    int alpha = Integer.MIN_VALUE;
                    int beta = Integer.MAX_VALUE;

                    int possibleHeuristics = Integer.MIN_VALUE;

                    int heuristic = strategy.alphabetaMinimax(board_state, moves.get(i), 4, alpha, beta,true, possibleHeuristics);

                    if (heuristic > bestValue) {
                        pitTOPlay = moves.get(i).getPit();
                        bestValue = heuristic;
                    }
                    else if (heuristic == bestValue) {
                        //in favour of the pit that has a larger number
                        if (moves.get(i).getPit() > pitTOPlay) {
                            pitTOPlay = moves.get(i).getPit();
                            bestValue = heuristic;
                        }
                    }
                }

                System.out.println("Pit chosen by minimax: " + pitTOPlay);
                move = new HusMove(pitTOPlay, player_id);

            }
            else {

                /**
                 * this ratio determines which strategy to use
                 * this is the ratio of my rocks / opponent's rocks
                 *
                 * 0.7 is the threshold ratio
                 *
                 * ratio >=0.7 attacking strategy
                 * ratio < 0.7 defensive strategy
                 *
                 * each strategy has a heuristic function and it is evaluated by alpha beta prunning
                 *
                 */

                /*

                 */
                double ratio = MyTools.myRockToOpRockRatio(my_pits, op_pits);

                /**
                 * this is the attacking strategy
                 * when the ratio is >= 0.7
                 *
                 */

                // around 40 / 56 and above
                if (ratio >= 0.7) {

                    System.out.println(" 0.7 <=RATIO ");
                    int pit_to_play = moves.get(MyTools.randomLegalMove(moves.size())).getPit();


                    //System.out.println("random pit :" + pit_to_play);

                    int depth;
                    if (board_state.getTurnNumber() <= 40 && ratio >=1.4) {
                        depth = 8;
                    }
                    else if (ratio >= 2.0) {
                        depth = 6;
                    }
                    else {
                        depth = 7;
                    }

                    int bestValue = -100000;
                    for (int i = 0; i < moves.size(); i++) {

                        int alpha = Integer.MIN_VALUE;
                        int beta = Integer.MAX_VALUE;

                        int possibleHeuristic = Integer.MIN_VALUE;

                        int heuristic = strategy.alphabetaMinimax(board_state, moves.get(i), depth, alpha, beta, true, possibleHeuristic);

                        if (heuristic > bestValue) {
                            pit_to_play = moves.get(i).getPit();
                            bestValue = heuristic;
                        }
                        else if (heuristic == bestValue) {
                            //in favour of the pit that has a larger number
                            if (moves.get(i).getPit() > pit_to_play) {
                                pit_to_play = moves.get(i).getPit();
                                bestValue = heuristic;
                            }
                        }
                    }

                    move = new HusMove(pit_to_play, player_id);
                    System.out.println("Depth is "  + depth);
                    //System.out.println("Pit chosen by minimax: " + pit_to_play);

                }
                /**
                 * this is the defensive strategy when the ratio is < 0.7
                 */
                // ratio of 36 / 60 at least
                else if (ratio < 0.7) {

                    System.out.println("Defensive Min max");

                    move = moves.get(MyTools.randomLegalMove(moves.size()));
                    int pit_to_play = move.getPit();

                    int depth;
                    if (ratio >= 0.5) {
                        depth = 7;
                    }
                    //less rock, the algorithm can have time to go deeper
                    else {
                        depth = 8;
                    }
                    int bestValue = Integer.MIN_VALUE;
                    //implement minmax with new heuristic function
                    for (int i = 0; i < moves.size(); i++) {

                        int alpha = Integer.MIN_VALUE;
                        int beta = Integer.MAX_VALUE;
                        int heuristics = strategy.alphaBetaDefensive(board_state, moves.get(i), depth, alpha, beta, true, 0);

                        //get the maximum value of heuristics from all the moves
                        if (heuristics > bestValue) {
                            pit_to_play = moves.get(i).getPit();
                            //loop back and compare
                            bestValue = heuristics;
                        }
                        else if (heuristics == bestValue) {
                            //in favour of the pit that has a larger number
                            if (moves.get(i).getPit() > pit_to_play) {
                                pit_to_play = moves.get(i).getPit();
                                bestValue = heuristics;
                            }
                        }
                    }

                    System.out.println("Depth is "  + depth);
                    move = new HusMove(pit_to_play, player_id);

                }
                //should never get here
                else {
                    move = moves.get(MyTools.randomLegalMove(moves.size()));
                    System.out.println("Random move ");
                }

            }
        }



        // But since this is a placeholder algorithm, we won't act on that information.
        return move;
    }



}