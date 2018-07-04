package com.micronix.unifymodbusclient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableColumn.CellEditEvent;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLController implements  Initializable {

 /*   @FXML
    private Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
   */ 
    private TreeItem treeItemRoot;
    
    @FXML
    private TreeTableView treeTableView;
    @FXML
    private TreeTableColumn treeTableColumnName;
    @FXML
    private TreeTableColumn treeTableColumnAddr;
    @FXML
    private ContextMenu treeTableViewContextMenu;
    @FXML
    private MenuItem menuItemGroup;
    @FXML
    private MenuItem menuItemWord;
    @FXML
    private TreeTableColumn<?, ?> treeTableColumnAccess;
    @FXML
    private TreeTableColumn<?, ?> treeTableColumnType;
    @FXML
    private TreeTableColumn<?, ?> treeTableColumnVolue;
    @FXML
    private TreeTableColumn<?, ?> treeTableColumnBar;
    @FXML
    private void menuItemGroupOnAction(ActionEvent event) {
        treeItemRoot.getChildren().add(new TreeItem(new MbItem("Новая группа")));
    }
    @FXML
    private void menuItemWordOnAction(ActionEvent event) {
        treeItemRoot.getChildren().add(new TreeItem(new MbItem("Новый элемент","",0,"")));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        treeTableView.setRowFactory(ttv -> {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItemGroup = new MenuItem("Группа");
            MenuItem menuItemWord = new MenuItem("Элемент");
            MenuItem menuItemProperty = new MenuItem("Параметры");
            contextMenu.getItems().addAll(menuItemGroup,menuItemWord,menuItemProperty);
            TreeTableRow<MbItem> row = new TreeTableRow<MbItem>() {
                @Override
                public void updateItem(MbItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(null);
                    } else {
                        // configure context menu with appropriate menu items, 
                        // depending on value of item
                        setContextMenu(contextMenu);
                    }
                }
            };
            menuItemGroup.setOnAction(evt -> {
                TreeItem treeItem = row.getTreeItem();
                treeItem.getChildren().add(new TreeItem(new MbItem("Новая группа")));
            });
            menuItemWord.setOnAction(evt -> {
                TreeItem treeItem = row.getTreeItem();
                treeItem.getChildren().add(new TreeItem(new MbItem("Новый элемент","",0,"word")));
            });
            menuItemProperty.setOnAction(evt -> {
       
                try {
                    // TreeItem treeItem = row.getTreeItem();
                    //  treeItem.getChildren().add(new TreeItem(new MbItem("Новый элемент","",0,"word")));
                    MbItem item = row.getItem();
                    Stage stage = new Stage();
                    Parent root;
                    FXMLLoader  loader = new FXMLLoader(this.getClass().getResource("/fxml/EditForm.fxml"));
                    
                  
                    root = (Parent) loader.load();
                    ((EditFormController)loader.getController()).setMbItem(item);
                    stage.setScene(new Scene(root));
                    stage.setTitle("My modal window");
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner( this.treeTableView.getScene().getWindow());
                    stage.show();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
     

            });
            return row;
        });
        
        treeTableColumnName.setCellValueFactory(new TreeItemPropertyValueFactory<MbItem, String>("name"));
        
        treeTableColumnName.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        
        
        treeTableColumnName.setOnEditCommit(new EventHandler<CellEditEvent<MbItem, String>>(){
            @Override
            public void handle(CellEditEvent<MbItem, String> event) {
               event.getTreeTableView().getEditingCell().getTreeItem().getValue().setName(event.getNewValue());
            }

        } );
        
        treeTableColumnAddr.setCellValueFactory(new TreeItemPropertyValueFactory<MbItem, Integer>("addr"));


        treeItemRoot = new TreeItem();
        treeTableView.setShowRoot(false);
        treeTableView.setRoot(treeItemRoot);
        treeTableView.setEditable(true);

        // TODO
    }

}
