package com.BusBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnect {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:db.s3db");

        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public static void CreateDB() throws SQLException {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'ChatId' STRING UNIQUE, 'menu' VARCHAR(255) NULL);");
        statmt.executeQuery("SELECT * FROM users");
        System.out.println("Таблица создана или уже существует.");
    }

    // --------Заполнение таблицы--------
    public static void WriteDB(String id, String menu) throws SQLException {
        statmt.execute("REPLACE INTO 'users' ('ChatId', 'menu') VALUES (" + id + ", '" + menu + "'); ");
        System.out.println("Юзер с чата № " + id + " перешел в меню " + menu);
    }

    // -------- Вывод таблицы--------
    public static void ReadDB() throws SQLException {
        resSet = statmt.executeQuery("SELECT * FROM users");

        while (resSet.next()) {
            int id = resSet.getInt("id");
            String ChatId = resSet.getString("ChatId");
            String menu = resSet.getString("menu");
            System.out.println("ID = " + id);
            System.out.println("ChatId = " + ChatId);
            System.out.println("menu = " + menu);
            System.out.println();
        }

        System.out.println("Таблица выведена");
    }

    public static String[] ReadOne(String ChatId) throws SQLException {
        resSet = statmt.executeQuery("SELECT * FROM users WHERE ChatId = " + ChatId);
        String[] response = new String[3];
        while (resSet.next()) {
            response[0] = resSet.getString("id");
            response[1] = resSet.getString("ChatId");
            response[2] = resSet.getString("menu");
        }
        System.out.println("Данные получены по юзеру " + response[1]);

        return response;

    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException {
        conn.close();
        statmt.close();
        resSet.close();

        System.out.println("Соединения закрыты");
    }
}
