package guessmarket.client.main;

import guessmarket.client.component.main.AppMainController;
import guessmarket.client.util.http.HttpClientUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static guessmarket.client.util.Constants.APP_MAIN_FXML_RESOURCE_LOCATION;

public class GuessMarketClient extends Application {

    private AppMainController appMainController;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(560);
        primaryStage.setMinWidth(820);
        primaryStage.setTitle("Guess Market");

        URL mainPage = getClass().getResource(APP_MAIN_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(mainPage);
            Parent root = fxmlLoader.load();
            appMainController = fxmlLoader.getController();

            Scene scene = new Scene(root, 1150, 720);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (appMainController != null) {
            appMainController.close();
        }
        HttpClientUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
