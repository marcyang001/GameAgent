package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import student_player.mytools.MyTools;
import student_player.mytools.PotentialOutCome;
import student_player.mytools.PotentialOutCome;

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
    public HusMove chooseMove(HusBoardState board_state)
    {
        // Get the contents of the pits so we can use it to make decisions.
        int[][] pits = board_state.getPits();

        // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.

        int[] my_pits = pits[player_id];
        int[] op_pits = pits[opponent_id];
        //the number associated with my_pit[0] is the number of rocks in that specific hole

        HusMove move = null;


        // Use code stored in ``mytools`` package.




        //check my valid moves for capture


        // Get the legal moves for the current board state.
        ArrayList<HusMove> moves = board_state.getLegalMoves();


        //first move => get the greatest relay
        if (board_state.getTurnNumber() == 0) {
            move = moves.get(0);
        }
        else {

            System.out.println("FIRST PLAYER ID: " + board_state.firstPlayer());

            List<PotentialOutCome> tm = MyTools.ColumnWithLargestSum(op_pits, MyTools.Outcome.GAIN);
            PotentialOutCome pg = MyTools.potentialOutCome(tm, my_pits, MyTools.Outcome.GAIN);
            //TreeMap tm_loss = MyTools.ColumnWithLargestSum(my_pits, MyTools.Outcome.LOSS);
            //PotentialOutCome pl = MyTools.potentialOutCome(tm_loss, op_pits, MyTools.Outcome.LOSS);


            if (pg != null) {
                move = new HusMove(pg.pitToMove, player_id);
                System.out.println("TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pg.pitToMove);


                if (board_state.isLegal(move)) {
                    System.out.println("Legal move");
                    return move;
                }
                else {

                    System.out.println("Not legal move");
                    move = (HusMove) board_state.getRandomMove();
                }
            }
            else {
                System.out.println("Random Move");
                move = (HusMove) board_state.getRandomMove();
            }

        }


        // But since this is a placeholder algorithm, we won't act on that information.
        return move;
    }
    // We can see the effects of a move like this...
    //HusBoardState cloned_board_state = (HusBoardState) board_state.clone();
    //cloned_board_state.move(move);


}