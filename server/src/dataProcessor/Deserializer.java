package dataProcessor;

import element.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class Deserializer {

    private static final Logger logger = Logger.getGlobal();

    private static Message message = null;

    public static Message deserializer (byte[] data) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
            message = (Message) objectInputStream.readObject();
            objectInputStream.close();
            logger.info("Получен объект типа Message");
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            logger.warning("Получен неверный объект");
        }
        return message;
    }
}