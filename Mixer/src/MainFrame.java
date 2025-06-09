import javax.swing.*;

/* Script created by Nigel Garcia
 * June 9 2025
 * MainFrame
 * Where all the front end events and inputs happen
 */

public class MainFrame {
    //This Method displays the Frame
    //ONLY CALL ON THIS FUNCTION WHEN YOU NEED THE FRAME TO BE DISPLAYED 
    public void DisplayMainFrame() throws Exception {
        //Essential Components
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        //lists
        JTextPane ingredientList = new JTextPane();

        //Scrollbars
        JScrollPane ingredientListScroll = new JScrollPane(ingredientList);

        //essential frame display stuff
        frame.setResizable(false);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        panel.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("The Cooking Station");

        //Setting component positions
        ingredientListScroll.setBounds(40, 40, 300, 500);

        //adding components to the frame
        frame.add(panel);
        panel.add(ingredientListScroll);
        frame.setVisible(true);
    }
}
