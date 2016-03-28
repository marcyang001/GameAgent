package student_player.mytools;

/**
 * Created by marcyang on 2016-03-27.
 *
 * This class stores the potential move for attack.
 * It contains:
 *         the pit number that should move and
 *         the potential gain /capture of opponent rocks
 */
public class PotentialAttack {

    public int rocks = 0;
    public int pitToMove = -1;


    public PotentialAttack(int pitToMove, int rocks) {
        this.pitToMove = pitToMove;
        this.rocks = rocks;
    }


}
