import javax.swing.*;

public class IngredientInfoFrame {
    public void DisplayFrame(JButton btn, JFrame mFrame)
    {
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        frame.setUndecorated(true);

        frame.setResizable(false);
        frame.setSize(0, 0);
        frame.setLocationRelativeTo(mFrame);
        panel.setLayout(null);
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        frame.add(panel);

        frame.setVisible(true);
    }
}
