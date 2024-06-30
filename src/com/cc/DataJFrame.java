package com.cc;

import javax.swing.*;
import java.awt.*;

public class DataJFrame extends JFrame {
    public DataJFrame(){
        initFrame();
        initView();
    }

    private void initView() {
        JLabel goal = new JLabel("max=2*x1+x2+3*x3");
        goal.setBounds(200,0,800,100);
        goal.setFont(new Font("宋体",Font.BOLD,30));
        this.getContentPane().add(goal);



        JButton button = new JButton("calculate");
    }

    private void initFrame() {
        this.setSize(800,600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("灵敏度分析");
    }
}
