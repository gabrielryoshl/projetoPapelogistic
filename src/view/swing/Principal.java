package view.swing;

import javax.swing.JFrame;

public class Principal {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Papelogistic");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Chama o seu JPanel
        frame.add(new LoginPage());

        frame.setVisible(true);
        
        
    }
}
