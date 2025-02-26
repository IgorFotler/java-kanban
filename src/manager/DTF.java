package manager;

import java.time.format.DateTimeFormatter;

public class DTF {
    public static DateTimeFormatter getDTF() {
        return DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy");
    }
}
