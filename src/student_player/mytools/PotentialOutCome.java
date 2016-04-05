package student_player.mytools;

/**
 * Created by marcyang on 2016-03-27.
 *
 * This class stores the potential move for gains and losses.
 * It contains:
 *         the pit number that should move and
 *         the potential gain /capture of opponent rocks or potential loss of my rocks
 */
public class PotentialOutCome {

    public int rocks;
    public int pitToMove;

    public int enemy_frontEmptyPit;
    public int my_frontEmptyPit;



    public PotentialOutCome(int pitToMove, int rocks) {
        this.pitToMove = pitToMove;
        this.rocks = rocks;
    }

    public PotentialOutCome(int[] pits) {

        for (int i = 0; i < pits.length/2; i++) {

        }


    }






}
