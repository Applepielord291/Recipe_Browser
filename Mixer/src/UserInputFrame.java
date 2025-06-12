import javax.swing.*;

/* Nigel Garcia
 * June 12, 2025
 * UserInputFrame
 * this frame displays textboxes that the user can add information into the database
 */

public class UserInputFrame {
    public void userDisplayFrame()
    {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        frame.setResizable(false);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        frame.add(panel);
        frame.setVisible(true);
    }
}
