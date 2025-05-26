import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ECommerceGUI extends Application {
    private Stage primaryStage;
    private BorderPane mainPane;
    private User currentUser;
    private Seller seller;
    private Buyer currentBuyer;
    private Scene loginScene, sellerScene, buyerScene, cartScene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        seller = new Seller("TechStore");
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        primaryStage.setTitle("E-Commerce System");
        mainPane = new BorderPane();

        // Create scenes for different panels
        loginScene = new Scene(createLoginPane(), 800, 600);
        sellerScene = new Scene(createSellerPane(), 800, 600);
        buyerScene = new Scene(createBuyerPane(), 800, 600);
        cartScene = new Scene(createCartPane(), 800, 600);

        // Set initial scene
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private Pane createLoginPane() {
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Welcome to E-Commerce System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button sellerButton = new Button("Continue as Seller");
        Button buyerButton = new Button("Continue as Buyer");

        // Layout
        pane.add(titleLabel, 0, 0, 2, 1);
        pane.add(new Label("User ID:"), 0, 1);
        pane.add(userIdField, 1, 1);
        pane.add(new Label("Name:"), 0, 2);
        pane.add(nameField, 1, 2);
        pane.add(new Label("Email:"), 0, 3);
        pane.add(emailField, 1, 3);
        pane.add(new Label("Password:"), 0, 4);
        pane.add(passwordField, 1, 4);
        pane.add(loginButton, 0, 5, 2, 1);
        pane.add(sellerButton, 0, 6, 2, 1);
        pane.add(buyerButton, 0, 7, 2, 1);

        // Center buttons
        GridPane.setHalignment(loginButton, javafx.geometry.HPos.CENTER);
        GridPane.setHalignment(sellerButton, javafx.geometry.HPos.CENTER);
        GridPane.setHalignment(buyerButton, javafx.geometry.HPos.CENTER);

        // Event handlers
        loginButton.setOnAction(e -> {
            currentUser = new User();
            currentUser.userId = userIdField.getText();
            currentUser.name = nameField.getText();
            currentUser.email = emailField.getText();
            currentUser.password = passwordField.getText();
            currentUser.login();
            showAlert(Alert.AlertType.INFORMATION, "Login successful!");
        });

        sellerButton.setOnAction(e -> primaryStage.setScene(sellerScene));

        buyerButton.setOnAction(e -> {
            currentBuyer = new Buyer("BUYER" + System.currentTimeMillis(), "Default Address");
            primaryStage.setScene(buyerScene);
        });

        return pane;
    }

    private Pane createSellerPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setAlignment(Pos.TOP_CENTER);

        TextField productIdField = new TextField();
        productIdField.setPromptText("Product ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");

        formPane.add(new Label("Product ID:"), 0, 0);
        formPane.add(productIdField, 1, 0);
        formPane.add(new Label("Name:"), 0, 1);
        formPane.add(nameField, 1, 1);
        formPane.add(new Label("Description:"), 0, 2);
        formPane.add(descField, 1, 2);
        formPane.add(new Label("Price:"), 0, 3);
        formPane.add(priceField, 1, 3);
        formPane.add(new Label("Stock:"), 0, 4);
        formPane.add(stockField, 1, 4);

        Button addButton = new Button("Add Product");
        Button backButton = new Button("Back to Login");

        VBox buttonBox = new VBox(10, addButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        formPane.add(buttonBox, 0, 5, 2, 1);

        TextArea productList = new TextArea();
        productList.setEditable(false);
        productList.setPrefHeight(300);

        addButton.setOnAction(e -> {
            try {
                Product product = new Product(
                    productIdField.getText(),
                    nameField.getText(),
                    descField.getText(),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(stockField.getText())
                );
                seller.addProduct(product);

                StringBuilder sb = new StringBuilder();
                for (Product p : seller.getProducts()) {
                    sb.append("ID: ").append(p.getProductId())
                      .append(", Name: ").append(p.getName())
                      .append(", Price: $").append(p.getPrice())
                      .append(", Stock: ").append(p.getStock())
                      .append("\n");
                }
                productList.setText(sb.toString());

                productIdField.clear();
                nameField.clear();
                descField.clear();
                priceField.clear();
                stockField.clear();

                showAlert(Alert.AlertType.INFORMATION, "Product added successfully!");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Please enter valid numbers for price and stock.");
            }
        });

        backButton.setOnAction(e -> primaryStage.setScene(loginScene));

        pane.setTop(formPane);
        pane.setCenter(productList);
        return pane;
    }

    private Pane createBuyerPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        VBox productBox = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(productBox);
        scrollPane.setFitToWidth(true);

        for (Product product : seller.getProducts()) {
            BorderPane productCard = new BorderPane();
            productCard.setPadding(new Insets(10));
            productCard.setStyle("-fx-border-style: solid; -fx-border-width: 1; -fx-border-color: gray;");

            Label nameLabel = new Label(product.getName());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Label priceLabel = new Label("$" + product.getPrice());
            Label stockLabel = new Label("Stock: " + product.getStock());

            VBox infoBox = new VBox(5, priceLabel, stockLabel);
            Button addToCartButton = new Button("Add to Cart");

            productCard.setTop(nameLabel);
            productCard.setCenter(infoBox);
            productCard.setBottom(addToCartButton);
            BorderPane.setAlignment(nameLabel, Pos.CENTER);
            BorderPane.setAlignment(addToCartButton, Pos.CENTER);

            addToCartButton.setOnAction(e -> {
                if (product.getStock() > 0) {
                    currentBuyer.addToCart(product);
                    showAlert(Alert.AlertType.INFORMATION, product.getName() + " added to cart!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Sorry, this product is out of stock!");
                }
            });

            productBox.getChildren().add(productCard);
        }

        Button viewCartButton = new Button("View Cart");
        Button backButton = new Button("Back to Login");
        HBox buttonBox = new HBox(10, viewCartButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        viewCartButton.setOnAction(e -> primaryStage.setScene(cartScene));
        backButton.setOnAction(e -> primaryStage.setScene(loginScene));

        pane.setCenter(scrollPane);
        pane.setBottom(buttonBox);
        return pane;
    }

    private Pane createCartPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        if (currentBuyer == null) {
            Label messageLabel = new Label("Please log in as a buyer to view your cart");
            messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            pane.setCenter(messageLabel);
            return pane;
        }

        VBox cartItemsBox = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(cartItemsBox);
        scrollPane.setFitToWidth(true);

        for (Product product : currentBuyer.getCart()) {
            BorderPane itemPane = new BorderPane();
            itemPane.setPadding(new Insets(10));
            itemPane.setStyle("-fx-border-style: solid; -fx-border-width: 1; -fx-border-color: gray;");

            Label nameLabel = new Label(product.getName());
            Label priceLabel = new Label("$" + product.getPrice());

            itemPane.setLeft(nameLabel);
            itemPane.setRight(priceLabel);

            cartItemsBox.getChildren().add(itemPane);
        }

        Label totalLabel = new Label("Total: $" + currentBuyer.getTotalAmount());
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button checkoutButton = new Button("Checkout");
        Button backButton = new Button("Back to Products");
        HBox buttonBox = new HBox(10, checkoutButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        checkoutButton.setOnAction(e -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("PayPal", "PayPal", "Crypto");
            dialog.setTitle("Checkout");
            dialog.setHeaderText("Select Payment Method");
            dialog.showAndWait().ifPresent(choice -> {
                PaymentMethod paymentMethod = choice.equals("PayPal") ? new PayPal() : new Crypto();
                paymentMethod.amount = currentBuyer.getTotalAmount();
                paymentMethod.processPayment();

                Order order = new Order("ORD" + System.currentTimeMillis(),
                    currentBuyer, currentBuyer.getCart());
                currentBuyer.clearCart();

                showAlert(Alert.AlertType.INFORMATION,
                    "Order placed successfully!\nTotal amount: $" + order.calculateTotal());
                primaryStage.setScene(buyerScene);
            });
        });

        backButton.setOnAction(e -> primaryStage.setScene(buyerScene));

        pane.setTop(totalLabel);
        pane.setCenter(scrollPane);
        pane.setBottom(buttonBox);
        BorderPane.setAlignment(totalLabel, Pos.CENTER);
        return pane;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}