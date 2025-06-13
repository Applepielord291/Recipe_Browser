import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

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
    //dont forget to change this value to the actual path once the database is setup
    private final String dbUrl = "jdbc:ucanaccess://Mixer\\Database\\Tool Mixer.accdb";
    private JButton[] ingredientListBtn = null; //have arraysize change depending on database elements
    private int x = -500; private int y = 1000; //for ingredList

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

        UIManager.put("ScrollBar.maximumThumbSize", new Dimension(0, 0));

        //ScrollBars
        JScrollBar sb = new JScrollBar();
        JScrollBar ingredScrollBar = ingredientListScroll.getVerticalScrollBar();
        ingredScrollBar.setUI(new ScrollBarVisual());
        

        //Buttons
        //add images to buttons later
        JButton addIngredientBtn = new JButton("Add ingredient");
        JButton addRecipeBtn = new JButton("Add Recipe");
        JButton exitBtn = new JButton("Exit");
        JButton startBtn = new JButton("More Information");

        //Labels
        JLabel bgAnim = new JLabel(new ImageIcon("Mixer\\Graphics\\Background\\MainFramebackGround.gif"));

        //Images
        ImageIcon bgList = new ImageIcon("Mixer\\Graphics\\Background\\ListBg.png");
        BufferedImage b = new BufferedImage(bgList.getIconWidth(), bgList.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        
        //Functions
        Connection connection = initConnection();
        int ingredientBtnCount = reloadRecipes(connection);
        //initialize button list
        ingredientListBtn = new JButton[ingredientBtnCount+1];
        sb.setMaximum(ingredientBtnCount * 10);
        BufferedImage bgList1 = rotateAnimateList(b, 9);
        JLabel dawd = new JLabel(new ImageIcon(bgList1));
        dawd.setBounds(x, y, bgList1.getWidth(), bgList1.getHeight());
        //desired position is -90, -95

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
        startBtn.addActionListener(e -> userClickedStart());
        sb.addMouseWheelListener(new scrollListener(ingredientListBtn));

        //Setting component positions
        ingredientListScroll.setBounds(40, 40, 300, 500);
        addIngredientBtn.setBounds(300, 650, 200, 25);
        exitBtn.setBounds(525, 600, 125, 25);
        recipeListScroll.setBounds(850, 40, 300, 500);
        addRecipeBtn.setBounds(300, 620, 200, 25);
        startBtn.setBounds(512, 550, 150, 30);
        bgAnim.setBounds(0, 0, 1200, 700);
        sb.setBounds(0, -900, 350, 1500);

        sb.setOpaque(false);
        //sb.repaint(0, 0, 0, 0);


        //adding components to the frame
        frame.add(panel);
        panel.add(dawd);
        panel.add(addIngredientBtn);
        panel.add(exitBtn);
        panel.add(addRecipeBtn);
        panel.add(startBtn);
        reloadIngredients(ingredientList);
        panel.add(sb);
        
        panel.add(bgAnim);
        frame.setVisible(true);

        sb.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 20000));

        sb.repaint();
        
        //list backgound initial animation
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        while (x < -90 || y > -95)
        {
            if (x < -90) x = 1 + dawd.getBounds().x;
            else if (y > -95) y = dawd.getBounds().y - 1;
            
            scheduledExecutorService.schedule(() -> {
                dawd.setBounds(x, y, dawd.getWidth(), dawd.getHeight());
                
            }, 1, TimeUnit.SECONDS);
            if (x >= -90 && y <= -95)
            {
                scheduledExecutorService.shutdown();
            }
        }
        
        DisplayBtns(panel, bgAnim, ingredientListBtn, connection, dawd);
        Timer iBtnListInit = new Timer(1000/60, new ButtonInit(ingredientListBtn));
        iBtnListInit.start();
        ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(1);
        scheduledExecutorService2.schedule(() -> {
            iBtnListInit.stop();
            scheduledExecutorService2.shutdown();
        }, 375, TimeUnit.MILLISECONDS);
        
    }
    private void DisplayBtns(JPanel panel, JLabel bgAnim, JButton[] ingredientListBtn, Connection conn, JLabel listBg)
    {
        
        Timer timer = new Timer(1/10000, new AddButtons(panel, bgAnim, ingredientListBtn, conn, listBg));
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
    
    /*public void adjustmentValueChanged(AdjustmentEvent e)
    {
        if (e.getValue() < current) //up
        {
            
        }
        else if (e.getValue() > current)
        {
            
        }
    }*/
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int current = e.getWheelRotation();
        if (current == 1)
        {
            for (int j = 0; j < ingredBtn.length && ingredBtn[j] != null; j++)
            {
                ingredBtn[j].setBounds(-4 + ingredBtn[j].getBounds().x, 20 + ingredBtn[j].getBounds().y, 300, 35);
                offset += 50;
            }
        }
        else if (current == -1)
        {
            for (int j = 0; j < ingredBtn.length && ingredBtn[j] != null; j++)
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
    public AddButtons(JPanel ingredList, JLabel bg, JButton[] ingredientBtn, Connection conn, JLabel listB)
    {
        panel = ingredList;
        bgAnim = bg;
        ingredBtn = ingredientBtn;
        connection = conn;
        listBg = listB;
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
                String command = "SELECT * FROM Ingredients WHERE IngredientID = ?";

                PreparedStatement ps = connection.prepareStatement(command);
                ps.setInt(1, i+1);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next())
                {
                    ingredBtn[i] = new JButton(rs.getString("IngredientName"));
                    ingredBtn[i].setBounds((-offset/5) + 40, offset - 150, 300, 35);
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
    public ButtonInit(JButton[] btnList)
    {
        btn = btnList;
    }
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        for (int i = 0; i < btn.length && btn[i] != null; i++)
        {
            btn[i].setBounds(5 + btn[i].getBounds().x, btn[i].getBounds().y, 300, 35);
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
