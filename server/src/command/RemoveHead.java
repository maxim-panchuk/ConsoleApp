package command;

import element.Message;
import serverWorker.Server;

public class RemoveHead {
    public String run (Message message) {
        return Server.movieCollectionManager.deleteHead(message);
    }
}
