import service.RecognitionService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayingCardRecognitionApplication {

    public static void main(String[] args) {
        Map<String, int[][]> matrixMap = getMatrxiMap();
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
                    String cardsDetected = service.doRecognition(img, realName, matrixMap);
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
    }

    private static Map<String, int[][]> getMatrxiMap() {
        String path = "tmp";
        File dir = new File(path);

        File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));
        Map<String, int[][]> matrixMap = new HashMap<>();

        for (File file : files) {
            try {
                var fileName = file.getName().replaceAll(".png", "");
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    int[][] matrix = new int[30][30];
                    for (int i = 0; i < 30; i++) {
                        for (int j = 0; j < 30; j++) {
                            Color color = new Color(img.getRGB(j, i));
                            if (isNotWhite(color) == 0) {
                                matrix[i][j] = 0;
                            } else {
                                matrix[i][j] = 1;
                            }
                        }
                    }

                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 30; j++) {
                            matrix[i][j] = 0;
                        }
                    }

                    matrixMap.put(fileName, matrix);

                }

            } catch (IOException e) {
                System.err.println("Ошибка при чтении " + file.getName() + ": " + e.getMessage());
            }
        }
       return matrixMap;
    }

    private static int isNotWhite(Color color) {
        if (color.getBlue() + color.getGreen() + color.getRed() > 550) {
            return 0;
        }
        return color.getBlue() + color.getGreen() + color.getRed() >= 360 && color.getRed() <= color.getGreen() ? 0 : 1;
    }
}
