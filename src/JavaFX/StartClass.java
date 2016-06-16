/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaFX;

import Bean.CertificateWrapper;
import Security.FileUtil;
import Security.Generator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.sql.Date;
import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;

/**
 *
 * @author Tijana
 */
public class StartClass extends Application {

    GridPane stranica = new GridPane();
    public CertificateWrapper selektovani = null;
    public int selektovaniId;
    private final String pattern = "yyyy-MM-dd";
    public FileUtil fileUtil;
    private ArrayList<CertificateWrapper> keys;

    public Label keyUsage = new Label("Key Usage?");
    public CheckBox ku = new CheckBox();

    public Label keyUSageIsCritical = new Label("Is Key Usage Critical?");
    public CheckBox kuic = new CheckBox();

    public String password;

    @Override
    public void start(Stage primaryStage) {
        keys = new ArrayList<>();
        pocetna(primaryStage);
    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("P12", "*.p12")
        );
    }

    public void pocetna(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1340, 600, Color.WHITE);

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
        importMenuItem.setOnAction(actionEvent -> {

            fileUtil = new FileUtil();
            FileChooser fileChooser = new FileChooser();
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Type password");

                ButtonType next = new ButtonType("Next", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(next, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                PasswordField pass = new PasswordField();
                pass.setPromptText("Password");

                grid.add(new Label("Password:"), 0, 1);
                grid.add(pass, 1, 1);

                Node nextButton = dialog.getDialogPane().lookupButton(next);
                nextButton.setDisable(true);

                pass.textProperty().addListener((observable, oldValue, newValue) -> {
                    nextButton.setDisable(newValue.trim().isEmpty());
                });

                dialog.getDialogPane().setContent(grid);

                Platform.runLater(() -> pass.requestFocus());

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == next) {
                        password = pass.getText();
                        return pass.getText();
                    }
                    return null;
                });

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(usernamePassword -> {
                    CertificateWrapper cw = fileUtil.importKeyStore(file.getPath(), password);
                    keys.add(cw);
                    pocetna(primaryStage);
                });
            }

        });
        exportMenuItem.setOnAction(actionEvent -> {
            if (selektovani == null) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("You have to select keypair first!");
                alert.showAndWait();
            } else if (selektovani.getCertificate() == null) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("You have to sign certificate first!");
                alert.showAndWait();
            } else {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Type password");

                ButtonType next = new ButtonType("Next", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(next, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                PasswordField pass = new PasswordField();
                pass.setPromptText("Password");

                grid.add(new Label("Password:"), 0, 1);
                grid.add(pass, 1, 1);

                Node nextButton = dialog.getDialogPane().lookupButton(next);
                nextButton.setDisable(true);

                pass.textProperty().addListener((observable, oldValue, newValue) -> {
                    nextButton.setDisable(newValue.trim().isEmpty());
                });

                dialog.getDialogPane().setContent(grid);

                Platform.runLater(() -> pass.requestFocus());

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == next) {
                        password = pass.getText();
                        return pass.getText();
                    }
                    return null;
                });

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(usernamePassword -> {

                    fileUtil = new FileUtil();
                    FileChooser fileChooser = new FileChooser();

                    //Set extension filter
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("P12 files (*.p12)", "*.p12");
                    fileChooser.getExtensionFilters().add(extFilter);

                    //Show save file dialog
                    File file = fileChooser.showSaveDialog(primaryStage);

                    if (file != null) {
                        fileUtil.exportKeyStore(file.getPath(), password, selektovani);
                    }

                });

            }
        });

        keyMenu.getItems().addAll(generateMenuItem, importMenuItem, exportMenuItem);
        Menu certMenu = new Menu("Certificate");
        MenuItem csrMenuItem = new MenuItem("CSR");
        MenuItem signMenuItem = new MenuItem("Sign");
        MenuItem generateCertMenuItem = new MenuItem("Generate");

        csrMenuItem.setOnAction(actionEvent -> {
            if (selektovani == null) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("You have to select keypair first!");
                alert.showAndWait();
            } else {
                fileUtil = new FileUtil();
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("P10 files (*.p10)", "*.p10");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(primaryStage);

                if (file != null) {
                    fileUtil.exportCSR(selektovani, file.getPath());
                }
            }
        });

        generateCertMenuItem.setOnAction(actionEvent -> {
            if (selektovani == null) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("You have to select keypair first!");
                alert.showAndWait();
            } else if (selektovani.getCertificate() == null) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("You have to sign certificate first!");
                alert.showAndWait();
            } else {
                fileUtil = new FileUtil();
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CER files (*.cer)", "*.cer");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(primaryStage);

                if (file != null) {
                    fileUtil.exportCertificate(selektovani.getCertificate(), file.getPath());
                }
            }
        });
        signMenuItem.setOnAction(actionEvent -> {
            if (selektovani == null) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("You have to select keypair first!");
                alert.showAndWait();
            } else if (selektovani.isIsSign()) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Certificate is already signed!");
                alert.showAndWait();
            } else {
                Generator gen = new Generator();
                X509Certificate cert = gen.generateCertificate(selektovani);
                selektovani.setCertificate(cert);
                selektovani.setIsSign(true);
                keys.get(selektovaniId).setCertificate(cert);
                keys.get(selektovaniId).setIsSign(true);
                pocetna(primaryStage);
            }
        });

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

        table.getColumns().addAll(keySize, startDate, expiryDate, serialNumber, commonName, organizationUnitName, organizationName, localityName, stateName, countryName, basicConstraint, alternativeNames, keyUsage, isSign);
        root.setCenter(table);

        ObservableList<CertificateWrapper> kljucevi = FXCollections.observableArrayList();
        for (int i = 0; i < keys.size(); i++) {
            kljucevi.add(keys.get(i));
        }

        table.setItems(kljucevi);

        table.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if (table.getSelectionModel().getSelectedItem() != null) {
                selektovani = (CertificateWrapper) table.getSelectionModel().getSelectedItem();
                selektovaniId = table.getSelectionModel().getSelectedIndex();
            }
        });

        primaryStage.setTitle("Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void generateKeys(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1340, 600, Color.WHITE);

        Label keySize = new Label("Key Size");
        TextField keySizeText = new TextField();

        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter
                    = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };

        Label startDate = new Label("Start Date");
        DatePicker startDatePicker = new DatePicker();

        startDatePicker.setConverter(converter);
        startDatePicker.setPromptText(pattern.toLowerCase());

        Label expiryDate = new Label("Expiry Date");
        DatePicker expiryDatePicker = new DatePicker();

        expiryDatePicker.setConverter(converter);
        expiryDatePicker.setPromptText(pattern.toLowerCase());

        DatePicker pomocni = new DatePicker();

        pomocni.setConverter(converter);
        pomocni.setPromptText(pattern.toLowerCase());

        pomocni.setValue(LocalDate.now());
        pomocni.setChronology(HijrahChronology.INSTANCE);

        final Callback<DatePicker, DateCell> cellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(
                                pomocni.getValue().plusDays(0))) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }

        };

        startDatePicker.setDayCellFactory(cellFactory);

        startDatePicker.setOnAction(event -> {
            LocalDate date = startDatePicker.getValue();
            final Callback<DatePicker, DateCell> dayCellFactory
                    = new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item.isBefore(
                                    startDatePicker.getValue().plusDays(1))) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            }
                        }
                    };
                }
            };
            expiryDatePicker.setDayCellFactory(dayCellFactory);
        });

        Label serialNumber = new Label("Serial Number");
        TextField serialNumberText = new TextField();

        Label versionNumber = new Label("Version Number");
        ObservableList<String> versionNumberList
                = FXCollections.observableArrayList(
                        "v3"
                );
        ComboBox versionNumberCombo = new ComboBox(versionNumberList);
        versionNumberCombo.getSelectionModel().select(0);

        Label commonName = new Label("Common Name");
        TextField commonNameText = new TextField();

        Label organizationUnitName = new Label("Organization Unit Name");
        TextField organizationUnitNameText = new TextField();

        Label organizationName = new Label("Organization Name");
        TextField organizationNameText = new TextField();

        Label localityName = new Label("Locality Name");
        TextField localityNameText = new TextField();

        Label stateName = new Label("State Name");
        TextField stateNameText = new TextField();

        Label countryName = new Label("Country Name");
        TextField countryNameText = new TextField();

        Label basicConstraintExtension = new Label("Basic Constraint Extension?");
        CheckBox bce = new CheckBox();

        Label basicConstraintExtensionIsCritical = new Label("Is Basic Constraint Extension Critical?");
        CheckBox bceic = new CheckBox();

        Label isItCA = new Label("Is it CA?");
        CheckBox ica = new CheckBox();

        Label pathDepth = new Label("Path Depth?");
        TextField pathDepthText = new TextField();

        basicConstraintExtensionIsCritical.setDisable(true);
        bceic.setDisable(true);
        bceic.setSelected(false);
        isItCA.setDisable(true);
        ica.setDisable(true);
        ica.setSelected(false);
        pathDepth.setDisable(true);
        pathDepthText.setDisable(true);
        pathDepthText.setText("1");

        bce.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    basicConstraintExtensionIsCritical.setDisable(false);
                    bceic.setDisable(false);
                    isItCA.setDisable(false);
                    ica.setDisable(false);
                    pathDepth.setDisable(false);
                    pathDepthText.setDisable(false);
                } else {
                    basicConstraintExtensionIsCritical.setDisable(true);
                    bceic.setDisable(true);
                    bceic.setSelected(false);
                    isItCA.setDisable(true);
                    ica.setDisable(true);
                    ica.setSelected(false);
                    pathDepth.setDisable(true);
                    pathDepthText.setDisable(true);
                    pathDepthText.setText("1");
                }
            }
        });

        Label alternativeIssuerNames = new Label("Alternative Issuer Names?");
        CheckBox ain = new CheckBox();

        Label alternativeIssuerNamesIsCritical = new Label("Is Alternative Issuer Names Critical?");
        CheckBox ainic = new CheckBox();

        Label alternativeNames = new Label("Alternative Names");
        TextField alternativeNamesText = new TextField();

        alternativeIssuerNamesIsCritical.setDisable(true);
        ainic.setDisable(true);
        ainic.setSelected(false);
        alternativeNames.setDisable(true);
        alternativeNamesText.setDisable(true);
        alternativeNamesText.setText("");

        ain.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    alternativeIssuerNamesIsCritical.setDisable(false);
                    ainic.setDisable(false);
                    alternativeNames.setDisable(false);
                    alternativeNamesText.setDisable(false);
                } else {
                    alternativeIssuerNamesIsCritical.setDisable(true);
                    ainic.setDisable(true);
                    ainic.setSelected(false);
                    alternativeNames.setDisable(true);
                    alternativeNamesText.setDisable(true);
                    alternativeNamesText.setText("");
                }
            }
        });

        Label keyUsage = new Label("Key Usage?");
        CheckBox ku = new CheckBox();

        Label keyUSageIsCritical = new Label("Is Key Usage Critical?");
        CheckBox kuic = new CheckBox();

        Label digitalSignature = new Label("Digital Signature?");
        CheckBox ds = new CheckBox();
        Label dataEncipherment = new Label("Data Encipherment?");
        CheckBox de = new CheckBox();
        Label crlSignature = new Label("Crl Signature?");
        CheckBox cs = new CheckBox();
        Label nonRepudiation = new Label("Non Repudiation?");
        CheckBox nr = new CheckBox();
        Label keyAgreement = new Label("Key Agreement?");
        CheckBox ka = new CheckBox();
        Label encipherOnly = new Label("Encipher Only?");
        CheckBox eo = new CheckBox();
        Label keyEncipherment = new Label("Key Encipherment?");
        CheckBox ke = new CheckBox();
        Label keyCertificateSignature = new Label("Key Certificate Signature?");
        CheckBox kcs = new CheckBox();
        Label decipherOnly = new Label("Decipher Only?");
        CheckBox decO = new CheckBox();

        ku.setSelected(false);
        keyUSageIsCritical.setDisable(true);
        kuic.setDisable(true);
        kuic.setSelected(false);
        digitalSignature.setDisable(true);
        ds.setDisable(true);
        ds.setSelected(false);
        dataEncipherment.setDisable(true);
        de.setDisable(true);
        de.setSelected(false);
        crlSignature.setDisable(true);
        cs.setDisable(true);
        cs.setSelected(false);
        nonRepudiation.setDisable(true);
        nr.setDisable(true);
        nr.setSelected(false);
        keyAgreement.setDisable(true);
        ka.setDisable(true);
        ka.setSelected(false);
        encipherOnly.setDisable(true);
        eo.setDisable(true);
        eo.setSelected(false);
        keyEncipherment.setDisable(true);
        ke.setDisable(true);
        ke.setSelected(false);
        keyCertificateSignature.setDisable(true);
        kcs.setDisable(true);
        kcs.setSelected(false);
        decipherOnly.setDisable(true);
        decO.setDisable(true);
        decO.setSelected(false);

        ku.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    keyUSageIsCritical.setDisable(false);
                    kuic.setDisable(false);
                    digitalSignature.setDisable(false);
                    ds.setDisable(false);
                    dataEncipherment.setDisable(false);
                    de.setDisable(false);
                    crlSignature.setDisable(false);
                    cs.setDisable(false);
                    nonRepudiation.setDisable(false);
                    nr.setDisable(false);
                    keyAgreement.setDisable(false);
                    ka.setDisable(false);
                    encipherOnly.setDisable(false);
                    eo.setDisable(false);
                    keyEncipherment.setDisable(false);
                    ke.setDisable(false);
                    keyCertificateSignature.setDisable(false);
                    kcs.setDisable(false);
                    decipherOnly.setDisable(false);
                    decO.setDisable(false);
                } else {
                    keyUSageIsCritical.setDisable(true);
                    kuic.setDisable(true);
                    kuic.setSelected(false);
                    digitalSignature.setDisable(true);
                    ds.setDisable(true);
                    ds.setSelected(false);
                    dataEncipherment.setDisable(true);
                    de.setDisable(true);
                    de.setSelected(false);
                    crlSignature.setDisable(true);
                    cs.setDisable(true);
                    cs.setSelected(false);
                    nonRepudiation.setDisable(true);
                    nr.setDisable(true);
                    nr.setSelected(false);
                    keyAgreement.setDisable(true);
                    ka.setDisable(true);
                    ka.setSelected(false);
                    encipherOnly.setDisable(true);
                    eo.setDisable(true);
                    eo.setSelected(false);
                    keyEncipherment.setDisable(true);
                    ke.setDisable(true);
                    ke.setSelected(false);
                    keyCertificateSignature.setDisable(true);
                    kcs.setDisable(true);
                    kcs.setSelected(false);
                    decipherOnly.setDisable(true);
                    decO.setDisable(true);
                    decO.setSelected(false);
                }

            }
        });

        ComboBox razmak1 = new ComboBox(versionNumberList);
        razmak1.setVisible(false);
        ComboBox razmak2 = new ComboBox(versionNumberList);
        razmak2.setVisible(false);

        stranica.add(keySize, 0, 0);
        stranica.add(keySizeText, 1, 0);

        stranica.add(startDate, 0, 1);
        stranica.add(startDatePicker, 1, 1);

        stranica.add(expiryDate, 0, 2);
        stranica.add(expiryDatePicker, 1, 2);

        stranica.add(serialNumber, 0, 3);
        stranica.add(serialNumberText, 1, 3);

        stranica.add(versionNumber, 0, 4);
        stranica.add(versionNumberCombo, 1, 4);

        stranica.add(commonName, 0, 5);
        stranica.add(commonNameText, 1, 5);

        stranica.add(organizationUnitName, 0, 6);
        stranica.add(organizationUnitNameText, 1, 6);

        stranica.add(organizationName, 0, 7);
        stranica.add(organizationNameText, 1, 7);

        stranica.add(localityName, 0, 8);
        stranica.add(localityNameText, 1, 8);

        stranica.add(stateName, 0, 9);
        stranica.add(stateNameText, 1, 9);

        stranica.add(countryName, 0, 10);
        stranica.add(countryNameText, 1, 10);

        stranica.add(razmak1, 2, 0);

        stranica.add(basicConstraintExtension, 3, 0);
        stranica.add(bce, 4, 0);
        stranica.add(basicConstraintExtensionIsCritical, 3, 1);
        stranica.add(bceic, 4, 1);
        stranica.add(isItCA, 3, 2);
        stranica.add(ica, 4, 2);
        stranica.add(pathDepth, 3, 3);
        stranica.add(pathDepthText, 4, 3);
        stranica.add(alternativeIssuerNames, 3, 5);
        stranica.add(ain, 4, 5);
        stranica.add(alternativeIssuerNamesIsCritical, 3, 6);
        stranica.add(ainic, 4, 6);
        stranica.add(alternativeNames, 3, 7);
        stranica.add(alternativeNamesText, 4, 7);

        stranica.add(razmak2, 5, 0);

        stranica.add(keyUsage, 6, 0);
        stranica.add(ku, 7, 0);
        stranica.add(keyUSageIsCritical, 6, 1);
        stranica.add(kuic, 7, 1);

        stranica.add(digitalSignature, 6, 2);
        stranica.add(ds, 7, 2);
        stranica.add(dataEncipherment, 6, 3);
        stranica.add(de, 7, 3);
        stranica.add(crlSignature, 6, 4);
        stranica.add(cs, 7, 4);
        stranica.add(nonRepudiation, 6, 5);
        stranica.add(nr, 7, 5);
        stranica.add(keyAgreement, 6, 6);
        stranica.add(ka, 7, 6);
        stranica.add(encipherOnly, 6, 7);
        stranica.add(eo, 7, 7);
        stranica.add(keyEncipherment, 6, 8);
        stranica.add(ke, 7, 8);
        stranica.add(keyCertificateSignature, 6, 9);
        stranica.add(kcs, 7, 9);
        stranica.add(decipherOnly, 6, 10);
        stranica.add(decO, 7, 10);

        stranica.setHgap(10); //horizontal gap in pixels => that's what you are asking for
        stranica.setVgap(10); //vertical gap in pixels
        stranica.setPadding(new Insets(30, 10, 10, 10));
        stranica.setAlignment(Pos.TOP_CENTER);

        Button back = new Button("Back");
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pocetna(primaryStage);
            }
        });

        Button generate = new Button("Generate");
        generate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //generisanje kljuca
                try {
                    CertificateWrapper cw = new CertificateWrapper();
                    Generator gen = new Generator();

                    KeyPair keyPair = gen.generateKeyPair(Integer.parseInt(keySizeText.getText()));
                    cw.setKeyPair(keyPair);
                    cw.setKeySize(Integer.parseInt(keySizeText.getText()));

                    LocalDate localDate = startDatePicker.getValue();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
                    cw.setStartDate(new Date(calendar.getTimeInMillis()));

                    localDate = expiryDatePicker.getValue();
                    calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
                    cw.setExpiryDate(new Date(calendar.getTimeInMillis()));

                    cw.setSerialNumber(BigInteger.valueOf(Long.parseLong(serialNumberText.getText())));
                    cw.setCn(commonNameText.getText());
                    cw.setOu(organizationNameText.getText());
                    cw.setO(organizationNameText.getText());
                    cw.setL(localityNameText.getText());
                    cw.setSt(stateNameText.getText());
                    cw.setC(countryNameText.getText());

                    //basic constratint
                    if (bce.isSelected()) {
                        cw.setBasicConstraint(ica.isSelected());
                        cw.setBasicConstraintIsCritical(bceic.isSelected());
                        cw.setBasicConstraintPath(Integer.parseInt(pathDepthText.getText()));
                    } else {
                        cw.setBasicConstraint(null);
                    }

                    //alternative name
                    if (ain.isSelected()) {
                        cw.setAlternativeName(alternativeNamesText.getText());
                        cw.setAlternativeNameIsCritical(ainic.isSelected());
                    } else {
                        cw.setAlternativeName(null);
                    }

                    //key usage
                    if (ku.isSelected()) {
                        cw.setKeyUsageIsCritical(kuic.isSelected());
                        cw.calculateKeyUsage(ds.isSelected(), nr.isSelected(), ke.isSelected(), de.isSelected(), ka.isSelected(), kcs.isSelected(), cs.isSelected(), eo.isSelected(), decO.isSelected());
                    } else {
                        cw.setKeyUsageIsCritical(null);
                    }

                    cw.setIsSign(false);

                    keys.add(cw);
                    keyUSageIsCritical.setDisable(true);
                    kuic.setDisable(true);
                    kuic.setSelected(false);
                    digitalSignature.setDisable(true);
                    ds.setDisable(true);
                    ds.setSelected(false);
                    dataEncipherment.setDisable(true);
                    de.setDisable(true);
                    de.setSelected(false);
                    crlSignature.setDisable(true);
                    cs.setDisable(true);
                    cs.setSelected(false);
                    nonRepudiation.setDisable(true);
                    nr.setDisable(true);
                    nr.setSelected(false);
                    keyAgreement.setDisable(true);
                    ka.setDisable(true);
                    ka.setSelected(false);
                    encipherOnly.setDisable(true);
                    eo.setDisable(true);
                    eo.setSelected(false);
                    keyEncipherment.setDisable(true);
                    ke.setDisable(true);
                    ke.setSelected(false);
                    keyCertificateSignature.setDisable(true);
                    kcs.setDisable(true);
                    kcs.setSelected(false);
                    decipherOnly.setDisable(true);
                    decO.setDisable(true);
                    decO.setSelected(false);
                    alternativeIssuerNamesIsCritical.setDisable(true);
                    ainic.setDisable(true);
                    ainic.setSelected(false);
                    alternativeNames.setDisable(true);
                    alternativeNamesText.setDisable(true);
                    alternativeNamesText.setText("");
                    basicConstraintExtensionIsCritical.setDisable(true);
                    bceic.setDisable(true);
                    bceic.setSelected(false);
                    isItCA.setDisable(true);
                    ica.setDisable(true);
                    ica.setSelected(false);
                    pathDepth.setDisable(true);
                    pathDepthText.setDisable(true);
                    pathDepthText.setText("1");
                    pocetna(primaryStage);
                } catch (Exception e) {
                    Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        });
        GridPane dugmici = new GridPane();
        dugmici.add(back, 0, 0);
        dugmici.add(generate, 1, 0);
        dugmici.setAlignment(Pos.TOP_RIGHT);
        dugmici.setHgap(10); //horizontal gap in pixels => that's what you are asking for
        dugmici.setVgap(10); //vertical gap in pixels
        dugmici.setPadding(new Insets(30, 10, 10, 10));
        stranica.add(dugmici, 3, 14);

        root.setCenter(stranica);
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
