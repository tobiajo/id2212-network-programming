package xyz.johansson.id2212.hw2.marketrmi.client;

import se.kth.id2212.ex2.bankrmi.Account;
import se.kth.id2212.ex2.bankrmi.Bank;
import se.kth.id2212.ex2.bankrmi.RejectedException;
import xyz.johansson.id2212.hw2.marketrmi.server.Client;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientDriver extends UnicastRemoteObject implements Client {

    public static final String BANK_NAME = "Nordea";
    public static final String MARKET_NAME = "Tradera";

    private String userName;
    private Market market;
    private Trader trader;
    private se.kth.id2212.ex2.bankrmi.Account bankAccount;

    public ClientDriver() throws RemoteException {
    }

    public static void main(String[] args) throws RemoteException {
        try {
            new ClientDriver().run();
        } catch (ConnectException | NotBoundException | MalformedURLException | RejectedException e) {
            System.out.println("fatal error: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void callback(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public void run() throws RemoteException, RejectedException, NotBoundException, MalformedURLException {
        connectMarket();
        connectBank();
        userInterface();
        System.exit(0);
    }

    private String getInput() {
        return new Scanner(System.in).nextLine();
    }

    private void connectMarket() throws ConnectException, RemoteException, NotBoundException, MalformedURLException, RejectedException {
        while (true) {
            try {
                System.out.print("Enter user name: ");
                userName = getInput();
                IllegalNameException.check(userName);
                market = (Market) Naming.lookup(MARKET_NAME);
                trader = market.getTrader(userName);
                if (trader == null) {
                    trader = market.newTrader(this, userName);
                    trader.addItem("watch");
                    trader.addItem("camera");
                    trader.addItem("gameboy");
                    System.out.println(MARKET_NAME + ": Created a new trader");
                } else {
                    System.out.println(MARKET_NAME + ": Retrieved existing trader");
                }
                break;
            } catch (IllegalNameException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }

    private void connectBank() throws ConnectException, RemoteException, NotBoundException, MalformedURLException, RejectedException {
        Bank bank = (Bank) Naming.lookup(BANK_NAME);
        bankAccount = bank.getAccount(userName);
        if (bankAccount == null) {
            bankAccount = bank.newAccount(userName);
            bankAccount.deposit(1000);
            System.out.println(BANK_NAME + ": Created a new account");
        } else {
            System.out.println(BANK_NAME + ": Retrieved existing account");
        }
    }

    private void userInterface() throws RemoteException {
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

    private void items() throws RemoteException {
        while (true) {
            System.out.print("\n1. List\n2. Add\n3. Remove\n4. Back\n> ");
            switch (getInput()) {
                case "1":
                    System.out.println(trader.getItems());
                    break;
                case "2":
                    System.out.print("Item: ");
                    try {
                        boolean status = trader.addItem(getInput());
                        if (status) {
                            System.out.println("Add succeeded");
                        } else {
                            System.out.println("Add failed");
                        }
                    } catch (IllegalNameException e) {
                        System.out.println("error: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.print("Item: ");
                    if (trader.removeItem(getInput())) {
                        System.out.println("Remove succeeded");
                    } else {
                        System.out.println("Remove failed");
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

    private void market() throws RemoteException {
        while (true) {
            System.out.print("\n1. Sell\n2. Buy\n3. Inspect\n4. Wish\n5. Back\n> ");
            switch (getInput()) {
                case "1":
                    System.out.print("Item: ");
                    String item = getInput();
                    System.out.print("Price: ");
                    try {
                        float price = Float.parseFloat(getInput());
                        switch (market.sell(item, price, trader)) {
                            case 0:
                                System.out.println("Item placed in market");
                            case 1:
                                System.out.println("Sell failed: item not found");
                            case 2:
                                System.out.println("Sell failed: item already on market");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("error: " + e.getMessage());
                    }
                    break;
                case "2":
                    System.out.print("Item: ");
                    item = getInput();
                    int status = market.buy(item, trader);
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
                    break;
                case "3":
                    System.out.println(market.inspect());
                    break;
                case "4":
                    System.out.print("Item: ");
                    item = getInput();
                    System.out.print("Price: ");
                    try {
                        float price = Float.parseFloat(getInput());
                        market.wish(this, item, price);
                        System.out.println("Wish registered");
                    } catch (NumberFormatException e) {
                        System.out.println("error: " + e.getMessage());
                    }
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
