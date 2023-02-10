import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

abstract class AbstractCommand extends BotCommand {

    AbstractCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    protected void respond(Long chatId, String result, AbsSender sender) {
        SendMessage response = new SendMessage();
        response.setText(result);
        response.enableMarkdown(true);
        response.setChatId(chatId);
        try {
            sender.execute(response);
        } catch (TelegramApiException e) {
            System.err.println("Got an api error: " + e + ". In a chat: " + chatId +
                    ". Executing a command: " + this.getCommandIdentifier());
        }
    }
}
