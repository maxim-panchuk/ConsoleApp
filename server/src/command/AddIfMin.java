package command;

import element.Message;
import serverWorker.Server;

public class AddIfMin {
    public String run (Message message) {
        return Server.movieCollectionManager.insertIfMin(message);
    }
}
