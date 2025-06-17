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
        ImageIcon recipeInstructions = new ImageIcon("Mixer\\Graphics\\Labels\\Instructions.png");
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        JLabel nameTitleLbl = new JLabel(recipeNameIcon);
        JLabel recipeName = new JLabel(btn.getText());
        JLabel recipeInstructionsLbl = new JLabel(recipeInstructions);
        JLabel recipeLinkLbl = new JLabel("Recipe Link");
        JTextField recipeLink = new JTextField();
        JTextPane recipeRequirements = new JTextPane();
        recipeRequirements.setEditable(false);
        JScrollPane recipeRequirementsScroll = new JScrollPane(recipeRequirements);
        recipeRequirements.setText(getIng(con, btn));

        recipeName.setFont(new Font("Arial", 0, 20));
        recipeRequirements.setFont(new Font("Arial", 0, 20));
       // recipeLink.setFont(new Font("Arial", 0, 16));

        JButton exitBtn = new JButton(exitNormal);
        exitBtn.setRolloverIcon(exitHovered);

        frame.setUndecorated(true);
        
        recipeLink.revalidate(); recipeLink.repaint();

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
        recipeLink.setText(getLink(con, recipeLink, btn));

        nameTitleLbl.setBounds(10, 10, 250, 45);
        recipeName.setBounds(20, 70, 300, 25);
        recipeRequirementsScroll.setBounds(300, 70, 250, 150);
        recipeInstructionsLbl.setBounds(300, 10, 250, 45);
        exitBtn.setBounds(275, 450, 125, 25);
        recipeLinkLbl.setBounds(10, 250, 250, 25);
        recipeLink.setBounds(10, 350, 250, 25);

        frame.add(panel);

        panel.add(nameTitleLbl);
        panel.add(recipeName);
        panel.add(recipeInstructionsLbl);
        panel.add(recipeRequirementsScroll);
        panel.add(exitBtn);
        panel.add(recipeLinkLbl);
        panel.add(recipeLink);

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
                return rs.getString(6);
            }
            else return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    private String getLink(Connection con, JTextField link, JButton selBtn)
    {
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM RecipeTable WHERE RecipeName = ?");
            ps.setString(1, selBtn.getText());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getString(5);
            }
            else return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
