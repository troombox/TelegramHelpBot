public interface IBotData {
    /**
     * getToken method returns the token given by Telegram API for the bot
     * @return String containing the token
     */
    String getToken();

    /**
     * getBotUName method returns the username chosen for the bot
     * @return String containing bot username token
     */
    String getBotUName();
}
