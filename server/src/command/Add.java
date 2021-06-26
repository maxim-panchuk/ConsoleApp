package command;

import element.Message;
import element.Movie;
import serverWorker.Server;

public class Add {
    public String run (Message message) {
        return  Server.movieCollectionManager.insertMovieToDb(message);
    }
}
