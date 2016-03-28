package student_player.mytools;


import student_player.StudentPlayer;
import java.util.*;

public class MyTools extends StudentPlayer{






    public enum Outcome {
        GAIN,
        LOSS

    }
    /**
     * @param opPit: opponent's pit or my_pits
     * @return: sorted list of tuples <Sum, Pit#>, if Pit is empty then Pit# is zero.
     *          the list is sorted from largest to the smallest
     * **/

    public static List<PotentialOutCome> ColumnWithLargestSum(int[] opPit, Outcome result) {



        List<PotentialOutCome> tm = new ArrayList<>();


        PotentialOutCome po = null;
        for (int i = 0; i < (opPit.length)/2; i++) {
            int outer = opPit[i];
            int inner = opPit[opPit.length-1-i];
            int sum =  outer + inner;
            if (inner != 0 && result == Outcome.GAIN) {
                po = new PotentialOutCome(opPit.length - 1 - i, sum);
                tm.add(po);
            }
            else if (inner >1 && result == Outcome.LOSS){
                po = new PotentialOutCome(opPit.length - 1 - i, sum);
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


        PotentialOutCome PotentialOutCome = null;

        int potOut = 0;
        //Set set = largestPits.entrySet();

        outerloop:
        for (int i = 0; i <  largestPits.size(); i++) {


            int given_pit = largestPits.get(i).pitToMove;

            int sumValue = largestPits.get(i).rocks;
            //map enemy pit index to my corresponding pit index

            //the given pit is enemy pit and you map to my pit
            // if the given pit is my pit, then you map to the enemy pit
            int mapping_pit = (the_pits.length - 1 - given_pit) + the_pits.length / 2;

            System.out.println("sumValue: " + sumValue);
            //back track 10 pits and find the one that leads to capture this pit
            for (int j = 2; j <= 11; j++) {
                int tempPit = mapping_pit - j;

                System.out.println("my pit: " + tempPit);

                //if the number of rocks is equal to the number of moves required to that pit
                if (the_pits[tempPit] == j) {

                    //System.out.println("THERE IS A POSSIBLE CAPTURE");
                    if (result == Outcome.GAIN) {
                        //potential gain is the largest sum of that column
                        potOut = sumValue;
                        PotentialOutCome = new PotentialOutCome(tempPit, potOut);
                        break outerloop;
                    }
                    else if (result == Outcome.LOSS){

                        //System.out.println("Defend the potential loss: " + given_pit);
                        potOut = sumValue;
                        //move this pit if you want defend the potential loss
                        PotentialOutCome = new PotentialOutCome(given_pit, potOut);
                        break outerloop;
                    }


                }
            }


        }

        return PotentialOutCome;

    }
















}
