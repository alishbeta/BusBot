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
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;

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
              sendMSG(message, getMainMenuKeyboard(), "Какой вид транспорта Вас интересует?");
          }else if (text.equals("Автобус")){
              sendMSG(message, getBackKeyboard(), "Укажите номер автобуса:");
              DbConnect.WriteDB(message.getChatId().toString(), "Автобус");
          }else if (text.equals("Трамвай")){
              sendMSG(message, getBackKeyboard(), "Укажите номер Трамвая:");
              DbConnect.WriteDB(message.getChatId().toString(), "Трамвай");
          }else if (text.equals("Тролейбус")){
              sendMSG(message, getBackKeyboard(), "Укажите номер Тролейбуса:");
              DbConnect.WriteDB(message.getChatId().toString(), "Тролейбус");
          } else if(text.matches("\\d+")){
              String[] data = DbConnect.ReadOne(message.getChatId().toString());
              if (data[2].equals("Автобус")){
                  String transport = "bus";
                  GetRouts(transport, message);
              }else if (data[2].equals("Трамвай")){
                  String transport = "tram";
                  GetRouts(transport, message);
              }else if (data[2].equals("Тролейбус")){
                  String transport = "trolleybus";
                  GetRouts(transport, message);
              }
          } else{
              sendMSG(message, getMainMenuKeyboard(), "Какой вид транспорта Вас интересует?");
              DbConnect.WriteDB(message.getChatId().toString(), "main");
          }
      }
    }

    private void GetRouts(String transport, Message message){
        try{
            Document page = Parser.GetPage(transport, message.getText());
            StringBuilder msg = new StringBuilder();
            StringBuilder msg2 = new StringBuilder();

            String title = Parser.GetTitle(page);
            List<String> directions = Parser.GetDirection(page);
            List<String> timesAB = Parser.GetTimesAB(page);
            List<String> timesBA = Parser.GetTimesBA(page);

            msg.append("<b>" + title + "</b>" );
            msg.append("\n");
            msg.append("<i>(" + directions.get(0)+")</i>");
            msg2.append("<b>" + title + "</b>" );
            msg2.append("\n");
            msg2.append("<i>(" + directions.get(1)+")</i>");

            Pattern p = Pattern.compile("[А-Яф-я]+", Pattern.UNICODE_CHARACTER_CLASS);

            for (int i = 0; i < timesAB.size(); i++) {
                msg.append(timesAB.get(i) + " ");
                if (p.matcher(timesAB.get(i)).find()){
                    msg.append("\n");
                }
            }

            for (int i = 0; i < timesBA.size(); i++) {
                msg2.append(timesBA.get(i) + " ");
                if (p.matcher(timesBA.get(i)).find()){
                    msg2.append("\n");
                }
            }
            sendMSG(message, getBackKeyboard(), msg.toString());
            sendMSG(message, getBackKeyboard(), msg2.toString());
        }catch (HttpStatusException e){
            sendMSG(message, getBackKeyboard(), "Данный маршрут не найдет.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMSG(Message message, ReplyKeyboardMarkup replyKeyboard, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setParseMode("HTML");
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
