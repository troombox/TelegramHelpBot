import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;

public class Bot extends TelegramLongPollingBot {

    private final String START_MSG = "/start";
    private final String STOP_MSG = "/stop";

    private IBotData botData;
    private HashMap<Long,DecisionMaker> decisionMakers = new HashMap<>();

    public Bot(IBotData botData){
        this.botData = botData;
    }

    @Override
    public String getBotUsername() {
        return botData.getBotUName();
    }

    @Override
    public String getBotToken() {
        return botData.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        //if update contains a message
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageReceived = update.getMessage().getText().trim().toLowerCase();
            //if the message is bot command (starting with "/") we need to take care of it
            if(isSystemMessage(messageReceived)){
                if(messageReceived.equals(STOP_MSG)){
                    sendMessage("Stopping the process. You can start over by sending me a /start!",String.valueOf(update.getMessage().getChatId()));
                    decisionMakers.remove(update.getMessage().getChatId());
                    System.out.println("DM stopped and removed, chatID:" + update.getMessage().getChatId());
                    return;
                }
                if(messageReceived.equals(START_MSG)){
                    decisionMakers.put(update.getMessage().getChatId(), new DecisionMaker());
                    System.out.println("new DM running, chatID:" + update.getMessage().getChatId());
                    sendMessage("Hello. Let's start troubleshooting your problem!\n(I understand words 'yes' and 'no', and not much else)",String.valueOf(update.getMessage().getChatId()));
                    sendMessage(decisionMakers.get(update.getMessage().getChatId()).getNextQuestion(),String.valueOf(update.getMessage().getChatId()));
                    return;
                }
            }
            //here we work with user response that's not a bot command
            if(!messageReceived.equals("yes") && !messageReceived.equals("no")){
                sendMessage("Sorry, wrong input. please respond with yes / no", String.valueOf(update.getMessage().getChatId()));
                return;
            }
            DecisionMaker current = decisionMakers.get(update.getMessage().getChatId());
            current.receiveUserAnswer(messageReceived);
            sendMessage(current.getNextQuestion(), String.valueOf(update.getMessage().getChatId()));
        }
    }

    private void sendMessage(String message, String chatID){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isSystemMessage(String msg){
        if(msg.startsWith("/")){
            return true;
        }
        return false;
    }

}
