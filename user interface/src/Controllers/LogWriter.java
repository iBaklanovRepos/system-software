package Controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogWriter {
    private static DateTimeFormatter df;
    private static LocalDateTime logDate;

    public static void writeLog(MainWindowController controller, String action){
        df = DateTimeFormatter.ofPattern("HH:mm:ss d MMMM yyyy");
        logDate = LocalDateTime.now();//время для логов
        controller.getProgramLogTextArea().appendText(logDate.format(df) + " \t" + action + "\n");
    }
}
