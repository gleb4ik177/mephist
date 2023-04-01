import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print(">");
            String command = scanner.nextLine();
            switch (command) {
                case "/exit" -> {
                    return;
                }
                case "/start" -> {
                    System.out.println("Доступные команды:\n /exit - завершить работу\n" +
                            " /add_review - добавить отзыв преподавателю\n /see_reviews - посмотреть отзывы о преподе\n" +
                            " /add_teacher - добавить преподавателя");
                }
                case "/see_reviews" -> {
                    System.out.print(" Введите ФИО преподавателя: \n ");
                    String fio = scanner.nextLine();
                    HashMap<Integer, String> suitable_teachers = sql_requests.teacher_id_by_name(fio);
                    ArrayList<Integer> ids = new ArrayList<Integer>();
                    int k = 0;
                    for(Map.Entry<Integer, String> entry: suitable_teachers.entrySet()){
                        ids.add(entry.getKey());
                        System.out.println(" "+(k+1)+") "+entry.getValue());
                        k++;
                    }
                    System.out.print(" Укажите номер конкретного преподавателя: ");
                    int num = Integer.valueOf(scanner.nextLine());
                    System.out.println("Преподаватель: "+suitable_teachers.get(ids.get(num-1)));
                    sql_requests.select_reviews_for_teacher(ids.get(num-1));
                }
                case "/add_teacher" -> {
                    System.out.print(" Введите ФИО преподавателя: \n ");
                    String fio = scanner.nextLine();
                    Teacher new_teacher = new Teacher(fio);
                    sql_requests.add_teacher(new_teacher);
                }
                case "/add_review" -> {
                    ArrayList<String> fakults= new ArrayList<String>(Arrays.asList("Ияфит", "Лаплаз",
                            "ИФИБ", "ИНТЭЛ", "ИИКС", "ИФТИС", "ИФТЭБ", "ИМО", "ФБИУКС", "ВИШ"));
                    System.out.print(" Введите ФИО преподавателя: \n ");
                    String fio = scanner.nextLine();
                    HashMap<Integer, String> suitable_teachers = sql_requests.teacher_id_by_name(fio);
                    ArrayList<Integer> ids = new ArrayList<Integer>();
                    int k = 0;
                    for(Map.Entry<Integer, String> entry: suitable_teachers.entrySet()){
                        ids.add(entry.getKey());
                        System.out.println(" "+(k+1)+") "+entry.getValue());
                        k++;
                    }
                    System.out.print(" Укажите номер конкретного преподавателя: ");
                    int num_teacher = Integer.valueOf(scanner.nextLine());
                    int k1 = 1;
                    for(String fuck: fakults){
                        System.out.println(" "+k1+") "+fuck);
                        k1++;
                    }
                    System.out.print(" Укажите номер вашего института из списка: ");
                    int num_fuck = Integer.valueOf(scanner.nextLine());
                    System.out.print(" Укажите ваш курс: ");
                    int course = Integer.valueOf(scanner.nextLine());

                    Review new_review = new Review(course, fakults.get(num_fuck-1), dates.get_now());

                    System.out.print(" Оцените характер преподавателя(от -5.0 до 5.0): ");
                    double har = Double.valueOf(scanner.nextLine());
                    System.out.print(" Оцените уровень преподавания преподавателя(от -5.0 до 5.0): ");
                    double prep = Double.valueOf(scanner.nextLine());
                    System.out.print(" Оцените приём зачётов/экзаменов у преподавателя(от -5.0 до 5.0): ");
                    double exam = Double.valueOf(scanner.nextLine());
                    new_review.add_value_double(1, har);
                    new_review.add_value_double(2, prep);
                    new_review.add_value_double(3, exam);

                    System.out.print(" Хотите оставить текстовый отзыв(да/нет): ");
                    String answer = scanner.nextLine();
                    if(answer.equalsIgnoreCase("да")){
                        System.out.print(" Ваш заголовок: ");
                        String header = scanner.nextLine();
                        System.out.print(" Ваш текст: \n ");
                        String body = scanner.nextLine();
                        new_review.add_value_string(4, header);
                        new_review.add_value_string(5, body);
                    }
                    sql_requests.add_review(ids.get(num_teacher-1), new_review);
                    System.out.println(" Спасибо за ответ!");

                }
            }
        }
    }
}