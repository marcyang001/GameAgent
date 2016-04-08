package student_player.mytools;


import student_player.StudentPlayer;
import java.util.*;

public class MyTools extends StudentPlayer{




    /**check if the pit move will result at least two relays **/

    public static boolean mintTwoReplays(int[] pits, int startPit) {
        boolean status = false;

        int endPit = pits[startPit] + startPit;
        if (endPit <= 31) {

            if (pits[endPit] != 0) {
                status = true;
            }
        }
        else {

            // 31 - 29 = 2
            // 12 - 2 -1
            try {
                endPit = pits[startPit] - (31 - startPit) - 1;
                if (pits[endPit] != 0) {
                    status = true;
                }
            }
            catch(ArrayIndexOutOfBoundsException e) {
                System.out.println("ARRAY OUT OF BOUNDS");
            }
        }

        return status;
    }



    public static int possibleCapture(int[] mypit, int[] oppit, int startPit) {

        int captureRocks = 0;
        if (mypit[startPit] == 0) {
            return captureRocks;
        }

        int endPit = mypit[startPit] + startPit;
        if (endPit > 31) {
            endPit = mypit[startPit]+ startPit - 31 - 1;
        }



        if (endPit >=16 && endPit <= 31) {
            if (mypit[endPit] != 0) {
                //System.out.println("Exception!!!! ");
                int mapping_pit = (oppit.length - 1 - endPit) + oppit.length / 2;

                captureRocks = oppit[mapping_pit] + oppit[oppit.length-1 - mapping_pit];
            }

        }


        return captureRocks;
    }

    public static double myRockToOpRockRatio(int[] mypit, int[] opPit) {


        int my_totalRocks = 0;
        int op_totalRocks = 0;
        for (int i = 0; i < mypit.length/2; i++) {
            my_totalRocks = my_totalRocks + mypit[i] + mypit[mypit.length -1 - i];
            op_totalRocks = op_totalRocks + opPit[i] + opPit[opPit.length -1 - i];
        }

        //System.out.println("My total Rocks: " + my_totalRocks);
        //System.out.println("Opponent total Rocks: " + op_totalRocks);
        double ratio = (double)my_totalRocks / op_totalRocks;


        return ratio;

    }





    /**
     *
     * Generate a random legal move
     *
     */

    public static int randomLegalMove(int maximum) {
        int randomNum = (int)(Math.random() * maximum);

        return randomNum;

    }

















}
