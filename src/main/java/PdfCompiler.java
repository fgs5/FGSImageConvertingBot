import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class PdfCompiler {
    private final Bot bot;
    private final Long chatId;

    public PdfCompiler(Bot bot, Long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    public String compile() throws WritingException {
        PDDocument document = new PDDocument();
        PDImageXObject[] files = getFiles(document);
        for (var file : files) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(file, 0, 0);
            } catch (IOException e) {
                throw new WritingException("Failed to either create a stream or to write in it: " +
                        Arrays.toString(e.getStackTrace()) + ".");
            }
        }
        try {
            String directory = "pdfs/" + this.chatId + "_" + bot.getShortDirectory(this.chatId) + ".pdf";
            File dir = new File("pdfs");
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            Path path = Path.of(directory);
            if (Files.exists(path)) {
                Files.delete(path);
            }
            document.save(path.toAbsolutePath().toString());
            document.close();
            return directory;
        } catch (IOException e) {
            throw new WritingException("Failed to save a pdf file or to delete previous: " +
                    Arrays.toString(e.getStackTrace()) + ".");
        }
    }

    private PDImageXObject[] getFiles(PDDocument document) throws WritingException {
        int count = bot.getNext(this.chatId);
        PDImageXObject[] files = new PDImageXObject[count];
        for (int i = 0; i < count; i++) {
            try {
                String path = bot.getDirectory(this.chatId) + "/" + bot.format(i) + ".jpg";
                files[i] = PDImageXObject.createFromFile(
                        Paths.get(path).toAbsolutePath().toString(),
                        document
                );
            } catch (IOException e) {
                throw new WritingException("Failed to read an image: " + Arrays.toString(e.getStackTrace()) + ".");
            }
        }
        return files;
    }
}
