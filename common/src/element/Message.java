package element;

import java.io.Serializable;

public class Message implements Serializable {

    private String command = null;
    private String arg = null;
    private Movie movie = null;
    private User user = null;

    public Message (String command, String arg, Movie movie, User user) {
        this.command = command;
        this.arg = arg;
        this.movie = movie;
        this.user = user;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getArgument() {
        return arg;
    }

    public void setArgument(String arg) {
        this.arg = arg;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public User getUser() {return user;}

    public void setUser (User user) {
        this.user = user;
    }
}
