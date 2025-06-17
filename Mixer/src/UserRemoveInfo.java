import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserRemoveInfo {
    private String command;
    private String removeCommand;
    private String column;
    private Connection conn;
    private JFrame mFrame;
    private boolean canClose = false;
    public UserRemoveInfo(String com, String col, Connection con, String rem, JFrame frame)
    {
        command = com;
        column = col;
        conn = con;
        removeCommand = rem;
        mFrame = frame;
    }
    public void DisplayFrame() 
    {
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();
        JTextField ingredientNameTxt = new JTextField(30);
        JButton confirmBtn = new JButton("Confirm");
        JButton exitBtn = new JButton("exit");

        frame.setUndecorated(true);

        JLabel validCheck = new JLabel("");

        frame.setResizable(false);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(mFrame);
        panel.setLayout(null);
        ingredientNameTxt.setLayout(new BorderLayout());
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        ingredientNameTxt.setBounds(20, 20, 200, 25);
        confirmBtn.setBounds(20, 50, 200, 25);
        validCheck.setBounds(20, 100, 100, 25);
        exitBtn.setBounds(300, 300, 100, 25);

        frame.setModal(false);
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame, validCheck));
        exitBtn.addActionListener(e -> userClickedExit(frame));

        startFrameTransition(frame, false, 100);

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        panel.add(validCheck);
        panel.add(exitBtn);
        frame.setVisible(true);
    }
    private void userClickedExit(JDialog frame)
    {
        canClose = true;
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));
        frame.dispose();
    }
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
    private void userClickedConfirm(JTextField txt, JDialog frame, JLabel validLbl)
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
