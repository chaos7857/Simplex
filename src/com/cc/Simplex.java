package com.cc;

import java.util.ArrayList;

public class Simplex {

    private double[][] A;
    private double[] b;
    private double[] c;
    private int[] cla;
    private final double M = 9999;

    private int numConstraints;
    private int numVariables;
    private double[][] tableau;
    ArrayList<Integer> x;
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
        System.out.println();
        System.out.println(numConstraints+" : "+numVariables);

    }

    public Simplex() {
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
    public void pivot(int pivotRow, int pivotColumn) {
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

            pivot(pivotRow, pivotColumn);
            showTableau(tableau);
        }

    }

    private void showTableau(double[][] tableau) {
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {
                if (tableau[i][j]==M){
                    System.out.print("  M    ");
                }else {
                    System.out.print(tableau[i][j]+"    ");
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

}
