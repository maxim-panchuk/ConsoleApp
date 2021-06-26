package command;

import serverWorker.Server;

public class Info {
    public String run () {
        return Server.movieCollectionManager.outInfo();
    }
}
