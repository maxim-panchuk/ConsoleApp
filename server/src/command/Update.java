package command;

import element.Message;
import serverWorker.Server;

public class Update {
    public String run (Message message){
        return Server.movieCollectionManager.updateElement(message);
    }
}
