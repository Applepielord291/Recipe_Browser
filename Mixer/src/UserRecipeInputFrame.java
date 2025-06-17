import javax.swing.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserRecipeInputFrame {
    private int ingredientLength = 0;
    private JButton[] ingredientList = null;
    private String[] selectedIngredients = null;
    public UserRecipeInputFrame(int length)
    {
        ingredientLength = length;
    }
    public void userDisplayFrame(Connection conn, JFrame mFrame) 
    {
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        frame.setUndecorated(true);

        JTextField ingredientNameTxt = new JTextField();
        JTextField linkTxt = new JTextField();

        JFileChooser fileChooser = new JFileChooser();

        JButton confirmBtn = new JButton("Confirm");
        JButton selectBtn = new JButton("Select Image");

        JPanel ingredientBg = new JPanel();

        JScrollPane ingBgScroll = new JScrollPane(ingredientBg);

        displayIngredientBtns(conn, ingredientBg);

        frame.setResizable(false);
        frame.setSize(0, 0);
        frame.setLocationRelativeTo(mFrame);
        panel.setLayout(null);
        ingredientBg.setLayout(new BoxLayout(ingredientBg, BoxLayout.Y_AXIS));
        panel.setSize(550, 550);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        ingredientNameTxt.setBounds(20, 20, 200, 25);
        confirmBtn.setBounds(20, 50, 200, 25);
        ingBgScroll.setBounds(20, 100, 200, 300);
        linkTxt.setBounds(250, 20, 200, 25);
        selectBtn.setBounds(250, 60, 200, 25);

        frame.setModal(false);
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e)
            {
                
            }
            @Override
            public void windowGainedFocus(WindowEvent e) {}
        });

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame, conn));
        selectBtn.addActionListener(e -> userImageSearch(fileChooser, panel));

        startFrameTransition(frame, false, 100);

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        panel.add(ingBgScroll);
        panel.add(linkTxt);
        panel.add(selectBtn);
        frame.setVisible(true);
    }
    private void userImageSearch(JFileChooser fc, JPanel p)
    {
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            String selImage = file.getAbsolutePath();
            ImageIcon selIcon = new ImageIcon(selImage);
            JLabel selLbl = new JLabel(selIcon);
            selLbl.setBounds(250, 150, 300, 300);
            p.add(selLbl);
            System.out.println(selLbl);
            p.revalidate(); p.repaint();
            System.out.println("done");
        }
    }
    private void startFrameTransition(JDialog frame, boolean isQuit, int speed)
    {
        Timer timer = new Timer(1/2, new FrameTransition(frame, speed, 550));
        timer.start();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            timer.stop();
        }, 1, TimeUnit.SECONDS);
        scheduledExecutorService.shutdown();
    }
    private void displayIngredientBtns(Connection con, JPanel p)
    {
        ingredientList = new JButton[ingredientLength];
        selectedIngredients = new String[ingredientLength];
        try
        {
            int i = 0;
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM Ingredients");
            while (rs.next())
            {
                ingredientList[i] = new JButton(rs.getString(2));
                ingredientList[i].setMinimumSize(new Dimension(200, 35));
                ingredientList[i].setMaximumSize(new Dimension(200, 35));
                ingredientList[i].addActionListener(e -> userClickedIngredient(e));
                p.add(ingredientList[i]);
                i++;
            }
        }
        catch (Exception e)
        {

        }
        
    }
    private void userClickedIngredient(ActionEvent e)
    {
        JButton selBtn = (JButton)e.getSource();
        String txt = selBtn.getText();
        for (int i = 0; i < selectedIngredients.length; i++)
        {
            if (selectedIngredients[i] == null)
            {
                selectedIngredients[i] = txt;
                break;
            }
        }
    }
    private void userClickedConfirm(JTextField ingredientNameTxt, JDialog frame, Connection conn)
    {
        String res = ingredientNameTxt.getText();
        ingredientNameTxt.setText("");
        String resultSelect = "";
        for (int i = 0; i < selectedIngredients.length; i++)
        {
            if (selectedIngredients[i] != null)
            {
                resultSelect += selectedIngredients[i] + ",";
            }
        }
        System.out.println(resultSelect);
        System.out.println(res);
        try
        {
            String command = "INSERT INTO RecipeTable (RecipeName, Requirements) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(command);
            ps.setString(1, res);
            ps.setString(2, resultSelect);
            ps.executeUpdate();

            Frame[] frames = JFrame.getFrames();
            for (int i = 0; i < frames.length; i++)
            {
                frames[i].dispose();
            }
            Main main = new Main();
            main.reloadProgram();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
