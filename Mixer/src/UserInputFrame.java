import java.awt.Font;
import java.awt.Frame;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

/* Script created by Nigel Garcia
 * June 12, 2025
 * UserInputFrame
 * this frame displays textboxes that the user can add information into the database
 */

public class UserInputFrame {
    private boolean canClose = false;
    //Frame and functionality developed by Shannon
    //Design made by Nigel
    public void userDisplayFrame(Connection conn, JFrame mFrame)
    {
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        ImageIcon frameBg = new ImageIcon("Mixer\\Graphics\\Background\\InsertIngredientBg.png");
        ImageIcon confirmNormalIcon = new ImageIcon("Mixer\\Graphics\\Buttons\\ConfirmNormal.png");
        ImageIcon confirmHoveredIcon = new ImageIcon("Mixer\\Graphics\\Buttons\\ConfirmHovered.gif");
        ImageIcon exitNormalIcon = new ImageIcon("Mixer\\Graphics\\Buttons\\ExitNormal.png");
        ImageIcon exitHoveredIcon = new ImageIcon("Mixer\\Graphics\\Buttons\\ExitHovered.gif");
        JLabel bgLbl = new JLabel(frameBg);

        JTextField ingredientNameTxt = new JTextField();
        ingredientNameTxt.setBorder(BorderFactory.createEmptyBorder());
        ingredientNameTxt.setFont(new Font("Arial", 0, 20));

        JButton confirmBtn = new JButton(confirmNormalIcon);
        confirmBtn.setRolloverIcon(confirmHoveredIcon);
        JButton exitBtn = new JButton(exitNormalIcon);
        exitBtn.setRolloverIcon(exitHoveredIcon);

        frame.setUndecorated(true);

        frame.setResizable(false);
        frame.setSize(0, 0);
        frame.setLocationRelativeTo(mFrame);
        panel.setLayout(null);
        panel.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        ingredientNameTxt.setBounds(25, 65, 345, 65);
        confirmBtn.setBounds(105, 150, 200, 25);
        exitBtn.setBounds(140, 190, 125, 25);
        bgLbl.setBounds(0, 0, 400, 400);

        frame.setModal(false);
        frame.addWindowFocusListener(new DialogCloseManager(frame, canClose));

        confirmBtn.addActionListener(e -> userClickedConfirm(ingredientNameTxt, frame, conn));
        exitBtn.addActionListener(e -> userClickedExit(frame));

        startFrameTransition(frame, false, 100);

        frame.add(panel);
        panel.add(ingredientNameTxt);
        panel.add(confirmBtn);
        panel.add(exitBtn);

        panel.add(bgLbl);
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
    private void userClickedConfirm(JTextField ingredientNameTxt, JDialog Tframe, Connection conn)
    {
        String res = ingredientNameTxt.getText();
        ingredientNameTxt.setText("");
        System.out.println(res);
        try
        {
            String command = "INSERT INTO Ingredients (IngredientName) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(command);
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
            Tframe.addWindowFocusListener(new DialogCloseManager(Tframe, canClose));
            ps.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
