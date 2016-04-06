package student_player.mytools;


import student_player.StudentPlayer;
import java.util.*;

public class MyTools extends StudentPlayer{






    public enum Outcome {
        GAIN,
        LOSS,
        Defensive

    }
    /**
     * @param opPit: opponent's pit or my_pits
     * @return: sorted list of tuples <Sum, Pit#>, if Pit is empty then Pit# is zero.
     *          the list is sorted from largest to the smallest
     * **/

    public static List<PotentialOutCome> ColumnWithLargestSum(int[] opPit, Outcome result) {



        List<PotentialOutCome> tm = new ArrayList<PotentialOutCome>();


        PotentialOutCome po = null;
        for (int i = 0; i < (opPit.length)/2; i++) {
            int outer = opPit[i];
            int inner = opPit[opPit.length-1-i];
            int sum =  outer + inner;
            //get all the largest sum from enemy pit for potential gain
            if (inner != 0 && result == Outcome.GAIN) {
                po = new PotentialOutCome(opPit.length - 1 - i, sum);
                tm.add(po);
            }
            //get all the largest sum from my pits for potential loss
            else if (inner >1 && result == Outcome.LOSS){
                po = new PotentialOutCome(opPit.length - 1 - i, sum);
                tm.add(po);
            }
            //get all the moves where you cannot move the inner row but you have to move the outer row
            else if (result == Outcome.Defensive && inner <=1) {
                po = new PotentialOutCome(i, sum);
                tm.add(po);
            }
        }

        Collections.sort(tm, new Comparator<PotentialOutCome>() {
            @Override
            public int compare(PotentialOutCome o1, PotentialOutCome o2) {
                //sort the list in descending order
                return o2.rocks - o1.rocks;
            }
        });
        return tm;
    }

    /**
     *
     * @param largestPits => sorted list of enemy pits that contain the largest sum
     * @param the_pits => my current pits
     * @param result => type gain / loss
     * @return gives the Potential_Attack, which contains which pit to move and its potential gain.
     */


    public static PotentialOutCome potentialOutCome(List<PotentialOutCome> largestPits, int[]the_pits, Outcome result) {


        PotentialOutCome PotentialOutCome = new PotentialOutCome(-1, Integer.MIN_VALUE);
        ArrayList<PotentialOutCome> queue = new ArrayList<PotentialOutCome>();
        int potOut;
        //Set set = largestPits.entrySet();

        outerloop:
        for (int i = 0; i <  largestPits.size(); i++) {


            int given_pit = largestPits.get(i).pitToMove;

            if (given_pit > 31) {
                System.out.println("null pointer exception!!!!!!");
            }

            int sumValue = largestPits.get(i).rocks;
            //map enemy pit index to my corresponding pit index

            //the given pit is enemy pit and you map to my pit (GAIN)
            // if the given pit is my pit, then you map to the enemy pit (LOSS)
            int mapping_pit = (the_pits.length - 1 - given_pit) + the_pits.length / 2;

            //System.out.println("sumValue: " + sumValue+ " " + result);
            //back track 10 pits and find the one that leads to capture this pit
            innerloop:
            for (int j = 2; j <= 11; j++) {
                int tempPit = mapping_pit - j;


                //if the number of rocks is equal to the number of moves required to that pit
                if (the_pits[tempPit] == j && the_pits[mapping_pit] != 0) {

                        //System.out.println("THERE IS A POSSIBLE CAPTURE");
                    if (result == Outcome.GAIN) {
                        //potential gain is the largest sum of that column
                        potOut = sumValue;
                        PotentialOutCome = new PotentialOutCome(tempPit, potOut);

                        queue.add(PotentialOutCome);
                        //break outerloop;
                    } else if (result == Outcome.LOSS) {

                        //System.out.println("Defend the potential loss: " + given_pit);
                        potOut = sumValue;

                        //move this pit if you want defend the potential loss
                        PotentialOutCome = new PotentialOutCome(given_pit, potOut);
                        //queue.add(PotentialOutCome);

                        break outerloop;
                    }


                }
            }

        }// end looping all the largest sum sets

        if (result == Outcome.GAIN && queue.size() != 0) {
            //if there are two similar pits one next to the other,
            // favour the one in front
            int pitToMove = queue.get(0).pitToMove;
            int largestSum = queue.get(0).rocks;

            for (int i = 0; i < queue.size(); i++){
                if (largestSum == queue.get(i).rocks && queue.get(i).pitToMove > pitToMove) {
                    return queue.get(i);
                }
            }

            return queue.get(0);
        }

        //return the potential loss
        return PotentialOutCome;

    }


