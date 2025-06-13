import javax.swing.*;

/* Script created by Nigel Garcia
 * June 12, 2025
 * UserInputFrame
 * this frame displays textboxes that the user can add information into the database
 */

public class UserInputFrame {
    //Frame and functionality developed by Shannon
    //Design made by Nigel
    public void userDisplayFrame()
    {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JTextPane ingredientNameTxt = new JTextPane();
        JButton confirmBtn = new JButton("Confirm");

        frame.setResizable(false);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        ingredientNameTxt.setBounds(20, 20, 200, 25);
        confirmBtn.setBounds(20, 50, 200, 25);

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt));

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        frame.setVisible(true);
    }
    private void userClickedConfirm(JTextPane txt)
    {
        String res = txt.getText();
        txt.setText("");
        System.out.println(res);
    }
}
