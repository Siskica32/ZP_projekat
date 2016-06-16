/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaFX;

import Bean.CertificateWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Tijana
 */
public class StartClass extends Application {

    BorderPane stranica = new BorderPane();
    public CertificateWrapper selektovani;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1300, 800, Color.WHITE);

        //menuBar
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);
        Menu keyMenu = new Menu("Key");
        MenuItem generateMenuItem = new MenuItem("Generate");
        MenuItem importMenuItem = new MenuItem("Import");
        MenuItem exportMenuItem = new MenuItem("Export");
        generateMenuItem.setOnAction(actionEvent -> {
            generateKeys(primaryStage);
        });
        keyMenu.getItems().addAll(generateMenuItem, importMenuItem, exportMenuItem);
        Menu certMenu = new Menu("Certificate");
        MenuItem csrMenuItem = new MenuItem("CSR");
        MenuItem signMenuItem = new MenuItem("Sign");
        MenuItem generateCertMenuItem = new MenuItem("Generate");
        certMenu.getItems().addAll(csrMenuItem, signMenuItem, generateCertMenuItem);
        menuBar.getMenus().addAll(keyMenu, certMenu);

        TableView table = new TableView();

        TableColumn keySize = new TableColumn("Key Size");
        keySize.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("keySizeString"));

        TableColumn startDate = new TableColumn("Start Date");
        startDate.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("startDateString"));

        TableColumn expiryDate = new TableColumn("Expiry Date");
        expiryDate.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("expiryDateString"));
        
        TableColumn serialNumber = new TableColumn("Serial Number");
        serialNumber.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("serialNumberString"));
        
        TableColumn commonName = new TableColumn("Common Name");
        commonName.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("cnString"));
        
        TableColumn organizationUnitName = new TableColumn("Organization Unit Name");
        organizationUnitName.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("ouString"));
        
        TableColumn organizationName = new TableColumn("Organization  Name");
        organizationName.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("oString"));
        
        TableColumn localityName = new TableColumn("Locality  Name");
        localityName.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("lString"));
        
        TableColumn stateName = new TableColumn("State  Name");
        stateName.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("stString"));
        
        TableColumn countryName = new TableColumn("Country  Name");
        countryName.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("cString"));
        
        TableColumn basicConstraint = new TableColumn("Basic Constraint");
        basicConstraint.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("basicConstraintString"));
        
        TableColumn basicConstraintPath = new TableColumn("Basic Constraint Path");
        basicConstraintPath.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("basicConstraintPathString"));
        
        TableColumn alternativeNames = new TableColumn("Alternative Names");
        alternativeNames.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("alternativeNameString"));
        
        TableColumn keyUsage = new TableColumn("Key Usage");
        keyUsage.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("keyUsageString"));
        
        TableColumn isSign = new TableColumn("Is Sign");
        isSign.setCellValueFactory(new PropertyValueFactory<CertificateWrapper, String>("isSignString"));

        
        table.getColumns().addAll(keySize, startDate, expiryDate, serialNumber, commonName, organizationUnitName, organizationName, localityName, stateName, countryName, basicConstraint, keyUsage, isSign);
        root.setCenter(table);
        
        ObservableList<CertificateWrapper>  kljucevi = FXCollections.observableArrayList();
        CertificateWrapper cw = new CertificateWrapper();
        cw.setKeySizeString("1024");
        cw.setStartDateString("2016-06-16");
        cw.setExpiryDateString("2016-06-18");
        cw.setSerialNumberString("123456");
        cw.setCnString("Dragance");
        cw.setOuString("Opatija");
        cw.setOString("Opatija");
        cw.setlString("Pakao");
        cw.setStString("Beograd");
        cw.setcString("Srbija");
        cw.setBasicConstraintString("true");
        cw.setAlternativeNameString("CN=");
        cw.setKeyUsageString("1");
        cw.setIsSignString("false");
        kljucevi.add(cw);
        
        table.setItems(kljucevi);
        
         table.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if (table.getSelectionModel().getSelectedItem() != null) {
                selektovani = (CertificateWrapper) table.getSelectionModel().getSelectedItem();
                
            }
        });

        
        
        primaryStage.setTitle("Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void generateKeys(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1300, 800, Color.WHITE);

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        // File menu - new, save, exit
        Menu keyMenu = new Menu("Key");
        MenuItem generateMenuItem = new MenuItem("Generate");
        MenuItem importMenuItem = new MenuItem("Import");
        MenuItem exportMenuItem = new MenuItem("Export");

        generateMenuItem.setOnAction(actionEvent -> {
            generateKeys(primaryStage);
        });

        keyMenu.getItems().addAll(generateMenuItem, importMenuItem, exportMenuItem);

        Menu certMenu = new Menu("Certificate");
        MenuItem csrMenuItem = new MenuItem("CSR");
        MenuItem signMenuItem = new MenuItem("Sign");
        MenuItem generateCertMenuItem = new MenuItem("Generate");

        certMenu.getItems().addAll(csrMenuItem, signMenuItem, generateCertMenuItem);

        menuBar.getMenus().addAll(keyMenu, certMenu);
        primaryStage.setTitle("Generate Keys");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
