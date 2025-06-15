import javax.swing.*;

import java.awt.Frame;
import java.sql.*;

public class UserRecipeInputFrame {
    public void userDisplayFrame(Connection conn) 
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

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame, conn));

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        frame.setVisible(true);
    }
    private void userClickedConfirm(JTextPane txt, JFrame frame, Connection conn)
    {
        String res = txt.getText();
        txt.setText("");
        System.out.println(res);
        try
        {
            String command = "INSERT INTO RecipeTable (RecipeName) VALUES (?)";
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
