import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.AffineTransform;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* Script created by Nigel Garcia
 * June 9 2025
 * MainFrame
 * Where all the front end events and inputs happen
 */

public class MainFrame {
    //dont forget to change this value to the actual path once the database is setup
    private final String dbUrl = "jdbc:ucanaccess://Mixer\\Database\\Tool Mixer.accdb";
    private JButton[] ingredientListBtn = null; //have arraysize change depending on database elements

    //This Method displays the Frame
    //ONLY CALL ON THIS FUNCTION WHEN YOU NEED THE FRAME TO BE DISPLAYED 
    public void DisplayMainFrame() throws Exception {
        //Essential Components
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        //hide frame window
        frame.setUndecorated(true);

        //lists
        //These are where the checkboxes will be placed
        JPanel ingredientList = new JPanel();
        JPanel recipeList = new JPanel();

        //ScrollPanes
        JScrollPane ingredientListScroll = new JScrollPane(ingredientList);
        JScrollPane recipeListScroll = new JScrollPane(recipeList);

        //ScrollBars
        JScrollBar sb = new JScrollBar();
        JScrollBar ingredScrollBar = ingredientListScroll.getVerticalScrollBar();
        ingredScrollBar.setUI(new ScrollBarVisual());

        //Buttons
        //add images to buttons later
        JButton addIngredientBtn = new JButton("Add ingredient");
        JButton addRecipeBtn = new JButton("Add Recipe");
        JButton exitBtn = new JButton("Exit");
        JButton startBtn = new JButton("Start");

        //Labels
        JLabel bgAnim = new JLabel(new ImageIcon("Mixer\\Graphics\\Background\\MainFramebackGround.gif"));

        //Functions
        Connection connection = initConnection();
        int ingredientBtnCount = reloadRecipes(connection);
        //initialize button list
        ingredientListBtn = new JButton[ingredientBtnCount+1];

        //essential frame display stuff
        frame.setResizable(false);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        //ingredientList.setLayout(new BoxLayout(ingredientList, BoxLayout.Y_AXIS));
        ingredientList.setLayout(null);
        recipeList.setLayout(null);
        panel.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        //Component Listeners
        addIngredientBtn.addActionListener(e -> userAddIngredient());
        exitBtn.addActionListener(e -> userClickedExit(frame));
        addRecipeBtn.addActionListener(e -> userAddRecipe());
        //startBtn.addActionListener(e -> userClickedStart());
        startBtn.addActionListener(e -> rotateComponents(panel, bgAnim, ingredientListBtn, connection));
        sb.addAdjustmentListener(new scrollListener(ingredientListBtn));

        //Setting component positions
        ingredientListScroll.setBounds(40, 40, 300, 500);
        addIngredientBtn.setBounds(90, 550, 200, 25);
        exitBtn.setBounds(525, 600, 125, 25);
        recipeListScroll.setBounds(850, 40, 300, 500);
        addRecipeBtn.setBounds(900, 550, 200, 25);
        startBtn.setBounds(512, 550, 150, 30);
        bgAnim.setBounds(0, 0, 1200, 700);
        sb.setBounds(400, 300, 35, 300);

        //adding components to the frame
        frame.add(panel);
        panel.add(addIngredientBtn);
        panel.add(exitBtn);
        panel.add(addRecipeBtn);
        panel.add(startBtn);
        reloadIngredients(ingredientList);
        panel.add(sb);

        panel.add(bgAnim);
        frame.setVisible(true);
    }
    private void rotateComponents(JPanel ingredientList, JLabel bgAnim, JButton[] ingredientListBtn, Connection conn)
    {
        Timer timer = new Timer(1/10000, new ButtonRotation(ingredientList, bgAnim, ingredientListBtn,conn));
        timer.start();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            timer.stop();
        }, 1, TimeUnit.SECONDS);
        scheduledExecutorService.shutdown();
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
        // Function made by Shannon Duldulao
        //this is used for Connecting the database into the program
        try {
            Connection Connect = DriverManager.getConnection(dbUrl);
            return Connect;
        }
        catch (SQLException e){
            System.out.println(e);
            return null;
        }
    }
    //used to reload the recipe list
    //call this function whenever user makes changes to ingredients
    private int reloadRecipes(Connection conn)
    {
        try
        {
            int total = 0;
            Statement s = conn.createStatement();
            s.execute("SELECT * FROM Ingredients");
            ResultSet rs = s.getResultSet();
            while (rs!=null && rs.next())
            {
                total = Integer.parseInt(rs.getString(1));
            }
            return total;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return 0;
        }
    }
    //used to reload the Ingredient List
    //call this function whenever user makes changes to ingredients
    private void reloadIngredients(JPanel ingredientList)
    {
        /*JButton[] ingredBtn = new JButton[40];
        for (int i = 0; i < ingredBtn.length; i++)
        {
            ingredBtn[i] = new JButton("Test!");
            ingredBtn[i].setMaximumSize(new Dimension(500, 65));
            ingredientList.add(ingredBtn[i]);
            ingredientList.revalidate();
            ingredientList.repaint();
        }*/
    }
}
class scrollListener implements AdjustmentListener
{
    private JButton[] ingredBtn = null;
    int j = 0;
    int offset = 50;
    public scrollListener(JButton[] btnList)
    {
        if (btnList != null)
        {
            ingredBtn = btnList;
        }
    }
    int current = 1;
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        if (e.getValue() < current) //up
        {
            for (int j = 0; j < ingredBtn.length && ingredBtn[j] != null; j++)
            {
                current = e.getValue();
                ingredBtn[j].setBounds(2 + ingredBtn[j].getBounds().x, ingredBtn[j].getBounds().y - 10, 300, 35);
                offset += 50;
            }
        }
        else if (e.getValue() > current)
        {
            for (int j = 0; j < ingredBtn.length && ingredBtn[j] != null; j++)
            {
                current = e.getValue();
                ingredBtn[j].setBounds(-2 + ingredBtn[j].getBounds().x, 10 + ingredBtn[j].getBounds().y, 300, 35);
                offset += 50;
            }
        }
    }
}
class ButtonRotation implements ActionListener
{
    JButton[] ingredBtn = null;
    private JPanel ingredientList = null;
    private JLabel bgAnim = null;
    Connection connection = null;
    int i = 0;
    int j = 0;
    int offset = 50;
    public ButtonRotation(JPanel ingredList, JLabel bg, JButton[] ingredientBtn, Connection conn)
    {
        ingredientList = ingredList;
        bgAnim = bg;
        ingredBtn = ingredientBtn;
        connection = conn;
    }
    MainFrame mainFrame = new MainFrame();
    int angle = 0;
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        
        if (i < ingredBtn.length-1)
        {
            ingredientList.remove(bgAnim);
            try
            {
                Statement s = connection.createStatement();
                s.execute("SELECT IngredientName FROM Ingredients" + " WHERE IngredientID = 3");
                ResultSet rs = s.getResultSet();
                if (rs.next())
                {
                    ingredBtn[i] = new JButton(rs.getString("IngredientName"));
                }
                ingredBtn[i].setBounds((-offset/5) + 100, offset - 150, 300, 35);
                ingredientList.add(ingredBtn[i]);
                ingredientList.revalidate();
                ingredientList.repaint();
                i++;
                offset += 50;
                ingredientList.add(bgAnim);
                ingredientList.revalidate();
                ingredientList.repaint();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        
    }
}
class ScrollBarVisual extends BasicScrollBarUI
{
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
    {
        
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = null;
        JScrollBar scrollBar = (JScrollBar)c;
        if (!scrollBar.isEnabled() || thumbBounds.width > thumbBounds.height)
        {
            return;
        }
        else if (isDragging)
        {
            color = Color.DARK_GRAY;
        }
        else if (isThumbRollover())
        {
            color = Color.LIGHT_GRAY;
        }
        else
        {
            color = Color.GRAY;
        }
        g2.setPaint(color);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
        g2.setPaint(Color.WHITE);
        g2.drawRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
        g2.dispose();
    }

    @Override
    protected void setThumbBounds(int x, int y, int width, int height)
    {
        super.setThumbBounds(x, y, width, height);
        scrollbar.repaint();
    }
}
