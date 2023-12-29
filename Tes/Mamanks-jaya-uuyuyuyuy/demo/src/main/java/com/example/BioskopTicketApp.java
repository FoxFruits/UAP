/**
 * @Author Elang Dwi Setiawan Diqlas
 */
package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashSet;

import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.image.ImageView;


import com.example.Others.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BioskopTicketApp extends Application {

    private Stage primaryStage;
    private String pembeli;
    private String jamPenayangan;
    private Film film;
    private List<Receipt> daftarPemesanan = new ArrayList<>();
    private Set<Integer> kursiPilihanUser = new HashSet<>();
    private TableView<Receipt> tableView = new TableView<>();
    private Tab inputTab;
    private Label inputDetailsLabel = new Label();
    private double totalAmount;
    private Map<String, Set<Integer>> reservedSeatsByShowtime = new HashMap<>();


    // Declare UI components at the class level
    private TextField pembeliField;
    private ComboBox<String> jamPenayanganComboBox;
    private ComboBox<Film> filmComboBox;
    private GridPane seatGridPane;

    public static void main(String[] args) {
        launch(args);
    }

    //ini main scene
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Aplikasi Pemesanan Tiket Bioskop");

        TabPane tabPane = new TabPane();
        inputTab = createInputTab();
        Tab dataTab = createDataTab();

        tabPane.getTabs().addAll(inputTab, dataTab);

        Scene scene = new Scene(tabPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //tab data
    private Tab createDataTab() {
        Tab dataTab = new Tab("Hasil Data");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        TableColumn<Receipt, String> namaCol = new TableColumn<>("Nama"); //pembuatan instance namacol yang polymorp dari class receipt dan display bentuk string
        namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));

        TableColumn<Receipt, String> jamPenayanganCol = new TableColumn<>("Jam Penayangan");
        jamPenayanganCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJamPenayangan()));

        TableColumn<Receipt, String> filmCol = new TableColumn<>("Film");
        filmCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilm()));

        TableColumn<Receipt, String> kursiCol = new TableColumn<>("Kursi Terpilih");
        kursiCol.setCellValueFactory(cellData -> {
            Set<Integer> kursiPilihanUser = cellData.getValue().getKursiPilihanUser();
            return new SimpleStringProperty(kursiPilihanUser.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        });

        tableView.getColumns().addAll(namaCol, jamPenayanganCol, filmCol, kursiCol);
        tableView.setItems(FXCollections.observableArrayList(daftarPemesanan));

        Label inputDetailsLabel = new Label("Input Details:\n");

        vbox.getChildren().addAll(inputDetailsLabel, tableView);
        HBox buttonsHBox = new HBox();
        buttonsHBox.setSpacing(10);

        Button deleteButton = new Button("Hapus Data");
        Button updateButton = new Button("Update Data");

        buttonsHBox.getChildren().addAll(deleteButton, updateButton);
        vbox.getChildren().add(buttonsHBox);

        deleteButton.setOnAction(event -> {
            // Handle the delete action here
            deleteSelectedData();
        });

        updateButton.setOnAction(event -> {
            // Handle the update action here
            updateSelectedData();
        });

        dataTab.setContent(vbox);

        return dataTab;
    }

    private void updateSelectedData() {
        Receipt selectedReceipt = tableView.getSelectionModel().getSelectedItem();
        if (selectedReceipt != null) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Update Data");
            dialog.setHeaderText(null);

            // Membuat label dan field untuk nama
            Label nameLabel = new Label("Update Buyer Name:");
            TextField nameField = new TextField(selectedReceipt.getNama());

            // Membuat label dan choice box untuk reschedule jam penayangan
            Label showtimeLabel = new Label("Reschedule Showtime:");
            ChoiceBox<String> showtimeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("12:00", "15:00", "18:00", "21:00"));
            showtimeChoiceBox.getSelectionModel().select(selectedReceipt.getJamPenayangan());

            // Membuat layout grid untuk dialog
            GridPane grid = new GridPane();
            grid.add(nameLabel, 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(showtimeLabel, 0, 1);
            grid.add(showtimeChoiceBox, 1, 1);

            dialog.getDialogPane().setContent(grid);

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    String newName = nameField.getText();
                    String newShowtime = showtimeChoiceBox.getValue(); // Ensure getting selected time correctly

                    return new Pair<>(newName, newShowtime);
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(pair -> {
                selectedReceipt.setNama(pair.getKey());
                selectedReceipt.setJamP(pair.getValue());
                tableView.refresh();
                showAlert("Info", "Data updated successfully.");

                // Update data pada file TXT
                savePemesananToFile(daftarPemesanan);
            });
        } else {
            showAlert("Info", "Select the data you want to update.");
        }
    }

    /**
     * Method penghapusan data
     *
     */
    private void deleteSelectedData() {
        Receipt selectedReceipt = tableView.getSelectionModel().getSelectedItem();
        if (selectedReceipt != null) {
            daftarPemesanan.remove(selectedReceipt);
            tableView.setItems(FXCollections.observableArrayList(daftarPemesanan));
            showAlert("Info", "Data berhasil dihapus.");

            // Update data pada file TXT
            savePemesananToFile(daftarPemesanan);
        } else {
            showAlert("Info", "Pilih data yang ingin dihapus.");
        }
    }

    /**
     *
     * @param daftarPemesanan
     */
    private void savePemesananToFile(List<Receipt> daftarPemesanan) {
        Path filePath = Paths.get("D:\\CoolYeah\\Semester 3\\Prak. Program Lanjut\\Tes\\Mamanks-jaya-uuyuyuyuy\\data_pemesanan.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            // Menulis data pemesanan ke dalam file
            for (Receipt receipt : daftarPemesanan) {
                writer.write(receipt.generateReceipt());
                writer.newLine();
            }

            System.out.println("Data pemesanan berhasil disimpan ke dalam file.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menyimpan data ke dalam file.");
        }
    }


    private Tab createInputTab() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        pembeliField = new TextField();
        pembeliField.setPromptText("Nama Pembeli");
        gridPane.add(new Label("Nama Pembeli:"), 0, 0);
        gridPane.add(pembeliField, 1, 0);

        jamPenayanganComboBox = new ComboBox<>();
        jamPenayanganComboBox.getItems().addAll("12:00", "15:00", "18:00", "21:00");
        jamPenayanganComboBox.setPromptText("Pilih");
        gridPane.add(new Label("Jam Penayangan:"), 0, 1);
        gridPane.add(jamPenayanganComboBox, 1, 1);

        filmComboBox = new ComboBox<>();
        filmComboBox.getItems().addAll(
                new Film("Kimi No Nawa", "/com/example/IMG/kimi_no_nawa_poster.jpg"),
                new Film("Spiderman Home Coming", "/com/example/IMG/spi.jpg"),
                new Film("Spirited Away", "/com/example/IMG/spirited_away_poster.jpg"));
        filmComboBox.setPromptText("Pilih");
        gridPane.add(new Label("Nama Film:"), 0, 2);
        gridPane.add(filmComboBox, 1, 2);

        Button lihatKursiButton = new Button("Lihat Kursi Tersedia");
        gridPane.add(lihatKursiButton, 1, 3);

        lihatKursiButton.setOnAction(event -> {
            try {
                validateInput(pembeliField, jamPenayanganComboBox, filmComboBox);
                pembeli = pembeliField.getText();
                jamPenayangan = jamPenayanganComboBox.getValue();
                film = filmComboBox.getValue();



                showSeatSelection();
            } catch (InputValidationException e) {
                showAlert("Error", e.getMessage());
            }
        });

        Tab inputTab = new Tab("Input Data");
        inputTab.setContent(gridPane);

        return inputTab;
    }

    private void showSeatSelection() {
        BorderPane borderPane = new BorderPane();
        VBox movieInfoVBox = new VBox();
        HBox posterAndCheckboxHBox = new HBox();
        seatGridPane = new GridPane();
        seatGridPane.setPadding(new Insets(20, 20, 20, 20));
        seatGridPane.setVgap(10);
        seatGridPane.setHgap(10);

        // Add movie title and jam tayang labels above the poster
        Label titleLabel = new Label("Movie: " + film.getName());
        Label showtimeLabel = new Label("Jam Tayang: " + jamPenayangan);
        movieInfoVBox.getChildren().addAll(titleLabel, showtimeLabel);

        // Add movie info (title and showtime) above the poster
        ImageView posterImageView = new ImageView(getClass().getResource(film.getPosterPath()).toExternalForm());
        posterImageView.setFitWidth(300);
        posterImageView.setFitHeight(400);

        posterAndCheckboxHBox.getChildren().addAll(posterImageView, seatGridPane);
        borderPane.setTop(movieInfoVBox);
        borderPane.setCenter(posterAndCheckboxHBox);

        // Create a sub-grid for checkboxes
        int rowCount = 0;
        int colCount = 0;
        String movieShowtimeKey = film.getName() + "_" + jamPenayangan;

        for (int i = 1; i <= 40; i++) {
            CheckBox checkBox = new CheckBox(String.valueOf(i));
            checkBox.setPrefWidth(60);
            checkBox.setPadding(new Insets(5));

            // Check if the seat is already reserved for the current showtime and movie
            if (reservedSeatsByShowtime.containsKey(movieShowtimeKey) && reservedSeatsByShowtime.get(movieShowtimeKey).contains(i)) {
                checkBox.setDisable(true);
            }

            seatGridPane.add(checkBox, colCount, rowCount);

            int kursiNumber = i;
            checkBox.setOnAction(event -> {
                if (!checkBox.isDisabled()) {
                    if (checkBox.isSelected()) {
                        if (kursiPilihanUser.size() >= 3) {
                            showAlert("Info", "Anda hanya dapat memesan maksimal 3 kursi.");
                            checkBox.setSelected(false);
                        } else {
                            kursiPilihanUser.add(kursiNumber);
                        }
                    } else {
                        kursiPilihanUser.remove(Integer.valueOf(kursiNumber));
                    }
                }
            });

            rowCount++;
            if (rowCount == 10) {
                rowCount = 0;
                colCount++;
            }
        }



        // Create button for booking in the center
        Button pesanButton = new Button("Pesan");
        VBox centerVBox = new VBox(pesanButton);
        centerVBox.setAlignment(Pos.CENTER);
        borderPane.setBottom(centerVBox);



        pesanButton.setOnAction(event -> {
            if (kursiPilihanUser.isEmpty()) {
                showAlert("Info", "Pilih kursi terlebih dahulu.");
            } else {
                // Calculate the total amount based on the number of seats selected
                totalAmount = calculateTotalAmount(kursiPilihanUser.size());

                // Show the total amount to the user before entering the payment
                showAlert("Total Amount", "Total amount to be paid: " + totalAmount);

                double amountPaid = promptForAmount();
                if (amountPaid > 0) {
                    boolean paymentSuccessful = PaymentManager.processPayment(kursiPilihanUser, amountPaid, totalAmount);
                    if (paymentSuccessful) {
                        showNotaAndRecordPurchase();
                        resetUI();
                    }
                }
            }
        });



        Scene seatSelectionScene = new Scene(borderPane, 900, 500); // Sesuaikan ukuran jika diperlukan
        primaryStage.setScene(seatSelectionScene);
    }

    // Add a method to calculate the total amount based on the number of seats selected
    private double calculateTotalAmount(int numberOfSeats) {
        // Assuming the same seat price as in PaymentManager
        return numberOfSeats * PaymentManager.SEAT_PRICE;
    }

    private double promptForAmount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Payment");
        dialog.setHeaderText(null);
        // Show the total amount in the content of the dialog
        dialog.setContentText("Enter the amount paid (Total: " + totalAmount + "):");

        try {
            Optional<String> result = dialog.showAndWait();
            return result.map(Double::parseDouble).orElse(0.0);
        } catch (NumberFormatException e) {
            // Handle the case where the user enters a non-numeric value
            showAlert("Error", "Invalid input. Please enter a numeric value for the payment amount.");
            return promptForAmount(); // Recursive call to prompt the user again
        }
    }
    private String formatSelectedSeats(Set<Integer> selectedSeats) {
        return selectedSeats.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    private void showNotaAndRecordPurchase() {
        StringBuilder nota = new StringBuilder();
        nota.append("===== Nota Pemesanan =====\n");
        nota.append("Nama Pembeli: ").append(pembeli).append("\n");
        nota.append("Jam Penayangan: ").append(jamPenayangan).append("\n");
        nota.append("Nama Film: ").append(film.getName()).append("\n");
        nota.append("Kursi Terpilih: ");

        for (int kursi : kursiPilihanUser) {
            nota.append("Kursi ").append(kursi).append(" ");
        }


        String movieShowtimeKey = film.getName() + "_" + jamPenayangan;


        reservedSeatsByShowtime.computeIfAbsent(movieShowtimeKey, k -> new HashSet<>()).addAll(kursiPilihanUser);

        showNotaAndRecordPurchase(nota.toString());


    }

    private void showNotaAndRecordPurchase(String nota) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nota Pemesanan");
        alert.setHeaderText(null);
        alert.setContentText(nota);
        alert.showAndWait();

        Receipt receipt = new Receipt(pembeli, jamPenayangan, film.getName(), kursiPilihanUser);
        daftarPemesanan.add(receipt);

        savePemesananToFile(daftarPemesanan);

        tableView.setItems(FXCollections.observableArrayList(daftarPemesanan));

        inputDetailsLabel.setText("Input Details:\nNama Pembeli: " + pembeli +
                "\nJam Penayangan: " + jamPenayangan + "\nNama Film: " + film.getName());

        primaryStage.setScene(inputTab.getContent().getScene());
        clearInputFields();
    }

    private void clearInputFields() {
        pembeli = "";
        jamPenayangan = "";
        film = null;
        kursiPilihanUser.clear();
    }

    private void validateInput(TextField pembeliField, ComboBox<String> jamPenayanganComboBox, ComboBox<Film> filmComboBox) throws InputValidationException {
        if (pembeliField.getText().isEmpty() || jamPenayanganComboBox.getValue() == null || filmComboBox.getValue() == null) {
            throw new InputValidationException("Mohon lengkapi semua informasi sebelum melihat kursi tersedia.");
        }
    }



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class InputValidationException extends Exception {
        public InputValidationException(String message) {
            super(message);
        }
    }

    private void resetUI() {
        pembeliField.clear();
        jamPenayanganComboBox.getSelectionModel().clearSelection();
        filmComboBox.getSelectionModel().clearSelection();

        for (Node node : seatGridPane.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                checkBox.setSelected(false);
                checkBox.setDisable(false);
            }
        }

        // Check if the film object is not null before using it
        if (film != null && jamPenayangan != null) {
            String movieShowtimeKey = film.getName() + "_" + jamPenayangan;

            // Clear the reserved seats for the current showtime and movie
            reservedSeatsByShowtime.remove(movieShowtimeKey);
        }
    }


    public static void setRoot(String string) {
    }
}