    /**
     *
     * @param largestPits => sorted list of enemy pits that contain the largest sum
     * @param the_pits => my current pits
     * @param result => type gain / loss
     * @return gives the list of possible losses or possible gains.
     */


    public static ArrayList<PotentialOutCome> potentialMoves(List<PotentialOutCome> largestPits, int[]the_pits, Outcome result) {


        PotentialOutCome PotentialOutCome = null;
        ArrayList<PotentialOutCome> potentialMoves = new ArrayList<PotentialOutCome>();
        int potOut = 0;
        //Set set = largestPits.entrySet();

        outerloop:
        for (int i = 0; i <  largestPits.size(); i++) {


            int given_pit = largestPits.get(i).pitToMove;

            if (given_pit > 31) {
                System.out.println("null pointer exception!!!!!!");
            }

            int sumValue = largestPits.get(i).rocks;
            //map enemy pit index to my corresponding pit index

            //the given pit is enemy pit and you map to my pit (GAIN)
            // if the given pit is my pit, then you map to the enemy pit (LOSS)
            int mapping_pit = (the_pits.length - 1 - given_pit) + the_pits.length / 2;

            //System.out.println("sumValue: " + sumValue+ " " + result);
            //back track 10 pits and find the one that leads to capture this pit
            innerloop:
            for (int j = 2; j <= 11; j++) {
                int tempPit = mapping_pit - j;


                //if the number of rocks is equal to the number of moves required to that pit
                if (the_pits[tempPit] == j && the_pits[mapping_pit] != 0) {

                    //System.out.println("THERE IS A POSSIBLE CAPTURE");
                    if (result == Outcome.GAIN) {
                        //potential gain is the largest sum of that column
                        potOut = sumValue;
                        PotentialOutCome = new PotentialOutCome(tempPit, potOut);

                        potentialMoves.add(PotentialOutCome);
                        //break outerloop;
                    } else if (result == Outcome.LOSS) {

                        //System.out.println("Defend the potential loss: " + given_pit);
                        potOut = sumValue;

                        //move this pit if you want defend the potential loss
                        PotentialOutCome = new PotentialOutCome(given_pit, potOut);
                        potentialMoves.add(PotentialOutCome);

                        //break outerloop;
                    }


                }
            }

        }// end looping all the largest sum sets


        //return the potential loss
        return potentialMoves;

    }


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

    //public static int numRockCapture(int op_pits, ) {


    //}





    public static int getMyTotalRocks(int[] mypit) {

        int totalRocks = 0;
        for (int i = 0; i < mypit.length/2; i++) {
            totalRocks = totalRocks + mypit[i] + mypit[mypit.length -1 - i];
        }
        return totalRocks;
    }

    public static double myRockToOpRockRatio(int[] mypit, int[] opPit) {


        int my_totalRocks = 0;
        int op_totalRocks = 0;
        for (int i = 0; i < mypit.length/2; i++) {
            my_totalRocks = my_totalRocks + mypit[i] + mypit[mypit.length -1 - i];
            op_totalRocks = op_totalRocks + opPit[i] + opPit[opPit.length -1 - i];
        }

        double ratio = my_totalRocks / op_totalRocks;


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
