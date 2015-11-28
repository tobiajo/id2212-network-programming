package xyz.johansson.id2212.hw1.hangman.server;

import java.io.Serializable;

/**
 * Game state.
 * 
 * @author Tobias Johansson
 */
public class GameState implements Serializable {

    private final int misses;
    private final String word;

    /**
     * Constructor.
     *
     * @param misses Number of misses.
     * @param word The word the client see.
     */
    public GameState(int misses, String word) {
        this.misses = misses;
        this.word = word;
    }

    /**
     * Get misses.
     *
     * @return misses
     */
    public int getMisses() {
        return misses;
    }

    /**
     * Get word.
     *
     * @return word
     */
    public String getWord() {
        return word;
    }
}
