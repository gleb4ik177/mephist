import java.util.HashMap;

public class Review {
    private String fakult;    private int course;    private String date;    private int id;
    private HashMap<Integer, String> mapa_string = new HashMap<>();
    private HashMap<Integer, Double> mapa_double = new HashMap<>();
    public Review (int course, String fakult, String date) {
        this.course = course;
        this.fakult = fakult;
        this.date = date;
    }

    public String get_value_string(int key) {
        return mapa_string.get(key);
    }

    public Double get_value_double(int key) {
        return mapa_double.get(key);
    }

    public void add_value_string(int key, String value) {
        mapa_string.put(key, value);
    }

    public void add_value_double(int key, double value) {
        mapa_double.put(key, value);
    }

    public String get_fakult() {
        return fakult;
    }

    public void set_fakult(String new_fakult) {
        this.fakult = new_fakult;
    }

    public int get_course() {
        return course;
    }

    public void set_course(int new_course) {
        this.course = new_course;
    }

    public String get_date() {
        return date;
    }

    public void set_date(String new_date) {
        this.date = new_date;
    }

    public int get_id() {
        return id;
    }

    public void set_id(int new_id) {
        this.id = new_id;
    }

}