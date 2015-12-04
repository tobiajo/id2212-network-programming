package xyz.johansson.id2212.hw2.marketrmi.client;

public class IllegalNameException extends Exception {
    public IllegalNameException(String reason) {
        super(reason);
    }

    public static void check(String name) throws IllegalNameException {
        if (name.length() == 0) {
            throw new IllegalNameException("empty name");
        }
        for (char ch : name.toCharArray()) {
            if (ch < 'A' || ch > 'z') {
                System.out.println(ch);
                throw new IllegalNameException("illegal name");
            }
        }
    }
}
