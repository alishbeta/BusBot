package com.BusBot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.sql.SQLException;

public class Main {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DbConnect.Conn();
        DbConnect.CreateDB();
        //DbConnect.CloseDB();
        //DbConnect.WriteDB();
        //DbConnect.ReadDB();


        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());
        try {

            //Parser.GetPage();

            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        } finally {
			
		}
		

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e){
            e.printStackTrace();
        } finally {
			
		}
    }
}
