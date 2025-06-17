import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* Nigel garcia
 * June 13 2025
 * user Recipe Input frame
 * window pops up and users can intert their own recipes into the database!
 */

public class UserRecipeInputFrame {
    private int ingredientLength = 0;
    private JButton[] ingredientList = null;
    private String[] selectedIngredients = null;
    private String imgFilePath = "";
    private boolean canClose = false;
    //Construct.
    //(to get length)
    public UserRecipeInputFrame(int length)
    {
        ingredientLength = length;
    }
    public void userDisplayFrame(Connection conn, JFrame mFrame) 
    {
        //frame essentials
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        //hide border
        frame.setUndecorated(true);

        //JTextFields/textpanes
        JTextField ingredientNameTxt = new JTextField();
        JTextField linkTxt = new JTextField();
        JTextPane recipeInstructions = new JTextPane();

        //ImageIcons
        ImageIcon frameBg = new ImageIcon("Mixer\\Graphics\\Background\\RecipeSelectionBg.png");
        ImageIcon confirmNormal = new ImageIcon("Mixer\\Graphics\\Buttons\\ConfirmNormal.png");
        ImageIcon confirmHover = new ImageIcon("Mixer\\Graphics\\Buttons\\ConfirmHovered.gif");
        ImageIcon exitNormal = new ImageIcon("Mixer\\Graphics\\Buttons\\ExitNormal.png");
        ImageIcon exitHover = new ImageIcon("Mixer\\Graphics\\Buttons\\ExitHovered.gif");
        JLabel frameBgLbl = new JLabel(frameBg);

        //JFile chooser
        //for selecting images, however image in db no work and no time n stuff so ...
        //here lies the remnants of what couldhave been, a sloppy image selector
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg"));

        //JButtons and setting hover icons RAHH
        JButton confirmBtn = new JButton(confirmNormal);
        confirmBtn.setRolloverIcon(confirmHover);
        JButton selectBtn = new JButton("Select Image");
        JButton exitBtn = new JButton(exitNormal);
        exitBtn.setRolloverIcon(exitHover);

        //Panel for ingredients
        JPanel ingredientBg = new JPanel();

        //scroll panes
        JScrollPane ingBgScroll = new JScrollPane(ingredientBg);
        JScrollPane recipeInsScroll = new JScrollPane(recipeInstructions);

        //functions
        displayIngredientBtns(conn, ingredientBg);

        //frame essentials
        frame.setResizable(false);
        frame.setSize(0, 0);
        frame.setLocationRelativeTo(mFrame);
        panel.setLayout(null);
        ingredientBg.setLayout(new BoxLayout(ingredientBg, BoxLayout.Y_AXIS));
        panel.setSize(550, 550);
        frame.setTitle("The Cooking Station");

        //set bounds n stuff for components
        ingredientNameTxt.setBounds(40, 60, 200, 25);
        confirmBtn.setBounds(250, 480, 200, 25);
        exitBtn.setBounds(50, 480, 125, 25);
        ingBgScroll.setBounds(40, 200, 200, 135);
        linkTxt.setBounds(315, 60, 200, 25);
        selectBtn.setBounds(315, 195, 200, 25);
        recipeInsScroll.setBounds(25, 380, 500, 100);
        frameBgLbl.setBounds(0, 0, 550, 550);

        //no blocking
        frame.setModal(false);
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));

        //listeners
        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, linkTxt, recipeInstructions, frame, conn));
        selectBtn.addActionListener(e -> userImageSearch(fileChooser, panel, frameBgLbl));
        exitBtn.addActionListener(e -> userClickedExit(frame));

        //frame transition animation (real>?!?!?!)
        startFrameTransition(frame, false, 100);

        //frame adding
        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        panel.add(ingBgScroll);
        panel.add(linkTxt);
        panel.add(selectBtn);
        panel.add(recipeInsScroll);
        panel.add(exitBtn);

        //always add last
        panel.add(frameBgLbl);
        frame.setVisible(true);
    }
    //exit btn functionality 
    private void userClickedExit(JDialog frame)
    {
        canClose = true;
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));
        frame.dispose();
    }
    //image searching, but useless now
    //but I also dont feel like removing it
    //maybe if i need it one day ill look back and copy paste this
    private void userImageSearch(JFileChooser fc, JPanel p, JLabel bg)
    {
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            imgFilePath = file.getAbsolutePath();
            ImageIcon selIcon = new ImageIcon(imgFilePath);
            JLabel selLbl = new JLabel(selIcon);
            selLbl.setBounds(315, 180, 200, 200);
            p.remove(bg);
            p.add(selLbl);
            p.add(bg);
            p.revalidate(); p.repaint();
        }
    }
    //frame zoomy animation
    private void startFrameTransition(JDialog frame, boolean isQuit, int speed)
    {
        Timer timer = new Timer(1/2, new FrameTransition(frame, speed, 550));
        timer.start();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            frame.setBounds(frame.getBounds().x, frame.getBounds().y, 550, 550);
            timer.stop();
        }, 450, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }
    //ingredients display on panel for stuff
    //so that users can select ingredients included in stuff n stuff
    private void displayIngredientBtns(Connection con, JPanel p)
    {
        ingredientList = new JButton[ingredientLength];
        selectedIngredients = new String[ingredientLength];
        ImageIcon btnIcon = new ImageIcon("Mixer\\Graphics\\Buttons\\ingRec.png");
        try
        {
            int i = 0;
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM Ingredients");
            while (rs.next())
            {
                ingredientList[i] = new JButton(rs.getString(2), btnIcon);
                ingredientList[i].setHorizontalTextPosition(JButton.CENTER);
                ingredientList[i].setMinimumSize(new Dimension(200, 35));
                ingredientList[i].setMaximumSize(new Dimension(200, 35));
                ingredientList[i].addActionListener(e -> userClickedIngredient(e));
                ingredientList[i].setForeground(Color.WHITE);
                ingredientList[i].setFont(new Font("Arial", Font.PLAIN, 14));
                
                p.add(ingredientList[i]);
                p.revalidate(); p.repaint();
                i++;
            }
        }
        catch (Exception e)
        {

        }
    }
    //visual stuff and also helsp to keep track of selected ingredients
    private void userClickedIngredient(ActionEvent e)
    {
        JButton selBtn = (JButton)e.getSource();
        String txt = selBtn.getText();
        boolean x = false;
        ImageIcon btnIconUnsel = new ImageIcon("Mixer\\Graphics\\Buttons\\ingRec.png");
        ImageIcon btnIconSel = new ImageIcon("Mixer\\Graphics\\Buttons\\ingRecSel.gif");
        for (int i = 0; i < selectedIngredients.length; i++)
        {
            for (int j = 0; j < selectedIngredients.length; j++)
            {
                if (selectedIngredients[j] == txt)
                {
                    selectedIngredients[j] = null;
                    //place unselected icon here
                    selBtn.setIcon(btnIconUnsel);
                    x = true;
                }
            }
        
            if (selectedIngredients[i] == null && !x)
            {
                selectedIngredients[i] = txt;
                //place selected icon here
                selBtn.setIcon(btnIconSel);
                x = true;
                break;
            }
        }
    }
    //confirm functionality, when clicked insert info into recipe table n stuff
    private void userClickedConfirm(JTextField nameTxt, JTextField linkTxt, JTextPane recipeIns, JDialog frame, Connection conn)
    {
        String res = nameTxt.getText();
        String resultSelect = "";
        for (int i = 0; i < selectedIngredients.length; i++)
        {
            if (selectedIngredients[i] != null)
            {
                resultSelect += selectedIngredients[i] + ",";
            }
        }
        try
        {
            String command = "INSERT INTO RecipeTable (RecipeName, Requirements, RecipeLink, RecipeInstructions) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(command);
            ps.setString(1, res);
            ps.setString(2, resultSelect);
            ps.setString(3, linkTxt.getText());
            ps.setString(4,recipeIns.getText());
            ps.executeUpdate();

            Frame[] frames = JFrame.getFrames();
            for (int i = 0; i < frames.length; i++)
            {
                frames[i].dispose();
            }
            Main main = new Main();
            main.reloadProgram();
            canClose = true;
            frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));
            ps.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
