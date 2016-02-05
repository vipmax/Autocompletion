import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootLayoutController implements Initializable {
    @FXML
    private SplitPane splitPane;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TreeView<CustomItem> treeView;
    @FXML
    private TextArea textArea;


    AutoComplete autoComplete = new AutoComplete();
    Map<String, String> mapReferenceAndClass = new HashMap<>();

    Image folderImage = new Image(getClass().getResourceAsStream("folder.png"));

    Thread savingThread = new Thread(() -> {
        while (true) {
            try {
                File file = treeView.getSelectionModel().getSelectedItem().getValue().getFile();
                String text = textArea.getText();
                try {
//                    System.out.println("Write String To File");
                    FileUtils.writeStringToFile(file, text);
                } catch (IOException e) { }
            } catch (Exception e) { }
            try {
//                System.out.println("Sleep");
                Thread.sleep(10000000);
            } catch (InterruptedException e) { /*System.out.println("Interrupt");*/ }
        }
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        savingThread.setDaemon(true);
        savingThread.start();
        textArea.textProperty().addListener((ov, t, t1) -> {
//            System.out.println("Changed.");
            savingThread.interrupt();
        });
        treeView.setRoot(new TreeItem<>(new CustomItem("projects", null), new ImageView(folderImage)));

        new Thread(() -> {
            try {
                autoComplete.init();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @FXML
    private void createFile() {
    }

    private void addFileToTreeView (File file) {
        TreeItem<String> root = new TreeItem<String>(file.getName());
        root.setExpanded(true);
    }

    @FXML
    private void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("File Selection");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            addFileToTreeView(file);
            String code = FileUtils.readFileToString(file);
            textArea.setText(code);
        }
    }

    @FXML
    private void openFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("../"));
        File selectedDirectory = directoryChooser.showDialog(null);

        if(selectedDirectory == null){
            System.out.println("No Directory selected");
        }else{

            findFiles(selectedDirectory,treeView.getRoot());
            treeView.setShowRoot(true);
            treeView.getRoot().setExpanded(false);

        }

    }


    private void findFiles(File dir, TreeItem<CustomItem> parent) {
        TreeItem<CustomItem> root = new TreeItem<>((new CustomItem(dir.getName(), dir)), new ImageView(folderImage));
        root.setExpanded(false);
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files)
            if (file.isDirectory()) findFiles(file, root);
            else root.getChildren().add(new TreeItem<>(new CustomItem(file.getName(), file)));

        if(parent==null) treeView.setRoot(root);
        else parent.getChildren().add(root);
    }

    @FXML
    private void saveFile() {
    }

    @FXML
    private void saveAllFiles() {
    }

    @FXML
    private void saveAs() {
    }

    @FXML
    private void delete() {
    }

    @FXML
    private void exitFromProgram() {
        System.exit(0);
    }

    @FXML
    private void onClicked() throws IOException {
        showSelectedFile();
    }

    public void onKeyPressed(KeyEvent event) throws IOException {
        if(event.getCode()==KeyCode.ENTER) showSelectedFile();
    }

    private void showSelectedFile() throws IOException {
        try {
            TreeItem<CustomItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue().getFile().isFile())
                textArea.setText(FileUtils.readFileToString(selectedItem.getValue().getFile()));
        }catch (Exception e){}
    }

    public void onRun(ActionEvent actionEvent) {
        String main = treeView.getSelectionModel().getSelectedItem().getValue().getFile()
                .getAbsolutePath().split("java")[1].replace("/", ".");
        String mainClass = main.substring(1, main.length() - 1);
        String command = "mvn exec:java -Dexec.mainClass=" + mainClass;
        System.out.println("command = " + command);
        outputTextArea.setText("");
        splitPane.setDividerPosition(0, 0.7);
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line + "\n");
                outputTextArea.appendText(line + "\n");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onKeyPressedTextArea(KeyEvent keyEvent) {
        if (keyEvent.getCode() != KeyCode.PERIOD) return;

        String ref = getCurrentRef();
        if (ref==null) return;

        String code = textArea.getText();
        Map<String, String> userClassesAndReferences = getUserClassesAndReferences(code);

        if (userClassesAndReferences.keySet().contains(ref)) {
            String classWithoutPackage = userClassesAndReferences.get(ref);

            String classWithPackage = addPackage(code, classWithoutPackage);

            try {
                System.out.println("classWithPackage = " + classWithPackage);
                System.out.println(Arrays.toString(autoComplete.getMethods(classWithPackage)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private String getCurrentRef() {
        String code = textArea.getText();
        int caretPosition = textArea.getCaretPosition();
        String substring = code.substring(0, caretPosition);
        StringBuilder reverse = new StringBuilder(substring).reverse();


        String regexp = "([A-z]+)";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(reverse);

        if(matcher.find()) {
            String revref = matcher.group(1);
            String ref = new StringBuilder(revref).reverse().toString();
            System.out.println("ref = " + ref);
            return ref;
        }
        return null;
    }

    private String addPackage(String code, String classWithoutPackage) {
        System.out.println("classWithoutPackage = " + classWithoutPackage);
        Set<String> userImportClasses = getUserImportClases(code);
        for (String userImportClass : userImportClasses) {
            System.out.println("userImportClass = " + userImportClass);
            String[] split = userImportClass.replace(".", " ").split(" ");
            String userImportType = split[split.length - 1];
            if (userImportType.equals(classWithoutPackage)) {
               return userImportClass;
            }
            else{
                 if(userImportClass.endsWith("*")) {
                     Set<String> keySet = autoComplete.classes.keySet();
                     for (String key : keySet) {
                         if (key.startsWith(userImportClass.replace("*", classWithoutPackage)) & key.endsWith(classWithoutPackage)) {
                             return key;
                         }
                     }
                 }



            }

        }
        return "Not found";
    }


    private Set<String> deleteDublicates(Set<String> userImportClasses, Set<String> userClasses) {
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

    private Set<String> getUserImportClases(String code) {
        String importRegexp = "import (.*);";
        Pattern pattern = Pattern.compile(importRegexp);
        Matcher matcher = pattern.matcher(code);
        Set<String> imports = new HashSet<>();

        while (matcher.find()) imports.add(matcher.group(1));
        return imports;
    }

    private Map<String, String> getUserClassesAndReferences(String code) {
        String regexpForClassAndRef = "([A-z|<|>|,|?| ]+)\\s+([A-z]+)\\s+=\\s+";
        Pattern pattern = Pattern.compile(regexpForClassAndRef);
        Matcher matcher = pattern.matcher(code);



        while (matcher.find()) {
            String reference = matcher.group(2).trim();
            String classs = matcher.group(1).split("<")[0].trim();
            mapReferenceAndClass.put(reference, classs);
        }

        return mapReferenceAndClass;
    }
}
