package element;

import java.io.Serializable;

public class Something implements Serializable {
    private String someString = null;
    public Something (String someString) {
        this.someString = someString;
    }
    public String getSomeString () {
        return someString;
    }
}
