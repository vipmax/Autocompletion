import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import sun.nio.ch.ThreadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class RootLayoutController implements Initializable {
    @FXML
    private SplitPane splitPane;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TreeView<CustomItem> treeView;
    @FXML
    private TextArea textArea;



    Image folderImage = new Image(getClass().getResourceAsStream("folder.png"));

    Thread savingThread = new Thread(() -> {
        while (true) {
            try {
                File file = treeView.getSelectionModel().getSelectedItem().getValue().getFile();
                String text = textArea.getText();
                try {
                    System.out.println("Write String To File");
                    FileUtils.writeStringToFile(file, text);
                } catch (IOException e) { }
            } catch (Exception e) { }
            try {
                System.out.println("Sleep");
                Thread.sleep(10000000);
            } catch (InterruptedException e) { System.out.println("Interrupt"); }
        }
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        savingThread.setDaemon(true);
        savingThread.start();
        textArea.textProperty().addListener((ov, t, t1) -> {
            System.out.println("Changed.");
            savingThread.interrupt();
        });
        treeView.setRoot(new TreeItem<>(new CustomItem("projects", null), new ImageView(folderImage)));
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


}
