import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* Nigel Garcia
 * June 14 2025
 * user REmove info
 * remove either a ingredient or a recipe
 */

public class UserRemoveInfo {
    private String command;
    private String removeCommand;
    private String column;
    private Connection conn;
    private JFrame mFrame;
    private boolean canClose = false;
    //c-constructor??? 
    //nah
    public UserRemoveInfo(String com, String col, Connection con, String rem, JFrame frame)
    {
        command = com;
        column = col;
        conn = con;
        removeCommand = rem;
        mFrame = frame;
    }
    //frame display n stuff
    public void DisplayFrame() 
    {
        //ImageIcons
        ImageIcon removeElementBg = new ImageIcon("Mixer\\Graphics\\Background\\RemoveElementBg.png");
        ImageIcon confirmNormal = new ImageIcon("Mixer\\Graphics\\Buttons\\ConfirmNormal.png");
        ImageIcon confirmHover = new ImageIcon("Mixer\\Graphics\\Buttons\\ConfirmHovered.gif");
        ImageIcon exitNormal = new ImageIcon("Mixer\\Graphics\\Buttons\\ExitNormal.png");
        ImageIcon exitHovered = new ImageIcon("Mixer\\Graphics\\Buttons\\ExitHovered.gif");

        //Uhh, an amalgamation of components? I love organizing
        JLabel frameBg = new JLabel(removeElementBg);
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();
        JTextField ingredientNameTxt = new JTextField();
        JButton confirmBtn = new JButton(confirmNormal);
        confirmBtn.setRolloverIcon(confirmHover);
        JButton exitBtn = new JButton(exitNormal);
        exitBtn.setRolloverIcon(exitHovered);

        //hide frame border
        frame.setUndecorated(true);

        //frame essentials
        frame.setResizable(false);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(mFrame);
        panel.setLayout(null);
        ingredientNameTxt.setLayout(new BorderLayout());
        panel.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        //frame bounds setters
        ingredientNameTxt.setBounds(25, 65, 345, 65);
        confirmBtn.setBounds(105, 150, 200, 25);
        exitBtn.setBounds(140, 190, 125, 25);
        frameBg.setBounds(0, 0, 400, 400);

        //no blocking
        frame.setModal(false);
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));

        //listeners
        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame));
        exitBtn.addActionListener(e -> userClickedExit(frame));

        //starting frame animation (zoomy)
        startFrameTransition(frame, false, 100);

        //frame adding
        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        panel.add(exitBtn);

        //always add last
        panel.add(frameBg);
        frame.setVisible(true);
    }
    //exit bt n functionality
    private void userClickedExit(JDialog frame)
    {
        canClose = true;
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));
        frame.dispose();
    }
    //frame anim transition yehehehe
    private void startFrameTransition(JDialog frame, boolean isQuit, int speed)
    {
        Timer timer = new Timer(1/2, new FrameTransition(frame, speed, 400));
        timer.start();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            timer.stop();
        }, 1, TimeUnit.SECONDS);
        scheduledExecutorService.shutdown();
    }
    //confirm functionality, send info to database (in this case, delete info_)
    private void userClickedConfirm(JTextField txt, JDialog frame)
    {
        String res = txt.getText();
        boolean found = false;
        try
        {
            PreparedStatement ps = conn.prepareStatement(command);
            ps.setString(1, res);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                if (rs.getString(column).toLowerCase().equals(res.toLowerCase()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                //put error window saying to try again
            }
            else
            {
                ps = conn.prepareStatement(removeCommand);
                ps.setString(1, res);
                ps.executeUpdate();

                //close all frame sthen refrwesh program
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
