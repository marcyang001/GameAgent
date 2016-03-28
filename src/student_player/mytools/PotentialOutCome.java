package student_player.mytools;

/**
 * Created by marcyang on 2016-03-27.
 *
 * This class stores the potential move for gains and losses.
 * It contains:
 *         the pit number that should move and
 *         the potential gain /capture of opponent rocks
 */
public class PotentialOutCome {

    public int rocks;
    public int pitToMove;


    public PotentialOutCome(int pitToMove, int rocks) {
        this.pitToMove = pitToMove;
        this.rocks = rocks;
    }


}
