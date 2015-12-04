package xyz.johansson.id2212.hw2.marketrmi.server;

import se.kth.id2212.ex2.bankrmi.Bank;
import se.kth.id2212.ex2.bankrmi.RejectedException;
import xyz.johansson.id2212.hw2.marketrmi.client.IllegalNameException;
import xyz.johansson.id2212.hw2.marketrmi.client.Market;
import xyz.johansson.id2212.hw2.marketrmi.client.Trader;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

public class MarketImpl extends UnicastRemoteObject implements Market {

    private Map<String, Trader> traders;
    private Map<String, Sale> sales;
    private Map<String, Wish> wishes;
    private Bank bank;

    public MarketImpl(String bankName) throws RemoteException, NotBoundException, MalformedURLException {
        traders = new HashMap();
        sales = new HashMap();
        wishes = new HashMap();
        bank = (Bank) Naming.lookup(bankName);
    }

    @Override
    public Trader getTrader(String userName) throws RemoteException {
        return traders.get(userName);
    }

    @Override
    public Trader newTrader(Client client, String userName) throws RemoteException, IllegalNameException {
        IllegalNameException.check(userName);
        TraderImpl trader = new TraderImpl(client, userName);
        traders.put(userName, trader);
        return trader;
    }

    @Override
    public synchronized int sell(String item, float price, Trader seller) throws RemoteException {
        if (!seller.containsItem(item)) {
            return 1;
        }
        if (sales.get(item) != null) {
            return 2;
        }
        sales.put(item, new Sale(item, price, seller));
        seller.removeItem(item);
        Wish wish;
        if ((wish = wishes.get(item)) != null && wish.getPrice() >= price) {
            wish.getClient().callback("Wished item for sale: " + wish.getItem());
        }
        return 0;
    }

    @Override
    public synchronized int buy(String item, Trader buyer) throws RemoteException {
        Sale sale;
        if ((sale = sales.get(item)) == null) {
            return 1;
        }
        try {
            bank.getAccount(buyer.getName()).withdraw(sale.getPrice());
            bank.getAccount(sale.getSeller().getName()).deposit(sale.getPrice());
        } catch (RejectedException e) {
            return 2;
        }
        try {
            buyer.addItem(item);
        } catch (IllegalNameException e) {
            e.printStackTrace();
        }
        sales.remove(item);
        sale.getSeller().getClient().callback("Sell succeeded: " + sale.getItem());
        return 0;
    }

    @Override
    public String inspect() throws RemoteException {
        StringJoiner sj = new StringJoiner("\n", "", "");
        Iterator it = sales.values().iterator();
        while (it.hasNext()) {
            sj.add(it.next().toString());
        }
        return sj.toString();
    }

    @Override
    public void wish(Client client, String item, float price) throws RemoteException {
        wishes.put(item, new Wish(client, item, price));
    }

    /**
     * Inner classes
     */

    private static class Sale {

        String item;
        float price;
        Trader seller;

        Sale(String item, float price, Trader seller) {
            this.seller = seller;
            this.item = item;
            this.price = price;
        }

        String getItem() {
            return item;
        }

        float getPrice() {
            return price;
        }

        Trader getSeller() {
            return seller;
        }

        @Override
        public String toString() {
            try {
                return "item: " + item + "\tprice: " + price + "\tseller: " + seller.getName();
            } catch (RemoteException e) {
                return null;
            }
        }
    }

    private static class Wish {

        Client client;
        String item;
        float price;

        public Wish(Client client, String item, float price) {
            this.client = client;
            this.item = item;
            this.price = price;
        }

        public Client getClient() {
            return client;
        }

        public String getItem() {
            return item;
        }

        public float getPrice() {
            return price;
        }
    }
}
