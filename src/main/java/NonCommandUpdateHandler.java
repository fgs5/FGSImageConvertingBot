import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class NonCommandUpdateHandler {
    private final Bot bot;
    public NonCommandUpdateHandler(Bot bot) {
        this.bot = bot;
    }
    public String execute(Long chatId, Message message) {
        try {
            String fileId = fileIdOrNull(message);
            if (fileId == null) {
                return "Apologize, but we are not yet able to process text-only messages.";
            }
            GetFile getFile = new GetFile(fileId);
            File file = bot.execute(getFile);

            String nextName = bot.getAndUpdateNext(chatId);

            FileUtils.copyURLToFile(new URL(file.getFileUrl(bot.getBotToken())),
                    new java.io.File(bot.getDirectory(chatId) + "/" + nextName + ".jpg"));
            return "Successfully downloaded the image! " +
                    "Send more or use /compile command to convert.";
        } catch (TelegramApiException e) {
            System.err.println("Got an api error: " + Arrays.toString(e.getStackTrace()) +
                    ". While getting information about a file from a message: " +
                    message + ". From a chat: " + chatId);
            return "Apologize, but an error acquired during getting information about the" +
                    " file you sent. Most probably the problem is on the side of the Telegram.";
        } catch (MalformedURLException e) {
            System.err.println("Got a bad url error: " + Arrays.toString(e.getStackTrace()) +
                    ". While downloading a file from a message: " + message + ". From a chat: " + chatId);
            return "Apologize, but an error acquired during processing the url of the" +
                    " file you sent. Most probably the problem is on the side of the Telegram.";
        } catch (IOException e) {
            System.err.println("Got an IO error: " + Arrays.toString(e.getStackTrace()) +
                    ". While writing a file from a message: " + message + ". From a chat: " + chatId);
            return "Apologize, but an error acquired during writing the " +
                    " file you sent. Most probably the problem is on our side.";
        } catch (WritingException e) {
            System.err.println(e.getMessage());
            return "Apologize, but an error acquired during getting the amount of " +
                    "files in a directory. The problem is somewhere on our side.";
        }
    }

    public String fileIdOrNull(Message message) {
        if (message.getPhoto().isEmpty()) {
            return null;
        }
        List<PhotoSize> temp = message.getPhoto();
        return temp.get(temp.size() - 1).getFileId();
    }
}
