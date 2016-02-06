import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by max on 05.02.16.
 */
public class ChooseDialogController  {

    @FXML
    public TableView<TableView> methodTable;
    @FXML
    public TableColumn<TableView,String> methodName;
    @FXML
    public TableColumn<TableView,String> methodType;

    public Method[] methods;

    public void closeAutoCompleteWindow(Event event) {

    }

//        ObservableList<TableView> data = FXCollections.observableArrayList(
//                new TableView("1","1")
//        );

//
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        methodName.setCellValueFactory(new PropertyValueFactory<TableView,String>(""));
//        methodType.setCellValueFactory(new PropertyValueFactory<TableView,String>(""));
////        methodTable.setItems(data);
//    }

}
