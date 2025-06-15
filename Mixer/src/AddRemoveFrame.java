import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
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
        JFrame frame = new JFrame();
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

        addIngredientBtn.addActionListener(e -> userAddIngredient(connection));
        addRecipeBtn.addActionListener(e -> userAddRecipe(connection));
        removeIngredientBtn.addActionListener(e -> userRemove(true, connection));
        removeRecipeBtn.addActionListener(e -> userRemove(false, connection));
        cancelBtn.addActionListener(e -> userClickedCancel(frame));

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
        btn.addMouseListener(new ButtonHover());
    }
    private void startFrameTransition(JFrame frame, boolean isQuit, int speed)
    {
        Timer timer = new Timer(1/2, new FrameTransition(frame, speed));
        timer.start();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            timer.stop();
        }, 1, TimeUnit.SECONDS);
        scheduledExecutorService.shutdown();
    }
    private void userClickedCancel(JFrame frame)
    {
        frame.dispose();
    }
    private void userRemove(boolean which, Connection con)
    {
        if (which)
        {
            UserRemoveInfo uRemove = new UserRemoveInfo("SELECT * FROM Ingredients WHERE IngredientName = ?", "IngredientName", con, "DELETE * FROM Ingredients WHERE IngredientName = ?");
            uRemove.DisplayFrame();
        }
        else
        {
            UserRemoveInfo uRemove = new UserRemoveInfo("SELECT * FROM RecipeTable WHERE RecipeName = ?", "RecipeName", con, "DELETE FROM RecipeTable WHERE RecipeName = ?");
            uRemove.DisplayFrame();
        }
    }
    private void userAddIngredient(Connection conn)
    {
        //access database and ask user what ingredient to add.
        //after that, reload ingredient and recipe list
        UserInputFrame inputFrame = new UserInputFrame();
        inputFrame.userDisplayFrame(conn);
    }
    private void userAddRecipe(Connection conn)
    {
        //access database and ask user what recipe to add.
        //after that, reload recipe list
        UserRecipeInputFrame inputFrame = new UserRecipeInputFrame();
        inputFrame.userDisplayFrame(conn);
    }
}

class FrameTransition implements ActionListener
{
    private JFrame frame;
    private int xSize = 0;
    private int ySize = 0;
    private int speed = 0;
    public FrameTransition(JFrame f, int spd)
    {
        frame = f;
        speed = spd;
    }
    @Override 
    public void actionPerformed(ActionEvent arg0)
    {
        if (frame.getSize().width < 400)
        {
            xSize += speed; ySize += speed;
            frame.setBounds(frame.getBounds().x-50, frame.getBounds().y-50, ySize, xSize);
        }
    }
}

class ButtonHover implements MouseListener
{
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {}

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {}

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {}

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        JButton selBtn = (JButton)e.getSource();
        selBtn.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150, 255), 6));
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        JButton selBtn = (JButton)e.getSource();
        selBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 255), 3));
    }
    
}
