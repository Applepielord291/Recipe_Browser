import javax.swing.*;

import java.awt.BorderLayout;
import java.sql.*;

public class UserRemoveInfo {
    private String command;
    private String removeCommand;
    private String column;
    private Connection conn;
    public UserRemoveInfo(String com, String col, Connection con, String rem)
    {
        command = com;
        column = col;
        conn = con;
        removeCommand = rem;
    }
    public void DisplayFrame() 
    {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JTextField ingredientNameTxt = new JTextField(30);
        JButton confirmBtn = new JButton("Confirm");

        JLabel test = new JLabel("test");
        JLabel validCheck = new JLabel("tt");

        frame.setResizable(false);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        ingredientNameTxt.setLayout(new BorderLayout());
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        ingredientNameTxt.setBounds(20, 20, 200, 25);
        confirmBtn.setBounds(20, 50, 200, 25);
        validCheck.setBounds(20, 100, 100, 25);

        ingredientNameTxt.add(test, BorderLayout.LINE_END);

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame, validCheck));

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        panel.add(validCheck);
        frame.setVisible(true);
    }
    private void userClickedConfirm(JTextField txt, JFrame frame, JLabel validLbl)
    {
        System.out.println("is this even real");
        String res = txt.getText();
        boolean found = false;
        try
        {
            PreparedStatement ps = conn.prepareStatement(command);
            ps.setString(1, res);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                if (rs.getString(column).toLowerCase().equals(res.toLowerCase()))
                {
                    System.out.println("found");
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                System.out.println("none wofofdw fofudnd isisis thrhrealal wlwowlwowl");
                //put error window saying to try again
            }
            else
            {
                System.out.println("removig that teaghe");
                ps = conn.prepareStatement(removeCommand);
                ps.setString(1, res);
                ps.executeUpdate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
