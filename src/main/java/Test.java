import java.util.*;

/**
 * Created by max on 31.01.16.
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
//        full example

        List<String> list = new ArrayList<>();
        ArrayList<String> alist = new ArrayList<>();
        alist.add("1");
        alist.add("2");
        alist.add("3");
        alist.add("4");
        alist.add("5");
        alist.add("6");
        alist.add("7");
        alist.add("8");
        alist.add("9");
                                                    
        System.out.println("Hello from test");
        for (String a : alist) {
            System.out.println("a = " + a);
            Thread.sleep(1000);
        }
    }
}
