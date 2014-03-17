package org.grirefx;

/**
 * Author: Lazaros Tsochatzidis
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }


    org.grirefx.MainController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root =(Parent) loader.load();
        controller=loader.getController();


        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("GRire - Golden Retriever: an opensource Image Retrieval Engine");
        stage.show();
        controller.insertShutdownPrevention();
    }
}
