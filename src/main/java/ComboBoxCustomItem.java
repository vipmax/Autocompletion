import java.util.*;

/**
 * Created by max on 06.02.16.
 */
public class ComboBoxCustomItem {
    static class Tuple2 {
        String type, name;

        public Tuple2(String type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return type + "  " + name;
        }
    }

    private String methodName;
    private String methodReturnType;
    private List<Tuple2> params = new ArrayList<>();

    private static final int MAX_LENGHT = 60;



    public ComboBoxCustomItem(String methodName, List<Tuple2> params, String methodReturnType) {
        this.params = params;
        this.methodReturnType = methodReturnType;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
    }

    public List<Tuple2> getParams() {
        return params;
    }

    public void setParams(List<Tuple2> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methodName).append(params.toString().replace("[", "(").replace("]", ")"));

        if(stringBuilder.length() + methodReturnType.length() > MAX_LENGHT){
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(methodName).append("(...)");
            for (;stringBuilder2.length() + methodReturnType.length()<=MAX_LENGHT; ) stringBuilder2.append(" ");

            stringBuilder2.append("  ").append(methodReturnType);
            return stringBuilder2.toString();
        }

        for (;stringBuilder.length() + methodReturnType.length()<=MAX_LENGHT; ) stringBuilder.append(" ");

        stringBuilder.append("  ").append(methodReturnType);

        return stringBuilder.toString();
    }
}
