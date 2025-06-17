import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;

/* NIgel Gairca
 * June 17 2025
 * Dialog close manager
 * Dialog closing manager for all the JDialogs
 */

//youve seen this class referenced in all of my JDialogs
//essentialy, this class makes it so that a JDialog can only close if the user clicks the exit btn
//also to prevent users from accidentaly clicking off and losing progress n stuff
public class DialogCloseManager implements WindowFocusListener {
    private JDialog frame;
    private boolean canClose;
    public DialogCloseManager(JDialog f, boolean cClose)
    {
        frame = f;
        canClose = cClose;
    }
    @Override
    public void windowGainedFocus(WindowEvent e) {}

    @Override
    public void windowLostFocus(WindowEvent e) {
        if (!canClose) frame.setVisible(true);
        else frame.dispose();
    }
}
