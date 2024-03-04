package com.example.myapplication;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Database {
    private static final String DB_URL = "jdbc:oracle:thin:@172.20.8.27:1521/orcl.96.122.27";
    private static final String DB_USER = "IE_ALEXANDRU";
    private static final String DB_PASSWORD = "IE_ALEXANDRU";

    public List<List<Object>> getAllValuesFromTable(String tabel, String... columns) {
        List<List<Object>> allValues = new ArrayList<>();
        StringBuilder sqlQuery = new StringBuilder("Select ");
        for (int i = 0; i < columns.length; i++) {
            sqlQuery.append(columns[i]);
            if (i < columns.length - 1) {
                sqlQuery.append(",");
            }
        }
        sqlQuery.append(" FROM ").append(tabel);
        System.out.println(String.valueOf(sqlQuery));
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(String.valueOf(sqlQuery))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (resultSet.next()) {
                        List<Object> rowValues = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                            Object columnValue = resultSet.getObject(i);
                            rowValues.add(columnValue);
                        }
                        allValues.add(rowValues);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allValues;
    }
    public List<List<Object>> getAllValues(String sqlQuery) {
        List<List<Object>> allValues = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(String.valueOf(sqlQuery))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (resultSet.next()) {
                        List<Object> rowValues = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                            Object columnValue = resultSet.getObject(i);
                            rowValues.add(columnValue);
                        }
                        allValues.add(rowValues);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allValues;
    }
    public String[] getColumnWhere(String sql,String coloana) {
        String[] allValues = new String[50];
        int index=0;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("oracle.jdbc.OracleDriver");
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        allValues[index] = resultSet.getString(coloana);
                        index++;
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return allValues;
    }
    public String[] getColumn(String tabel,String coloana) {
        String[] allValues = new String[50];
        int index=0;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Load the Oracle JDBC driver
            Class.forName("oracle.jdbc.OracleDriver");
            try (Statement statement = connection.createStatement()) {
                String query = "SELECT " + coloana + " FROM "+tabel;
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        allValues[index] = resultSet.getString(coloana);
                        index++;
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return allValues;
    }
    public void commit() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<List<Object>> getPiese(String sqlQuery) {
        List<List<Object>> allValues = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (resultSet.next()) {
                        List<Object> rowValues = new ArrayList<>();
                        for (int i = 2; i <= columnCount-1; i++) { //index 2 peste ID si -1 pana la dimensiune
                            Object columnValue = resultSet.getObject(i);
                            rowValues.add(columnValue);
                        }
                        allValues.add(rowValues);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allValues;
    }

    public void insertLoc(String sqlQuery, Object... values) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
                for (int i = 0; i < values.length; i++) {
                    statement.setObject(i + 1, values[i]);
                }
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String[] removeNullValues(String[] array) {
        List<String> list = new ArrayList<>();
        for (String element : array) {
            if (element != null) {
                list.add(element);
            }
        }
        return list.toArray(new String[0]);
    }
    public String[] removeDuplicates(String[] array) {
        Set<String> uniqueSet = new HashSet<>(Arrays.asList(array));
        return uniqueSet.toArray(new String[0]);
    }

    public List<List<Object>> searchAccount(String username){
        List<List<Object>> accountInfo = new ArrayList<>();
        String sql = "SELECT * FROM ACCOUNTS WHERE USERNAME='"+username+"'";
        accountInfo=getAllValues(sql);

        return accountInfo;
    }

    public boolean checkUsername(String username) {
        String[] usernames = getColumn("ACCOUNTS","USERNAME");
        List<String> listOfUsers = Arrays.asList(usernames);
        return listOfUsers.contains(username);
    }
}
