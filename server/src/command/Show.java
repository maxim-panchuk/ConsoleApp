package command;

import serverWorker.Server;

public class Show {
    public String run () {
        return Server.movieCollectionManager.show();
    }
}
