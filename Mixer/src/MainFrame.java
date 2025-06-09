import javax.swing.*;

/* Script created by Nigel Garcia
 * June 9 2025
 * MainFrame
 * Where all the front end events and inputs happen
 */

public class MainFrame {
    //This Method displays the Frame
    //ONLY CALL ON THIS FUNCTION WHEN YOU NEED THE FRAME TO BE DISPLAYED 
    public void DisplayMainFrame() throws Exception {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        frame.setResizable(false);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        panel.setSize(900, 625);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Mixer");

        frame.add(panel);
        frame.setVisible(true);
    }
}
