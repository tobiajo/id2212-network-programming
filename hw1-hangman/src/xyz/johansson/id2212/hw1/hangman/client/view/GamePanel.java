package xyz.johansson.id2212.hw1.hangman.client.view;

import xyz.johansson.id2212.hw1.hangman.server.GameState;
import xyz.johansson.id2212.hw1.hangman.client.presenter.EventHandler;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Game panel.
 * 
 * @author Tobias Johansson
 */
public class GamePanel extends JPanel {

    private final EventHandler eventHandler;
    private JTextField textField;
    private JLabel label;
    private JButton button;

    /**
     * Constructor.
     *
     * @param eventHandler action event handler
     * @param gameState initial game state
     */
    public GamePanel(EventHandler eventHandler, GameState gameState) {
        this.eventHandler = eventHandler;
        buildGUI(gameState);
    }

    private void buildGUI(GameState gameState) {
        setBorder(new TitledBorder(new EtchedBorder(), "Game"));
        setLayout(new BorderLayout());
        add(label = new JLabel(), BorderLayout.NORTH);
        add(textField = new JTextField(gameState.getWord().length()), BorderLayout.WEST);
        add(button = new JButton("Guess"), BorderLayout.EAST);
        setLabel(gameState);
        button.addActionListener((ActionEvent ae) -> {
            new Thread(() -> {
                eventHandler.guessWord(this, textField.getText());
            }).start();
        });
    }
    
    private void setLabel(GameState gameState) {
        label.setIcon(getHangmanIcon(gameState.getMisses()));
        label.setText(gameState.getWord());
    }
    
    private ImageIcon getHangmanIcon(int misses) {
        StringBuilder sb = new StringBuilder();
        sb.append("image/Hangman-");
        sb.append(Integer.toString(misses));
        sb.append(".png");
        String path = sb.toString();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(path));
        return new ImageIcon(imageIcon.getImage().getScaledInstance(64, 64, Image.SCALE_AREA_AVERAGING));
    }

    /**
     * Update GUI.
     *
     * @param gameState new game state
     */
    public void update(GameState gameState) {
        textField.setText("");
        setLabel(gameState);
    }

    /**
     * Set if button is to be enabled or not.
     *
     * @param bool if button is to be enabled
     */
    public void setButtonEnabled(Boolean bool) {
        button.setEnabled(bool);
    }
}
