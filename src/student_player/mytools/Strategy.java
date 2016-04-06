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


    public Strategy(HusBoardState board_state, int player_id, int opponent_id) {
        this.m_board_state = (HusBoardState) board_state.clone();
        this.m_player_id = player_id;
        this.m_opponent_id = opponent_id;
    }


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
                    int bestValue = -100000000;
                    List<PotentialOutCome> lg_list = MyTools.ColumnWithLargestSum(m_board_state.getPits()[m_opponent_id], MyTools.Outcome.GAIN);
                    if (lg_list.size() > 0) {
                        PotentialOutCome outcome = MyTools.potentialOutCome(lg_list, m_board_state.getPits()[m_player_id], MyTools.Outcome.GAIN);
                        bestValue = outcome.rocks;
                    }


                    for (int i = 0; i < m_board_state.getLegalMoves().size(); i++) {
                        int v = minimax(m_board_state.getLegalMoves().get(i), depth - 1, false, bestValue);
                        bestValue = Math.max(bestValue, v);
                    }
                    //System.out.println("best value " + bestValue);
                    return bestValue;
                } else {

                    int bestValue = 100000000;
                    List<PotentialOutCome> loss_list = MyTools.ColumnWithLargestSum(m_board_state.getPits()[m_player_id], MyTools.Outcome.GAIN);
                    if (loss_list.size() > 0) {
                        PotentialOutCome outcome = MyTools.potentialOutCome(loss_list, m_board_state.getPits()[m_opponent_id], MyTools.Outcome.GAIN);
                        bestValue = outcome.rocks;
                    }

                    for (int i = 0; i < m_board_state.getLegalMoves().size(); i++) {
                        int v = minimax(m_board_state.getLegalMoves().get(i), depth - 1, true, bestValue);
                        bestValue = Math.min(bestValue, v);
                    }
                    return bestValue;
                }
            } else {
                return 0;
            }


        } catch (IllegalArgumentException e) {

            return 0;


        }


    }

}//end of class
