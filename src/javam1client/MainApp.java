/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1client;

import java.awt.Dimension;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javam1client.controller.InterfaceFXMLController;
import static javam1client.controller.InterfaceFXMLController.socket;

/**
 *
 * @author Mauyz
 */
public class MainApp extends Application {
    
    public static Stage fen;
    public static double width, height;
    
    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root = FXMLLoader.load(getClass().getResource("view/InterfaceClientFXML.fxml"));
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(screen.width - 500,screen.height-200);
        width = size.width ; 
        height = size.height;
        stage.initStyle(StageStyle.UNDECORATED);
        root.setPrefSize(width, height);
        Scene scene = new Scene(root,width,height);
        fen = stage;
        stage.setScene(scene);
        stage.setOnCloseRequest((WindowEvent event) -> {
            close();
        });
        stage.show();
    }
    
    public static void close(){
        InterfaceFXMLController.etat = false;
        if(socket != null){
            try {
                socket.close();
            } catch (IOException ex) {}
        }
        Platform.exit();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
