import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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
    private final String dbUrl = "jdbc:ucanaccess://Mixer\\Database\\Tool Mixer.accdb";
    private JButton[] ingredientListBtn = null; //list of ingredientss
    private JButton[] recipeListBtn = null; //list of recipes
    private int ingredientInitX = -500; private int ingredientInitY = 1000; //for ingredList

    //This Method displays the Frame
    //ONLY CALL ON THIS FUNCTION WHEN YOU NEED THE FRAME TO BE DISPLAYED 
    public void DisplayMainFrame() throws Exception {
        //Essential Components
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JPanel recipeList = new JPanel();

        //hide frame window
        frame.setUndecorated(true);

        //ScrollPanes
        JScrollPane recipeListScroll = new JScrollPane(recipeList);
        
        //ScrollBars
        JScrollBar sb = new JScrollBar();

        //Buttons
        //add images to buttons later
        JButton addIngredientBtn = new JButton("Add ingredient");
        JButton addRecipeBtn = new JButton("Add Recipe");
        JButton exitBtn = new JButton("Exit");
        JButton startBtn = new JButton("More Information");

        //ImageIcons
        ImageIcon bgList = new ImageIcon("Mixer\\Graphics\\Background\\ListBg.png");

        //BufferedImages
        BufferedImage ingredientBgListImg = new BufferedImage(bgList.getIconWidth(), bgList.getIconHeight(), BufferedImage.TYPE_INT_RGB);

        //Rotated version of bufferedImages
        BufferedImage ingredientBgListImgFinal = rotateAnimateList(ingredientBgListImg, 9);
        
        //Labels
        JLabel bgAnim = new JLabel(new ImageIcon("Mixer\\Graphics\\Background\\MainFramebackGround.gif"));
        JLabel ingredBgList = new JLabel(new ImageIcon(ingredientBgListImgFinal));

        //Functions and button list initialize
        Connection connection = initConnection();
        int ingredientBtnCount = reloadRecipesIngredients(connection, "SELECT * FROM Ingredients");
        int recipeBtnCount = reloadRecipesIngredients(connection, "SELECT * FROM RecipeTable");
        ingredientListBtn = new JButton[ingredientBtnCount+1];
        recipeListBtn = new JButton[recipeBtnCount+1];

        //scrollBar scales bases on buttonCount
        sb.setMaximum(ingredientBtnCount * 10);

        //essential frame display stuff
        frame.setResizable(false);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        recipeList.setLayout(new FlowLayout());
        panel.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        //Component Listeners
        addIngredientBtn.addActionListener(e -> userAddIngredient());
        exitBtn.addActionListener(e -> userClickedExit(frame));
        addRecipeBtn.addActionListener(e -> userAddRecipe());
        startBtn.addActionListener(e -> userClickedStart());
        sb.addMouseWheelListener(new scrollListener(ingredientListBtn));

        //Setting component positions
        recipeListScroll.setBounds(850, 50, 300, 500);
        addIngredientBtn.setBounds(300, 650, 200, 25);
        exitBtn.setBounds(525, 600, 125, 25);
        recipeListScroll.setBounds(850, 40, 300, 500);
        addRecipeBtn.setBounds(300, 620, 200, 25);
        startBtn.setBounds(512, 550, 150, 30);
        bgAnim.setBounds(0, 0, 1200, 700);
        sb.setBounds(0, -900, 350, 1500);
        ingredBgList.setBounds(ingredientInitX, ingredientInitY, ingredientBgListImgFinal.getWidth(), ingredientBgListImgFinal.getHeight());

        //Visual Changes to JComponents
        sb.setOpaque(false);
        recipeListScroll.setOpaque(false);
        sb.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 20000));
        recipeListScroll.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 255), 5));

        //adding components to the frame
        frame.add(panel);
        panel.add(ingredBgList);
        panel.add(recipeListScroll);
        panel.add(addIngredientBtn);
        panel.add(exitBtn);
        panel.add(addRecipeBtn);
        panel.add(startBtn);
        panel.add(sb);
        
        panel.add(bgAnim);
        frame.setVisible(true);

        /* POST FRAME INITIALIZATION */

        //Opening animation for the ingredientListBg
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        while (ingredientInitX < -90 || ingredientInitY > -95)
        {
            if (ingredientInitX < -90) ingredientInitX = 1 + ingredBgList.getBounds().x;
            else if (ingredientInitY > -95) ingredientInitY = ingredBgList.getBounds().y - 1;
            
            scheduledExecutorService.schedule(() -> {
                ingredBgList.setBounds(ingredientInitX, ingredientInitY, ingredBgList.getWidth(), ingredBgList.getHeight());
                
            }, 1, TimeUnit.SECONDS);
            if (ingredientInitX >= -90 && ingredientInitY <= -95)
            {
                scheduledExecutorService.shutdown();
            }
        }
        
        
        DisplayIngredientBtns(panel, bgAnim, ingredientListBtn, connection, ingredBgList, "SELECT * FROM Ingredients WHERE IngredientID = ?", "IngredientName", 40);
        DisplayRecipeBtns(recipeListBtn, recipeList);

        //Opening animation for the array of ingredient buttons
        Timer iBtnListInit = new Timer(1000/60, new ButtonInit(ingredientListBtn, 5));
        iBtnListInit.start();
        ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(1);
        scheduledExecutorService2.schedule(() -> {
            iBtnListInit.stop();
            scheduledExecutorService2.shutdown();
        }, 375, TimeUnit.MILLISECONDS);
        
    }
    private void DisplayRecipeBtns(JButton[] btns, JPanel panel)
    {
        for (int i = 0; i < btns.length; i++)
        {
            btns[i] = new JButton("test");
            panel.add(btns[i]);
        }
    }
    private void DisplayIngredientBtns(JPanel panel, JLabel bgAnim, JButton[] btnList, Connection conn, JLabel listBg, String command, String column, int initX)
    {
        Timer timer = new Timer(1/10000, new AddButtons(panel, bgAnim, btnList, conn, listBg, command, column, initX));
        timer.start();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            panel.remove(listBg);
            panel.remove(bgAnim);
            panel.add(listBg);
            panel.add(bgAnim);
            timer.stop();
        }, 1, TimeUnit.SECONDS);
        scheduledExecutorService.shutdown();
        listBg.revalidate();
        listBg.repaint();
    }
    private BufferedImage rotateAnimateList(BufferedImage image, int degrees)
    {
        // Calculate the new size of the image based on the angle of rotaion
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) Math.round(image.getWidth() * cos + image.getHeight() * sin);
        int newHeight = (int) Math.round(image.getWidth() * sin + image.getHeight() * cos);

        // Create a new image
        BufferedImage rotate = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotate.createGraphics();
        // Calculate the "anchor" point around which the image will be rotated
        int x = (newWidth - image.getWidth()) / 2;
        int y = (newHeight - image.getHeight()) / 2;
        // Transform the origin point around the anchor point
        AffineTransform at = new AffineTransform();
        at.setToRotation(radians, x + (image.getWidth() / 2), y + (image.getHeight() / 2));
        at.translate(x, y);
        g2d.setTransform(at);
        // Paint the originl image
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return rotate;
    }
    private void userAddIngredient()
    {
        //access database and ask user what ingredient to add.
        //after that, reload ingredient and recipe list
        UserInputFrame inputFrame = new UserInputFrame();
        inputFrame.userDisplayFrame();
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
    private int reloadRecipesIngredients(Connection conn, String command)
    {
        try
        {
            int total = 0;
            Statement s = conn.createStatement();
            s.execute(command);
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
}
class scrollListener implements MouseWheelListener
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
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int current = e.getWheelRotation();
        if (current == 1)
        {
            for (int j = 0; j < ingredBtn.length && ingredBtn[j] != null; j++) //up
            {
                ingredBtn[j].setBounds(-4 + ingredBtn[j].getBounds().x, 20 + ingredBtn[j].getBounds().y, 300, 35);
                offset += 50;
            }
        }
        else if (current == -1)
        {
            for (int j = 0; j < ingredBtn.length && ingredBtn[j] != null; j++) //down
            {
                ingredBtn[j].setBounds(4 + ingredBtn[j].getBounds().x, ingredBtn[j].getBounds().y - 20, 300, 35);
                offset += 50;
            }
        }
    }
}
class AddButtons implements ActionListener
{
    private JButton[] ingredBtn = null;
    private JPanel panel = null;
    private JLabel bgAnim = null;
    private Connection connection = null;
    private JLabel listBg = null;
    private int i = 0;
    private int offset = 50;
    private String command = "";
    private String column = "";
    private int initX = 0;
    public AddButtons(JPanel ingredList, JLabel bg, JButton[] ingredientBtn, Connection conn, JLabel listB, String com, String colName, int x)
    {
        panel = ingredList;
        bgAnim = bg;
        ingredBtn = ingredientBtn;
        connection = conn;
        listBg = listB;
        command = com;
        column = colName;
        initX = x;
    }
    MainFrame mainFrame = new MainFrame();
    int angle = 0;
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        if (i < ingredBtn.length-1)
        {
            panel.remove(bgAnim);
            try
            {
                PreparedStatement ps = connection.prepareStatement(command);
                ps.setInt(1, i+1);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next())
                {
                    ingredBtn[i] = new JButton(rs.getString(column));
                    ingredBtn[i].setBounds((-offset/5) + initX, offset - 150, 300, 35);
                    panel.add(ingredBtn[i]);
                    i++;
                    offset += 50;
                    panel.add(bgAnim);
                    panel.revalidate();
                    panel.repaint();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            panel.remove(bgAnim);
            panel.add(listBg);
            panel.add(bgAnim);
        }
    }
}
class ButtonInit implements ActionListener
{
    private JButton[] btn;
    private int initX = 0;
    public ButtonInit(JButton[] btnList, int x)
    {
        btn = btnList;
        initX = x;
    }
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        for (int i = 0; i < btn.length && btn[i] != null; i++)
        {
            btn[i].setBounds(initX + btn[i].getBounds().x, btn[i].getBounds().y, 300, 35);
        }
    }
}
