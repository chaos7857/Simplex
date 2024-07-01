package com.cc;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Simplex {

    private double[][] A;
    private double[] b;
    private double[] c;
    private int[] cla;
    private final double M = Double.MAX_VALUE;

    private int numConstraints;
    private int numVariables;
    public double[][] tableau;
    ArrayList<Integer> x;
    public Logger logger;
    double goal;
    public Simplex(double[][] A, double[] b, double[] c, int[] cla) {
        this.A = A;
        this.b = b;
        this.c = c;
        this.cla = cla;
        this.numConstraints = A.length;
        this.numVariables = A[0].length;
        int temp = 0;
        this.x = new ArrayList<>();
        for (int j : cla) {
            if (j == 1) {
                temp++;
                x.add(1 + numVariables + temp);
            } else if (j == -1) {
                temp++;
            }
        }
//        x.stream().forEach(e-> System.out.print(e+"  "));
//        System.out.println();
//        System.out.println(numConstraints+" : "+numVariables);

        logger = Logger.getLogger(this.getClass().getName());
        logger.info(numConstraints+" : "+numVariables);
    }

    public Simplex() {
    }
    
    // 这个方法是针对这个场景来的，所以对输入都有限制 
    public double[][] addVar(double[] A, double b, int cla){
        double[][] tableau_new = new double[tableau.length+1][tableau[0].length+1];
        for (int i = 0; i < numConstraints; i++) {
            System.arraycopy(tableau[i], 0, tableau_new[i], 0, tableau[0].length);
        }
        System.arraycopy(tableau[tableau.length-1], 0, tableau_new[tableau_new.length-1], 0, tableau[0].length);
        System.arraycopy(tableau[tableau.length-2], 0, tableau_new[tableau_new.length-2], 0, tableau[0].length);
        tableau_new[numConstraints][tableau_new[0].length-1] = 1;
        if (cla == -1){
            for (int i = 0; i < A.length; i++) {
                tableau_new[numConstraints][i+2] -= A[i];
            }
            tableau_new[numConstraints][1] = -b;
        } else {
            System.arraycopy(A, 0, tableau_new[numConstraints], 2, A.length);
            tableau_new[numConstraints][1] = b;
        }

        if (cla==0){
            tableau_new[numConstraints][0] = M;
            tableau_new[tableau_new.length-1][tableau_new[0].length-1] = M;
        }
        x.add(tableau_new[0].length-1);

        showTableau(tableau_new);
        return tableau_new;
    }

    public double[][] initTableau(){// 列初始单纯形表
        double[][] tableau = new double[numConstraints + 2][numVariables+numConstraints*2+2];
        int j;
        ArrayList<Integer> toRem = new ArrayList<>();
        for (int i = 0; i < numVariables; i++) {
            tableau[numConstraints+1][i+2] = c[i];
        }
        for (int i = 0; i < numConstraints; i++) {
            tableau[i][0] = cla[i]==1?0:M;
            tableau[i][1] = b[i];
            tableau[i][i+2+numVariables] = cla[i];
            tableau[i][i+2+numVariables+numConstraints] = (cla[i]==1)?0:1;
            tableau[numConstraints+1][i+2+numVariables + numConstraints] = M;
            for (j = 0; j < numVariables; j++) {
                tableau[i][j+2] = A[i][j];
            }
            for (j = 0; j < numConstraints; j++) {
                if (tableau[j][2+numVariables+i]!=0){
                    break;
                }
            }
            if (j == numConstraints){
                toRem.add(2+numVariables+i);
//                System.out.println(2+numVariables+i);
            }

            for (j = 0; j < numConstraints; j++) {
                if (tableau[j][2+numVariables+ numConstraints +i]!=0){
                    break;
                }
            }
            if (j == numConstraints){
//                System.out.println(2+numVariables+numConstraints+i);
                toRem.add(2+numVariables+numConstraints+i);
            }
        }

        double[][] tableau2 = new double[numConstraints + 2][numVariables+numConstraints*2+2-toRem.size()];
        for (int i = 0; i < numConstraints+2; i++) {
            j = 0;
            for (int k = 0; k < numVariables + numConstraints * 2 + 2; k++) {
                if (toRem.contains(k)){
                    j++;
                    continue;
                }
                tableau2[i][k-j] = tableau[i][k];
            }
        }
        // 计算检验数
        calSigma(tableau2);


        this.tableau = tableau2;
        for (int i = 2+numVariables; i <this.tableau[0].length; i++) {
            if (this.tableau[1+numConstraints][i]==M){
                x.add(i);
            }
        }
//        x.stream().forEach(e-> System.out.print(e+","));
//        System.out.println();
        showTableau(tableau2);
        return tableau2;
    }

    private void calSigma(double[][] tableau) {
        double temp;
        for (int i = 2; i < tableau[0].length; i++) {
            temp = 0;
            for (int k = 0; k < numConstraints; k++) {
                temp += tableau[k][i]* tableau[k][0];
            }
            tableau[numConstraints][i] = tableau[numConstraints+1][i] - temp;
        }
    }

    public int getPivotCol(){
        int pivotCol = -1;
        double min = 0;
        for (int i = 2; i < tableau[0].length; i++) {
            if (tableau[numConstraints][i]<min){
                min = tableau[numConstraints][i];
                pivotCol = i;
            }
        }
        return pivotCol;
    }

    public int getPivotRow(int pivotColumn) {
        int pivotRow = -1;
        double min = M;
        double sita;
        for (int i = 0; i < numConstraints; i++) {
            if (tableau[i][pivotColumn] > 0) {
                sita = tableau[i][1] / tableau[i][pivotColumn];
                if (sita < min) {
                    min = sita;
                    pivotRow = i;
                }
            }
        }
        return pivotRow;
    }
    public void pivot(double[][]tableau, int pivotRow, int pivotColumn) {
        double pivotValue = tableau[pivotRow][pivotColumn];
        for (int j = 1; j < tableau[0].length; j++) {
            tableau[pivotRow][j] /= pivotValue;
        }

        for (int i = 0; i < tableau.length-2; i++) {
            if (i != pivotRow) {
                double factor = tableau[i][pivotColumn];
                for (int j = 1; j < tableau[0].length; j++) {
                    tableau[i][j] -= factor * tableau[pivotRow][j];
                }
            }
        }
        // 换系数
        tableau[pivotRow][0] = tableau[tableau.length-1][pivotColumn];
        x.set(pivotRow, pivotColumn);

        // 更新检验数
        calSigma(tableau);
    }

    public void solve() {
        initTableau();
        while (true) {
            int pivotColumn = getPivotCol();
            if (pivotColumn < 0) {
                break;
            }

            int pivotRow = getPivotRow(pivotColumn);
            if (pivotRow < 0) {
                throw new ArithmeticException("Linear program is unbounded");
            }

            pivot(this.tableau,pivotRow, pivotColumn);
            showTableau(tableau);
        }
    }

    private void showTableau(double[][] tableau) {
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {
                if ((i==tableau.length-2||i== tableau.length-1)&&(j==0||j==1)){
                    System.out.print("----    ");
                    continue;
                }
                if (tableau[i][j]==M){
                    System.out.print("  M    ");
                }else {
                    System.out.print(tableau[i][j]+"    ");
//                    System.out.printf("%.2f    ",tableau[i][j]);
                }
            }
            System.out.println();
        }
        for (int i = 0; i < x.size(); i++) {
            System.out.print("x"+(x.get(i)-1)+"="+tableau[i][1]+"\t");
        }
//        x.stream().forEach(e-> System.out.print(e+"  "));
        System.out.println();
    }

    public double[][] transferStandard(double[][] tableau) {
        for (int i = 0; i < x.size()-1; i++) {
            double factor = tableau[tableau.length-3][x.get(i)];
            for (int j = 1; j < tableau[0].length; j++) {
                tableau[tableau.length-3][j] -= factor*tableau[i][j];
            }
        }
        showTableau(tableau);
        return tableau;
    }

    public void duiOu(double[][] tableau) {
        while (true) {
            if (tableau[tableau.length - 3][1] >= 0) {
                System.out.println("已达到最优解");
                break;
            } else {
                int pivotRow = tableau.length - 3;
                int pivotCol = -1;
                double min = M;
                double sita;
                for (int i = 2; i < tableau[0].length; i++) {
                    sita = 0;
                    if (tableau[pivotRow][i] < 0) {
                        sita -= tableau[tableau.length - 2][i] / tableau[pivotRow][i];
                        if (sita < min) {
                            min = sita;
                            pivotCol = i;
                        }
                    }
                }
                if (pivotCol <= 0) {
                    throw new ArithmeticException("Linear program is unbounded");
                }
                pivot(tableau, pivotRow, pivotCol);
                showTableau(tableau);
            }
        }
    }
}
