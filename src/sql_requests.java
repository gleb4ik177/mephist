import org.postgresql.gss.GSSOutputStream;
import org.postgresql.util.OSUtil;
import org.w3c.dom.ls.LSOutput;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.sql.*;


public class sql_requests {
    static ArrayList<Integer> double_fields = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
    static ArrayList<Integer> string_fields = new ArrayList<Integer>(Arrays.asList(4, 5));
    public static void add_teacher(Teacher teacher) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mephist",
                "gleb", "1549");
        Statement statement = connection.createStatement();
        String insertTableSQL = "INSERT INTO teachers"
                + "(name, new, date_insert) " + "VALUES"
                + "('"+teacher.get_name()+"'," + 1 + ",'"+ dates.get_now()+"')";
        System.out.println(insertTableSQL);
        statement.executeUpdate(insertTableSQL);
        connection.close();
    }
    public static int last_review_id() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mephist",
                "gleb", "1549");
        Statement statement = connection.createStatement();
        String select_last_review_id = "select review_id from reviews order by review_id desc limit 1";
        ResultSet rs_last_review = statement.executeQuery(select_last_review_id);
        int last_id = 0;
        while(rs_last_review.next())
            last_id = rs_last_review.getInt("review_id");
        connection.close();
        return last_id;
    }
    public static void add_review(int teacher_id, Review review) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mephist",
                "gleb", "1549");
        Statement statement = connection.createStatement();
        String insertTableSQL = "INSERT INTO reviews"
                + "(teacher_id, course, fakult, date, date_insert) " + "VALUES"
                + "("+teacher_id+"," + review.get_course() + ",'"+ review.get_fakult()+"','"
                + review.get_date() + "','" +dates.get_now()+"')";
        //System.out.println(insertTableSQL);
        statement.executeUpdate(insertTableSQL);

        for(int mark_id: double_fields){
            double mark = review.get_value_double(mark_id);
            insertTableSQL = "INSERT INTO ratings"
                    + "(double_column, review_id, mark_id, date_insert) " + "VALUES"
                    + "("+mark+"," + sql_requests.last_review_id() + ","+ mark_id+",'" +dates.get_now()+"')";
            //System.out.println(insertTableSQL);
            statement.executeUpdate(insertTableSQL);
        }

        for(int mark_id: string_fields){
            String mark = review.get_value_string(mark_id);
            if(mark != null){
                insertTableSQL = "INSERT INTO ratings"
                        + "(string_column, review_id, mark_id, date_insert) " + "VALUES"
                        + "('"+mark+"'," + sql_requests.last_review_id() + ","+ mark_id+",'" +dates.get_now()+"')";
                //System.out.println(insertTableSQL);
                statement.executeUpdate(insertTableSQL);
            }
        }
        connection.close();
    }
    public static HashMap<Integer, String>  teacher_id_by_name(String teacher_name) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mephist",
                "gleb", "1549");
        Statement statement = connection.createStatement();
        String selectId = "select teacher_id, name from teachers where name ~* '" + teacher_name + "'";
        ResultSet rs = statement.executeQuery(selectId);
        HashMap<Integer, String> mapa = new HashMap<>();
        while(rs.next()){
            mapa.put(rs.getInt("teacher_id"), rs.getString("name"));
        }
        connection.close();
        return mapa;
    }

    public static String name_mark_by_id(int id) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mephist",
                "gleb", "1549");
        Statement statement = connection.createStatement();
        String selectId = "select mark_name from marks where mark_id = " + id;
        ResultSet rs = statement.executeQuery(selectId);
        String name = "";
        while(rs.next()){
            name = rs.getString("mark_name");
        }
        connection.close();
        return name;
    }
    public static void select_reviews_for_teacher(int teacher_id) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mephist",
                "gleb", "1549");
        Statement statement = connection.createStatement();
        System.out.println("Отзывы: ");
        String selectNumbersOfReview = "select review_id from reviews where teacher_id = " + teacher_id;
        ResultSet rs = statement.executeQuery(selectNumbersOfReview);
        ResultSet rs_strings;
        ResultSet rs_body;
        ResultSet rs_date;
        while(rs.next()){
            int review_id = rs.getInt("review_id");
            Statement statement1 = connection.createStatement();
            String select_date = "select to_char(date, 'dd.mm.yyyy') as date from reviews where review_id = " + review_id;
            String select_info = "select mark_name, string_column, double_column, string_flag, double_flag from ratings join marks using(mark_id) where review_id = " + review_id + "order by query";
            rs_strings = statement1.executeQuery(select_info);
            while(rs_strings.next()){
                System.out.println("\t"+rs_strings.getString("mark_name") + ": ");
                if(rs_strings.getInt("double_flag") == 1){
                    System.out.println("\t"+rs_strings.getString("double_column"));
                }else if(rs_strings.getInt("string_flag") == 1) {
                    System.out.println("\t"+rs_strings.getString("string_column"));
                }
            }
            rs_date = statement1.executeQuery(select_date);
            while(rs_date.next()){
                System.out.println("\t"+rs_date.getString("date"));
            }
            System.out.println();
        }
        /*
        for(int i: double_fields){
            String select_double = "select round(avg(cast(double_column as numeric)),2) as average_double from reviews join ratings using(review_id) where teacher_id = "
                    + teacher_id + " and mark_id = " + i;
            ResultSet rs_double = statement.executeQuery(select_double);
            String mark_name = name_mark_by_id(i);
            double value = 0.0;
            while(rs_double.next()){
                value = rs_double.getDouble("average_double");
            }
            System.out.println(mark_name + ": " + value);
        }*/
        connection.close();
    }
    /*public static void update_arrays(){

    }*/
}

