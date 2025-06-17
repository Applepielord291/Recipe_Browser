import java.sql.*;
import javax.swing.*;

public class IngredientInfoFrame {
    private boolean canClose = false;
    public void DisplayFrame(JButton btn, JFrame mFrame, Connection con)
    {
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        JLabel nameTitleLbl = new JLabel("Recipe Name:");
        JLabel recipeName = new JLabel(btn.getText());
        JLabel recipeRequirementsLbl = new JLabel("Recipe Requirements");
        JLabel recipeRequirements = new JLabel(getIng(con, btn));

        JButton exitBtn = new JButton("exit");

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

        nameTitleLbl.setBounds(10, 10, 150, 25);
        recipeName.setBounds(10, 40, 150, 25);
        recipeRequirementsLbl.setBounds(150, 10, 150, 25);
        recipeRequirements.setBounds(150, 40, 200, 50);

        frame.add(panel);
        panel.add(nameTitleLbl);
        panel.add(recipeName);
        panel.add(recipeRequirementsLbl);
        panel.add(recipeRequirements);

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
