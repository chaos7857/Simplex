package com.cc;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DataJFrame extends JFrame implements ActionListener {

    private final Logger logger;
    private JButton init;
    double[][] A;// 系数矩阵
    double[] b;
    double[] c;// 价值系数，改为min形式
    int[] cla;// 约束条件类型，这里规定，-1为大于等于，0为等号，1为小于等于
    String[] fuHao = {">=", "=", "<="};
    double[][] tableau;
    private JTextArea result;
    private JButton next;
    private Simplex simplex;
    private int pivotColumn;
    private int pivotRow;
    private ArrayList<Integer> x;
    private JButton addVar;
    private JButton duiOu;
    private JButton standard;

    public DataJFrame(double[][] a, double[] b, double[] c, int[] cla) {
        this.A = a;
        this.b = b;
        this.c = c;
        this.cla = cla;
        logger = Logger.getLogger(this.getClass().getName());
        initFrame();
        initView();
    }

    private void initView() {

        JLabel goal = new JLabel("max z = 2*x1+x2+3*x3");
        goal.setBounds(0, 0, 314, 20);
        this.getContentPane().add(goal);

        JLabel st1 = new JLabel("x1+x2+x3>=5");
        st1.setBounds(10, 20, 200, 20);
        this.getContentPane().add(st1);

        JLabel st2 = new JLabel("2x1+x2+4x3<=7");
        st2.setBounds(10, 35, 200, 20);
        this.getContentPane().add(st2);

        JLabel st3 = new JLabel("x1,x2,x3>=0");
        st3.setBounds(10, 50, 200, 20);
        this.getContentPane().add(st3);

        JLabel st4 = new JLabel("x2+x3>=4");
        st4.setBounds(580, 35, 200, 20);
        this.getContentPane().add(st4);

        init = new JButton("初始单纯形表");
        init.setBounds(260, 25, 150, 40);
        init.addActionListener(this);
        this.getContentPane().add(init);

        next = new JButton("下一步");
        next.setBounds(260, 75, 150, 40);
        next.addActionListener(this);
        next.setEnabled(false);
        this.getContentPane().add(next);

        addVar = new JButton("添加条件");
        addVar.setBounds(420, 25, 150, 40);
        addVar.addActionListener(this);
        addVar.setEnabled(false);
        this.getContentPane().add(addVar);

        standard = new JButton("标准化");
        standard.setBounds(420, 75, 150, 40);
        standard.addActionListener(this);
        standard.setEnabled(false);
        this.getContentPane().add(standard);

        duiOu = new JButton("下一步（对偶）");
        duiOu.setBounds(580, 75, 150, 40);
        duiOu.addActionListener(this);
        duiOu.setEnabled(false);
        this.getContentPane().add(duiOu);

        result = new JTextArea();
        JScrollPane jp = new JScrollPane(result);
        jp.setBounds(0, 150, 780, 450);
        result.setEditable(false);
        this.getContentPane().add(jp);
    }

    private void initFrame() {
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("灵敏度分析");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info(e.getSource() + "");
        if (e.getSource() == init) {
            simplex = new Simplex(A, b, c, cla);
            simplex.initTableau();
            this.tableau = simplex.tableau;
            this.x = simplex.x;
            addToResult();

            init.setEnabled(false);
            next.setEnabled(true);
        } else if (e.getSource() == next) {
            pivotColumn = simplex.getPivotCol();
            if (pivotColumn < 0) {
                result.append("已经达到最优解\n");
                next.setEnabled(false);
                addVar.setEnabled(true);
            } else {
                pivotRow = simplex.getPivotRow(pivotColumn);
                if (pivotRow < 0) {
                    result.append("无解\n");
                    next.setEnabled(false);
                }
                tableau = simplex.pivot(this.tableau, pivotRow, pivotColumn);
                addToResult();
            }

        } else if (e.getSource() == addVar) {
            double[] A_new = {0, 1, 1};
            tableau = simplex.addVar(A_new, 4, -1);
            addToResult();
            addVar.setEnabled(false);
            standard.setEnabled(true);
        } else if (e.getSource() == standard) {
            tableau = simplex.transferStandard(tableau);
            addToResult();
            standard.setEnabled(false);
            duiOu.setEnabled(true);
        } else if (e.getSource() == duiOu) {
            if (tableau[tableau.length - 3][1] >= 0) {
                result.append("已达到最优解\n\n\n");
                duiOu.setEnabled(false);
            } else {
                int pivotRow = tableau.length - 3;
                int pivotCol = -1;
                double min = Double.MAX_VALUE;
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
                    result.append("无解\n");
                    duiOu.setEnabled(false);
                }
                simplex.pivot(tableau, pivotRow, pivotCol);
                addToResult();
            }

        }
    }

    private void addToResult() {
        // 显示
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {

                if (i == tableau.length - 2 && j == 0) {
                    result.append("检验数\t");
                    continue;
                } else if (i == tableau.length - 2 && j == 1) {
                    result.append("---\t");
                    continue;
                }
                if (i == tableau.length - 1 && j == 0) {
                    result.append("系数\t");
                    continue;
                } else if (i == tableau.length - 1 && j == 1) {
                    result.append("---\t");
                    continue;
                }
                if (tableau[i][j] > 100) {
                    result.append("∞\t");
                } else if (tableau[i][j] < -100) {
                    result.append("-∞\t");
                } else {
                    result.append(tableau[i][j] + "\t");
                }

            }
            result.append("\n");
        }
        for (int i = 0; i < x.size(); i++) {
            result.append("x" + (x.get(i) - 1) + "=" + tableau[i][1] + "\t");
        }
        result.append("\n\n");
    }
}