import java.awt.Font;
import java.sql.*;
import javax.swing.*;

public class IngredientInfoFrame {
    private boolean canClose = false;
    public void DisplayFrame(JButton btn, JFrame mFrame, Connection con)
    {
        ImageIcon exitNormal = new ImageIcon("Mixer\\Graphics\\Buttons\\ExitNormal.png");
        ImageIcon exitHovered = new ImageIcon("Mixer\\Graphics\\Buttons\\ExitHovered.gif");
        ImageIcon recipeNameIcon = new ImageIcon("Mixer\\Graphics\\Labels\\RecipeName.png");
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        JLabel nameTitleLbl = new JLabel(recipeNameIcon);
        JLabel recipeName = new JLabel(btn.getText());
        JLabel recipeInstructionsLbl = new JLabel("Recipe Instructions");
        JLabel recipeRequirements = new JLabel(getIng(con, btn));

        recipeName.setFont(new Font("Arial", 0, 20));

        JButton exitBtn = new JButton(exitNormal);
        exitBtn.setRolloverIcon(exitHovered);

        frame.setUndecorated(true);

        frame.setResizable(false);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(mFrame);
        panel.setLayout(null);
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        frame.setModal(false);
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));

        exitBtn.addActionListener(e -> userClickedExit(frame));

        nameTitleLbl.setBounds(10, 10, 250, 45);
        recipeName.setBounds(10, 60, 150, 25);
        recipeRequirements.setBounds(150, 10, 150, 25);
        recipeInstructionsLbl.setBounds(90, 10, 250, 45);
        exitBtn.setBounds(275, 450, 125, 25);

        frame.add(panel);
        panel.add(nameTitleLbl);
        panel.add(recipeName);
        panel.add(recipeInstructionsLbl);
        panel.add(recipeRequirements);
        panel.add(exitBtn);

        frame.setVisible(true);
    }
    private void userClickedExit(JDialog frame)
    {
        canClose = true;
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));
        frame.dispose();
    }
    private String getIng(Connection con, JButton selBtn)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM RecipeTable WHERE RecipeName = ?");
            ps.setString(1, selBtn.getText());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getString(3);
            }
            else return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
