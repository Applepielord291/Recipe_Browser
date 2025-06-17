import java.awt.Color;
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
        ImageIcon recipeLinkIcon = new ImageIcon("Mixer\\Graphics\\Labels\\RecipeLink.png");
        ImageIcon recipeInfoBg = new ImageIcon("Mixer\\Graphics\\Background\\RecipeInfoBg.png");
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        JLabel nameTitleLbl = new JLabel(recipeNameIcon);
        JLabel recipeName = new JLabel(btn.getText());
        JLabel recipeInstructionsLbl = new JLabel(recipeInstructions);
        JLabel recipeLinkLbl = new JLabel(recipeLinkIcon);
        JLabel frameBg = new JLabel(recipeInfoBg);
        JTextField recipeLink = new JTextField();
        JTextPane recipeRequirements = new JTextPane();
        recipeRequirements.setEditable(false);
        JScrollPane recipeRequirementsScroll = new JScrollPane(recipeRequirements);
        recipeRequirements.setText(getIng(con, btn));

        recipeName.setFont(new Font("Arial", 0, 20));
        recipeName.setForeground(new Color(255, 255, 255, 255));
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

        nameTitleLbl.setBounds(20, 10, 250, 45);
        recipeName.setBounds(20, 70, 300, 25);
        recipeRequirementsScroll.setBounds(425, 70, 250, 150);
        recipeInstructionsLbl.setBounds(425, 10, 250, 45);
        exitBtn.setBounds(275, 450, 125, 25);
        recipeLinkLbl.setBounds(20, 250, 250, 45);
        recipeLink.setBounds(20, 300, 250, 25);
        frameBg.setBounds(0, 0, 700, 500);

        frame.add(panel);

        panel.add(nameTitleLbl);
        panel.add(recipeName);
        panel.add(recipeInstructionsLbl);
        panel.add(recipeRequirementsScroll);
        panel.add(exitBtn);
        panel.add(recipeLinkLbl);
        panel.add(recipeLink);

        panel.add(frameBg);

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
