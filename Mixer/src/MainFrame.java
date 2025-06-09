import javax.swing.*;

public class MainFrame {
    public static void main(String[] args) throws Exception {
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
