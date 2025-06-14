/* Script created by Nigel Garcia
 * June 9 2025
 * Main
 * The main script, RUN THIS SCRIPT TO RUN THE PROGRAM
 */

 //Edit: This script may be redundant, however ill keep it just in case.

public class Main {
    public static void main(String[] args) throws Exception {
        //Display MainFrame instance
        MainFrame mainFrame = new MainFrame();
        mainFrame.DisplayMainFrame();
    }
    public void reloadProgram()
    {
        try
        {
            MainFrame mainFrame = new MainFrame();
            mainFrame.DisplayMainFrame();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
