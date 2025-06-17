import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

/* Nigel Garcia
 * June 16 2025
 * Frame transition
 * the starting animation for some of the JDialogs, where the frame scales up
 */

class FrameTransition implements ActionListener
{
    private JDialog frame;
    private int xSize = 0;
    private int ySize = 0;
    private int speed = 0;
    private int endGoal = 0;
    public FrameTransition(JDialog f, int spd, int goal)
    {
        frame = f;
        speed = spd;
        endGoal = goal;
    }
    //actual animation
    @Override 
    public void actionPerformed(ActionEvent arg0)
    {
        if (frame.getSize().width < endGoal)
        {
            xSize += speed; ySize += speed;
            frame.setBounds(frame.getBounds().x-50, frame.getBounds().y-50, ySize, xSize);
        }
    }
}