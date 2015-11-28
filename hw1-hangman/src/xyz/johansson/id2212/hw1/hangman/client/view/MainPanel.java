package xyz.johansson.id2212.hw1.hangman.client.view;

import xyz.johansson.id2212.hw1.hangman.client.presenter.EventHandler;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Main panel.
 * 
 * @author Tobias Johansson
 */
public class MainPanel extends JPanel {

    private final EventHandler eventHandler;
    private BorderLayout borderLayout;
    private JLabel statusLabel;

    /**
     * Constructor.
     */
    public MainPanel() {
        eventHandler = new EventHandler(this);
        buildGUI();
    }

    /**
     * Set center panel.
     *
     * @param panel new center panel
     */
    public void setCenterPanel(JPanel panel) {
        remove(borderLayout.getLayoutComponent(BorderLayout.CENTER));
        add(BorderLayout.CENTER, panel);
        framePack();
    }

    /**
     * Set status field text.
     *
     * @param status new status text
     */
    public void setStatusText(String status) {
        statusLabel.setText(status);
        framePack();
    }

    private void framePack() {
        ((JFrame) SwingUtilities.getRoot(this)).pack();
    }

    /**
     * Initial panels.
     */
    
    private void buildGUI() {
        setLayout(borderLayout = new BorderLayout());
        add(createMenuPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel(" "));
        return panel;
    }

    private JPanel createCenterPanel() {
        return new ConnectionPanel(eventHandler);
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        statusLabel = new JLabel(" ");
        panel.add(statusLabel);
        return panel;
    }
}
