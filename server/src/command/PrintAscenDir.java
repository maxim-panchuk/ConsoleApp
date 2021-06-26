package command;

import serverWorker.Server;

public class PrintAscenDir {
    public String run () {
        return Server.movieCollectionManager.printAscendingDirector();
    }
}
