package student_player.mytools;


import student_player.StudentPlayer;
import java.util.*;

public class MyTools extends StudentPlayer{




    /**
     * @Param opPit: opponent's pit or my_pits
     * @return: sorted list of tuples <Sum, Pit#>, if Pit is empty then Pit# is zero.
     *          the list is sorted from largest to the smallest
     * **/

    public static TreeMap<Integer, Integer> ColumnWithLargestSum(int[] opPit) {

        TreeMap<Integer, Integer> tm = new TreeMap<Integer, Integer>(Collections.reverseOrder());



        for (int i = 0; i < (opPit.length)/2; i++) {
            int inner = opPit[i];
            int outer = opPit[opPit.length-1-i];

            int sum =  inner + outer;
            if (outer != 0) {
                tm.put(sum, opPit.length-1-i);

            }

        }
        return tm;
    }



    public static PotentialAttack PotentialGain(TreeMap largestPits, int[] my_pits) {


        PotentialAttack Potential_Attack = null;
        int potentialGain = 0;


        Set set = largestPits.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {

            Map.Entry me = (Map.Entry)i.next();
            Integer sumValue = (Integer) me.getKey();
            Integer pitNumber = (Integer) me.getValue();

            int enemy_Pit = pitNumber;
            //map enemy pit index to my corresponding pit index
            int my_currentPit = (my_pits.length-1 - enemy_Pit) + my_pits.length/2;

            //back track 10 pits and find the one that leads to capture this pit

            for (int j = 2; j<=11; j++) {
                int tempPit = my_currentPit - j;

                //if the number of rocks is equal to the number of moves required to that pit
                if (my_pits[tempPit] == j) {

                    //potential gain is the largest sum of that column
                    potentialGain = sumValue;
                    Potential_Attack = new PotentialAttack(tempPit, potentialGain);
                    break;
                }
            }

        }


        return Potential_Attack;

    }














}
