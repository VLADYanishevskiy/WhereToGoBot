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
    private static List<Place> places;

    private String mainCategory = "empty";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try{
            telegramBotsApi.registerBot(new Bot());
            helper = new DBHelper();
            //places = helper.getAllPlaces();
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
                case "/Ресторан":{
                    sendMsg(message , " отлично , возможно что-то конкретное ? " , "Ресторан");
                    break;
                }
                case "/Концерт":{
                    sendMsg(message , " отлично , возможно что-то конкретное ? " , "Концерт");
                    break;
                }
                case "/Фестиваль":{
                    sendMsg(message , " отлично , возможно что-то конкретное ? " , "Фестиваль");
                    break;
                }
                case "/Все":{
                    switch (mainCategory){
                        case "Ресторан":{
                            sendMsg(message , helper.GetPlaceByCategory("Ресторан") , "");
                            mainCategory = "empty";
                            break;
                        }
                        case "Концерт":{
                            sendMsg(message , helper.GetPlaceByCategory("Концерт") , "");
                            mainCategory = "empty";
                            break;
                        }
                        case "Фестиваль":{
                            sendMsg(message , helper.GetPlaceByCategory("Фестиваль") , "");
                            mainCategory = "empty";
                            break;
                        }
                        case "empty":{
                            sendMsg(message , helper.getShowAllPlaces() , "");
                            mainCategory = "empty";
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }
                default:
                    sendMsg(message , helper.ShowBySubCategory(message.getText()) , "");
                    mainCategory = "empty";
                    break;
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
                keyboardFirstRow.add("/start");

                keyboardRowList.add(keyboardFirstRow);
                keyboardRowList.add(keyboardSecondRow);
                break;
            }
            case "ShowCategories":{

                KeyboardRow keyRow ;

                List<String> categories = helper.GetAllCategories();

                for (int i = 0 ; i < categories.size() ; i++){
                    keyRow =  new KeyboardRow();
                    keyRow.add("/"+categories.get(i));
                    keyboardRowList.add(keyRow);
                }
                KeyboardRow allrow = new KeyboardRow();
                allrow.add("/Все");
                keyboardRowList.add(allrow);
                keyboardFirstRow.add("/help");
                keyboardFirstRow.add("/start");
                keyboardRowList.add(keyboardFirstRow);
                break;
            }
            case "Ресторан":{ }
            case "Концерт":{ }
            case "Фестиваль":{
                mainCategory = buttonsSet;
                List<KeyboardRow> rows = getButtonsSubCategory(buttonsSet);
                keyboardRowList.addAll(rows);

                keyboardFirstRow.add("/help");
                keyboardFirstRow.add("/start");
                keyboardRowList.add(keyboardFirstRow);
                break;
            }

            default:
                keyboardFirstRow.add("/help");
                keyboardFirstRow.add("/start");
                keyboardRowList.add(keyboardFirstRow);
        }



        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public List<KeyboardRow> getButtonsSubCategory(String mainCategory){
        List<KeyboardRow> rows = new ArrayList();
        List<String> categories = helper.GetAllSubCategories(mainCategory);

        KeyboardRow keyRow ;
        for (int i = 0 ; i < categories.size() ; i++){
            keyRow = new KeyboardRow();
            keyRow.add("/"+categories.get(i));
            rows.add(keyRow);
        }

        return rows;
    }



    public String getBotUsername() {
        return "GoToWhereBot";
    }

    public String getBotToken() {
        return "839521919:AAGq8EpYQfQgGRXMOnS9K9z8QTYf1llagKc";
    }
}
