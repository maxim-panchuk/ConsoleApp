package element;

import java.io.Serializable;
import java.util.Scanner;

public class Location implements Serializable {
    private final Integer x; // Поле не может быть null
    private final Integer y; // Поле не может быть null
    private final Integer z; // Поле не может быть null
    private final String name; // Строка не может быть пустой, Поле может быть null

    public Location(Scanner scanner) {
        x = setX(scanner);
        y = setY(scanner);
        z = setZ(scanner);
        name = setTown(scanner);
    }

    public int setX (Scanner scanner) {
        int newLocationX;
        while (true) {

            try {
                System.out.println("Широта [-180; 180]: ");
                String newDirectorXStr = scanner.nextLine().trim();
                newLocationX = Integer.parseInt(newDirectorXStr);
                if (!(newLocationX >= -180 && newLocationX <= 180)) {
                    System.out.println("Введите число в указанных пределах");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число");
            }
        }
        return newLocationX;
    }

    public int setY (Scanner scanner) {
        int newLocationY;
        while (true) {
            try {
                System.out.println("Долгота [-180; 180]: ");
                String newDirectorYStr = scanner.nextLine().trim();
                newLocationY = Integer.parseInt(newDirectorYStr);
                if (!(newLocationY >= -180 && newLocationY <= 180)) {
                    System.out.println("Введите число в указанных пределах");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число");
            }
        }
        return newLocationY;
    }

    public int setZ (Scanner scanner) {
        int newLocationZ;
        while (true) {
            try {
                System.out.println("Высота [0; 2]: ");
                String newDirectorZStr = scanner.nextLine().trim();
                newLocationZ = Integer.parseInt(newDirectorZStr);
                if (!(newLocationZ >= 0 && newLocationZ <= 2)) {
                    System.out.println("Введите число в указанных пределах");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число");
            }
        }
        return newLocationZ;
    }

    public String setTown (Scanner scanner) {
        String newDirectorHomeTown;
        System.out.println("Город");
        newDirectorHomeTown = scanner.nextLine().trim();
        return newDirectorHomeTown;
    }

    @Override
    public String toString() {
        return "element.Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", name='" + name + '\'' +
                '}';
    }
}