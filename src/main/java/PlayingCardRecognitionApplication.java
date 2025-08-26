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

        int count = 0;
        int fails = 0;
        for (File file : files) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    String realName = file.getName().replaceAll(".png", "");
                    count++;
                    String cardsDetected = service.doRecognition(img);
                    System.out.println("Real name: " + realName + " my name: " + cardsDetected + " equals : " + realName.equals(cardsDetected));
                    if (!realName.equals(cardsDetected)) {
                        fails++;
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении " + file.getName() + ": " + e.getMessage());
            }
        }
        System.out.println("all: " + count);
        System.out.println("fails: " + fails);
    }
}
