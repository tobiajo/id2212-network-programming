package xyz.johansson.id2212.hw1.hangman.client.presenter;

import xyz.johansson.id2212.hw1.hangman.client.model.ServerInterface;
import xyz.johansson.id2212.hw1.hangman.client.view.GamePanel;
import xyz.johansson.id2212.hw1.hangman.server.GameState;
import xyz.johansson.id2212.hw1.hangman.client.view.ConnectionPanel;
import xyz.johansson.id2212.hw1.hangman.client.view.MainPanel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Event handler.
 * 
 * @author Tobias Johansson
 */
public class EventHandler {

    private final MainPanel mainPanel;
    private final ServerInterface serverInterface;

    /**
     * Constructor.
     *
     * @param mainPanel the main panel.
     */
    public EventHandler(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        serverInterface = new ServerInterface();
    }

    /**
     * Attempt to connect to server. If the connection is successful then set
     * the game panel, with the initial game state, as the main panels center
     * panel.
     *
     * @param connectionPanel the connection panel
     * @param host server IP address
     * @param port server port
     */
    public void connectionAttempt(ConnectionPanel connectionPanel, String host, int port) {
        SwingUtilities.invokeLater(() -> {
            connectionPanel.setButtonEnabled(false);
            mainPanel.setStatusText("Connecting... Please wait");
        });
        try {
            GameState initialGameState = serverInterface.connect(host, port);
            SwingUtilities.invokeLater(() -> {
                mainPanel.setCenterPanel(new GamePanel(this, initialGameState));
                mainPanel.setStatusText(" ");
            });
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> {
                connectionPanel.setButtonEnabled(true);
                mainPanel.setStatusText("Connection failed. Try again");
            });
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Send guess to server and update GUI with the new game state.
     *
     * @param gamePanel the game panel
     * @param guess the guess
     */
    public void guessWord(GamePanel gamePanel, String guess) {
        SwingUtilities.invokeLater(() -> {
            gamePanel.setButtonEnabled(false);
            mainPanel.setStatusText("Waits answer... Please wait");
        });
        try {
            GameState newGameState = serverInterface.guessWord(guess);
            updateGamePanel(gamePanel, newGameState);
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> {
                mainPanel.setStatusText("Connection lost. Please restart");
            });
            try {
                serverInterface.disconnect();
            } catch (IOException ex1) {
                Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateGamePanel(GamePanel gamePanel, GameState gameState) throws IOException, ClassNotFoundException {
        SwingUtilities.invokeLater(() -> {
            gamePanel.update(gameState);
        });
        if (!gameState.getWord().contains("-")) {
            SwingUtilities.invokeLater(() -> {
                mainPanel.setStatusText("Congratulations, you won!");
            });
        } else if (gameState.getMisses() == 6) {
            String fullWord = serverInterface.guessWord("dummy").getWord();
            SwingUtilities.invokeLater(() -> {
                mainPanel.setStatusText("The word was: " + fullWord);
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                mainPanel.setStatusText(" ");
                gamePanel.setButtonEnabled(true);
            });
        }
    }
}
