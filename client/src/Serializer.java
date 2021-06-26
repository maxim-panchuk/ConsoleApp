import element.Message;
import element.Something;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Serializer {
    private static ByteArrayOutputStream byteArrayOutputStream;

    public static byte[] serializeMessage (Message message) {
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Проблемы с сериализацией объекта");
        }
        return byteArrayOutputStream.toByteArray();
    }

//    public static byte[] serializeSomething (Something something) {
//        try {
//            byteArrayOutputStream = new ByteArrayOutputStream();
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//            objectOutputStream.writeObject(something);
//            objectOutputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return byteArrayOutputStream.toByteArray();
//    }
}

