package xyz.johansson.id2212.hw3.marketjdbc.server;

import se.kth.id2212.ex3.bankjdbc.Bank;
import se.kth.id2212.ex3.bankjdbc.RejectedException;
import xyz.johansson.id2212.hw3.marketjdbc.client.Market;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.HashMap;
import java.util.StringJoiner;

public class MarketImpl extends UnicastRemoteObject implements Market {

    private String marketDB;
    private String bankName;
    private Bank bank;
    private HashMap<Client, String> users;
    private PreparedStatement getAccount;
    private PreparedStatement newAccount;
    private PreparedStatement incSales;
    private PreparedStatement incPurchases;
    private PreparedStatement getUserItems;
    private PreparedStatement getUserItem;
    private PreparedStatement newUserItem;
    private PreparedStatement delUserItem;
    private PreparedStatement getMarketItems;
    private PreparedStatement getMarketItem;
    private PreparedStatement newMarketItem;
    private PreparedStatement delMarketItem;


    public MarketImpl(String marketDB, String bankName) throws RemoteException, SQLException, ClassNotFoundException, MalformedURLException, NotBoundException {
        this.marketDB = marketDB;
        this.bankName = bankName;
        users = new HashMap();
        Connection connection = getConnection(marketDB);
        createDatasource(connection);
        prepareStatements(connection);
        bank = (Bank) Naming.lookup(bankName);
    }

