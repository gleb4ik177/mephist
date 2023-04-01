import java.util.ArrayList;

interface Reviewable{
    public ArrayList<Review> get_reviews();
    public void add_review(Review review);
}

public class Teacher implements Reviewable{
    private int id;
    private String name;
    private ArrayList<Review> reviews = new ArrayList<Review>();
    public Teacher (String name) {
        this.name = name;
    }
    public Teacher (int id) {
        this.id = id;
    }
    public ArrayList<Review> get_reviews() {
        return reviews;
    }

    public void add_review(Review review) {
        reviews.add(review);
    }

    public int get_id() {
        return id;
    }

    public void set_id(int new_id) {
        this.id = new_id;
    }

    public String get_name() {
        return name;
    }

    public void set_name(String new_name) {
        this.name = new_name;
    }
}
