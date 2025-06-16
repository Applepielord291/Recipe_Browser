import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
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
        JDialog frame = new JDialog();
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

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame, validCheck));

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        panel.add(validCheck);
        frame.setVisible(true);
    }
    private void userClickedConfirm(JTextField txt, JDialog frame, JLabel validLbl)
    {
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
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                //put error window saying to try again
            }
            else
            {
                ps = conn.prepareStatement(removeCommand);
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
