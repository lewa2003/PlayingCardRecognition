import service.RecognitionService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PlayingCardRecognitionApplication {

    public static void main(String[] args) {
        String path = "C:\\imgs_marked";

        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Директория не существует или это не папка!");
            return;
        }

        File[] files = dir.listFiles((d, name) ->
                name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif"));

        if (files == null || files.length == 0) {
            System.out.println("В директории нет изображений.");
            return;
        }

        RecognitionService service = new RecognitionService();

        for (File file : files) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    System.out.println("Загружено: " + file.getName());
                    service.doRecognition(img);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении " + file.getName() + ": " + e.getMessage());
            }
        }
    }
}
