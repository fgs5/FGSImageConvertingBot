import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CompileCommand extends AbstractCommand {
    private final Bot bot;

    public CompileCommand(String commandIdentifier, String description, Bot bot) {
        super(commandIdentifier, description);
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        PdfCompiler compiler = new PdfCompiler(bot, chat.getId());
        try {
            compiler.compile();
            respond(chat.getId(), "Successfully compiled a pdf!", absSender);
            bot.deleteDirectory(chat.getId());
            /* :NOTE: send PDF back to user */
        } catch (WritingException e) {
            respond(chat.getId(),
                    "Failed to compile or send a pdf. The problem is somewhere on our side.", absSender);
            System.err.println(e.getMessage());
        }
    }
}
