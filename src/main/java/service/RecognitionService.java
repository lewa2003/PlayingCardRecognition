package service;


import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Data
public class RecognitionService {

    private int cardWidth = 55;
    private int cardHeight = 86;
    private int cnt = 0;
    int[][] positions = {{0,86}, {55, 0}, {55,86}};
    int[][] leftTops = {{147, 586}, {219, 586}, {290, 586}, {361, 586}, {432, 586}};
    int[][] suits = {{37, 63}, {48, 56}, {45, 58}};
    private Map<String, String> suitsMap = Map.of("11", "heartsСердца", "10", "diamondsБуби", "01", "clubsТрефы", "00", "spadesПики");

    public void doRecognition(BufferedImage table) throws IOException {

        StringBuilder fileName = new StringBuilder("tmp\\output");
        int n = 0;
        for (int[] lb : leftTops) {
            int x = lb[0];
            int y = lb[1];
            var cardImg = table.getSubimage(x, y, cardWidth, cardHeight);
            Color color = new Color(cardImg.getRGB(1, 3));
            if (color.getBlue() > 100 && color.getGreen() > 100 && color.getRed() > 100) {
                String suit = detectCardSuit(cardImg);
                File cardFile = new File("tmp\\" + "table = " + cnt + "; card = " + n++ + suit + ".png");
                ImageIO.write(cardImg, "png", cardFile);
                fileName.append(suit);
            }
        }

        File outputFile = new File(fileName + ".png");
        fileName.append(cnt);
        cnt++;
        ImageIO.write(table, "png", outputFile);
    }

    private String detectCardSuit(BufferedImage card) {
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

    private String detectCardRang(BufferedImage card) {
        return null;
    }
}
