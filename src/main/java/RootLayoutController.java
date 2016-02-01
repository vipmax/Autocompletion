import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

public class RootLayoutController {

    @FXML
    private TreeView<File> treeView;
    @FXML
    private TextArea textArea;
    private LinkedHashMap listFiles;

    @FXML
    private void createFile() {
    }

    private void addFileToTreeView (File file) {
        TreeItem<String> root = new TreeItem<String>(file.getName());
        root.setExpanded(true);

//        treeView.setRoot(root);
    }

    private void addFilesToTreeView (File file) {

//        TreeItem<String> root = new TreeItem<String>("Root Node");
//        root.setExpanded(true);
//        for (String itemString: rootItems) {
//            root.getChildren().add(new TreeItem<String>(itemString));
//        }
//
//        treeView.setRoot(root);
    }


    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("File Selection");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            System.out.println("Процесс открытия файла");

            addFileToTreeView(file);

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String text = null;
                while ((text = reader.readLine()) != null) {
                    textArea.appendText(text+"\n");
                    System.out.println(text);
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    @FXML
    private void openFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

//        treeView.
        if(selectedDirectory == null){
            System.out.println("No Directory selected");
        }else{

            findFiles(selectedDirectory,treeView.getRoot());
//
        }

    }


    private void findFiles(File dir, TreeItem<File> parent) {
        TreeItem<File> root = new TreeItem<>(dir);
        root.setExpanded(true);
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files)
            if (file.isDirectory()) findFiles(file, root);
            else root.getChildren().add(new TreeItem<>(file));

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

}
