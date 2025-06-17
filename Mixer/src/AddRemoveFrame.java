import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.Connection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
/* Script created by Nigel Garcia
 * May 15 2025
 */

public class AddRemoveFrame {
    public void DisplayFrame(Connection connection, JFrame mFrame)
    {
        JDialog frame = new JDialog();
        JPanel panel = new JPanel();

        frame.setUndecorated(true);

        JButton addIngredientBtn = new JButton("Add ingredient");
        JButton addRecipeBtn = new JButton("Add Recipe");
        JButton removeIngredientBtn = new JButton("Remove Ingredient");
        JButton removeRecipeBtn = new JButton("Remove Recipe");
        JButton cancelBtn = new JButton("cancel");

        ImageIcon bgAnim = new ImageIcon("Mixer\\Graphics\\Background\\AddRemoveBg.gif");
        JLabel bgAnimLbl = new JLabel(bgAnim);

        frame.setResizable(false);
        frame.setSize(0, 0);
        frame.setLocationRelativeTo(mFrame);
        panel.setLayout(null);
        panel.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        addIngredientBtn.addActionListener(e -> userAddIngredient(connection, mFrame));
        addRecipeBtn.addActionListener(e -> userAddRecipe(connection, mFrame));
        removeIngredientBtn.addActionListener(e -> userRemove(true, connection, mFrame));
        removeRecipeBtn.addActionListener(e -> userRemove(false, connection, mFrame));
        cancelBtn.addActionListener(e -> userClickedCancel(frame));

        frame.setModal(false);
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e)
            {
                frame.dispose();
            }
            @Override
            public void windowGainedFocus(WindowEvent e) {}
        });

        addIngredientBtn.setBounds(100, 40, 200, 35);
        addRecipeBtn.setBounds(100, 80, 200, 35);
        removeIngredientBtn.setBounds(100, 120, 200, 35);
        removeRecipeBtn.setBounds(100, 160, 200, 35);
        cancelBtn.setBounds(100, 200, 200, 35);
        bgAnimLbl.setBounds(-150, -50, 700, 500);

        startFrameTransition(frame, false, 100);

        changeButtonVisual(addIngredientBtn);
        changeButtonVisual(addRecipeBtn);
        changeButtonVisual(removeIngredientBtn);
        changeButtonVisual(removeRecipeBtn);
        changeButtonVisual(cancelBtn);

        frame.add(panel);
        panel.add(addIngredientBtn);
        panel.add(addRecipeBtn);
        panel.add(removeIngredientBtn);
        panel.add(removeRecipeBtn);
        panel.add(cancelBtn);

        //alwasy add last
        panel.add(bgAnimLbl);
        frame.setVisible(true);
    }
    private void changeButtonVisual(JButton btn)
    {
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalTextPosition(JButton.CENTER);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 255), 3));
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setForeground(Color.WHITE);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                JButton selBtn = (JButton)e.getSource();
                selBtn.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150, 255), 6));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton selBtn = (JButton)e.getSource();
                selBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 255), 3));
            }
        });
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
    private void userClickedCancel(JDialog frame)
    {
        frame.dispose();
    }
    private void userRemove(boolean which, Connection con, JFrame mFrame)
    {
        if (which)
        {
            UserRemoveInfo uRemove = new UserRemoveInfo("SELECT * FROM Ingredients WHERE IngredientName = ?", "IngredientName", con, "DELETE * FROM Ingredients WHERE IngredientName = ?", mFrame);
            uRemove.DisplayFrame();
        }
        else
        {
            UserRemoveInfo uRemove = new UserRemoveInfo("SELECT * FROM RecipeTable WHERE RecipeName = ?", "RecipeName", con, "DELETE FROM RecipeTable WHERE RecipeName = ?", mFrame);
            uRemove.DisplayFrame();
        }
    }
    private void userAddIngredient(Connection conn, JFrame mFrame)
    {
        //access database and ask user what ingredient to add.
        //after that, reload ingredient and recipe list
        UserInputFrame inputFrame = new UserInputFrame();
        inputFrame.userDisplayFrame(conn, mFrame);
    }
    private void userAddRecipe(Connection conn, JFrame mFrame)
    {
        //access database and ask user what recipe to add.
        //after that, reload recipe list
        int length = MainFrame.getIngredientLength();
        System.out.println(length);
        UserRecipeInputFrame inputFrame = new UserRecipeInputFrame(length);
        inputFrame.userDisplayFrame(conn, mFrame);
    }
}