    private Connection getConnection(String marketDB) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + marketDB, "root", "javajava");
    }

    private void createDatasource(Connection connection) throws ClassNotFoundException, SQLException {
        if (!tableExist(connection, "account")) {
            connection.createStatement().executeUpdate(
                    "CREATE TABLE account" +
                            "(" +
                            "username VARCHAR(255)," +
                            "password VARCHAR(255)," +
                            "sales INT," +
                            "purchases INT," +
                            "PRIMARY KEY (username)" +
                            ")"
            );
        }
        if (!tableExist(connection, "user_item")) {
            connection.createStatement().executeUpdate(
                    "CREATE TABLE user_item" +
                            "(" +
                            "id INT AUTO_INCREMENT," +
                            "username VARCHAR(255)," +
                            "item VARCHAR(255)," +
                            "PRIMARY KEY (id)" +
                            ")"
            );
        }
        if (!tableExist(connection, "market_item")) {
            connection.createStatement().executeUpdate(
                    "CREATE TABLE market_item" +
                            "(" +
                            "id INT," +
                            "username VARCHAR(255)," +
                            "item VARCHAR(255)," +
                            "price FLOAT," +
                            "PRIMARY KEY (id)" +
                            ")"
            );
        }
    }

    private boolean tableExist(Connection connection, String table) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet rs = dbm.getTables(null, null, table, null);
        boolean bool = rs.next();
        rs.close();
        return bool;
    }

    private void prepareStatements(Connection connection) throws SQLException {
        // account
        getAccount = connection.prepareStatement("SELECT * FROM account WHERE username = ?");
        newAccount = connection.prepareStatement("INSERT INTO account VALUES (?, ?, 0, 0)");
        incSales = connection.prepareStatement("UPDATE account SET sales = sales + 1 WHERE username = ?");
        incPurchases = connection.prepareStatement("UPDATE account SET purchases = purchases + 1 WHERE username = ?");
        // user_item
        getUserItems = connection.prepareStatement("SELECT * FROM user_item WHERE username = ?");
        getUserItem  = connection.prepareStatement("SELECT * FROM user_item WHERE id = ?");
        newUserItem  = connection.prepareStatement("INSERT INTO user_item (username, item) VALUES (?, ?)");
        delUserItem  = connection.prepareStatement("DELETE FROM user_item WHERE id = ? AND username = ?");
        // market_item
        getMarketItems = connection.prepareStatement("SELECT * FROM market_item");
        getMarketItem  = connection.prepareStatement("SELECT * FROM market_item WHERE id = ?");
        newMarketItem  = connection.prepareStatement("INSERT INTO market_item (id, username, item, price) VALUES (?, ?, ?, ?)");
        delMarketItem  = connection.prepareStatement("DELETE FROM market_item WHERE id = ?");
    }

    /**
     * Remote API
     */

    @Override
    public boolean accountExist(String username) throws RemoteException, SQLException {
        getAccount.setString(1, username);
        ResultSet rs = getAccount.executeQuery();
        boolean b = rs.next();
        rs.close();
        return b;
    }

    @Override
    public boolean newAccount(Client client, String username, String password) throws RemoteException, SQLException {
        if (accountExist(username)) {
            return false;
        }
        newAccount.setString(1, username);
        newAccount.setString(2, password);
        newAccount.executeUpdate();
        users.put(client, username);
        // callback ?
        return true;
    }

    @Override
    public boolean login(Client client, String username, String password) throws RemoteException, SQLException {
        getAccount.setString(1, username);
        ResultSet rs = getAccount.executeQuery();
        if (!rs.next() || !rs.getString("password").equals(password)) {
            rs.close();
            client.callback(marketDB + ": Wrong user credentials!");
            return false;
        }
        users.put(client, username);
        rs.close();
        client.callback(marketDB + ": Logged in");
        return true;
    }

    @Override
    public void logout(Client client) throws RemoteException {
        users.remove(client);
        client.callback(marketDB + ": Logged out");
    }

    /**
     * Items
     */

    @Override
    public String listItems(Client client) throws RemoteException, SQLException {
        String username = users.get(client);
        getUserItems.setString(1, username);
        ResultSet rs = getUserItems.executeQuery();
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        while (rs.next()) {
            sj.add(rs.getInt("id") + ": " + rs.getString("item"));
        }
        rs.close();
        return sj.toString();
    }

    @Override
    public void addItem(Client client, String item) throws RemoteException, SQLException {
        String username = users.get(client);
        newUserItem.setString(1, username);
        newUserItem.setString(2, item);
        newUserItem.executeUpdate();
    }

    @Override
    public int removeItem(Client client, int id) throws RemoteException, SQLException {
        String username = users.get(client);
        delUserItem.setInt(1, id);
        delUserItem.setString(2, username);
        return delUserItem.executeUpdate();
    }

    /**
     * Market
     */

    @Override
    public synchronized boolean sell(Client client, int id, float price) throws RemoteException, SQLException {
        getUserItem.setInt(1, id);
        ResultSet rs = getUserItem.executeQuery();
        if (!rs.next()) {
            rs.close();
            return false;
        }
        String item = rs.getString("item");
        rs.close();
        String seller = users.get(client);
        // remove
        delUserItem.setInt(1, id);
        delUserItem.setString(2, seller);
        delUserItem.executeUpdate();
        // add
        newMarketItem.setInt(1, id);
        newMarketItem.setString(2, seller);
        newMarketItem.setString(3, item);
        newMarketItem.setFloat(4, price);
        newMarketItem.executeUpdate();
        // activity record
        incSales.setString(1, seller);
        incSales.executeUpdate();
        return true;
    }

    @Override
    public synchronized int buy(Client client, int id) throws RemoteException, SQLException {
        getMarketItem.setInt(1, id);
        ResultSet rs = getMarketItem.executeQuery();
        if (!rs.next()) {
            rs.close();
            return 1;
        }
        String seller = rs.getString("username");
        String item = rs.getString("item");
        float price = rs.getFloat("price");
        rs.close();
        String buyer = users.get(client);
        // transaction
        try {
            bank.getAccount(buyer).withdraw(price);
            bank.getAccount(seller).deposit(price);
        } catch (RejectedException e) {
            return 2;
        }
        // remove
        delMarketItem.setInt(1, id);
        delMarketItem.executeUpdate();
        // add
        newUserItem.setString(1, buyer);
        newUserItem.setString(2, item);
        newUserItem.executeUpdate();
        // activity record
        incPurchases.setString(1, buyer);
        incPurchases.executeUpdate();
        return 0;
    }

    @Override
    public String inspect() throws RemoteException, SQLException {
        StringJoiner sj = new StringJoiner("\n", "", "");
        ResultSet rs = getMarketItems.executeQuery();
        while (rs.next()) {
            sj.add(rs.getInt("id") + ":\titem: " + rs.getString("item") + "\tprice: "
                    + rs.getFloat("price") + "\tseller: " + rs.getString("username"));
        }
        rs.close();
        return sj.toString();
    }

    @Override
    public String activity(Client client) throws RemoteException, SQLException {
        String username = users.get(client);
        getAccount.setString(1, username);
        ResultSet rs = getAccount.executeQuery();
        rs.next();
        String activity = "Sales: " + rs.getInt("sales") + "\nPurchases:" + rs.getInt("purchases");
        rs.close();
        return activity;
    }
}
