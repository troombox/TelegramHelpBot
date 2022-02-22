import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        //Using an instance of IBotData interface to initiate the bot
        IBotData botData = new BotData();

        try{
            // Instantiate Telegram Bots API
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            // Register our bot
            botsApi.registerBot(new Bot(botData));
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
