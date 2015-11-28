package xyz.johansson.id2212.hw1.hangman.client.view;

import xyz.johansson.id2212.hw1.hangman.client.presenter.EventHandler;
import xyz.johansson.id2212.hw1.hangman.server.ServerDriver;

import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Connection panel.
 * 
 * @author Tobias Johansson
 */
public class ConnectionPanel extends JPanel {

    private final EventHandler eventHandler;
    private JButton button;

    /**
     * Constructor.
     *
     * @param eventHandler action event handler
     */
    public ConnectionPanel(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
        buildGUI();
    }

    /**
     * Set if button is enabled or not.
     *
     * @param bool if button is to be enabled
     */
    public void setButtonEnabled(Boolean bool) {
        button.setEnabled(bool);
    }

    private void buildGUI() {
        setBorder(new TitledBorder(new EtchedBorder(), "Connection"));

        add(new JLabel("Host:"));
        JTextField hostField = new JTextField("localhost", 10);
        add(hostField);

        add(new JLabel("Port:"));
        JTextField portField = new JTextField(Integer.toString(ServerDriver.DEFAULT_PORT), 4);
        add(portField);

        button = new JButton("Connect");
        button.addActionListener((ActionEvent ae) -> {
            new Thread(() -> {
                eventHandler.connectionAttempt(this, hostField.getText(), Integer.parseInt(portField.getText()));
            }).start();
        });
        add(button);
    }
}
