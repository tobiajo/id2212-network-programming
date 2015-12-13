package xyz.johansson.id2212.hw5.hangmanandroid.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.io.IOException;

import xyz.johansson.id2212.hw1.hangman.client.model.ServerInterface;
import xyz.johansson.id2212.hw1.hangman.server.GameState;
import xyz.johansson.id2212.hw5.hangmanandroid.R;
import xyz.johansson.id2212.hw5.hangmanandroid.view.GameFragment;
import xyz.johansson.id2212.hw5.hangmanandroid.view.MainActivity;

public class EventHandler {

    public static final int DEFAULT_PORT = 55555;

    private MainActivity mainActivity;
    private ServerInterface serverInterface;

    public EventHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        serverInterface = new ServerInterface();
    }

    //-------------------------------------------------------------------------
    // Connection methods
    //-------------------------------------------------------------------------

    public void connectionAttempt(final String ipAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainActivity.setConnectionButton(false);
                mainActivity.setConnectionInfo("Connecting... Please wait");
                try {
                    GameState initialGameState = serverInterface.connect(ipAddress, DEFAULT_PORT);
                    changeFragment(getGameFragment(initialGameState));
                } catch (IOException e) {
                    mainActivity.setConnectionButton(true);
                    mainActivity.setConnectionInfo("Connection failed. Try again");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private GameFragment getGameFragment(GameState initialGameState) {
        GameFragment gameFragment = new GameFragment();
        Bundle args = new Bundle();
        args.putSerializable("initialGameState", initialGameState);
        gameFragment.setArguments(args);
        return gameFragment;
    }

    private void changeFragment(Fragment newFragment) {
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //-------------------------------------------------------------------------
    // Game methods
    //-------------------------------------------------------------------------

    public void guessWord(final String guess) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainActivity.setHangmanButton(false);
                mainActivity.setHangmanInfo("Waits answer... Please wait");
                try {
                    GameState newGameState = serverInterface.guessWord(guess);
                    updateGamePanel(newGameState);
                } catch (IOException ex) {
                    mainActivity.setHangmanInfo("Connection lost. Please restart");
                    try {
                        serverInterface.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverInterface.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateGamePanel(GameState gameState) throws IOException, ClassNotFoundException {
        mainActivity.setHangmanState(gameState);
        if (!gameState.getWord().contains("-")) {
            mainActivity.setHangmanInfo("Congratulations, you won!");
            serverInterface.disconnect();
        } else if (gameState.getMisses() == 6) {
            String fullWord = serverInterface.guessWord("dummy").getWord();
            mainActivity.setHangmanInfo("The word was: " + fullWord);
            serverInterface.disconnect();
        } else {
            mainActivity.setHangmanButton(true);
            mainActivity.setHangmanInfo("");
        }
    }
}
