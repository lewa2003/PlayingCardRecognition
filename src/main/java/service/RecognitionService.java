package service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RecognitionService {

    // Характеристические цветные точки номиналов
    private final Map<String, int[][]> rankColoredPointMap = new HashMap<>();

    // Характеристические белые(не цветные) точки номиналов
    private final Map<String, int[][]> rankWhitePointMap = new HashMap<>();

    // Характеристические цветные точки мастей
    private final Map<String, int[][]> suitColoredPointMap = new HashMap<>();

    // Характеристические белые(не цветные) точки мастей
    private final Map<String, int[][]> suitWhitePointMap = new HashMap<>();

    // Координаты верхних левых углов карт
    private final int[][] cardsLeftTops = {{147, 586}, {219, 586}, {290, 586}, {361, 586}, {432, 586}};

    // Координаты центральной точки масти
    private final int[] suitCenter = {37, 64};

    // 3 угла карты, для проверки наличия карты
    private final int[][] cardAngleCoordinates = {{2, 4}, {2, 82}, {51, 4}};

    // Максимальное значение модуля разности яркости двух цветов, для того, чтобы они считались одинаковыми
    private static final int LUMINANCE_THRESHOLD = 15;
    private static final int CARD_WIDTH = 55;
    private static final int CARD_HEIGHT = 86;

    public RecognitionService(){
        initRankMap();
        initSuitMap();
    }
    private void initRankMap() {
        rankColoredPointMap.put("2", new int[][]{{8, 9}, {18, 10}, {7, 25}, {18, 26}});
        rankColoredPointMap.put("3", new int[][]{{7, 7}, {16, 7}, {14, 11}, {18, 20}});
        rankColoredPointMap.put("4", new int[][]{{18, 25}, {18, 9}, {7, 20}, {18, 20}});
        rankColoredPointMap.put("5", new int[][]{{8, 7}, {18, 7}, {18, 20}, {8, 11}});
        rankColoredPointMap.put("6", new int[][]{{19, 21}, {13, 7}, {7, 20}, {7, 12}});
        rankColoredPointMap.put("7", new int[][]{{8, 7}, {13, 7}, {10, 26}, {14, 18}});
        rankColoredPointMap.put("8", new int[][]{{7, 12}, {18, 11}, {6, 22}, {19, 22}});
        rankColoredPointMap.put("9", new int[][]{{7, 13}, {19, 13}, {13, 17}, {12, 26}});
        rankColoredPointMap.put("10", new int[][]{{7, 8}, {7, 24}, {27, 16}});
        rankColoredPointMap.put("J", new int[][]{{16, 8}, {15, 21}, {11, 26}, {5, 24}});
        rankColoredPointMap.put("Q", new int[][]{{16, 7}, {7, 17}, {26, 16}, {25, 25}});
        rankColoredPointMap.put("K", new int[][]{{7, 8}, {13, 16}, {21, 25}, {21, 6}, {17, 20}});
        rankColoredPointMap.put("A", new int[][]{{12, 8}, {12, 21}});

        rankWhitePointMap.put("2", new int[][]{{6, 18}, {19, 21}});
        rankWhitePointMap.put("3", new int[][]{{6, 13}});
        rankWhitePointMap.put("4", new int[][]{{6, 11}, {8, 7}});
        rankWhitePointMap.put("5", new int[][]{{19, 13}, {6, 19}});
        rankWhitePointMap.put("6", new int[][]{{19, 12}});
        rankWhitePointMap.put("7", new int[][]{{7, 13}, {7, 19}, {18, 23}});
        rankWhitePointMap.put("J", new int[][]{{8, 9}, {6, 14}});
        rankWhitePointMap.put("Q", new int[][]{{7, 7}, {15, 16}});
        rankWhitePointMap.put("K", new int[][]{{13, 7}, {26, 17}, {13, 26}, {13, 9}});
        rankWhitePointMap.put("A", new int[][]{{12, 26}, {7, 7}, {17, 6}});
    }

    private void initSuitMap() {
        suitColoredPointMap.put("h", new int[][]{{49, 62}, {25, 62}, {37, 76}, {44, 55}, {29, 54}});
        suitColoredPointMap.put("d", new int[][]{{37, 51}, {38, 76}, {50, 63}, {26, 63}});
        suitColoredPointMap.put("s", new int[][]{{44, 57}, {45, 72}, {26, 67}, {45, 58}});
        suitColoredPointMap.put("c", new int[][]{{37, 54}, {28, 68}, {46, 68}, {37, 77}});

        suitWhitePointMap.put("d", new int[][]{{27, 52}, {47, 52}, {27, 74}, {47, 74}});
        suitWhitePointMap.put("c", new int[][]{{45, 58}, {28, 58}});
        suitWhitePointMap.put("s", new int[][]{{29, 54}, {45, 54}});
    }

    /**
     * @param table - image of gaming table
     * @return Cards on table, e.g. 10hAsQc
     * @throws IOException
     */
    public String doRecognition(BufferedImage table) throws IOException {
        StringBuilder cardsDetected = new StringBuilder();
        for (int[] lt : cardsLeftTops) {
            int x = lt[0];
            int y = lt[1];
            var cardImg = table.getSubimage(x, y, CARD_WIDTH, CARD_HEIGHT);
            if (isCard(cardImg)) {
                String rank = detectCardRank(cardImg);
                String suit = detectCardSuit(cardImg);
                cardsDetected.append(rank);
                cardsDetected.append(suit);
            } else {
                break;
            }
        }
        return cardsDetected.toString();
    }

    /**
     * @param card - card image
     * @return true if card is image of a playing card, false otherwise
     */
    // Сравниваем три точки в разных углах карты на попарное равество цвета, а центральную точку масти - на отличие с одним из углов
    private boolean isCard(BufferedImage card) {
        Color cardAngle1 = getColor(card, cardAngleCoordinates[0]);
        Color cardAngle2 = getColor(card, cardAngleCoordinates[1]);
        Color cardAngle3 = getColor(card, cardAngleCoordinates[2]);
        Color suitCenterColor = getColor(card, suitCenter);

        return isColorSimilar(cardAngle1, cardAngle2) && isColorSimilar(cardAngle2, cardAngle3) && !isColorSimilar(cardAngle1, suitCenterColor);
    }

    /**
     * @param image - image to take color from point
     * @param point - point coordinates to take color
     * @return Color on point for image
     */
    private Color getColor(BufferedImage image, int[] point) {
        return new Color(image.getRGB(point[0], point[1]));
    }

    /**
     * @param template - color to compare with
     * @param check - color for comparison
     * @return true if diff in colors luminance < luminanceTrashHold
     */
    // Для двух цветов высчитываем их яркости по формуле Relative luminance и сравниаем их значения
    private boolean isColorSimilar(Color template, Color check) {
        var t = 0.2126 * template.getRed() + 0.7152 * template.getGreen() + 0.0722 * template.getBlue();
        var c = 0.2126 * check.getRed() + 0.7152 * check.getGreen() + 0.0722 * check.getBlue();

        return Math.abs(t-c) < LUMINANCE_THRESHOLD;
    }

    /**
     * @param card - card image
     * @return card nominal, e.g. A. And "NO" if detection fails
     */
    // Для каждого номинала будем проверять его характеристические точки, пока не обнаружим полное совпадение
    private String detectCardRank(BufferedImage card) {
        Color cardBackgroundColor = getColor(card, cardAngleCoordinates[0]);

        return rankColoredPointMap.keySet().stream()
                .filter(rank -> matchesRankPattern(card, cardBackgroundColor, rank))
                .findFirst()
                .orElse("NO");
    }

    /**
     * @param card - card image
     * @param backgroundColor - background color of the card
     * @param rank - rank to check characteristic points
     * @return card rank matches pattern
     */
    // Для номинала проверяем, что все его цветные характерестические точки не равны цвету фона карты, а не цветные - равны
    private boolean matchesRankPattern(BufferedImage card, Color backgroundColor, String rank) {
        int[][] coloredPoints = rankColoredPointMap.get(rank);
        int[][] whitePoints = rankWhitePointMap.getOrDefault(rank, new int[0][]);

        return Arrays.stream(coloredPoints)
                .noneMatch(point -> isColorSimilar(backgroundColor, getColor(card, point)))
                && Arrays.stream(whitePoints)
                .allMatch(point -> isColorSimilar(backgroundColor, getColor(card, point)));
    }

    /**
     * @param card - card image
     * @return card suit, e.g. h (for Hearts). And "NO" if detection fails
     */
    // Для каждой масти будем проверять её характерестические точки, пока не обнаружим полное совпадение
    private String detectCardSuit(BufferedImage card) {
        Color suitCenterColor = getColor(card, suitCenter);
        return suitColoredPointMap.keySet().stream()
                .filter(rank -> matchesSuitPattern(card, suitCenterColor, rank))
                .findFirst()
                .orElse("NO");
    }

    /**
     * @param card - card image
     * @param suitColor - suit color of the card
     * @param suit - suit to check characteristic points
     * @return card suit matches pattern
     */
    // Для масти проверяем, что все её цветные характерестические точки равнцы цвету центральной точки масти, а не цветные - не равны
    private boolean matchesSuitPattern(BufferedImage card, Color suitColor, String suit) {
        int[][] coloredPoints = suitColoredPointMap.get(suit);
        int[][] whitePoints = suitWhitePointMap.getOrDefault(suit, new int[0][]);

        return Arrays.stream(coloredPoints)
                .allMatch(point -> isColorSimilar(suitColor, getColor(card, point)))
                && Arrays.stream(whitePoints)
                .noneMatch(point -> isColorSimilar(suitColor, getColor(card, point)));
    }
}
