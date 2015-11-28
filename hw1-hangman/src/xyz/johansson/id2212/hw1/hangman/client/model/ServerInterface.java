package xyz.johansson.id2212.hw1.hangman.client.model;

import xyz.johansson.id2212.hw1.hangman.server.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Server interface.
 * 
 * @author Tobias Johansson
 */
public class ServerInterface {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Constructor.
     */
    public ServerInterface() {
        socket = null;
        in = null;
        out = null;
    }

    /**
     * Connect to server, return initial game state.
     *
     * @param host server IP address
     * @param port server port
     * @return initial game state
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public GameState connect(String host, int port) throws IOException, ClassNotFoundException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        return (GameState) in.readObject();
    }

    /**
     * Send guess to server and return new game state.
     *
     * @param guess the guess
     * @return new game state
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public GameState guessWord(String guess) throws IOException, ClassNotFoundException {
        out.writeObject(guess);
        return (GameState) in.readObject();
    }

    /**
     * Disconnect from server.
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }
}
