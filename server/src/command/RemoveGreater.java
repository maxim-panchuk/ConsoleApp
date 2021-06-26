package command;

import element.Message;
import serverWorker.Server;

public class RemoveGreater {
    public String run(Message message) {
        return Server.movieCollectionManager.deleteGreater(message);
    }
}
