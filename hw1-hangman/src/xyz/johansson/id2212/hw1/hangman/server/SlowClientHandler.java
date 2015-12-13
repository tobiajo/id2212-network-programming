package xyz.johansson.id2212.hw1.hangman.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Slow client handler.
 * 
 * @author Tobias Johansson
 */
public class SlowClientHandler implements Runnable {

    /**
     * Artificial delay.
     */
    public static final int DELAY_MS = 1000;

    private final Socket socket;
    private final String word;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Constructor.
     *
     * @param socket client socket
     * @param word word for client to guess
     */
    public SlowClientHandler(Socket socket, String word) {
        this.socket = socket;
        this.word = word;
        System.out.println("(" + socket.getInetAddress() + ") connected");
    }

    /**
     * Run client handler.
     */
    @Override
    public void run() {
        try {
            System.out.println("(" + socket.getInetAddress() + ") word: " + word);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            try {
                playGame();
                System.out.println("(" + socket.getInetAddress() + ") game finished");
            } catch (IOException ex) {
                System.out.println("(" + socket.getInetAddress() + ") connection lost");
            }
            out.close();
            in.close();
            socket.close();
            System.out.println("(" + socket.getInetAddress() + ") disconnected");
        } catch (IOException ex) {
            Logger.getLogger(SlowClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void artificialDelay() {
        try {
            Thread.sleep(DELAY_MS);
        } catch (InterruptedException ex) {
            Logger.getLogger(SlowClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void playGame() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            sb.append('-');
        }
        GameState gameState = new GameState(0, sb.toString());

        artificialDelay();
        out.writeObject(gameState); // send inital game state

        while (gameState.getMisses() != 6 && !gameState.getWord().equals(word)) {
            try {
                String guess = ((String) in.readObject()).toLowerCase();
                gameState = getNewGameState(gameState, guess);
                artificialDelay();
                out.writeObject(gameState);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SlowClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (gameState.getMisses() == 6) out.writeObject(new GameState(6, word));
    }

    private GameState getNewGameState(GameState gameState, String guess) {
        int newGameStateMisses = gameState.getMisses();
        String newGameStateWord = gameState.getWord();
        if (guess.length() == 1) {
            if (word.contains(guess)) {
                char[] clientChars = gameState.getWord().toCharArray();
                for (int i = 0; i < word.length(); i++) {
                    if (word.charAt(i) == guess.charAt(0)) {
                        clientChars[i] = guess.charAt(0);
                    }
                }
                newGameStateWord = new String(clientChars);
            } else {
                newGameStateMisses++;
            }
        } else if (guess.equals(word)) {
            newGameStateWord = word;
        } else {
            newGameStateMisses++;
        }
        return new GameState(newGameStateMisses, newGameStateWord);
    }
}
