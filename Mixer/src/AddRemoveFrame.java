import java.sql.Connection;

import javax.swing.*;

/* Script created by Nigel Garcia
 * May 15 2025
 */

public class AddRemoveFrame {
    public void DisplayFrame(Connection connection)
    {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        JButton addIngredientBtn = new JButton("Add ingredient");
        JButton addRecipeBtn = new JButton("Add Recipe");
        JButton removeIngredientBtn = new JButton("Remove Ingredient");
        JButton removeRecipeBtn = new JButton("Remove Recipe");

        frame.setResizable(false);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        addIngredientBtn.addActionListener(e -> userAddIngredient(connection));
        addRecipeBtn.addActionListener(e -> userAddRecipe(connection));
        removeIngredientBtn.addActionListener(e -> userRemove(true, connection));
        removeRecipeBtn.addActionListener(e -> userRemove(false, connection));

        addIngredientBtn.setBounds(10, 10, 200, 35);
        addRecipeBtn.setBounds(10, 50, 200, 35);
        removeIngredientBtn.setBounds(10, 90, 200, 35);
        removeRecipeBtn.setBounds(10, 130, 200, 35);

        frame.add(panel);
        panel.add(addIngredientBtn);
        panel.add(addRecipeBtn);
        panel.add(removeIngredientBtn);
        panel.add(removeRecipeBtn);
        frame.setVisible(true);
    }
    private void userRemove(boolean which, Connection con)
    {
        if (which)
        {
            UserRemoveInfo uRemove = new UserRemoveInfo("SELECT * FROM Ingredients WHERE IngredientName = ?", "IngredientName", con, "DELETE * FROM Ingredients WHERE IngredientName = ?");
            uRemove.DisplayFrame();
        }
        else
        {
            UserRemoveInfo uRemove = new UserRemoveInfo("SELECT * FROM RecipeTable WHERE RecipeName = ?", "RecipeName", con, "DELETE FROM RecipeTable WHERE RecipeName = ?");
            uRemove.DisplayFrame();
        }
    }
    private void userAddIngredient(Connection conn)
    {
        //access database and ask user what ingredient to add.
        //after that, reload ingredient and recipe list
        UserInputFrame inputFrame = new UserInputFrame();
        inputFrame.userDisplayFrame(conn);
    }
    private void userAddRecipe(Connection conn)
    {
        //access database and ask user what recipe to add.
        //after that, reload recipe list
        UserRecipeInputFrame inputFrame = new UserRecipeInputFrame();
        inputFrame.userDisplayFrame(conn);
    }
}
