package command;

import serverWorker.Server;

public class PrintAsceending {
    public String run () {
        return Server.movieCollectionManager.printAscending();
    }
}
