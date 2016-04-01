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
    public HusMove chooseMove(HusBoardState board_state){
        // Get the contents of the pits so we can use it to make decisions.
        int[][] pits = board_state.getPits();

        // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
        final HusBoardState f_board_state = board_state;
        final int[] my_pits = pits[player_id];
        final int[] op_pits = pits[opponent_id];
        //the number associated with my_pit[0] is the number of rocks in that specific hole

        HusMove move = null;



        // Get the legal moves for the current board state.
        ArrayList<HusMove> moves = board_state.getLegalMoves();


        //first move => get the greatest relay
        //randomly choose the first and second move
        if (board_state.getTurnNumber() == 0) {
            move = moves.get(0);
        }
        else {
            if (board_state.getTurnNumber() == 1) {
                move = moves.get(MyTools.randomLegalMove(moves.size()));
            } else {

                //create a new thread, which is responsible for the potential gain

                class Pot_Gain implements Runnable {

                    //private volatile ArrayList<PotentialOutCome> pg_list = new ArrayList<PotentialOutCome>();
                    private volatile PotentialOutCome second_pg;
                    @Override
                    public void run() {

                        //get the list of largest enemy columns
                        List<PotentialOutCome> tm = MyTools.ColumnWithLargestSum(op_pits, MyTools.Outcome.GAIN);
                        ArrayList<PotentialOutCome> potentialMoves = MyTools.potentialMoves(tm, my_pits, MyTools.Outcome.GAIN);
                        // look for two steps ahead
                        if (potentialMoves.size() > 0) {
                            for (int i = 0; i < potentialMoves.size(); i++) {
                                //find the potential gain
                                HusMove p_move = new HusMove(potentialMoves.get(i).pitToMove, player_id);
                                HusBoardState cloned_board_state = (HusBoardState) f_board_state.clone();
                                cloned_board_state.move(p_move);
                                List<PotentialOutCome> second_steps = MyTools.ColumnWithLargestSum(cloned_board_state.getPits()[opponent_id], MyTools.Outcome.GAIN);
                                second_pg = MyTools.potentialOutCome(second_steps, cloned_board_state.getPits()[player_id], MyTools.Outcome.GAIN);

                            }
                        }

                    }

                    public PotentialOutCome getPg() {
                        System.out.println("calling another thread");
                        return second_pg;
                    }
                }

                Pot_Gain t_gain = new Pot_Gain();
                Thread cal_gain = new Thread(t_gain);
                cal_gain.start();


                //main thread is responsible for calculating the potential loss
                //get the list of my columns
                List < PotentialOutCome > tm_loss = MyTools.ColumnWithLargestSum(my_pits, MyTools.Outcome.LOSS);
                //find the potential loss
                PotentialOutCome pl = MyTools.potentialOutCome(tm_loss, op_pits, MyTools.Outcome.LOSS);

                try {
                    cal_gain.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                PotentialOutCome pg = t_gain.getPg();



                if (pl != null) {
                    System.out.println("Potential Loss: " + pl.rocks);
                }

                if (pg != null && pl != null) {

                    //when potential gain >= potential loss
                    if (pg.rocks >= pl.rocks) {
                        //attack/ capture
                        move = new HusMove(pg.pitToMove, player_id);

                        System.out.println("Attack TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pg.pitToMove);


                    } else {
                        //when potential loss > potential gain
                        //defend:
                        //compare whether to move the inner row or outer row.
                        //sumValue = 7, inner row is 2, outer row is 5, move the outer row
                        move = new HusMove(pl.pitToMove, player_id);
                        System.out.println("Defend TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pl.pitToMove);
                    }

                } else if (pg != null) {
                    //attack only
                    move = new HusMove(pg.pitToMove, player_id);
                    System.out.println("only attack TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pg.pitToMove);
                } else if (pl != null) {
                    //defend only
                    //move the inner row
                    move = new HusMove(pl.pitToMove, player_id);
                    System.out.println("Only Defend TURN NUMBER: " + board_state.getTurnNumber() + "Pit #: " + pl.pitToMove);

                } else {
                    //no gain no loss
                    System.out.println("Random Move");
                    move = moves.get(MyTools.randomLegalMove(moves.size()));
                    //plan two steps ahead

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