import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import java.io.File;
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


    private void findFiles(File dir, TreeItem<File> parent) {
        TreeItem<File> root = new TreeItem<>(dir);
        root.setExpanded(false);
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

    @FXML
    private void onClicked() throws IOException {
        TreeItem<File> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue().isFile())
            textArea.setText(FileUtils.readFileToString(selectedItem.getValue()));
    }


}
