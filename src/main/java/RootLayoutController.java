import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public class RootLayoutController {

    @FXML
    private TreeView<CustomItem> treeView;
    @FXML
    private TextArea textArea;
    private LinkedHashMap listFiles;

    @FXML
    private void createFile() {
    }

    private void addFileToTreeView (File file) {
        TreeItem<String> root = new TreeItem<String>(file.getName());
        root.setExpanded(true);
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
        TreeItem<CustomItem> root = new TreeItem<>(new CustomItem(dir.getName(), dir));
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

    public void onKeyPressed(Event event) throws IOException {
        showSelectedFile();
    }

    private void showSelectedFile() throws IOException {
        TreeItem<CustomItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue().getFile().isFile())
            textArea.setText(FileUtils.readFileToString(selectedItem.getValue().getFile()));
    }
}
