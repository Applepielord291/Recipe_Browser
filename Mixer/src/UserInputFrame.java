import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.*;
import javax.swing.*;

/* Script created by Nigel Garcia
 * June 12, 2025
 * UserInputFrame
 * this frame displays textboxes that the user can add information into the database
 */

public class UserInputFrame {
    //Frame and functionality developed by Shannon
    //Design made by Nigel
    public void userDisplayFrame(Connection conn)
    {
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();
        JTextPane ingredientNameTxt = new JTextPane();
        JButton confirmBtn = new JButton("Confirm");

        frame.setResizable(false);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        ingredientNameTxt.setBounds(20, 20, 200, 25);
        confirmBtn.setBounds(20, 50, 200, 25);

        frame.setModal(false);
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e)
            {
                frame.dispose();
            }
            @Override
            public void windowGainedFocus(WindowEvent e) {}
        });

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame, conn));

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        frame.setVisible(true);
    }
    private void userClickedConfirm(JTextPane txt, JDialog Tframe, Connection conn)
    {
        String res = txt.getText();
        txt.setText("");
        System.out.println(res);
        try
        {
            String command = "INSERT INTO Ingredients (IngredientName) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(command);
            ps.setString(1, res);
            ps.executeUpdate();
            
            Frame[] frames = JFrame.getFrames();
            for (int i = 0; i < frames.length; i++)
            {
                frames[i].dispose();
            }
            Main main = new Main();
            main.reloadProgram();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
