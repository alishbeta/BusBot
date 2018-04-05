import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {
    private static final Logger _Log = Logger.getLogger(Bot.class.getName());
    public static void main(String[] args){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update){
        Message message = update.getMessage();
        if(message != null && message.hasText()){
            if(message.getText().equals("/start")){
                sendMsg(message, "Привет, Лысый, Куку Янка");
            }
            else{
                sendMsg(message, "Я автобусный бот и я дурак!");
            }
        }
    }
    private void sendMsg(Message message, String s){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(s);
        try{
            sendMessage(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
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
