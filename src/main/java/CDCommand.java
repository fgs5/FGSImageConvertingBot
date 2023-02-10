import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CDCommand extends AbstractCommand {
    private final Bot bot;

    public CDCommand(String commandIdentifier, String description, Bot bot) {
        super(commandIdentifier, description);
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        if (strings.length > 0 && (strings[0].contains("/") || strings[0].contains(" "))) {
            respond(chat.getId(), "Unsupported filename. " +
                    "Please, use only names without whitespaces and '/' symbols.", absSender);
            return;
        }
        String directory = strings.length == 0 ? "" : strings[0];
        bot.updateDirectory(chat.getId(), directory);
        respond(chat.getId(), "Successfully changed a directory!", absSender);
    }
}
