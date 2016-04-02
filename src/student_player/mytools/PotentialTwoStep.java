package student_player.mytools;

/**
 * Created by marcyang on 2016-04-02.
 */
public class PotentialTwoStep {

    public int firstPitToMove;
    public int potentialLoss;
    public int potentialGain;


    /**
     * This class keeps track of the outcome after 1 step whether it is potential loss or potential gain
     * @param firstPitToMove
     * @param potentialLoss
     * @param potentialGain
     */
    public PotentialTwoStep(int firstPitToMove, int potentialLoss, int potentialGain) {
        this.firstPitToMove = firstPitToMove;
        this.potentialLoss = potentialLoss;
        this.potentialGain = potentialGain;

    }


}
