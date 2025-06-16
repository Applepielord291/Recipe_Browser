import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    private static JButton[] ingredientListBtn = null; //list of ingredientss
    private JButton[] recipeListBtn = null; //list of recipes
    private int ingredientInitX = -500; private int ingredientInitY = 1000; //for ingredList
    private JFrame frame = new JFrame();
    private String[] selectedIngredients = null;

    //This Method displays the Frame
    //ONLY CALL ON THIS FUNCTION WHEN YOU NEED THE FRAME TO BE DISPLAYED 
    public void DisplayMainFrame() throws Exception {
        //Essential Components
        
        JPanel panel = new JPanel();
        JPanel recipeList = new JPanel();

        //hide frame window
        frame.setUndecorated(true);

        //ScrollPanes
        JScrollPane recipeListScroll = new JScrollPane(recipeList);
        recipeListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        //ScrollBars
        JScrollBar sb = new JScrollBar();

        //Buttons
        //add images to buttons later
        JButton addRemoveMenu = new JButton("Settings");
        JButton exitBtn = new JButton("Exit");
        JButton recipeSearchBtn = new JButton("Recipe Search");

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
        int ingredientBtnCount = reloadRecipesIngredients(connection, "SELECT * FROM Ingredients", "UPDATE Ingredients SET IngredientID = ?");
        int recipeBtnCount = reloadRecipesIngredients(connection, "SELECT * FROM RecipeTable", "UPDATE RecipeTable SET RecipeID = ?");
        ingredientListBtn = new JButton[ingredientBtnCount+1];
        recipeListBtn = new JButton[recipeBtnCount];
        selectedIngredients = new String[ingredientBtnCount];

        //scrollBar scales bases on buttonCount
        sb.setMaximum(ingredientBtnCount * 10);

        //essential frame display stuff
        frame.setResizable(false);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        recipeList.setLayout(new BoxLayout(recipeList, BoxLayout.Y_AXIS));
        panel.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        //Component Listeners
        exitBtn.addActionListener(e -> userClickedExit(frame));
        addRemoveMenu.addActionListener(e -> displayAddRemoveMenu(connection));
        recipeSearchBtn.addActionListener(e -> searchForValidRecipes(connection, recipeList));

        sb.addMouseWheelListener(new scrollListener(ingredientListBtn));

        //Setting component positions
        exitBtn.setBounds(525, 600, 125, 25);
        recipeListScroll.setBounds(875, 80, 300, 500);
        bgAnim.setBounds(0, 0, 1200, 700);
        sb.setBounds(0, -900, 350, 1500);
        ingredBgList.setBounds(ingredientInitX, ingredientInitY, ingredientBgListImgFinal.getWidth(), ingredientBgListImgFinal.getHeight());
        addRemoveMenu.setBounds(525, 500, 125, 25);
        recipeSearchBtn.setBounds(525, 525, 125, 25);

        //Visual Changes to JComponents
        sb.setOpaque(false);
        //recipeList.setOpaque(false);
        sb.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 20000));
        recipeListScroll.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 255), 7));
        recipeList.setBackground(new Color(0, 0, 0, 120));
        recipeListScroll.setBackground(new Color(0, 0, 0, 120));

        //adding components to the frame
        frame.add(panel);
        panel.add(ingredBgList);
        panel.add(recipeListScroll);
        panel.add(exitBtn);
        panel.add(sb);
        panel.add(addRemoveMenu);
        panel.add(recipeSearchBtn);
        
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
        
        DisplayIngredientBtns(panel, bgAnim, ingredientListBtn, connection, ingredBgList, "SELECT * FROM Ingredients WHERE IngredientName = ?", "IngredientName", 40, sb);

        //Opening animation for the array of ingredient buttons
        Timer iBtnListInit = new Timer(1000/60, new ButtonInit(ingredientListBtn, 5));
        iBtnListInit.start();
        ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(1);
        scheduledExecutorService2.schedule(() -> {
            iBtnListInit.stop();
            scheduledExecutorService2.shutdown();
        }, 375, TimeUnit.MILLISECONDS);
        
    }
    private void searchForValidRecipes(Connection con, JPanel recipePanel)
    {
        int count = 0;
        recipePanel.removeAll();
        try
        {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM RecipeTable");
            while (rs.next())
            {
                //reset values back to normal
                String[] tempSelIng = new String[selectedIngredients.length];
                for (int i = 0; i < tempSelIng.length; i++)
                {
                    tempSelIng[i] = selectedIngredients[i];
                }
                int currentRow = 0;
                int currentBegin = 0;
                String[] req = new String[10];
                
                //split single string into multiple
                //count number of commas?
                String current = rs.getString(3);
                for (int i = 0; i < current.length(); i++)
                {
                    if (current.charAt(i) == ','||current.charAt(i) == '.')
                    {
                        req[currentRow] = current.substring(currentBegin, i);
                        currentBegin = i+1;
                        currentRow++;
                    }
                }
                int tester = 0;
                for (int i = 0; i < req.length; i++)
                {
                    if (req[i] != null) 
                    {
                        tester++;
                    }
                }
                for (int i = 0; i < tempSelIng.length; i++)
                {
                    for (int j = 0; j < req.length; j++)
                    {
                        if (tempSelIng[i]!=null && req[j]!=null && tempSelIng[i].toLowerCase().equals(req[j].toLowerCase()))
                        {
                            tester = tester-1;

                            //change to null for the valid recipe check
                            tempSelIng[i] = null;
                        }
                    }
                }
                //loop through array again, if all values in selected ingredients are null, then that means that the recipe is valid.
                //with how the system is, you MUST add a parameter where the user must select at least one ingredient
                //either that, or add a counter for each time something is found, and if found = 0, have recipe be invalid anyway.

                if (tester == 0)
                {
                    //save name to add it as a button
                    recipeListBtn[count] = new JButton(rs.getString(2));
                    recipeListBtn[count].setMinimumSize(new Dimension(300, 45));
                    recipeListBtn[count].setMaximumSize(new Dimension(300, 45));
                    recipeListBtn[count].addActionListener(e -> moreInfoIngredient(e, con));
                    recipeListBtn[count].setFocusPainted(false);
                    recipeListBtn[count].setForeground(Color.BLACK);
                    
                    recipePanel.add(recipeListBtn[count]); 
                    recipePanel.revalidate();
                    //sql command to find row with exact same recipe ingredients and uhh just take the name and add it as the button in the frame
                    count = count+1;
                }
                else
                {
                    //just end it here GRAHHH
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void moreInfoIngredient(ActionEvent e, Connection connection)
    {
        IngredientInfoFrame ingInfo = new IngredientInfoFrame();
        ingInfo.DisplayFrame((JButton)e.getSource(), frame, connection);
    }
    private void displayAddRemoveMenu(Connection con)
    {
        AddRemoveFrame addRem = new AddRemoveFrame();
        addRem.DisplayFrame(con, frame);
    }
    public void reloadFrame()
    {
        frame.dispose();
    }
    public static int getIngredientLength()
    {
        return ingredientListBtn.length;
    }

    private void DisplayIngredientBtns(JPanel panel, JLabel bgAnim, JButton[] btnList, Connection conn, JLabel listBg, String command, String column, int initX, JScrollBar sb)
    {
        Timer timer = new Timer(1/10000, new AddButtons(panel, bgAnim, btnList, conn, listBg, command, column, initX, sb, selectedIngredients));
        timer.start();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            panel.remove(listBg);
            panel.remove(bgAnim);
            panel.add(listBg);
            panel.add(bgAnim);
            listBg.revalidate();
            listBg.repaint();
            timer.stop();
        }, 1, TimeUnit.SECONDS);
        scheduledExecutorService.shutdown();
        
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
    private int reloadRecipesIngredients(Connection conn, String command, String command2)
    {
        try
        {
            int total = 0;
            Statement s = conn.createStatement();
            s.execute(command);
            ResultSet rs = s.getResultSet();
            while (rs!=null && rs.next())
            {
                total++;
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
class AddButtons implements ActionListener, MouseListener
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
    private JScrollBar sb = null;
    private Statement s = null;
    private ResultSet rs2 = null;
    private boolean runOnce = false;
    private ResultSet rs = null;
    private String[] ingredientSelected = null;
    private ImageIcon ingredientBtnUnselected = new ImageIcon("Mixer\\Graphics\\Buttons\\IngredientUnselected.png");
    private ImageIcon ingredientBtnHovered = new ImageIcon("Mixer\\Graphics\\Buttons\\IngredientHovered.png");
    public AddButtons(JPanel ingredList, JLabel bg, JButton[] ingredientBtn, Connection conn, JLabel listB, String com, String colName, int x, JScrollBar s, String[] stringList)
    {
        panel = ingredList;
        bgAnim = bg;
        ingredBtn = ingredientBtn;
        connection = conn;
        listBg = listB;
        command = com;
        column = colName;
        initX = x;
        sb = s;
        ingredientSelected = stringList;
    }
    MainFrame mainFrame = new MainFrame();
    int angle = 0;
    int skip = 0;
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        if (i < ingredBtn.length-1)
        {
            panel.remove(bgAnim);
            try
            {
                if (!runOnce)
                {
                    s = connection.createStatement();
                    s.execute("SELECT IngredientName FROM Ingredients");
                    rs2 = s.getResultSet();
                    runOnce = true;
                }

                PreparedStatement ps = connection.prepareStatement(command);
                if (rs2.next())
                {
                    ps.setString(1, rs2.getString(column));
                    rs = ps.executeQuery();
                }
                
                while (rs.next())
                {
                    panel.remove(sb);
                    panel.remove(bgAnim);
                    panel.remove(listBg);
                    ingredBtn[i] = new JButton(rs.getString(column), ingredientBtnUnselected);
                    ingredBtn[i].setHorizontalTextPosition(JButton.CENTER); ingredBtn[i].setHorizontalTextPosition(JButton.CENTER);
                    ingredBtn[i].setForeground(Color.WHITE); ingredBtn[i].setFont(new Font("Arial", Font.PLAIN, 16));
                    ingredBtn[i].setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20, 255), 3));
                    ingredBtn[i].setBounds((-offset/5) + initX, offset - 150, 300, 35);
                    ingredBtn[i].addActionListener(e -> userClickedIngredient(e));
                    ingredBtn[i].addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent t) 
                        {
                            JButton selBtn = (JButton)t.getSource();
                            selBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200, 255), 4));
                            selBtn.setIcon(ingredientBtnHovered);
                        }
                        public void mouseExited(MouseEvent t)
                        {
                            JButton selBtn = (JButton)t.getSource();
                            selBtn.setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20, 255), 3));
                            selBtn.setIcon(ingredientBtnUnselected);
                        }
                    });
                    panel.add(ingredBtn[i]);
                    i++;
                    offset += 50;
                    panel.add(listBg);
                    panel.add(bgAnim);
                    panel.revalidate();
                    panel.repaint();
                    panel.add(sb);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void userClickedIngredient(ActionEvent e)
    {
        ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(1);
        scheduledExecutorService2.schedule(() -> {
            JButton selectedBtn = (JButton)e.getSource();
            String selectIng = selectedBtn.getText();
            int fin = 0;
            int speed = 0;
            boolean x = false;
            for (int i = 0; i < ingredientSelected.length; i++)
            {
                for (int j = 0; j < ingredientSelected.length; j++)
                {
                    if (ingredientSelected[j] == selectIng)
                    {
                        ingredientSelected[j] = null;
                        fin = -85;
                        speed = -5;
                        x = true;
                    }
                }
            
                if (ingredientSelected[i] == null && !x)
                {
                    ingredientSelected[i] = selectIng;
                    fin = 85;
                    speed = 5;
                    x = true;
                    break;
                }
            }

            Timer timer = new Timer(1, new ButtonSelected(selectedBtn, fin, speed));
            timer.start();
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.schedule(() -> {
                timer.stop();
            }, 450, TimeUnit.MILLISECONDS);
            scheduledExecutorService.shutdown();
        }, 345, TimeUnit.MILLISECONDS);
        scheduledExecutorService2.shutdown();
    }
    @Override
    public void mouseEntered(MouseEvent e)
    {
        JButton selBtn = (JButton)e.getSource();
        selBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200, 255), 4));
    }
    @Override
    public void mouseExited(MouseEvent e)
    {
        JButton selBtn = (JButton)e.getSource();
        selBtn.setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20, 255), 3));
    }
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
}

class ButtonSelected implements ActionListener
{
    private JButton selBtn;
    private int finalX;
    private int speed;
    public ButtonSelected(JButton btn, int fin, int spd)
    {
        selBtn = btn;
        finalX = selBtn.getBounds().x;
        speed = spd;
    }
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        finalX += speed;
        selBtn.setBounds(finalX, selBtn.getBounds().y, 300, 35);
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

//TODO: Things to do
/* 
 * rework select button animation (icon animation instead of movement animation)
 * update database with more information
 * make all the frames and buttons look better
 * make transition animation when user launches program
 * add searching for searching for ingredient buttons
 * 
 */