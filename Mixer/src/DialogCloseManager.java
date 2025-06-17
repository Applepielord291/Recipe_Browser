import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;

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
