import java.time.LocalDateTime;

public class dates {
    public static String get_now(){
        LocalDateTime datetime = LocalDateTime.now();
        return datetime.toString().substring(0,26).replace("T", " ");
    }
}
