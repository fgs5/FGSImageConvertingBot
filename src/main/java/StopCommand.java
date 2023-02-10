import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static java.lang.System.exit;

public class StopCommand extends AbstractCommand {
    private final Bot bot;

    StopCommand(String commandIdentifier, String description, Bot bot) {
        super(commandIdentifier, description);
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        if (this.bot.checkAdmins(chat.getId())) {
            respond(chat.getId(), "Shutting down.", absSender);
            exit(0);
        }
        else {
            respond(chat.getId(), "You have no access to this command.", absSender);
        }
    }
}
