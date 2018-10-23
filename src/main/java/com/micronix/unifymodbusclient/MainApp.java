package com.micronix.unifymodbusclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class MainApp extends Application {
    

    private  File fileProperties = new File("properties.properties");
    @Override
    public void start(Stage stage) throws Exception {

       if( !Character.isLetter(new File(".").getAbsoluteFile().getPath().charAt(0)) )
       {
            Alert alert = new Alert(AlertType.WARNING,"", new ButtonType("Ок",ButtonData.YES));
            alert.setHeaderText("ВНИМАНИЕ! Программа частенко виснет при выпорлнении из сетевой папки.");
            alert.setContentText("Скопируйте на локальную ФС, что бы не иметь проблем....");
            alert.showAndWait();
       }

 

            
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        Parent root = loader.load();
        FXMLController cntrl = loader.getController();
        Properties properties = new Properties();
        try{
             properties.load(new FileInputStream(fileProperties)); 
             cntrl.setProperties(properties);
        }catch(Exception ex){
           Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, ex.getMessage());   
        }
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("unifyModbusClient");
        cntrl.setFsetTitle( stage::setTitle );
        stage.setScene(scene);
        stage.setOnCloseRequest((evnt)->{
           try {
               properties.store(new FileOutputStream(fileProperties), null);
           } catch (IOException ex) {
               Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, "Не удалось сохранить павраметры" + ex.getMessage());   
           }
           if(cntrl.getIsEdited()){
            Alert alert = new Alert(AlertType.WARNING,"Изменения будут утеряны...", new ButtonType("Да",ButtonData.YES), new ButtonType("Нет", ButtonData.NO),new ButtonType("Отмена", ButtonData.CANCEL_CLOSE));
            alert.setHeaderText("ВНИМАНИЕ! Сохранить изменения?");
            alert.setContentText("Если НЕТ изменения будут утеряны...");
            Optional<ButtonType> showAndWait = alert.showAndWait();
    
            if(showAndWait.get().getButtonData()== ButtonData.YES){
                 File f = cntrl.getOpenedFile();
                try {
                    if(f == null || !f.canWrite() ){
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setInitialDirectory(new File(".").getAbsoluteFile());
                        f = fileChooser.showSaveDialog(scene.getWindow());
                    }  
                    if(f == null || !f.canWrite() ){
                     evnt.consume();
                    }
                    FileWriter writer = new FileWriter(f);
                    writer.write( cntrl.gson.toJson(cntrl.getRoot()) );//writer.write(treeItemdata2GsonObject.gson.toJson(jSonObject));
                    writer.flush();
                } catch (IOException ex) {
                    evnt.consume();
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
               
            }
            if(showAndWait.get().getButtonData() == ButtonData.CANCEL_CLOSE){
                evnt.consume();
            }
           }
        });
          stage.getIcons().add(new Image( getClass().getResourceAsStream( "/logo.png" )));
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
 

    public static void main(String[] args) {

      System.out.println(  System.getProperty("property1") );  
        launch(args);
    }

}
