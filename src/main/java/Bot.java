import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Objects;

public final class Bot extends TelegramLongPollingCommandBot {
    private final NonCommandUpdateHandler nonCommandUpdateHandler;
    private final String token;
    private final String name;
    private final String paths;
    private final String format;
    private final Long adminId;
    private final HashMap<Long, String> userDirectory;
    private final HashMap<Long, Long> userCount;

    public Bot(String name, String token, String paths, Long adminId, String format) {
        super();
        this.name = name;
        this.token = token;
        this.paths = paths;
        this.format = format;
        this.adminId = adminId;
        this.userCount = new HashMap<>();
        this.userDirectory = new HashMap<>();
        this.nonCommandUpdateHandler = new NonCommandUpdateHandler(this);

        java.io.File directory = new java.io.File(paths);

        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        register(new StopCommand("stop", "Shuts down the bot.", this));
        register(new LSCommand("ls", "Provides user with the name " +
                "of the current directory and amount of files in it.", this));
        register(new CDCommand("cd", "Changes current directory to a provided one." +
                " Creates a new directory if needed. Only supports names without whitespaces and '/'. " +
                "No arguments equals to a `home` directory.", this));
        register(new HelpCommand("help", "Provides user with information about commands."));
        register(
                new CompileCommand("compile",
                "Compiles and sends back a pdf file, based on images saved in a current directory.",
                this)
        );
    }

    public String getDirectory(Long chatId) {
        if (!userDirectory.containsKey(chatId)) {
            userDirectory.put(chatId, paths + "/" + chatId);
        }
        return userDirectory.get(chatId);
    }

    public void updateDirectory(Long chatId, String directory) {
        userDirectory.put(chatId, paths + "/" + chatId + "/" + directory);
    }

    public boolean canUpload(Long chatId) {
        return checkAdmins(chatId) || userCount.get(chatId) < 100;
    }

    public void increaseCount(Long chatId) {
        userCount.merge(chatId, 1L, Long::sum);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();

        if (canUpload(chatId)) {
            increaseCount(chatId);
            respond(chatId, nonCommandUpdateHandler.execute(chatId, message), message);
        } else {
            respond(chatId, "Apologize, but you can't upload more than a hundred files at once. " +
                    "Convert some of the existing files to pdf to upload more.", message);
        }
    }

    private void respond(Long chatId, String result, Message message) {
        SendMessage response = new SendMessage();
        response.setText(result);
        response.setChatId(chatId);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            System.err.println("Got an api error: " + Arrays.toString(e.getStackTrace()) +
                    ". While responding to a message: " + message + ". From a chat: " + chatId);
        }
    }

    public int getNext(Long chatId) throws WritingException {
        String directory = getDirectory(chatId);
        java.io.File path = new java.io.File(directory);
        java.io.File log = new java.io.File(directory + "/log.txt");
        //noinspection ResultOfMethodCallIgnored
        path.mkdirs();
        if (!Files.exists(Path.of(directory + "/log.txt"))) {
            try {
                //noinspection ResultOfMethodCallIgnored
                log.createNewFile();
            } catch (IOException e) {
                throw new WritingException("Failed to create a new logging file at a directory: " + directory + "."
                        + Arrays.toString(e.getStackTrace()));
            }
            try {
                Files.write(log.toPath(), format(0).getBytes());
            } catch (IOException e) {
                throw new WritingException("Failed to write in a new logging file at a directory: " +
                        directory + "." + Arrays.toString(e.getStackTrace()));
            }
        }
        try (Scanner in = new Scanner(log)) {
            return in.nextInt();
        } catch (FileNotFoundException e) {
            throw new WritingException("Failed to read from a logging file at a directory: " + directory + "." +
                    Arrays.toString(e.getStackTrace()));
        }
    }

    public String getAndUpdateNext(Long chatId) throws WritingException {
        String directory = getDirectory(chatId);
        String cur = format(getNext(chatId));
        String next = format(getNext(chatId) + 1);
        try (Writer writer = new FileWriter(directory + "/log.txt", false)) {
            writer.write(next);
        } catch (IOException e) {
            throw new WritingException("Failed to override a file at a directory: " + directory + "." +
                    Arrays.toString(e.getStackTrace()));
        }
        return cur;
    }

    public String getShortDirectory(Long chatId) {
        String[] directoriesSplit = getDirectory(chatId).split("/");
        return directoriesSplit.length == 2 ? "home" : directoriesSplit[2];
    }

    public String format(int number) {
        return String.format(this.format, number);
    }

    public void deleteDirectory(Long chatId) {
        File directory = new File(getDirectory(chatId));
        String[] content = directory.list();
        if (content != null) {
            for (var fileName : content) {
                File file = new File(fileName);
                if (!file.delete()) {
                    System.err.println("Warning: failed to delete file: " + fileName + ".");
                }
            }
        }
        //noinspection ResultOfMethodCallIgnored
        directory.delete();
    }

    public boolean checkAdmins(Long chatId) {
        return Objects.equals(chatId, this.adminId);
    }
}
