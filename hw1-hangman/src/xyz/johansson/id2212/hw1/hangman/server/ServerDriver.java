package xyz.johansson.id2212.hw1.hangman.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server driver.
 * 
 * @author Tobias Johansson
 * @author Aruna Yedurupaka
 */
public class ServerDriver {

    /**
     * Default port number.
     */
    public static final int DEFAULT_PORT = 55555;
    
    private static final int THREADS = 100;

    /**
     * Start Hangman server.
     *
     * @param args The command line arguments, the first argument if given;
     * specifies port number to use.
     */
    public static void main(String[] args) {
        new ServerDriver().create(args);
    }
    
    private void create(String[] args) {
        int port;
        if (args.length == 1) port = Integer.parseInt(args[0]);
        else port = DEFAULT_PORT;

        List<String> words = getWords();
        System.out.println("Hangman server started. Loaded " + words.size() + " words.");
        
        Executor e = Executors.newFixedThreadPool(THREADS);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Listening on port: " + port);
            for (;;) {
                Socket clientSocket = serverSocket.accept();
                String word = getRandomString(words);
                e.execute(new SlowClientHandler(clientSocket, word));
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerDriver.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Hangman server stopped.");
    }
    
    private List getWords() {
        InputStream inStream = getClass().getResourceAsStream("wordsEn.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        int ch;
        StringBuilder sb = new StringBuilder();
        ArrayList<String> words = new ArrayList();
        try {
            while ((ch = in.read()) != -1) {
                if (ch == '\n') {
                    words.add(sb.toString());
                    sb = new StringBuilder();
                } else if (ch != '\r') {
                    sb.append((char) ch);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return words;
    }
    
    private String getRandomString(List<String> list) {
        return list.get((int)(Math.random()*list.size()));
    }
}
