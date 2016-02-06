import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by max on 06.02.16.
 */
public class Windows {
    private Stage stage;
    private Stage dialogStage;

    public Windows() {

    }

    public void showChooseDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("ChooseDialog.fxml"));
        AnchorPane page = null;
        try {
            page = (AnchorPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create the dialog Stage.
        dialogStage = new Stage();
        dialogStage.setTitle("Choose Method");
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initOwner(stage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        dialogStage.show();
    }
}
