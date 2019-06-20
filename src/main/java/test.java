import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Mxy");
        list.add("String");
        list.add("join");
        String join = String.join("-", list);//传入String类型的List集合，使用"-"号拼接
        System.out.println(join);
        String[] s = new String[]{"Yuan", "Mxy"};//传入String类型的数组，使用"-"号拼接
        String join2 = String.join("-", s);
        System.out.println(join2);
    }
}
