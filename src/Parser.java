import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.lang.annotation.ElementType;
import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

import java.time.LocalDateTime;

@Deprecated
public class Parser {
    public static String get_now(){
        LocalDateTime datetime = LocalDateTime.now();
        String date = datetime.toString().substring(0,26).replace("T", " ");
        return date;
    }
    public static void main(String[] args) throws IOException, SQLException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mephist",
                "gleb", "1549");
        Statement statement = connection.createStatement();
        /*String zapros = "select * from marks";
        ResultSet rs = statement.executeQuery(zapros);
        while(rs.next()){
            String id = rs.getString("mark_id");
            String name = rs.getString("mark_name");
        }*/

        Document doc = Jsoup.connect("http://www.mephist.ru/mephist/prepods.nsf/form_prepod?OpenForm")
                .timeout(100 * 1000).get();
        Elements teachers = doc.select("td[border=0] > table > tbody > tr > td.menu1 > a[href]");
        ArrayList<String> teacher_links = new ArrayList<String>();
        ArrayList<String> teacher_names = new ArrayList<String>(); //имена преподов
        ArrayList<String> teacher_ids = new ArrayList<String>(); //айдишники преподов
        for (Element teacher : teachers) {
            teacher_links.add("http://www.mephist.ru" + teacher.attr("href").replace('\\', '/'));
            Pattern pattern = Pattern.compile("id/.*");
            Matcher matcher = pattern.matcher(teacher.attr("href"));
            while (matcher.find()) teacher_ids.add(matcher.group().replace("id/", ""));
            teacher_names.add(teacher.attr("title"));
        }
        //System.out.println(teacher_links.size());
        ArrayList<Float> chars = new ArrayList<Float>(); //характер
        ArrayList<Float> preps = new ArrayList<Float>(); //преподавание
        ArrayList<Float> exams = new ArrayList<Float>(); //прием экзаменов
        int start = 0;
        int finish = 1101;

        /*for(int i = 0;i<1101;i++){
            String insertTableSQL = "INSERT INTO teachers"
                    + "(name, new, mephist_id, date_insert) " + "VALUES"
                    + "('"+teacher_names.get(i)+"',0,'" + teacher_ids.get(i)+"','"+ get_now()+"')";
            System.out.println(insertTableSQL);
            statement.executeUpdate(insertTableSQL);
        }*/

        int k = 1;
        int k1 = 7066;
        for (int i = start; i < 1101; i++) { //[0-300) [300-600) [600-900) [900-1101] //1101
            String teacher_link = teacher_links.get(i);
            //System.out.println(teacher_link);
            //System.out.println(teacher_ids.get(i));
            ArrayList<String> review_links = new ArrayList<String>();
            Document page = Jsoup.connect(teacher_link)
                    .timeout(100 * 1000).get();
            Elements char_el = page.select("tbody > tr > td.centercolumn > p > span[id = show_char]");
            Elements prep_el = page.select("tbody > tr > td.centercolumn > p > span[id = show_prep]");
            Elements exam_el = page.select("tbody > tr > td.centercolumn > p > span[id = show_exam]");
            if (char_el.text().equals("")) {
                char_el = page.select("tbody > tr > td.centercolumn > span[id = show_char]");
                prep_el = page.select("tbody > tr > td.centercolumn > span[id = show_prep]");
                exam_el = page.select("tbody > tr > td.centercolumn > span[id = show_exam]");
            }

            chars.add((float) Double.parseDouble(char_el.text()));
            preps.add((float) Double.parseDouble(prep_el.text()));
            exams.add((float) Double.parseDouble(exam_el.text()));
            //System.out.println(teacher_names.get(i));
            //System.out.println(chars.get(i-start) + " " + preps.get(i-start) + " " + exams.get(i-start));
            Elements tables = page.select("td.centercolumn > table");
            if (tables.size() >= 4) {/*
                Element tableOfReviews = tables.get(3);
                Elements reviews_links1 = tableOfReviews.select("tbody > tr > td > a[href]");
                for (Element review_link1 : reviews_links1) {
                    review_links.add("http://www.mephist.ru" + review_link1.attr("href").replace('\\', '/'));
                }
                ArrayList<String> review_headers = new ArrayList<String>(); //заголовки
                ArrayList<String> review_contents = new ArrayList<String>(); //содержимое отзывов
                ArrayList<String> review_dates = new ArrayList<String>(); //дата отзыва



                for (int j = 0;j<review_links.size();j++) {
                    String url_review = review_links.get(j);
                    //System.out.println(url_review);
                    Document review_page = Jsoup.connect(url_review)
                            .timeout(100 * 1000).get();
                    Elements review_header_el = review_page.select("td.centercolumn > p > u");
                    Elements review_all = review_page.select("td.centercolumn > p");
                    Elements fonts = review_page.select("td.centercolumn > div[align] > font");
                    Element review_date_el = fonts.get(2);
                    Pattern pattern = Pattern.compile("<p>.*</p>");
                    Matcher matcher = pattern.matcher(review_all.get(0).toString());
                    while (matcher.find()) {
                        String body = matcher.group().toString().replaceAll("<u>.*</u>", "");
                        if(body.equals(""))
                            review_contents.add("----");
                        else review_contents.add(body.replace("<br>", "")
                                    .replace("<p>", "").replace("</p>", "")
                                    .replace("&lt;", "<").replace("&gt;", ">")
                                    .replace("'", "''"));

                    }
                    review_headers.add(review_header_el.get(0).text().replace("'", "''"));
                    String date = review_date_el.text().replaceAll("([0-9]+)/([0-9]+)/([0-9]+)", "$3-$1-$2");;
                    review_dates.add(date);
                    System.out.print((j+1)+") ");
                    System.out.println(review_headers.get(j));
                    System.out.println(" " + review_contents.get(j));
                    System.out.println(" " + review_dates.get(j));

                    String insertTableSQL = "INSERT INTO reviews"
                            + "(teacher_id, date, date_insert) " + "VALUES"
                            + "("+(i+1)+",'" + review_dates.get(j)+"','"+ get_now()+"')";
                    System.out.println(insertTableSQL);
                    statement.executeUpdate(insertTableSQL);

                    insertTableSQL = "INSERT INTO ratings"
                            + "(string_column, review_id, mark_id, date_insert) " + "VALUES"
                            + "('"+review_headers.get(j)+"',"+k+"," + 4 +",'"+ get_now()+"')";
                    System.out.println(insertTableSQL);
                    statement.executeUpdate(insertTableSQL);

                    insertTableSQL = "INSERT INTO ratings"
                            + "(string_column, review_id, mark_id, date_insert) " + "VALUES"
                            + "('"+review_contents.get(j)+"',"+k+"," + 5 +",'"+ get_now()+"')";
                    System.out.println(insertTableSQL);
                    statement.executeUpdate(insertTableSQL);

                    insertTableSQL = "INSERT INTO ratings"
                            + "(double_column, review_id, mark_id, date_insert) " + "VALUES"
                            + "('"+chars.get(i)+"',"+k+"," + 1 +",'"+ get_now()+"')";
                    System.out.println(insertTableSQL);
                    statement.executeUpdate(insertTableSQL);

                    insertTableSQL = "INSERT INTO ratings"
                            + "(double_column, review_id, mark_id, date_insert) " + "VALUES"
                            + "('"+preps.get(i)+"',"+k+"," + 2 +",'"+ get_now()+"')";
                    System.out.println(insertTableSQL);
                    statement.executeUpdate(insertTableSQL);

                    insertTableSQL = "INSERT INTO ratings"
                            + "(double_column, review_id, mark_id, date_insert) " + "VALUES"
                            + "('"+exams.get(i)+"',"+k+"," + 3 +",'"+ get_now()+"')";
                    System.out.println(insertTableSQL);
                    statement.executeUpdate(insertTableSQL);
                    k++;
                }
                System.out.println();
            */
            } else {/*
                System.out.println((i+1)+teacher_names.get(i));
                String insertTableSQL = "INSERT INTO reviews"
                        + "(teacher_id, date_insert) " + "VALUES"
                        + "("+(i+1)+",'"+ get_now()+"')";
                System.out.println(insertTableSQL);
                statement.executeUpdate(insertTableSQL);

                insertTableSQL = "INSERT INTO ratings"
                        + "(double_column, review_id, mark_id, date_insert) " + "VALUES"
                        + "('"+chars.get(i)+"',"+k1+"," + 1 +",'"+ get_now()+"')";
                System.out.println(insertTableSQL);
                statement.executeUpdate(insertTableSQL);

                insertTableSQL = "INSERT INTO ratings"
                        + "(double_column, review_id, mark_id, date_insert) " + "VALUES"
                        + "('"+preps.get(i)+"',"+k1+"," + 2 +",'"+ get_now()+"')";
                System.out.println(insertTableSQL);
                statement.executeUpdate(insertTableSQL);

                insertTableSQL = "INSERT INTO ratings"
                        + "(double_column, review_id, mark_id, date_insert) " + "VALUES"
                        + "('"+exams.get(i)+"',"+k1+"," + 3 +",'"+ get_now()+"')";
                System.out.println(insertTableSQL);
                statement.executeUpdate(insertTableSQL);
                k1++;
                //System.out.println("---Нет отзывов");
                //System.out.println();
            }
            //break;
            //System.out.println(i+1);
            */
            }
            connection.close();

        }
    }

}

