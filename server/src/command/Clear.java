package command;

import element.Message;
import serverWorker.Server;

public class Clear {
    public String run (Message message) {
        return Server.movieCollectionManager.clear(message);
    }
}
