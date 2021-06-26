package command;

import serverWorker.Server;

public class Help {
    public String run()  {
        return Server.movieCollectionManager.help();
    }
}
