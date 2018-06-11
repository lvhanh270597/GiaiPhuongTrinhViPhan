
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author PhatNguyen
 */
public class setupPanel {
    private int num_of_lb;
    private JLabel lb[];
    private JPanel windowContent;
    private JPanel pl;
    

    public setupPanel(JPanel windowContent, int n) {
        //windowContent= new JPanel();
        lb = new JLabel[n];
        this.windowContent = windowContent;
        this.num_of_lb = n;
        
        
        for(int i = 0; i < n; i++) {
            lb[i] = new JLabel("Bước "+(i+1));
        }
        
        pl = new JPanel ();
        GridLayout gl =new GridLayout(4,3);
        pl.setLayout(gl);
        
        for(int i = 0; i < n; i++) {
            pl.add(lb[i]);
        }
        
        windowContent.add("Center", pl);
        
//        JFrame frame = new JFrame("");
//        frame.setContentPane(windowContent);
//        frame.pack();
//        frame.setVisible(true);
    }
    
//    public static void main(String[] args) {
//        setupPanel p = new setupPanel();
//    }
    
}
