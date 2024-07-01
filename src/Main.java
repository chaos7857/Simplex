import com.cc.Simplex;

import java.util.logging.Logger;

public class Main {
    public static final double M = Double.MAX_VALUE;
    public static void main(String[] args) {
        /*
         * 碳中和一般是指国家、企业、产品、活动或个人在一定时间内直接或间接产生的二氧化碳或温室气体排放总量，通过植树造林、节能减排等形式，
         * 以抵消自身产生的二氧化碳或温室气体排放量，实现正负抵消，达到相对“零排放”。
         * 已知某M区域内有三种方式吸收二氧化碳，分别是植树造林x1、节能减排x2、碳补偿x3，三种方式单位面积吸收二氧化碳量相同。
         * 考虑到M区域碳排放情况，
         * s.t.
         * 三种方式的每日吸收量应不少于5（万立方），x1+x2+x3>=5
         * 植树造林的单位成本为2（元），节能减排单位成本为1（元），碳补偿单位成本为4（元）。考虑不到政府经费有限，故每日用于碳排放的支出不高于7（万元）。
         * 2*x1+x2+4*x3<=7
         * 已知通过三种方式吸收每立方二氧化碳可获得经济收益分别为2（元）、1（元）、3（元），
         * max z = 2*x1+x2+3*x3
         * 试问该M市如何确定各种方式的吸收量？
         *
         *
         *
         * 该市在方案实际运行时发现，植树造林和节能减排的吸收量不少于3（万立方）才能满足市民的需求。
         * x2+x3>=4
         * 为了提高市民满意度，试问该市政府如何调整各方式吸收量，才能满足市民需求的同时使经济效益最大化？
         * */

        /*
         * max z = 2*x1+x2+3*x3
         * s.t.
         * -x1-x2-x3+x4=-5
         * 2*x1+x2+4*x3+x5=7
         * -x1<=-3
         * -x2<=-3
         * */



//        Test1:
//        double[][] A = {
//                {1, -2, 1},
//                {-4, 1, 2},
//                {-2, 0, 1}
//        };
//        double[] b = {11, 3, 1};
////        double[] c = {-3, 1, 1};
//        double[] c = {3, -1, -1};
//        int[] cla = {1, -1, 0};

//        Test2:
//        double[][] A = {
//                {1, 2},
//                {4, 0},
//                {0, 4}
//        };
//        double[] b = {8,16,12};
//        double[] c = {-2,-3};
//        int[] cla = {1,1,1};

//        原问题
//        double[][] A = {
//                {2, 1, 4},
//                {1, 1, 1},
//        };
//        double[] b = {7, 5};
//        double[] c = {-2,-1,-3};
//        int[] cla = {1,-1};

//      添加约束后直接
        double[][] A = {
                {2, 1, 4},
                {1, 1, 1},
                {0,1,1}
        };
        double[] b = {7, 5,4};
        double[] c = {-2,-1,-3};
        int[] cla = {1,-1,-1};

        Logger logger = Logger.getLogger("main");

        long start = System.currentTimeMillis();
        logger.info(start+"");
        Simplex simplex = new Simplex(A, b, c, cla);
        simplex.solve();


//        对偶
//        double[] A_new = {0, 1, 1};
//        double[][] tableau = simplex.addVar(A_new, 4, -1);
//        double[][] tableau_new = simplex.transferStandard(tableau);
//        simplex.duiOu(tableau_new);

        long end  = System.currentTimeMillis();
        logger.info(end+"");
//        System.out.println("用时:"+(end-start));
        logger.info("用时:"+(end-start));
    }
}