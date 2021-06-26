package command;

import element.Message;
import serverWorker.Server;

public class RemoveId {
   public String run (Message message) {
       return Server.movieCollectionManager.deleteMovieById(message);
   }
}
