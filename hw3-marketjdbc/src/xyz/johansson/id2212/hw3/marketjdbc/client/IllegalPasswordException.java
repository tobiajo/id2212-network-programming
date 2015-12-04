package xyz.johansson.id2212.hw3.marketjdbc.client;

public class IllegalPasswordException extends Exception {
    public IllegalPasswordException(String reason) {
        super(reason);
    }

    public static void check(String name) throws IllegalPasswordException {
        if (name.length() < 8) {
            throw new IllegalPasswordException("least 8 characters");
        }
    }
}
