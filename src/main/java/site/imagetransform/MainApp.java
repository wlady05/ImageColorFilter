/*
 * (c) 2015 wlady
 */

package site.imagetransform;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.application.Application;

public class MainApp extends Application {
    private SceneController sceneController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));

        Parent root = fxmlLoader.load();

        sceneController = fxmlLoader.getController();

        Scene scene;

        scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        sceneController.stop();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     *
     * <p>
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args
     * the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
