import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class HelpCommand extends AbstractCommand {
    public HelpCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        respond(chat.getId(), "**Notice that since you are not a moderator, " +
                "you have a limit of 100 existing files.** " +
                "Commands for this bot:" + System.lineSeparator() +
                "/ls — Provides user with the name of the current " +
                "directory and amount of files in it." + System.lineSeparator() +
                "/cd — Changes current directory to a provided one. " +
                "Creates a new directory if needed. Only supports names without whitespaces and '/'. " +
                "No arguments equals to a `home` directory." + System.lineSeparator() +
                "/compile — Compiles and sends back a pdf file, based on images saved in a current directory. " +
                "Deletes the directory." + System.lineSeparator() +
                "/help — Shows this message.", absSender);
    }
}
