package com.BusBot;

//import org.telegram.telegrambots.ApiContextInitializer;
//import org.telegram.telegrambots.TelegramBotsApi;
import org.jsoup.HttpStatusException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {
    //private static final Logger _Log = Logger.getLogger(com.BusBot.Bot.class.getName());

    @Override
    public void onUpdateReceived(Update update){
        Message message = update.getMessage();

        try {
            handleIncomingMessage(message);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleIncomingMessage(Message message) throws SQLException, ClassNotFoundException, IOException {
      if (message != null && message.hasText()){
          //DbConnect.Conn();
          String text = message.getText();
          if (text.equals("/start")){
              messageOnMainMenu(message, getMainMenuKeyboard());
          }else if (text.equals("Автобус")){
              messageOnBusMenu(message, getBackKeyboard());
          } else if(text.matches("\\d+")){
              // TODO: 06.04.2018 Для примера, смотри DbConnect.ReadOne(message.getChatId().toString()); возвращает id, chatId, menu(активное меню)
              // TODO: 05.04.2018 Тут обрабатываем номера транспорта. Смотри в базе в каком меню юзер и генерируем урл с номером транспорта.
              String[] data = DbConnect.ReadOne(message.getChatId().toString());
              if (data[2].equals("Автобус")){
                  String Transport = "bus";
                  try{
                      Parser.GetPage(Transport, message.getText());
                  }catch (HttpStatusException e){
                      sendMSG(message, getBackKeyboard(), "Данный маршрут не найдет.");
                  }

              }
              System.out.println(data[2]);
          } else{
              messageOnMainMenu(message, getMainMenuKeyboard());
          }
          //DbConnect.CloseDB();
      }
    }

    private void messageOnMainMenu(Message message, ReplyKeyboardMarkup replyKeyboard) throws SQLException {
        DbConnect.WriteDB(message.getChatId().toString(),"start");
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setText("Какой вид транспорта Вас интересует?");
        try{
            execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    private void messageOnBusMenu(Message message, ReplyKeyboardMarkup replyKeyboard) throws SQLException {
        DbConnect.WriteDB(message.getChatId().toString(), message.getText());
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setText("Укажите номер автобуса");
        try{
            execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    private void sendMSG(Message message, ReplyKeyboardMarkup replyKeyboard, String s) throws SQLException {
        DbConnect.WriteDB(message.getChatId().toString(), message.getText());
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setText(s);
        try{
            execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("Автобус");
        keyboardFirstRow.add("Трамвай");
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add("Тролейбус");
        keyboardSecondRow.add("Маршрутное такси");
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getBackKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("Назад");
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    @Override
    public  String getBotUsername(){
        return "BusBot";
    }
    @Override
    public  String getBotToken(){
        return "410832212:AAE4OctbtxPXKYGd4iHlTnv70PGUcb5YsHM";
    }
}
