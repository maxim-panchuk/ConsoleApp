package element;

import java.io.Serializable;
import java.util.Scanner;

public class Coordinates implements Serializable {

    private final int newMovieX;
    private final Double newMovieY;

    public Coordinates(Scanner scanner) {
        newMovieX = setX(scanner);
        newMovieY = setY(scanner);
    }

    public int setX (Scanner scanner) {
        int x;
        while (true) {
            try {
                System.out.println("Широта съемок [-180; 180] : ");
                x = Integer.parseInt(scanner.nextLine().trim());
                if (x >= -180 && x <= 180) {
                    break;
                } else {
                    System.out.println("Широта вводится в пределах [-180;180]");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите Integer");
            }
        }
        return x;
    }

    public Double setY (Scanner scanner) {
        double y;
        while (true) {
            try {
                System.out.println("Долгота съемок [-180; 180] : ");
                y = Double.parseDouble(scanner.nextLine().trim());
                if (y >= -180 && y <= 180) {
                    break;
                } else {
                    System.out.println("Долгота вводится в пределах [-180;180]");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите Double");
            }
        }
        return y;
    }

    @Override
    public String toString() {
        return "element.Coordinates{" +
                "newMovieX=" + newMovieX +
                ", newMovieY=" + newMovieY +
                '}';
    }
}