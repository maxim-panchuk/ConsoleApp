package command;

import element.Message;
import serverWorker.Server;

public class NameFilter {
    public String run (Message message) {
        String regex = message.getArgument();
        return Server.movieCollectionManager.nameFilter(regex);
    }
}
