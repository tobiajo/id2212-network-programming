package xyz.johansson.id2212.hw3.marketjdbc.client;

import se.kth.id2212.ex3.bankjdbc.Bank;
import se.kth.id2212.ex3.bankjdbc.RejectedException;
import xyz.johansson.id2212.hw3.marketjdbc.server.Client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientDriver extends UnicastRemoteObject implements Client {

    public static final String BANK_NAME = "Nordea";
    public static final String MARKET_NAME = "Tradera";

    private String username;
    private String password;
    private Market market;
    private se.kth.id2212.ex3.bankjdbc.Account bankAccount;

    public ClientDriver() throws RemoteException {
    }

    public static void main(String[] args) {
        try {
            new ClientDriver().run();
        } catch (RemoteException | NotBoundException | MalformedURLException | RejectedException | SQLException e) {
            System.out.println("fatal error: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void callback(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public void run() throws RemoteException, MalformedURLException, SQLException, NotBoundException, RejectedException {
        connectMarket();
        connectBank();
        userInterface();
        System.exit(0);
    }

    private String getInput() {
        return new Scanner(System.in).nextLine();
    }

    private void connectMarket() throws RemoteException, NotBoundException, MalformedURLException, SQLException {
        market = (Market) Naming.lookup(MARKET_NAME);
        System.out.println("Connected to: " + MARKET_NAME);
        while (true) {
            System.out.print("Enter user name: ");
            username = getInput();
            try {
                IllegalNameException.check(username);
                if (!market.accountExist(username)) {
                    System.out.print("User not found\nCreate a new user (yes/no/exit): ");
                    switch (getInput()) {
                        case "yes":
                            while (true) {
                                System.out.print("Enter password: ");
                                password = getInput();
                                try {
                                    IllegalPasswordException.check(password);
                                    if (market.newAccount(this, username, password)) {
                                        System.out.println(MARKET_NAME + ": User created");
                                        System.out.println(MARKET_NAME + ": Logged in");
                                        return;
                                    } else {
                                        System.out.println(MARKET_NAME + ": Someone took your user name," +
                                                "please choose a new one");
                                        break;
                                    }
                                } catch (IllegalPasswordException e) {
                                    System.out.println("error: " + e.getMessage());
                                }
                            }
                            break;
                        case "no":
                            break;
                        case "exit":
                            System.exit(0);
                            break;
                    }
                } else {
                    System.out.print("Enter password: ");
                    password = getInput();
                    if (market.login(this, username, password)) {
                        return;
                    }
                }
            } catch (IllegalNameException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }

    private void connectBank() throws RemoteException, NotBoundException, MalformedURLException, RejectedException {
        Bank bank = (Bank) Naming.lookup(BANK_NAME);
        bankAccount = bank.getAccount(username);
        if (bankAccount == null) {
            bankAccount = bank.newAccount(username);
            bankAccount.deposit(1000);
            System.out.println("You received a welcome gift from " + BANK_NAME + "!");
        }
        System.out.println("Balance at " + BANK_NAME + ": " + bankAccount.getBalance());
    }

    private void userInterface() throws RemoteException, SQLException {
        while (true) {
            System.out.print("\n1. Bank\n2. Items\n3. Market\n4. Exit\n> ");
            switch (getInput()) {
                case "1":
                    bank();
                    break;
                case "2":
                    items();
                    break;
                case "3":
                    market();
                    break;
                case "4":
                    market.logout(this);
                    return;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    private void bank() throws RemoteException {
        while (true) {
            System.out.print("\n1. Get balance\n2. Deposit\n3. Withdraw\n4. Back\n> ");
            switch (getInput()) {
                case "1":
                    System.out.println("Balance: " + bankAccount.getBalance());
                    break;
                case "2":
                    System.out.print("Deposit: ");
                    try {
                        bankAccount.deposit(Float.parseFloat(getInput()));
                    } catch (NumberFormatException | RejectedException e) {
                        System.out.println("error: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.print("Withdraw: ");
                    try {
                        bankAccount.withdraw(Float.parseFloat(getInput()));
                    } catch (NumberFormatException | RejectedException e) {
                        System.out.println("error: " + e.getMessage());
                    }
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    private void items() throws RemoteException, SQLException {
        while (true) {
            System.out.print("\n1. List\n2. Add\n3. Remove\n4. Back\n> ");
            switch (getInput()) {
                case "1":
                    System.out.println(market.listItems(this));
                    break;
                case "2":
                    System.out.print("Item: ");
                    try {
                        String item = getInput();
                        IllegalNameException.check(item);
                        market.addItem(this, item);
                        System.out.println("Add succeeded");
                    } catch (IllegalNameException e) {
                        System.out.println("error: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.print("ID: ");
                    try {
                        int id = Integer.parseInt(getInput());
                        if (market.removeItem(this, id) != 0) {
                            System.out.println("Remove succeeded");
                        } else {
                            System.out.println("Remove failed");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("error: " + e.getMessage());
                    }
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    private void market() throws RemoteException, SQLException {
        while (true) {
            System.out.print("\n1. Sell\n2. Buy\n3. Inspect\n4. Activity\n5. Back\n> ");
            switch (getInput()) {
                case "1":
                    try {
                        System.out.print("ID: ");
                        int id = Integer.parseInt(getInput());
                        System.out.print("Price: ");
                        float price = Float.parseFloat(getInput());
                        if (market.sell(this, id, price)) {
                            System.out.println("Item placed in market");
                        } else {
                            System.out.println("Sell failed: item not found");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("error: " + e.getMessage());
                    }
                    break;
                case "2":
                    try {
                        System.out.print("Item: ");
                        int id = Integer.parseInt(getInput());
                        int status = market.buy(this, id);
                        switch (status) {
                            case 0:
                                System.out.println("Buy succeeded");
                                break;
                            case 1:
                                System.out.println("Buy failed: item not in market");
                                break;
                            case 2:
                                System.out.println("Buy failed: bank transaction error");
                                break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("error: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.println(market.inspect());
                    break;
                case "4":
                    System.out.println(market.activity(this));
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }
}
