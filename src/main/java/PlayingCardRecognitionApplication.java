import service.RecognitionService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
        Set<String> cards = new HashSet<>();
        for (File file : files) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    String realName = file.getName().replaceAll(".png", "");
                    count++;

                    String cardsDetected = service.doRecognition(img, cards);
                    if (!realName.equals(cardsDetected)) {
                        fails++;
                        System.out.println("Real name: " + realName + " my name: " + cardsDetected + " equals : " + realName.equals(cardsDetected));
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении " + file.getName() + ": " + e.getMessage());
            }
        }
        System.out.println("all: " + count);
        System.out.println("fails: " + fails);
        System.out.println("Unic comb: " + cards.size());
        System.out.println(cards);
    }
}
