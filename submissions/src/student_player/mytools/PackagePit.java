package student_player.mytools;

/**
 * Created by marcyang on 2016-04-02.
 */
public class PackagePit {

    public int my_totalRocks;
    public int my_frontRocks;
    public int my_backRocks;

    public int my_movableRocks;
    public int my_unmovableRocks;


    /**
     * This class packages all the data needed in one iteration
     * @param my_totalRocks
     * @param my_frontRocks
     * @param my_backRocks
     * @param my_movableRocks
     * @param my_unmovableRocks
     */
    public PackagePit(int my_totalRocks, int my_frontRocks, int my_backRocks, int my_movableRocks,int my_unmovableRocks) {

        this.my_totalRocks = my_totalRocks;
        this.my_frontRocks = my_frontRocks;
        this.my_backRocks = my_backRocks;

        this.my_movableRocks = my_movableRocks;
        this.my_unmovableRocks = my_unmovableRocks;

    }


}
