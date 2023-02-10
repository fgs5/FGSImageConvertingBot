import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class LSCommand extends AbstractCommand {

    private final Bot bot;
    public LSCommand(String commandIdentifier, String description, Bot bot) {
        super(commandIdentifier, description);
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String directory = bot.getShortDirectory(chat.getId());
        try {
            int amount = bot.getNext(chat.getId());
            String noun = amount == 1 ? " file." : " files.";
            respond(chat.getId(), "You are currently in a `" + directory +
                    "` directory with " + amount + noun, absSender);
        } catch (WritingException e) {
            respond(chat.getId(), "Apologize, but we have failed to locate you.", absSender);
            System.err.println(e.getMessage());
        }
    }
}
