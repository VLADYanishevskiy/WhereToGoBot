import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingBot {

    private static DBHelper helper ;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try{
            telegramBotsApi.registerBot(new Bot());
            helper = new DBHelper();
        }catch(TelegramApiException e){
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void sendMsg(Message message , String text , String buttonSet){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try{
            setButtons(sendMessage , buttonSet);
            execute(sendMessage);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message != null && message.hasText()){
            switch(message.getText()){
                case "bot":{}
                case "Bot":{}
                case "бот":{}
                case "Бот":{}
                case "старт":{}
                case "Старт":{}
                case "/start":{
                    sendMsg(message , " Доброго времени) \n Хотите куда-то сходить ?" , "YesNo");
                    break;
                }

                case "Да":{}
                case "да":{}
                case "/yesiwant":{
                    sendMsg(message , "Отлично , укажите категорию : " , "ShowCategories");
                    break;
                }
                case "Нет":{}
                case "нет":{}
                case "/nothx":{
                    sendMsg(message , ":( \n В любом случае мы всегда готовы что-то вам предложить) " , "");
                    break;
                }
                case "/help":{
                    sendMsg(message , "чем могу помочь ?" , "");
                    break;
                }



                default:
            }
        }
    }

    public void setButtons(SendMessage sendMessage , String buttonsSet){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        switch(buttonsSet){
            case "YesNo":{

                keyboardFirstRow.add("/yesiwant");
                keyboardFirstRow.add("/nothx");

                KeyboardRow keyboardSecondRow = new KeyboardRow();
                keyboardSecondRow.add(new KeyboardButton("/help"));

                keyboardRowList.add(keyboardFirstRow);
                keyboardRowList.add(keyboardSecondRow);
                break;
            }
            case "ShowCategories":{
                //TODO: show categories from table
                break;
            }
            default:
                keyboardFirstRow.add("/help");
                keyboardRowList.add(keyboardFirstRow);
        }



        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }


    public String getBotUsername() {
        return "GoToWhereBot";
    }

    public String getBotToken() {
        return "839521919:AAGq8EpYQfQgGRXMOnS9K9z8QTYf1llagKc";
    }
}
