package element;

import java.io.Serializable;
import java.util.Scanner;

public class Person implements Serializable {

    private final String regex2 = "\\d+[.]\\d+";
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final double height; //Значение поля должно быть больше 0
    private final element.Color eyeColor; //Поле может быть null
    private final element.Country nationality; //Поле может быть null
    private final Location location; //Поле не может быть null

    public Person(Scanner scanner) {
        name = setPerName(scanner);
        height = setHeight(scanner);
        eyeColor = setColor(scanner);
        nationality = setNationality(scanner);
        location = setLocation(scanner);
    }

    public String setPerName (Scanner scanner) {
        String newDirectorName;
        while (true) {
            System.out.println("Введите имя директора:    ");
            newDirectorName = scanner.nextLine().trim();
            if (newDirectorName.isEmpty()) {
                System.out.println("Имя не должно быть пустым, повтори ввод");
            } else {
                break;
            }
        }
        return newDirectorName;
    }

    public double setHeight (Scanner scanner) {
        double newHeight;
        while (true) {
            try {
                System.out.println("Введите рост директора, рост больше 0:   ");
                String newHeightString = scanner.nextLine().trim();
                if (!newHeightString.matches(regex2)) {
                    System.out.println("Рост должен быть введен в формате double");
                } else if (Double.parseDouble(newHeightString) < 0) {
                    System.out.println("Рост должен быть больше 0!");
                }

                else {
                    newHeight = Double.parseDouble(newHeightString);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Слишком большое число, либо не соблюден формат double, повторите ввод");
            }
        }
        return newHeight;
    }

    public element.Color setColor (Scanner scanner) {
        System.out.println("Выберите цвет глаз : RED ; WHITE ; BROWN");
        element.Color newColor;
        switch (scanner.nextLine()) {
            case "RED": {
                newColor = element.Color.RED;
                break;
            }
            case "WHITE": {
                newColor = element.Color.WHITE;
                break;
            }
            case "BROWN": {
                newColor = element.Color.BROWN;
                break;
            }
            default: {
                //System.out.println("Неверный ввод цвета глаз, значение будет выбрано по умолчанию");
                newColor = element.Color.RED;
            }
        }
        return newColor;
    }

    public element.Country setNationality (Scanner scanner) {
        System.out.println("Выберите национальность директора : UNITED_KINGDOM ; GERMANY ; ITALY");
        element.Country newCountry;
        switch (scanner.nextLine()) {
            case "UNITED_KINGDOM": {
                newCountry = element.Country.UNITED_KINGDOM;
                break;
            }
            case "GERMANY": {
                newCountry = element.Country.GERMANY;
                break;
            }
            case "ITALY": {
                newCountry = element.Country.ITALY;
                break;
            }
            default: {
                //System.out.println("Неверный ввод национальности, значение будет выбрано по умолчанию");
                newCountry = element.Country.ITALY;
            }
        }
        return newCountry;
    }

    public Location setLocation (Scanner scanner) {
        return new Location(scanner);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "element.Person{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", eyeColor=" + eyeColor +
                ", nationality=" + nationality +
                ", location=" + location +
                '}';
    }
}