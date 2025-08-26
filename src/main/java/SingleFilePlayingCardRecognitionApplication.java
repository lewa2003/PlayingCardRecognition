import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SingleFilePlayingCardRecognitionApplication {

    private static int cardWidth = 55;
    private static int cardHeight = 86;
    static int[][] leftTops = {{147, 586}, {219, 586}, {290, 586}, {361, 586}, {432, 586}};
    static int[][] suits = {{37, 63}, {48, 56}, {45, 58}};
    private static final Map<String, String> suitsMap = Map.of("11", "h", "10", "d", "01", "c", "00", "s");
    public static void main(String[] args) {
        Map<String, int[][]> rankPointMap = new HashMap<>();
        rankPointMap.put("2", new int[][]{{8, 9}, {18, 10}, {7, 25}, {18, 26}});
        rankPointMap.put("3", new int[][]{{7, 7}, {16, 7}, {14, 11}, {18, 20}});
        rankPointMap.put("4", new int[][]{{18, 25}, {18, 9}, {7, 20}, {18, 20}});
        rankPointMap.put("5", new int[][]{{8, 7}, {18, 7}, {18, 20}, {8, 11}});
        rankPointMap.put("6", new int[][]{{19, 21}, {13, 7}, {7, 20}, {7, 12}});
        rankPointMap.put("7", new int[][]{{8, 7}, {13, 7}, {10, 26}, {14, 18}});
        rankPointMap.put("8", new int[][]{{7, 12}, {18, 11}, {6, 22}, {19, 22}});
        rankPointMap.put("9", new int[][]{{7, 13}, {19, 13}, {13, 17}, {12, 26}});
        rankPointMap.put("10", new int[][]{{7, 8}, {7, 24}, {27, 16}});
        rankPointMap.put("J", new int[][]{{16, 8}, {15, 21}, {11, 26}, {5, 24}});
        rankPointMap.put("Q", new int[][]{{16, 7}, {7, 17}, {26, 16}, {25, 25}});
        rankPointMap.put("K", new int[][]{{7, 8}, {13, 16}, {21, 25}, {21, 6}, {17, 20}});
        rankPointMap.put("A", new int[][]{{12, 8}, {12, 21}});

        Map<String, int[][]> rankPointEmptyMap = new HashMap<>();
        rankPointEmptyMap.put("5", new int[][]{{19, 13}, {6, 19}});
        rankPointEmptyMap.put("K", new int[][]{{13, 7}, {26, 17}, {13, 26}, {13, 9}});
        rankPointEmptyMap.put("2", new int[][]{{6, 18}, {19, 21}});
        rankPointEmptyMap.put("6", new int[][]{{19, 12}});
        rankPointEmptyMap.put("3", new int[][]{{6, 13}});
        rankPointEmptyMap.put("4", new int[][]{{6, 11}, {8, 7}});
        rankPointEmptyMap.put("7", new int[][]{{7, 13}, {7, 19}, {18, 23}});
        rankPointEmptyMap.put("Q", new int[][]{{7, 7}, {15, 16}});
        rankPointEmptyMap.put("J", new int[][]{{8, 9}, {6, 14}});
        rankPointEmptyMap.put("A", new int[][]{{12, 26}, {7, 7}, {17, 6}});

        //String path = args[0];
        String path = "C:\\imgs_marked";

        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Директория не существует или это не папка!");
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));

        if (files == null || files.length == 0) {
            System.out.println("В директории нет изображений.");
            return;
        }
        int n = 0;
        int fails = 0;
        for (File file : files) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    n++;
                    String cardsDetected = doRecognition(img, rankPointMap, rankPointEmptyMap);
                    var equals = cardsDetected.equals(file.getName().replaceAll(".png", ""));
                    //System.out.println(file.getName() + " - " + cardsDetected + " equals : " + equals);
                    if (!equals) {
                        System.out.println(file.getName() + " - " + cardsDetected + " equals : " + equals);
                        fails++;
                    }
                    }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении " + file.getName() + ": " + e.getMessage());
            }
        }
        System.out.println("Ok: " + n);

        System.out.println("Fails: " + fails);
    }
    public static String doRecognition(BufferedImage table, Map<String, int[][]> rankPointMap,
                                     Map<String, int[][]> rankPointEmptyMap) throws IOException {
        StringBuilder cardsDetected = new StringBuilder();
        for (int[] lb : leftTops) {
            int x = lb[0];
            int y = lb[1];
            var cardImg = table.getSubimage(x, y, cardWidth, cardHeight);
            Color color = new Color(cardImg.getRGB(1, 3));
            if (color.getBlue() > 100 && color.getGreen() > 100 && color.getRed() > 100) {
                cardsDetected.append(detectCardRank(cardImg, rankPointMap, rankPointEmptyMap));
                cardsDetected.append(detectCardSuit(cardImg));
            }
        }
        return cardsDetected.toString();
    }

    private static String detectCardSuit(BufferedImage card) {
        Color color1 = new Color(card.getRGB(suits[0][0], suits[0][1]));
        Color color2 = new Color(card.getRGB(suits[1][0], suits[1][1]));
        Color color3 = new Color(card.getRGB(suits[2][0], suits[2][1]));
        String result = color1.getRed() > color1.getGreen() ? "1" : "0";
        if (result.equals("1")) {
            result += color2.getRed() > color2.getGreen() ? "1" : "0";
        } else {
            result += color3.getRed() > 100 ? "1" : "0";
        }
        return suitsMap.get(result);
    }

    private static String detectCardRank(BufferedImage card, Map<String, int[][]> rankPointMap,
                                  Map<String, int[][]> rankPointEmptyMap) {
        for (var key : rankPointMap.keySet()) {
            var points = rankPointMap.get(key);
            var match = true;
            for (var point :  points) {
                Color color = new Color(card.getRGB(point[0], point[1]));
                match = match && isNotWhite(color);
            }
            var emptyPoints = rankPointEmptyMap.get(key);
            if (emptyPoints != null) {
                for (var point : emptyPoints) {
                    Color color = new Color(card.getRGB(point[0], point[1]));
                    match = match && !isNotWhite(color);
                }
            }
            if (match) {
                return key;
            }
        }
        return "NO";
    }

    private static boolean isNotWhite(Color color) {
        if (color.getBlue() + color.getGreen() + color.getRed() > 520) {
            return false;
        }
        return color.getBlue() + color.getGreen() + color.getRed() < 360 || color.getRed() > color.getGreen();
    }
}
