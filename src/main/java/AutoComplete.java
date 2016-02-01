import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by max on 31.01.16.
 */
public class AutoComplete {

    Map<String, String> classes = new HashMap<>();

    public static void main(String[] args) throws Exception {
        AutoComplete autoComplete = new AutoComplete();
        autoComplete.init();

        long startTime = System.nanoTime();
        autoComplete.test(AutoComplete.getUserCode());
        System.out.println("elapsed = " + (System.nanoTime() - startTime) / 1000000 + " ms");
    }

    private static String getUserCode() throws IOException {
        File file = new File("src/main/java/Test.java");
        String code = FileUtils.readFileToString(file);
        System.out.println(code);
        return code;
    }


    private void init() throws IOException, ClassNotFoundException {
        initJars("target/deps");
        initJars("/usr/lib/jvm/java-8-oracle/jre/lib/");
        initJars("/usr/lib/jvm/java-8-oracle/jre/lib/ext/");

        System.out.println("classes size = " + classes.size());
    }

    private void initJars(String directory) throws IOException {
        File[] files = new File(directory).listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) continue;
            ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    StringBuilder className = new StringBuilder();
                    className.append(entry.getName().replace("/", "."));
                    className.setLength(className.length() - ".class".length());
                    classes.put(className.toString(), file.getAbsolutePath());
                }
            }
        }
    }

    private void test(String code) throws Exception {

        List<String> userImportClasses = getUserImportClases(code);
        List<String> userClasses = getUserClasses(code);



        Set<String> classesForAnalisys = deleteDublicates(userImportClasses, userClasses);

        for (String classs : classesForAnalisys) {
            Method[] methods = getMethods(classs);
            System.out.println("\nFor class = " + classs + " Found " + methods.length + " methods");
            for (Method method : methods) {
                System.out.println("method = " + method);
            }
            Arrays.stream(methods).forEach(System.out::println);
        }

    }

    private Set<String> deleteDublicates(List<String> userImportClasses, List<String> userClasses) {
        Set<String> classesForAnalisys = new HashSet<>();
        for (String userClass : userClasses)
            for (String userImportClass : userImportClasses) {
                String[] split = userImportClass.replace(".", " ").split(" ");
                String userImportType = split[split.length - 1];
                if (userImportType.equals(userClass))
                    classesForAnalisys.add(userImportClass);
            }
        return classesForAnalisys;
    }

    private Method[] getMethods(String classs) throws Exception {
        String jar = classes.get(classs);
        Class<?> loadClass = new URLClassLoader(new URL[]{new File(jar).toURL()}).loadClass(classs);
        return loadClass.getDeclaredMethods();

    }

    private List<String> getUserImportClases(String code) {
        String importRegexp = "import (.*);";
        Pattern pattern = Pattern.compile(importRegexp);
        Matcher matcher = pattern.matcher(code);
        List<String> imports = new ArrayList<>();

        while (matcher.find()) imports.add(matcher.group(1));
        return imports;
    }

    private List<String> getUserClasses(String code) {
        String regexp = "(.*) (.*)=";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(code);
        List<String> classes = new ArrayList<>();

        while (matcher.find()) {
            String classs = matcher.group(1).trim().split(" ")[0].trim().split("<")[0];
            classes.add(classs);
        }
        return classes;
    }
}
