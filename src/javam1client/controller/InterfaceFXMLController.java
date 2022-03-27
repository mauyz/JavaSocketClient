/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1client.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.util.Duration;
import javam1client.MainApp;
import javam1client.model.Client;
import javam1client.model.GetService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author Mauyz
 */

public class InterfaceFXMLController implements Initializable {
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ToolBar mainToolbar;
    @FXML
    private Button windowMin;
    @FXML
    private Button windowMax;
    @FXML
    private Button windowClose;
    @FXML
    private ToolBar menuToolbar;
    @FXML
    private Label etatLbl;
    @FXML
    private AnchorPane settingPane;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField jsonTxt;
    @FXML
    private Button applyBtn;
    @FXML
    private Button defaultBtn;
    @FXML
    private TextField numberTxt;
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField adressTxt;
    @FXML
    private Button updateBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private TextField moneyTxt;
    @FXML
    private Button addBtn;
    @FXML
    private AnchorPane crudPane;
    @FXML
    private Button refreshBtn;
    @FXML
    private Label reponseLbl;
    @FXML
    private TextField serverIP;
    @FXML
    private Region viel;
    @FXML
    private ProgressIndicator prog;
    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableColumn<Client, Integer> numberCol;
    @FXML
    private TableColumn<Client, String> nameCol;
    @FXML
    private TableColumn<Client, String> adressCol;
    @FXML
    private TableColumn<Client, Integer> moneyCol;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */ 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        checkConfig();
        connect();
        completeView();
        service.start();
        //System.out.println(service.getState());*/
    }
    
    private void checkConfig(){
        config = new File("socketclient.conf");
                if (config.exists()){
            try {
                try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                    while(in.ready()){
                        String line = in.readLine();
                        switch(line.substring(0,line.indexOf("=")).trim()){
                            case "IP":{
                                serverIP.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            case "Port":{
                                serverPort.setText(line.substring(line.indexOf("=")+1).trim());
                                break;
                            }
                            default:{
                                jsonTxt.setText(line.substring(line.indexOf("=")+2));
                                break;
                            }
                        }
                    }
                }
            } catch (FileNotFoundException  ex) {
                Log = Log + ex.getMessage()+"\n";
            } catch (IOException ex) {
                Log = Log + ex.getMessage()+"\n";               
            }
        }
        else {
            try {
                try (FileWriter fw = new FileWriter(config)) {
                    String str = "IP = "+ serverIP.getText()+"\n"+
                            "Port = "+serverPort.getText()+"\n"+
                            "JsonFile = "+jsonTxt.getText();
                    fw.write(str);
                }
            } catch (IOException ex) {
                Log = Log + ex.getMessage()+"\n";
            }
        }
        pathJson = jsonTxt.getText();
        jsonFile = new File(pathJson);
    }
    
    private void connect(){
        ipServer = serverIP.getText();
        port = Integer.valueOf(serverPort.getText());
        new Thread(new Connexion()).start();
    }
    
    private void handleServerMesssage(String msg){
        recu = JSONObject.fromObject(msg);
        if(recu.has("Client list")){
            list = recu.getJSONArray("Client list");
        }
        if(recu.has("Retour")){
            if(recu.getString("Retour").contains("not")) error = true;
            printResult(recu.getString("Retour"));
       }
    }
    
    private void initTable(){
        JSONObject select = new JSONObject();
        select.put("Type", "Select");
        send.println(select);
        send.flush();    
    }
    
    private void showNotifError(String error){
       Notifications.create().owner(crudPane).darkStyle().hideAfter(Duration.seconds(3)).position(Pos.CENTER_RIGHT).text(error).showError();
    }
    
    private void completeView(){
        clientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        mainToolbar.getItems().add(spacer1);
        ImageView logo = new ImageView(new Image(MainApp.class.getResourceAsStream("view/images/logo.png")));
        mainToolbar.getItems().add(logo);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        menuToolbar.getItems().add(spacer2);
        Button settingBtn = new Button();
        settingBtn.setId("settingBtn");
        settingBtn.setTooltip(new Tooltip("Settings"));
        settingBtn.setGraphic(new ImageView(new Image(MainApp.class.getResourceAsStream("view/images/settings.png"))));
        settingBtn.setOnAction((ActionEvent event) -> {
            checkConfig();
            if(settingPane.isVisible()){
                crudPane.setVisible(true);
                settingPane.setVisible(false);
            }
            else {
                crudPane.setVisible(false);
                settingPane.setVisible(true);
            }
        });
        menuToolbar.getItems().add(settingBtn);
        
        numberCol.setCellValueFactory(new PropertyValueFactory("numero"));
        nameCol.setCellValueFactory(new PropertyValueFactory("nom"));
        adressCol.setCellValueFactory(new PropertyValueFactory("adresse"));
        moneyCol.setCellValueFactory(new PropertyValueFactory("solde"));
        clientTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Client> observable, Client oldValue, Client newValue) -> {
            showClientForm(newValue);
        });
        
        viel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
        service = new GetService(list);
        prog.progressProperty().bind(service.progressProperty());
        viel.visibleProperty().bind(service.runningProperty());
        prog.visibleProperty().bind(service.runningProperty());
        clientTable.itemsProperty().bind(service.valueProperty());        
    }
    
    private void showClientForm(Client c){
       if(c==null) {
            clearForm();
        }
       else{
        moneyTxt.setText(String.valueOf(c.getSolde())); 
        numberTxt.setText(String.valueOf(c.getNumero()));
        nameTxt.setText(c.getNom());
        adressTxt.setText(c.getAdresse());
        moneyTxt.setText(String.valueOf(c.getSolde()));
       }        
    }
    
    private void clearForm(){
        numberTxt.setText("");
        nameTxt.setText("");
        adressTxt.setText("");
        moneyTxt.setText("");
    }
    
    private void maximizeWindow() {        
        final Screen screen = Screen.getScreensForRectangle(MainApp.fen.getX(), MainApp.fen.getY(), 1, 1).get(0);
        if (maximized) {
            maximized = false;
            if (backupWindowBounds != null) {
                MainApp.fen.setX(backupWindowBounds.getMinX());
                MainApp.fen.setY(backupWindowBounds.getMinY());
                MainApp.fen.setWidth(backupWindowBounds.getWidth());
                MainApp.fen.setHeight(backupWindowBounds.getHeight());
            }
        } else {
            maximized = true;
            backupWindowBounds = new Rectangle2D(MainApp.fen.getX(), MainApp.fen.getY(), MainApp.fen.getWidth(), MainApp.fen.getHeight());
            MainApp.fen.setX(screen.getVisualBounds().getMinX());
            MainApp.fen.setY(screen.getVisualBounds().getMinY());
            MainApp.fen.setWidth(screen.getVisualBounds().getWidth());
            MainApp.fen.setHeight(screen.getVisualBounds().getHeight());
        }
    }
    
    private void sendFileContent(){
        String newContent = "";
        try (LineNumberReader in = new LineNumberReader(new FileReader(jsonFile))){
            while(in.ready()){
                error = false;
                String line = in.readLine();
                send.println(line);
                send.flush();
                if(error) {
                    System.out.println("Not");
                    newContent +=line+"\n";
                }
            }
            }catch (IOException ex){
                showNotifError(ex.getMessage());
            }
        try{
            if(newContent.equals("")){
                Files.delete(Paths.get(jsonFile.getAbsolutePath()));
            }
            else{
               FileWriter f = new FileWriter(jsonFile);
               f.write("");
               f.write(newContent);
            }
        }catch(IOException ex){
            showNotifError(ex.getMessage());
        }
        
        Platform.runLater(() -> {
            refreshAction(null);
        });
    }
    
    private void printResult(String res){
       Platform.runLater(() -> {
            reponseLbl.setText(res);
        });
    }
    
    @FXML
    private void windowMinAction(ActionEvent event) {
        MainApp.fen.setIconified(true);
    }
    @FXML
    private void windowMaxAction(ActionEvent event) {
        maximizeWindow();
    }
    @FXML
    private void windowCloseAction(ActionEvent event) {
        MainApp.close();
    }
    @FXML
    private void mainToolbarDraggedAction(MouseEvent event) {
        if(!maximized) {
             MainApp.fen.setX(event.getScreenX()- x);
             MainApp.fen.setY(event.getScreenY()- y);
         }
    }
    @FXML
    private void mainToolbarClickedAction(MouseEvent event) {
        if(event.getClickCount() == 2)
            maximizeWindow();
    }
    @FXML
    private void mainToolbarPressedAction(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
    @FXML
    private void applyAction(ActionEvent event) {
        if(serverPort.getText().equals("") || serverIP.getText().equals("")|| jsonTxt.getText().equals("")){
            Notifications.create().owner(settingPane).text("Invalid entry !").hideAfter(Duration.seconds(5)).darkStyle().position(Pos.CENTER_RIGHT).showWarning();
           }
        else {
            if(!config.exists())checkConfig();
            try {
                String newContent;
                try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                    newContent = "";
                    while(in.ready()){
                        String line = in.readLine();
                        switch(line.substring(0,line.indexOf("=")).trim()){
                            case "IP":{
                                newContent += "IP = "+serverIP.getText().trim()+"\n";
                                break;
                            }
                            case "Port":{
                                newContent += "Port = "+serverPort.getText().trim()+"\n";
                                break;
                            }
                            default:{
                                newContent += "JsonFile = "+jsonTxt.getText();
                                break;
                            }
                        }
                    }
                    in.close();
                }
               try (FileWriter fw = new FileWriter(config)) {
                    fw.write("");
                    fw.write(newContent);
                }
                if(!ipServer.equals(serverIP.getText()) || port != Integer.valueOf(serverPort.getText())){
                    if(socket != null){
                        socket.close();
                    }
                    ipServer = serverIP.getText();
                    port = Integer.valueOf(serverPort.getText());
                }
                } catch (IOException ex) {
                   showNotifError(ex.getMessage());
                }
        }
    }
    @FXML
    private void defaultAction(ActionEvent event) {
        if(!config.exists())checkConfig();
            try {
                String newContent;
                try (LineNumberReader in = new LineNumberReader(new FileReader(config))) {
                    newContent = "";
                    while(in.ready()){
                        String line = in.readLine();
                        switch(line.substring(0,line.indexOf("=")).trim()){
                            case "IP":{
                                newContent += "IP = 127.0.0.1\n";
                                break;
                            }
                            case "Port":{
                                newContent += "Port = 4000\n";
                                break;
                            }
                            default:{
                                newContent += "JsonFile = jsonData.json";
                                break;
                            }
                        }
                    }
                    in.close();
                }
               try (FileWriter fw = new FileWriter(config)) {
                    fw.write("");
                    fw.write(newContent);
                }
               serverIP.setText("127.0.0.1");
               serverPort.setText("4000");
               jsonTxt.setText("jsonData.json");
                if(!ipServer.equals("127.0.0.1") || port != 4000){
                    if(socket != null)socket.close();
                    ipServer = serverIP.getText();
                    port = Integer.valueOf(serverPort.getText());
                }
                } catch (IOException ex) {
                   showNotifError(ex.getMessage());
                }
    }
    @FXML
    private void updateAction(ActionEvent event) {
        if(numberTxt.getText().isEmpty() || nameTxt.getText().isEmpty() || adressTxt.getText().isEmpty() || moneyTxt.getText().isEmpty()){
            showNotifError("Invalid entry");
        }
        else{
            Integer num = Integer.valueOf(numberTxt.getText());
            String nom = nameTxt.getText();
            String adr = adressTxt.getText();
            Integer solde = Integer.valueOf(moneyTxt.getText());
            JSONObject update = new JSONObject();
            update.put("Type", "Update");
            update.put("numero",num);
            update.put("nom", nom);
            update.put("adresse", adr);
            update.put("solde", solde);
            if(etatLbl.getText().equals("Online")){
                send.println(update);
                send.flush();
                sent = true;
                refreshAction(null);
            }
            else{
                String newContent = "";
                if(jsonFile.exists()){
                    try (LineNumberReader in = new LineNumberReader(new FileReader(jsonFile))){
                        while(in.ready()){
                            String line = in.readLine();
                            newContent += line+"\n";
                        }
                    }catch (IOException ex){
                        showNotifError(ex.getMessage());
                    }
                }
                newContent += update.toString();
                try (FileWriter fw = new FileWriter(jsonFile)) {
                    fw.write("");
                    fw.write(newContent);
                }catch (IOException ex){
                    showNotifError(ex.getMessage());
                }
                for(int j = 0; j < list.size(); j++){
                    JSONObject client = JSONObject.fromObject(list.get(j));
                    if(client.getInt("numero") == num){
                        client.put("nom", nom);
                        client.put("adresse", adr);
                        client.put("solde", solde);
                        list.set(j, client);
                    }
                }
                printResult("Local update successfull");
                refreshAction(null);
            }
        }
    }
    @FXML
    private void addAction(ActionEvent event) {
        if(nameTxt.getText().isEmpty() || adressTxt.getText().isEmpty() || moneyTxt.getText().isEmpty()){
            showNotifError("Invalid entry");
        }
        else{
            String nom = nameTxt.getText();
            String adr = adressTxt.getText();
            Integer solde = Integer.valueOf(moneyTxt.getText());
            JSONObject insert = new JSONObject();
            insert.put("Type", "Insert");
            insert.put("nom", nom);
            insert.put("adresse", adr);
            insert.put("solde", solde);
            if(etatLbl.getText().equals("Online")){
                send.println(insert);
                send.flush();
                sent = true;
                refreshAction(null);
            } 
            else{
                String newContent = "";
                if(jsonFile.exists()){
                    try (LineNumberReader in = new LineNumberReader(new FileReader(jsonFile))){
                        while(in.ready()){
                            String line = in.readLine();
                            newContent += line+"\n";
                        }
                    }catch (IOException ex){
                        showNotifError(ex.getMessage());
                    }
                }
                newContent += insert.toString();
                try (FileWriter fw = new FileWriter(jsonFile)) {
                    fw.write("");
                    fw.write(newContent);
                }catch (IOException ex){
                    showNotifError(ex.getMessage());
                }
                JSONObject newClient = new JSONObject();
                newClient.put("numero",0);
                newClient.put("nom", nom);
                newClient.put("adresse",adr);
                newClient.put("solde", solde);
                list.add(newClient);
                printResult("Local insertion successfull");
                refreshAction(null);
            }
        }
    }
    @FXML
    private void refreshAction(ActionEvent event) {
        if(etatLbl.getText().equals("Online")){
            initTable();
        }
        if(sent){
            try
            {
                Thread.sleep(1000);
            }catch(InterruptedException e){}
        }
        sent = false;
        reponseLbl.setText("");
        service.setArray(list);
        service.restart();
    }
    @FXML
    private void deleteAction(ActionEvent event) {
        if(nameTxt.getText().isEmpty() || adressTxt.getText().isEmpty() || moneyTxt.getText().isEmpty()){
            showNotifError("Invalid entry");
        }
        else{
            Alert alert = new Alert(
                                Alert.AlertType.CONFIRMATION,"Do you really delete this person ??",
                                        new ButtonType("Yes", ButtonBar.ButtonData.YES),
                                                    new ButtonType("No", ButtonBar.ButtonData.OK_DONE));
            alert.setHeaderText(null);
            alert.setX(MainApp.fen.getX() + clientTable.getWidth()-100);
            alert.setY(y + MainApp.height/2);
            alert.showAndWait();
            if(alert.getResult().getButtonData() == ButtonBar.ButtonData.YES){
                Integer num = Integer.valueOf(numberTxt.getText());
                JSONObject delete = new JSONObject();
                delete.put("Type", "Delete");
                delete.put("numero", num);
                if(etatLbl.getText().equals("Online")){
                    send.println(delete);
                    send.flush();
                    sent = true;
                    refreshAction(null);
                }
                else{
                    String newContent = "";
                    if(jsonFile.exists()){
                        try (LineNumberReader in = new LineNumberReader(new FileReader(jsonFile))){
                            while(in.ready()){
                                String line = in.readLine();
                                newContent += line+"\n";
                            }
                        }catch (IOException ex){
                            showNotifError(ex.getMessage());
                        }
                    }
                    newContent += delete.toString();
                    try (FileWriter fw = new FileWriter(jsonFile)) {
                        fw.write("");
                        fw.write(newContent);
                    }catch (IOException ex){
                        showNotifError(ex.getMessage());
                    }
                    for(int j = 0; j < list.size(); j++){
                        JSONObject client = JSONObject.fromObject(list.get(j));
                        if(client.getInt("numero") == num){
                            list.remove(j);
                        }
                    }
                    printResult("Local delete successfull");
                    refreshAction(null);
                }
            } 
        }
    }

    @FXML
    private void selectTable(KeyEvent event) {
       if(event.getCode() == KeyCode.UP || 
               event.getCode() == KeyCode.DOWN)
           showClientForm(clientTable.getSelectionModel().getSelectedItem());
    }
    
    public class Connexion implements Runnable{
        
        private Boolean connected = false;
        private String line;
        
        @Override
        public void run() {
            while(etat){
                if(!connected){
                    Platform.runLater(() -> {
                        etatLbl.setText("Offline");
                    });
                    try{
                        socket = new Socket(ipServer,port);
                        come = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        send = new PrintWriter(socket.getOutputStream());
                        if(come.readLine().equals("JavaM1")){
                            connected = true;
                            if(list.isEmpty())initTable();
                            Platform.runLater(() -> {
                                etatLbl.setText("Online");
                                sent = true;
                                refreshAction(null);
                            });
                        }
                        else {
                            socket.close();
                            connected = false;
                            continue;
                        }
                    }catch(UnknownHostException ex){
                        continue;
                    }catch (IOException ex) {
                        continue;
                    }
                }
                try {
                    line = come.readLine();
                    if(line == null){
                        socket.close();
                        connected = false;
                    }
                    else{
                        if(jsonFile.exists()){
                            sendFileContent();
                            sent = true;
                        }
                        handleServerMesssage(line);
                    }
                }catch (IOException ex) {
                    connected = false;
                }
            }        
        }
    }
    
    public static Socket socket = null;
    public static String Log = "", ipServer, pathJson;
    public static int port;
    public static Boolean etat = true , error = false , sent = false;
    
    double x, y;
    boolean maximized = false;
    private Rectangle2D backupWindowBounds = null;
    GetService service;
    File jsonFile;
    File config;
    JSONArray list = new JSONArray();
    JSONObject recu, envoi, clients;
    private PrintWriter send = null;
    private BufferedReader come = null;
    
}
        


