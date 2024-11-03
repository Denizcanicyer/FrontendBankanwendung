import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.lang.reflect.Type;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.google.gson.*;
public class Enter extends Application {
    private final Gson gson = new Gson();
    @Override
    public void start(Stage primaryStage) {
        Label resultLabel1 = new Label();
        TextField textField = new TextField();
        textField.setPromptText("Typ");

        TextField textField1 = new TextField();
        textField.setPromptText("Menge");


        Stage newWindow = new Stage();
        Button button = new Button("Anzeigen");
        Label resultLabel = new Label();
        Label kontostand = new Label();
        Button loeschen = new Button("Eintrag Löschen");
        TableView<Transaction> tableView = new TableView();
        Button reload = new Button("Reload");
        Button  ausgaben = new Button("Ausgaben");
        Button  einnahmen = new Button("Einnahmen");
        Button bearbeiten = new Button("Bearbeiten");

        List<Transaction> transactions=null;
        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // Use a StringBuilder to store the response
                StringBuilder response = new StringBuilder();
                String inputLine;

                // Read each line from the buffered reader
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Close the reader
                in.close();
                Gson gson = new Gson();
                // Define the type for the list of Transaction objects
                Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();

                // Deserialize JSON string into a List of Transaction objects
                transactions = gson.fromJson(String.valueOf(response), transactionListType);
                ObservableList<Transaction> observableTransactions = FXCollections.observableArrayList(transactions);
                tableView = new TableView<>();

                // Create columns for the TableView
                TableColumn<Transaction, Integer> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
                typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

                TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

                TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
                categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

                // Add columns to the TableView
                tableView.getColumns().addAll(idColumn, typeColumn, amountColumn, categoryColumn);

                // Set items for the TableView
                tableView.setItems(observableTransactions);
                System.out.println("Response: " + response.toString());
                resultLabel.setText("Transaction submitted successfully.");
                double sum = observableTransactions.stream().mapToDouble(Transaction::getAmount).sum();
                kontostand.setText(String.valueOf(sum));
                tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loeschen.setOnAction(e -> {
                            sendDeleteRequestByID(newValue.getId());
                        });
                        bearbeiten.setOnAction(event -> {
                            Transaction transaction = new Transaction();
                            transaction.setId(newValue.getId());
                            transaction.setAmount(newValue.getAmount());
                            transaction.setCategory(newValue.getCategory());
                            transaction.setType(newValue.getType());
                            openNewWindow(transaction);
                        });
                    }

                });

            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());

        }
        final ComboBox priorityComboBox = new ComboBox();
        priorityComboBox.getItems().addAll(
                "Einnahme","Ausgabe"
        );
        Button button2 = new Button("Hinzufügen");
        Button button1 = new Button("Lösche alle Einträge");
        button2.setOnAction(e -> {
            openNewWindowHinzufügen();
        });
        button1.setOnAction(e -> {
            sendDeleteRequest(resultLabel1);
        });


        reload.setOnAction(event -> {
            openNewWindowAnzeigen();
        });
        ausgaben.setOnAction(event -> {
            openNewWindowAusgaben();
        });
        einnahmen.setOnAction(event -> {
            openNewWindowEinnahmen();
        });

        // Layout
        VBox vbox = new VBox(10,tableView, button2,button1,bearbeiten,loeschen,reload,kontostand,ausgaben,einnahmen);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center");
        // Scene
        Scene scene = new Scene(vbox, 500, 500);
        // Stage setup
        primaryStage.setTitle("JavaFX TextField Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void sendGetRequest(ObservableList<Transaction> observableTransactions,Label resultLabel) {
        List<Transaction> transactions=null;
        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // Use a StringBuilder to store the response
                StringBuilder response = new StringBuilder();
                String inputLine;

                // Read each line from the buffered reader
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Close the reader
                in.close();
                Gson gson = new Gson();
                // Define the type for the list of Transaction objects
                Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();

                // Deserialize JSON string into a List of Transaction objects
                 transactions = gson.fromJson(String.valueOf(response), transactionListType);
                 observableTransactions = FXCollections.observableArrayList(transactions);

                System.out.println("Response: " + response.toString());
                resultLabel.setText("Transaction submitted successfully.");

            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());

        }

    }
    private void sendPostRequest(Transaction transaction, Label resultLabel) {
        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Convert Transaction object to JSON
            String jsonInputString = gson.toJson(transaction);

            // Send JSON data
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                resultLabel.setText("Transaction submitted successfully.");
            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());
        }
    }
    private void sendPutRequest(Transaction transaction, Label resultLabel) {
        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions/update");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Convert Transaction object to JSON
            String jsonInputString = gson.toJson(transaction);

            // Send JSON data
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                resultLabel.setText("Transaction submitted successfully.");
            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());
        }
    }
    private void sendPostRequestCategory(Transaction transaction, Label resultLabel) {
        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions/category/"+transaction.getCategory());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Convert Transaction object to JSON
            String jsonInputString = gson.toJson(transaction);

            // Send JSON data
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                resultLabel.setText("Transaction submitted successfully.");
            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());
        }
    }
    private void sendDeleteRequest( Label resultLabel) {
        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                resultLabel.setText("Transaction submitted successfully.");
            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());
        }
    }
    private void sendDeleteRequestByID(  int id) {
        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions/"+ id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

            } else {

            }

        } catch (Exception e) {

        }
    }

    private void openNewWindow(Transaction transaction) {
        Stage newWindow = new Stage();

        Label resultLabel = new Label();

        TextField amound = new TextField();
        TableView<Transaction> tableView = new TableView();
        Button abschicken = new Button("Abschicken");
        List<Transaction> transactions=null;
        try {
            amound.setText(String.valueOf(transaction.getAmount()));

            abschicken.setOnAction(event -> {
                try {
                    // URL for the API endpoint
                    URL url = new URL("http://localhost:8080/api/transactions/update");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    // Set request method to POST
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                     transaction.setAmount(Double.parseDouble(amound.getText()));
                     System.out.println(transaction.getAmount());
                    // Convert Transaction object to JSON
                    String jsonInputString = gson.toJson(transaction);

                    // Send JSON data
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Check response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        resultLabel.setText("Transaction submitted successfully.");
                    } else {
                        resultLabel.setText("Error: " + responseCode);
                    }

                } catch (Exception e) {
                    resultLabel.setText("Exception: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());

        }

        VBox vbox = new VBox(amound,resultLabel,abschicken);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center");

        // Scene
        Scene scene = new Scene(vbox, 500, 500); // Block interaction with the main window


        // Stage setup
        newWindow.setTitle("JavaFX TextField Example");
        newWindow.setScene(scene);

        // Show the new window
        newWindow.show();
    }
    private void openNewWindowHinzufügen() {
        Stage newWindow = new Stage();

        Transaction transaction = new Transaction();
        // Label to display the user input
        Label label = new Label("Typ:");
        Label label1 = new Label("Menge:");
        Label label2 = new Label("Kategorie:");

        Label resultLabel1 = new Label();
        // TextField for user input
        TextField textField = new TextField();
        textField.setPromptText("Typ");

        TextField textField1 = new TextField();
        textField.setPromptText("Menge");


        final ComboBox priorityComboBox = new ComboBox();
        priorityComboBox.getItems().addAll(
                "Einnahme","Ausgabe"
        );

        Button button = new Button("Hinzufügen");

        Button button1 = new Button("Lösche alle Einträge");
        // Label to show the result
        Label resultLabel = new Label();
        Label deleteLabel = new Label();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Button click event handler
        button.setOnAction(e -> {
            String name = textField.getText();  // Get text from TextField
            transaction.setType(textField.getText());
            String inputText = textField1.getText();
            double number = Double.parseDouble(inputText);
            if(priorityComboBox.getValue().toString().equals("Ausgabe")){
                number *=-1;
            }
            transaction.setAmount(number);
            transaction.setCategory(priorityComboBox.getValue().toString());

            sendPostRequest(transaction, resultLabel);



        });
        button1.setOnAction(e -> {
            sendDeleteRequest(resultLabel1);
        });

        VBox vbox = new VBox(10,label, textField,label1,textField1,label2,priorityComboBox,button,button1, resultLabel,resultLabel1);

        vbox.setStyle("-fx-padding: 20; -fx-alignment: center");

        // Scene
        Scene scene = new Scene(vbox, 500, 500); // Block interaction with the main window


        // Stage setup
        newWindow.setTitle("JavaFX TextField Example");
        newWindow.setScene(scene);

        // Show the new window
        newWindow.show();
    }
    private void openNewWindowAnzeigen() {
        Stage newWindow = new Stage();
        TableView<Transaction> tableView1 = new TableView();
        List<Transaction> transactions1=null;
        Label resultLabel= new Label();
        Label kontostand = new Label();
        Button loeschen = new Button("Eintrag Löschen");
        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Set request method to POST
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // Use a StringBuilder to store the response
                StringBuilder response = new StringBuilder();
                String inputLine;

                // Read each line from the buffered reader
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // Close the reader
                in.close();
                Gson gson = new Gson();
                // Define the type for the list of Transaction objects
                Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();

                // Deserialize JSON string into a List of Transaction objects
                transactions1 = gson.fromJson(String.valueOf(response), transactionListType);
                ObservableList<Transaction> observableTransactions = FXCollections.observableArrayList(transactions1);
                tableView1 = new TableView<>();

                // Create columns for the TableView
                TableColumn<Transaction, Integer> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
                typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

                TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

                TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
                categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

                // Add columns to the TableView
                tableView1.getColumns().addAll(idColumn, typeColumn, amountColumn, categoryColumn);

                // Set items for the TableView
                tableView1.setItems(observableTransactions);
                System.out.println("Response: " + response.toString());
                resultLabel.setText("Transaction submitted successfully.");
                double sum = observableTransactions.stream().mapToDouble(Transaction::getAmount).sum();
                kontostand.setText(String.valueOf(sum));
                tableView1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loeschen.setOnAction(e -> {
                            sendDeleteRequestByID(newValue.getId());
                        });
                    }
                });

            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());

        }
        VBox vbox = new VBox(10,tableView1);

        vbox.setStyle("-fx-padding: 20; -fx-alignment: center");

        // Scene
        Scene scene = new Scene(vbox, 500, 500); // Block interaction with the main window


        // Stage setup
        newWindow.setTitle("JavaFX TextField Example");
        newWindow.setScene(scene);

        // Show the new window
        newWindow.show();
    }
    private void openNewWindowAusgaben() {
        Stage newWindow = new Stage();
        TableView<Transaction> tableView1 = new TableView();
         List<Transaction> transactions1 = null;
        Label resultLabel = new Label();


        Button eingeben = new Button();

        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions/category/Ausgabe");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Set request method to POST
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // Use a StringBuilder to store the response
                StringBuilder response = new StringBuilder();
                String inputLine;

                // Read each line from the buffered reader
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // Close the reader
                in.close();
                Gson gson = new Gson();
                // Define the type for the list of Transaction objects
                Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();

                // Deserialize JSON string into a List of Transaction objects
                transactions1 = gson.fromJson(String.valueOf(response), transactionListType);
                ObservableList<Transaction> observableTransactions = FXCollections.observableArrayList(transactions1);
                tableView1 = new TableView<>();

                // Create columns for the TableView
                TableColumn<Transaction, Integer> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
                typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

                TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

                TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
                categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

                // Add columns to the TableView
                tableView1.getColumns().addAll(idColumn, typeColumn, amountColumn, categoryColumn);

                // Set items for the TableView
                tableView1.setItems(observableTransactions);
                System.out.println("Response: " + response.toString());
                resultLabel.setText("Transaction submitted successfully.");
                double sum = observableTransactions.stream().mapToDouble(Transaction::getAmount).sum();

                tableView1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

                });

            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());

        }


        VBox vbox = new VBox(10,tableView1);

        vbox.setStyle("-fx-padding: 20; -fx-alignment: center");

        // Scene
        Scene scene = new Scene(vbox, 500, 500); // Block interaction with the main window


        // Stage setup
        newWindow.setTitle("JavaFX TextField Example");
        newWindow.setScene(scene);

        // Show the new window
        newWindow.show();
    }
    private void openNewWindowEinnahmen() {
        Stage newWindow = new Stage();
        TableView<Transaction> tableView1 = new TableView();
        List<Transaction> transactions1 = null;
        Label resultLabel = new Label();


        Button eingeben = new Button();

        try {
            // URL for the API endpoint
            URL url = new URL("http://localhost:8080/api/transactions/category/Einnahme");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Set request method to POST
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // Use a StringBuilder to store the response
                StringBuilder response = new StringBuilder();
                String inputLine;

                // Read each line from the buffered reader
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // Close the reader
                in.close();
                Gson gson = new Gson();
                // Define the type for the list of Transaction objects
                Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();

                // Deserialize JSON string into a List of Transaction objects
                transactions1 = gson.fromJson(String.valueOf(response), transactionListType);
                ObservableList<Transaction> observableTransactions = FXCollections.observableArrayList(transactions1);
                tableView1 = new TableView<>();

                // Create columns for the TableView
                TableColumn<Transaction, Integer> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
                typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

                TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

                TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
                categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

                // Add columns to the TableView
                tableView1.getColumns().addAll(idColumn, typeColumn, amountColumn, categoryColumn);

                // Set items for the TableView
                tableView1.setItems(observableTransactions);
                System.out.println("Response: " + response.toString());
                resultLabel.setText("Transaction submitted successfully.");
                double sum = observableTransactions.stream().mapToDouble(Transaction::getAmount).sum();

                tableView1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

                });

            } else {
                resultLabel.setText("Error: " + responseCode);
            }

        } catch (Exception e) {
            resultLabel.setText("Exception: " + e.getMessage());

        }


        VBox vbox = new VBox(10,tableView1);

        vbox.setStyle("-fx-padding: 20; -fx-alignment: center");

        // Scene
        Scene scene = new Scene(vbox, 500, 500); // Block interaction with the main window


        // Stage setup
        newWindow.setTitle("JavaFX TextField Example");
        newWindow.setScene(scene);

        // Show the new window
        newWindow.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
