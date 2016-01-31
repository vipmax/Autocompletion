import org.apache.spark.SparkConf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by max on 31.01.16.
 */
public class Test {
    public static void main(String[] args) {
//        full example
        org.apache.spark.SparkConf sparkConf = new SparkConf();
        SparkConf sparkConf2 = new SparkConf();
//        sparkConf.getOp

//      more
        CharSequence code = "null";
        System.out.println(code);
        String regexp = "([A-z]*) *([A-z]*) *= ";
        System.out.println(regexp);

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(code);
    }
}
