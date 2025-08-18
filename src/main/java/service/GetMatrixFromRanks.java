package service;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class GetMatrixFromRanks {

    private static Map<String, String> ranksColorMap = Map.ofEntries(
            Map.entry("100100", "2"),
            Map.entry("000010", "3"),
            Map.entry("101100", "4"),
            Map.entry("010010", "5"),
            Map.entry("011000", "6"),
            Map.entry("100111", "7"),
            Map.entry("111010", "8"),
            Map.entry("110000", "9"),
            Map.entry("011111", "10"),
            Map.entry("000000", "J"),
            Map.entry("011001", "Q"),
            Map.entry("011011", "K"),
            Map.entry("001100", "A")
    );


    private static int[][] ranksCoord = {{18, 12}, {7, 12}, {6, 20}, {13,21}, {7, 7}, {21, 7}};
    private static int[][] moves = {{1, 1}, {1, 0}, {1, -1}, {0, 1}, {0, 0}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};


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
        rankPointMap.put("10", new int[][]{{7, 8}, {7, 24}, {19, 7}, {26, 17}});
        rankPointMap.put("J", new int[][]{{16, 8}, {15, 21}, {11, 26}, {5, 24}});
        rankPointMap.put("Q", new int[][]{{16, 7}, {7, 17}, {26, 16}, {25, 25}});
        rankPointMap.put("K", new int[][]{{7, 8}, {13, 16}, {21, 25}, {21, 6}});
        rankPointMap.put("A", new int[][]{{12, 8}, {13, 21}, {22, 26}, {3, 26}});

        Map<String, int[][]> rankPointEmptyMap = new HashMap<>();
        rankPointEmptyMap.put("5", new int[][]{{19, 13}, {6, 19}});
        rankPointEmptyMap.put("K", new int[][]{{13, 7}, {26, 17}});
        rankPointEmptyMap.put("2", new int[][]{{6, 18}, {19, 21}});
        rankPointEmptyMap.put("6", new int[][]{{19, 12}});
        rankPointEmptyMap.put("3", new int[][]{{6, 13}});
        rankPointEmptyMap.put("4", new int[][]{{6, 11}, {8, 7}});
        rankPointEmptyMap.put("7", new int[][]{{7, 13}, {7, 19}, {18, 23}});
        rankPointEmptyMap.put("Q", new int[][]{{7, 7}, {15, 16}});

        String path = "tmp";
        File dir = new File(path);

        File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));

        int[][] countMatrix = new int[30][30];
        Map<String, List<String>> countMatrixMap = new HashMap<>();
        Map<String, int[][]> matrixMap = new HashMap<>();
        Set<String> allNames = new HashSet<>();
        Map<String, List<String>> notWhiteMap = new HashMap<>();
        for (File file : files) {
            try {
                var fileName = file.getName().replaceAll(".png", "");
                String newFileName = "tmp\\" + fileName + ".txt";
                allNames.add(fileName);
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    /*
                    int[][] matrix = new int[30][30];
                    for (int i = 0; i < 30; i++) {
                        for (int j = 0; j < 30; j++) {
                            Color color = new Color(img.getRGB(j, i));
                            if (isNotWhite(color) == 0) {
                                matrix[i][j] = 0;
                            } else {
                                matrix[i][j] = 1;
                                countMatrix[i][j]++;
                                var key = i + "#" + j;

                                if (key.equals("12#18") || key.equals("12#7") || key.equals("20#6") ||
                                        key.equals("21#13") || key.equals("7#7") || key.equals("7#21")) {
                                    if (notWhiteMap.containsKey(key)) {
                                        notWhiteMap.get(key).add(fileName);
                                    } else {
                                        List<String> list = new ArrayList<>();
                                        list.add(fileName);
                                        notWhiteMap.put(key, list);
                                    }
                                }

                                if (countMatrixMap.containsKey(key)) {
                                    countMatrixMap.get(key).add(fileName);
                                } else {
                                    List<String> list = new ArrayList<>();
                                    list.add(fileName);
                                    countMatrixMap.put(key, list);
                                }
                            }
                        }

                     */

                    for (var key : rankPointMap.keySet()) {
                        var points = rankPointMap.get(key);
                        var match = true;
                        for (var point :  points) {
                            Color color = new Color(img.getRGB(point[0], point[1]));
                            var isNotWhite = isNotWhite(color);
                            match = match && isNotWhite == 1;
                        }
                        var emptyPoints = rankPointEmptyMap.get(key);
                        if (emptyPoints != null) {
                            for (var point : emptyPoints) {
                                Color color = new Color(img.getRGB(point[0], point[1]));
                                var isNotWhite = isNotWhite(color);
                                match = match && isNotWhite == 0;
                            }
                        }
                        if (match) {
                            System.out.println("File name : " + fileName + " my value: " + key);
                            break;
                        }
                    }
                    /*

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < ranksCoord.length; i++) {
                        sb.append(isNotWhite(new Color(img.getRGB(ranksCoord[i][0], ranksCoord[i][1]))));
                    }
                    String key = sb.toString();
                    System.out.println("File name : " + fileName + " my value: " + ranksColorMap.get(key));


                     */
                    /*
                    printInFile(matrix, newFileName);
                    printMatrix(matrix);
                    matrixMap.put(file.getName(), matrix);

                     */
                }

            } catch (IOException e) {
                System.err.println("Ошибка при чтении " + file.getName() + ": " + e.getMessage());
            }
        }

        /*
        Map<String, StringBuilder> keys = new HashMap<>();
        for (var val : ranksColorMap.values()) {
            keys.put(val, new StringBuilder());
        }

        for (var key : notWhiteMap.keySet()) {
            System.out.println(key);
            for (var rank : notWhiteMap.get(key)) {
                keys.get(rank).append(1);
            }
            List<String> notIn = allNames.stream()
                    .filter(e -> !notWhiteMap.get(key).contains(e))
                    .toList();
            for (var rank : notIn) {
                keys.get(rank).append(0);
            }
            System.out.println("Contains: " + notWhiteMap.get(key));
            System.out.println("Not contains: " + allNames.stream()
                    .filter(e -> !notWhiteMap.get(key).contains(e))
                    .toList());
        }

        for (var rank : keys.keySet()) {
            System.out.println("Rank : " + rank + " key : " + keys.get(rank));
        }


        /*
        printMatrix(countMatrix);
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                var key = i + "#" + j;
                List<String> cont = countMatrixMap.get(key);
                if (countMatrix[i][j] == 1 && cont.contains("A")) {

                    System.out.println(key);
                    System.out.println("Contains: " + countMatrixMap.get(key));
                    System.out.println("Not contains: " + allNames.stream()
                            .filter(e -> !countMatrixMap.get(key).contains(e))
                            .toList());
                }
            }
        }

         */
    }

    private static void printMatrix(int[][] matrix) {
        for (int k = 0; k < 30; k++) {
            System.out.print(k);
            System.out.print(" ");
            if (k < 10) {
                System.out.print(" ");
            }
        }
        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(i);
            System.out.print(" ");
            if (i < 10) {
                System.out.print(" ");
            }
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
                if (matrix[i][j] < 10) {
                    System.out.print(" ");
                }
            }
            System.out.println(); // Move to the next line after each row
        }
    }
    @SneakyThrows
    private static void printInFile(int[][] matrix, String newFileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(newFileName))) {
            writer.print("   ");
            for (int k = 0; k < 30; k++) {
                writer.print(k);
                writer.print(" ");
                if (k < 10) {
                    writer.print(" ");
                }
            }
            writer.println();
            for (int i = 0; i < matrix.length; i++) {
                writer.print(i);
                writer.print(" ");
                if (i < 10) {
                    writer.print(" ");
                }
                for (int j = 0; j < matrix[i].length; j++) {
                    writer.print(matrix[i][j]);
                    if (j < matrix[i].length - 1) {
                        writer.print(", ");
                    }
                }
                writer.println();
            }
        }
    }

    private static int isNotWhiteAround(int x, int y, BufferedImage img) {
        boolean result = false;
        for (int i = 0; i < 9; i++) {
            int newX = x + moves[i][0];
            int newY = y + moves[i][1];
            newX = Math.max(newX, 0);
            newX = Math.min(newX, 29);
            newY = Math.max(newY, 0);
            newY = Math.min(newY, 29);
            Color color = new Color(img.getRGB(newX, newY));
            result = result || isNotWhite(color) == 1;
        }
        return result ? 1 : 0;
    }

    private static int isNotWhite(Color color) {
        if (color.getBlue() + color.getGreen() + color.getRed() > 550) {
            return 0;
        }
        return color.getBlue() + color.getGreen() + color.getRed() >= 360 && color.getRed() <= color.getGreen() ? 0 : 1;
    }
}
