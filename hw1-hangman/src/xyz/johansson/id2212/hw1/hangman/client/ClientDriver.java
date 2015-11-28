package xyz.johansson.id2212.hw1.hangman.client;

import xyz.johansson.id2212.hw1.hangman.client.view.MainPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Client driver.
 * 
 * @author Tobias Johansson
 */
public class ClientDriver {

    /**
     * Start Hangman client.
     *
     * @param args The command line arguments, not used.
     */
    public static void main(String[] args) {
        /**
         * invokeLater(Runnable doRun): Causes doRun.run() to be executed
         * asynchronously on the AWT event dispatching thread.
         *
         * https://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
         */
        SwingUtilities.invokeLater(() -> {
            new ClientDriver().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Hangman");
        frame.add(new MainPanel());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
