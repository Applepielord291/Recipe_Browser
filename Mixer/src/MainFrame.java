import javax.swing.*;
import java.sql.*;

/* Script created by Nigel Garcia
 * June 9 2025
 * MainFrame
 * Where all the front end events and inputs happen
 */

public class MainFrame {
    //dont forget to change this value to the actual path oncethe database is setup
    private final String dbUrl = null;

    //This Method displays the Frame
    //ONLY CALL ON THIS FUNCTION WHEN YOU NEED THE FRAME TO BE DISPLAYED 
    public void DisplayMainFrame() throws Exception {
        //Essential Components
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        //hide frame window
        frame.setUndecorated(true);

        //lists
        JTextPane ingredientList = new JTextPane();
        JTextPane recipeList = new JTextPane();

        //Scrollbars
        JScrollPane ingredientListScroll = new JScrollPane(ingredientList);
        JScrollPane recipeListScroll = new JScrollPane(recipeList);

        //Buttons
        //add images to buttons later
        JButton addIngredientBtn = new JButton("Add ingredient");
        JButton addRecipeBtn = new JButton("Add Recipe");
        JButton exitBtn = new JButton("Exit");
        JButton startBtn = new JButton("Start");

        //uneditable
        ingredientList.setEditable(false);
        recipeList.setEditable(false);

        //Labels
        JLabel bgAnim = new JLabel(new ImageIcon("Mixer\\Graphics\\Background\\MainFramebackGround.gif"));

        //Functions
        Connection connection = initConnection();
        reloadRecipes();
        reloadIngredients();

        //essential frame display stuff
        frame.setResizable(false);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        panel.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        //Component Listeners
        addIngredientBtn.addActionListener(e -> userAddIngredient());
        exitBtn.addActionListener(e -> userClickedExit(frame));
        addRecipeBtn.addActionListener(e -> userAddRecipe());
        startBtn.addActionListener(e -> userClickedStart());

        //Setting component positions
        ingredientListScroll.setBounds(40, 40, 300, 500);
        addIngredientBtn.setBounds(90, 550, 200, 25);
        exitBtn.setBounds(525, 600, 125, 25);
        recipeListScroll.setBounds(850, 40, 300, 500);
        addRecipeBtn.setBounds(900, 550, 200, 25);
        startBtn.setBounds(512, 560, 150, 30);
        bgAnim.setBounds(0, 0, 1200, 700);

        //adding components to the frame
        frame.add(panel);
        panel.add(ingredientListScroll);
        panel.add(addIngredientBtn);
        panel.add(exitBtn);
        panel.add(recipeListScroll);
        panel.add(addRecipeBtn);
        panel.add(startBtn);
        panel.add(bgAnim);
        frame.setVisible(true);
    }
    private void userAddIngredient()
    {
        //access database and ask user what ingredient to add.
        //after that, reload ingredient and recipe list
    }
    private void userClickedExit(JFrame frame)
    {
        //popup window asking user to confirm exit
        //if user clicks yes, exit program.
        int ans = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "WARNING", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) 
        {
            frame.dispose();
            System.exit(0);
        }
    }
    private void userAddRecipe()
    {
        //access database and ask user what recipe to add.
        //after that, reload recipe list
    }
    private void userClickedStart()
    {
        //Nothing right now
    }
    //this will be used to initialize the connection for databases
    private Connection initConnection()
    {
        return null;
    }
    //used to reload the recipe list
    //call this function whenever user makes changes to ingredients
    private void reloadRecipes()
    {
        //Nothing right now
    }
    //used to reload the Ingredient List
    //call this function whenever user makes changes to ingredients
    private void reloadIngredients()
    {
        //Nothing right now
    }
}
