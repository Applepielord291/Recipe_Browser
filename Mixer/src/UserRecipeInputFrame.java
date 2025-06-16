import javax.swing.*;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.sql.*;

public class UserRecipeInputFrame {
    private int ingredientLength = 0;
    private JButton[] ingredientList = null;
    private String[] selectedIngredients = null;
    public UserRecipeInputFrame(int length)
    {
        ingredientLength = length;
    }
    public void userDisplayFrame(Connection conn) 
    {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        JTextPane ingredientNameTxt = new JTextPane();

        JButton confirmBtn = new JButton("Confirm");

        JPanel ingredientBg = new JPanel();

        JScrollPane ingBgScroll = new JScrollPane(ingredientBg);

        displayIngredientBtns(conn, ingredientBg);

        frame.setResizable(false);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        ingredientBg.setLayout(new FlowLayout());
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        ingredientNameTxt.setBounds(20, 20, 200, 25);
        confirmBtn.setBounds(20, 50, 200, 25);
        ingBgScroll.setBounds(325, 1, 300, 300);

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame, conn));

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        panel.add(ingBgScroll);
        frame.setVisible(true);
    }
    private void displayIngredientBtns(Connection con, JPanel p)
    {
        ingredientList = new JButton[ingredientLength];
        selectedIngredients = new String[ingredientLength];
        try
        {
            int i = 0;
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM Ingredients");
            while (rs.next())
            {
                ingredientList[i] = new JButton(rs.getString(2));
                ingredientList[i].setPreferredSize(new Dimension(200, 35));
                ingredientList[i].addActionListener(e -> userClickedIngredient(e));
                p.add(ingredientList[i]);
                i++;
            }
        }
        catch (Exception e)
        {

        }
        
    }
    private void userClickedIngredient(ActionEvent e)
    {
        JButton selBtn = (JButton)e.getSource();
        String txt = selBtn.getText();
        for (int i = 0; i < selectedIngredients.length; i++)
        {
            if (selectedIngredients[i] == null)
            {
                selectedIngredients[i] = txt;
                break;
            }
        }
    }
    private void userClickedConfirm(JTextPane txt, JFrame frame, Connection conn)
    {
        String res = txt.getText();
        txt.setText("");
        String resultSelect = "";
        for (int i = 0; i < selectedIngredients.length; i++)
        {
            if (selectedIngredients[i] != null)
            {
                resultSelect += selectedIngredients[i] + ",";
            }
        }
        System.out.println(resultSelect);
        System.out.println(res);
        try
        {
            String command = "INSERT INTO RecipeTable (RecipeName, Requirements) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(command);
            ps.setString(1, res);
            ps.setString(2, resultSelect);
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
