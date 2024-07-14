package dev.AP.assignment;

//Changes
/* UI Update
 * Exit button fix
 * Default black/white name
 * Timer bug fix
 * Pause menu implementation*/

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HelloApplication extends Application {
    HBox root = new HBox();
    private Stage stage;
    private String player1Name;
    private String player2Name;
    private int boardSize = 8;
    private Color[][] board;
    private boolean whiteTurn = true;
    private Text turnLabel;
    private Text timerLabel;
    private Timer timer;
    private int timeSeconds = 0;
    private boolean isPaused = false;
    private Button pauseButton;
    private GridPane boardGrid;
    private Text player1Score;
    private Text player2Score;
    private String loggedInUser;

    public HelloApplication() {
        File saveDir = new File("saves");
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Scene scene = new Scene(root, 1024, 576);
        stage.setScene(scene);
        showLoginScene();
        stage.setTitle("Othello");
        stage.show();
    }

    private void showLoginScene() {
        // Title
        Text title = new Text("Othello");
        title.setFont(Font.font(24));
        title.setFill(Color.WHITE);

        HBox titleBox = new HBox(title);
        title.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 70));
        title.setUnderline(true);
        title.setFill(Color.WHITE);
        title.setEffect(new DropShadow());
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setBackground(new Background(new BackgroundFill(Color.rgb(38, 38, 39), CornerRadii.EMPTY,
                Insets.EMPTY)));
        titleBox.setPadding(new Insets(10, 0, 10, 0));

        // Username
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 20));
        usernameLabel.setTextFill(Color.WHITE);
        TextField usernameField = new TextField();
        usernameField.setBackground(new Background(new BackgroundFill(Color.rgb(25, 24, 24), new CornerRadii(10, 10,
                10, 10, false), Insets.EMPTY)));
        usernameField.setStyle("-fx-text-fill: white;");
        usernameField.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 20));
        HBox usernameBox = new HBox(10, usernameLabel, usernameField);
        usernameBox.setAlignment(Pos.CENTER);

        // Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 20));
        passwordLabel.setTextFill(Color.WHITE);
        TextField passwordField = new TextField();
        passwordField.setBackground(new Background(new BackgroundFill(Color.rgb(25, 24, 24), new CornerRadii(10, 10,
                10, 10, false), Insets.EMPTY)));
        passwordField.setStyle("-fx-text-fill: white;");
        passwordField.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 20));
        HBox passwordBox = new HBox(10, passwordLabel, passwordField);
        passwordBox.setAlignment(Pos.CENTER);

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-padding: 8 15 15 15;" +
                "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (authenticateUser(username, password)) {
                loggedInUser = username;
                showNewGameScene();
            } else {
                // Show error message
                Label errorLabel = new Label("Invalid username or password");
                errorLabel.setTextFill(Color.RED);
                root.getChildren().add(errorLabel);
            }
        });

        HBox loginBox = new HBox(10, loginButton);
        loginBox.setAlignment(Pos.CENTER);

        //Button Hover Effect
        loginButton.setOnMouseEntered(event -> {
            loginButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        loginButton.setOnMouseExited(event -> {
            loginButton.setStyle("-fx-padding: 8 15 15 15;" +
                    "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                    "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        // Layout
        VBox layout = new VBox(20, titleBox, usernameBox, passwordBox, loginBox);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(Color.rgb(38, 38, 39), CornerRadii.EMPTY,
                Insets.EMPTY)));

        StackPane root = new StackPane(layout);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

//        Scene scene = new Scene(root, 1024, 576);
//        stage.setScene(scene);
        stage.getScene().setRoot(root);
    }

    private boolean authenticateUser(String username, String password) {
        // Implement your authentication logic here
        // For this example, we'll use a simple check
        return !username.isEmpty() && !password.isEmpty();
    }

    private void showNewGameScene() {
        // Title
        Text title = new Text("Othello");
        title.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 70));
        title.setUnderline(true);
        title.setFill(Color.WHITE);
        title.setEffect(new DropShadow());

        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setBackground(new Background(new BackgroundFill(Color.rgb(38, 38, 39), new CornerRadii(10, 10, 10,
                10, false), Insets.EMPTY)));
        titleBox.setPadding(new Insets(10, 0, 10, 0));

        // Player 1 Name
        Label player1Label = new Label("Player 1 Name:");
        player1Label.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 20));
        player1Label.setTextFill(Color.WHITE);
        TextField player1Field = new TextField();
        player1Field.setBackground(new Background(new BackgroundFill(Color.rgb(25, 24, 24), new CornerRadii(10, 10,
                10, 10, false), Insets.EMPTY)));
        player1Field.setStyle("-fx-text-fill: white;");
        player1Field.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 20));
        HBox player1Box = new HBox(10, player1Label, player1Field);
        player1Box.setAlignment(Pos.CENTER);

        // Player 2 Name
        Label player2Label = new Label("Player 2 Name:");
        player2Label.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 20));
        player2Label.setTextFill(Color.WHITE);
        TextField player2Field = new TextField();
        player2Field.setBackground(new Background(new BackgroundFill(Color.rgb(25, 24, 24), new CornerRadii(10, 10,
                10, 10, false), Insets.EMPTY)));
        player2Field.setStyle("-fx-text-fill: white;");
        player2Field.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 20));
        HBox player2Box = new HBox(10, player2Label, player2Field);
        player2Box.setAlignment(Pos.CENTER);

        // Difficulty
        ComboBox<String> difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll("6X6 Grid", "8X8 Grid", "10X10 Grid");
        difficultyComboBox.setPromptText("Difficulty");
        difficultyComboBox.setStyle("-fx-padding: 8 15 15 15;" +
                "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;-fx-font-size: 1.1em;");
        difficultyComboBox.setOnAction(e -> {
            String selected = difficultyComboBox.getValue();
            if (selected.equals("6X6 Grid")) {
                boardSize = 6;
            } else if (selected.equals("8X8 Grid")) {
                boardSize = 8;
            } else if (selected.equals("10X10 Grid")) {
                boardSize = 10;
            }
        });

        Button playButton = new Button("Play");
        playButton.setStyle("-fx-padding: 8 15 15 15;" +
                "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        playButton.setOnAction(e -> {
            player1Name = player1Field.getText();
            player2Name = player2Field.getText();
            showGameBoardScene(false);
            startTimer(timeSeconds);
        });

        HBox difficultyBox = new HBox(10, difficultyComboBox, playButton);
        difficultyBox.setAlignment(Pos.CENTER);

        // Hover effects
        playButton.setOnMouseEntered(event -> {
            playButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        playButton.setOnMouseExited(event -> {
            playButton.setStyle("-fx-padding: 8 15 15 15;" +
                    "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                    "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        Button loadGameButton = new Button("Load Game");
        loadGameButton.setStyle("-fx-padding: 8 15 15 15; " +
                "                -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0; " +
                "                -fx-background-radius: 8; " +
                "                -fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%)" +
                ", #9d4024, #d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c); " +
                "                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1); " +
                "                -fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        loadGameButton.setOnAction(e -> promptLoadGame());

        loadGameButton.setOnMouseEntered(event -> {
            loadGameButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        loadGameButton.setOnMouseExited(event -> {
            loadGameButton.setStyle("-fx-padding: 8 15 15 15;" +
                    "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                    "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        Button deleteButton = new Button("Delete Saved File");
        deleteButton.setStyle("-fx-padding: 8 15 15 15; " +
                "                -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0; " +
                "                -fx-background-radius: 8; " +
                "                -fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%)" +
                ", #9d4024, #d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c); " +
                "                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1); " +
                "                -fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        deleteButton.setOnAction(e -> promptDeleteSaveGame());

        deleteButton.setOnMouseEntered(event -> {
            deleteButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        deleteButton.setOnMouseExited(event -> {
            deleteButton.setStyle("-fx-padding: 8 15 15 15;" +
                    "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                    "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        });


        // Layout
        VBox layout = new VBox(20, titleBox, player1Box, player2Box, difficultyBox, loadGameButton, deleteButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(Color.rgb(38, 38, 39), CornerRadii.EMPTY,
                Insets.EMPTY)));

        StackPane root = new StackPane(layout);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

//        Scene scene = new Scene(root, 1024, 576);
        stage.getScene().setRoot(root);
//        stage.setScene();
    }

    private void showGameBoardScene(Boolean val) {
        Scene scene;

        // Top section with player info and game controls
        Text player1NameText;
        Text player2NameText;

        if (!player1Name.isBlank() || !player1Name.isEmpty())
            player1NameText = new Text(player1Name);
        else
            player1NameText = new Text("White");

        if (!player1Name.isBlank() || !player1Name.isEmpty())
            player2NameText = new Text(player2Name);
        else
            player2NameText = new Text("Black");

        player1NameText.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 40));
        player2NameText.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 40));
        player1Score = new Text("2");
        player1Score.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 40));
        player2Score = new Text("2");
        player2Score.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 40));
        turnLabel = new Text("White turn");
        turnLabel.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 40));
        timerLabel = new Text("00:00");
        timerLabel.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 40));

        player1NameText.setFill(Color.WHITE);
        player2NameText.setFill(Color.WHITE);
        player1Score.setFill(Color.WHITE);
        player2Score.setFill(Color.BLACK);
        turnLabel.setFill(Color.WHITE);
        timerLabel.setFill(Color.WHITE);

        HBox player1Box = new HBox(10, player1NameText, player1Score);
        player1Box.setAlignment(Pos.CENTER);
        HBox player2Box = new HBox(10, player2NameText, player2Score);
        player2Box.setAlignment(Pos.CENTER);

        VBox turnBox = new VBox(5, turnLabel, timerLabel);
        turnBox.setAlignment(Pos.CENTER);

        VBox topBox = new VBox(50, player1Box, turnBox, player2Box);
        topBox.setAlignment(Pos.CENTER);
        topBox.setBackground(new Background(new BackgroundFill(Color.rgb(25, 24, 24), CornerRadii.EMPTY,
                Insets.EMPTY)));
        topBox.setPadding(new Insets(10));

        VBox emptyBox = new VBox();

        VBox sideBox = new VBox(50, topBox);
        sideBox.setAlignment(Pos.CENTER);
        sideBox.setEffect(new DropShadow());

        // Buttons
        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-padding: 8 15 15 15;" +
                "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#13a343 0%,#12903b 100%), #249d40, " +
                "#3ad86e, radial-gradient(center 50% 50%, radius 100%,#3ad96e,#2cc54e);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        resetButton.setOnAction(e -> resetGame());

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-padding: 8 15 15 15;" +
                " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#1373a3 0%,#126790 100%), #24819d, " +
                "#3aa4d8, radial-gradient(center 50% 50%, radius 100%,#3aa5d9,#2ca3c5);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 1.1em;-fx-text-fill: white;");


        saveButton.setOnAction(e -> promptSaveGame());

        Button loadButton = new Button("Load");
        loadButton.setStyle("-fx-padding: 8 15 15 15;" +
                " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#4313a3 0%,#3b1290 100%), #40249d, " +
                "#6e3ad8, radial-gradient(center 50% 50%, radius 100%,#6e3ad9,#4e2cc5);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 1.1em;-fx-text-fill: white;");
//        loadButton.setOnAction(e -> );

        pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-padding: 8 15 15 15;" +
                " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a3132b 0%,#901228 100%), #9d2445, " +
                "#d83a55, radial-gradient(center 50% 50%, radius 100%,#d93a56,#c52c57);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 1.1em;-fx-text-fill: white;");
        pauseButton.setOnAction(e -> {
            if (pauseButton.getText().equals("Pause")) {
                showPauseMenu();
                root.setEffect(new GaussianBlur());
                root.setDisable(true);
            } else {
                root.setEffect(null);
            }
            togglePause();
        });

        Button endButton = new Button("Exit");
        endButton.setStyle("-fx-padding: 8 15 15 15;" +
                "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        endButton.setOnAction(e -> System.exit(0));

        VBox topButtonsBox = new VBox(10, resetButton, saveButton, loadButton, pauseButton);
        topButtonsBox.setAlignment(Pos.CENTER);
        topButtonsBox.setPadding(new Insets(10));

        VBox buttonsBox = new VBox(100, topButtonsBox, endButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10));


        // Game board
        if (!val) {
            board = new Color[boardSize][boardSize];
            initializeBoard();
        }

        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setPadding(new Insets(20));
        boardGrid.setBackground(new Background(new BackgroundFill(Color.rgb(39, 38, 38), CornerRadii.EMPTY,
                Insets.EMPTY)));
        boardGrid.setEffect(new DropShadow());

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                StackPane cell = createCell(row, col);
                boardGrid.add(cell, col, row);
            }
        }
        // Title
        Text title = new Text("Othello");
        title.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 70));
        title.setUnderline(true);
        title.setFill(Color.WHITE);
        title.setEffect(new DropShadow());

        // Hover effects
        resetButton.setOnMouseEntered(event -> {
            resetButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        resetButton.setOnMouseExited(event -> {
            resetButton.setStyle("-fx-padding: 8 15 15 15;" +
                    "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#13a343 0%,#12903b 100%), #249d40, " +
                    "#3ad86e, radial-gradient(center 50% 50%, radius 100%,#3ad96e,#2cc54e);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        saveButton.setOnMouseEntered(event -> {
            saveButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        saveButton.setOnMouseExited(event -> {
            saveButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#1373a3 0%,#126790 100%), #24819d, " +
                    "#3aa4d8, radial-gradient(center 50% 50%, radius 100%,#3aa5d9,#2ca3c5);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        loadButton.setOnMouseEntered(event -> {
            loadButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        loadButton.setOnMouseExited(event -> {
            loadButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#4313a3 0%,#3b1290 100%), #40249d, " +
                    "#6e3ad8, radial-gradient(center 50% 50%, radius 100%,#6e3ad9,#4e2cc5);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        pauseButton.setOnMouseEntered(event -> {
            pauseButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        pauseButton.setOnMouseExited(event -> {
            pauseButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a3132b 0%,#901228 100%), #9d2445, " +
                    "#d83a55, radial-gradient(center 50% 50%, radius 100%,#d93a56,#c52c57);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        endButton.setOnMouseEntered(event -> {
            endButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        endButton.setOnMouseExited(event -> {
            endButton.setStyle("-fx-padding: 8 15 15 15;" +
                    "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                    "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        // Layout
        VBox boardBox = new VBox(1, title, boardGrid);
        boardBox.setAlignment(Pos.CENTER);

        root = new HBox(150, buttonsBox, boardBox, sideBox);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(39, 38, 38), CornerRadii.EMPTY, Insets.EMPTY)));

        addGhostPieces();

//        scene = new Scene(root, 1024, 576);
//        stage.setScene(scene);
        stage.getScene().setRoot(root);
        boardGrid.getChildren().clear();
        restoreBoardUI();
        updateScores();
        turnLabel.setText(whiteTurn ? "White turn" : "Black turn");

    }

    private void initializeBoard() {
        int mid = boardSize / 2;
        board[mid - 1][mid - 1] = Color.WHITE;
        board[mid][mid] = Color.WHITE;
        board[mid - 1][mid] = Color.BLACK;
        board[mid][mid - 1] = Color.BLACK;
    }

    private StackPane createCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setMinSize(50, 50);
        cell.setMaxSize(50, 50);
        cell.setStyle("-fx-background-color: " + ((row + col) % 2 == 0 ? "#FF914D" : "#BB6835") + ";");
        cell.setOnMouseClicked(e -> {
            clearGhostPieces();
            handleCellClick(row, col);
            addGhostPieces();
        });
        if (board[row][col] != null) {
            cell.getChildren().add(createPiece(board[row][col]));
        }
        return cell;
    }

    private void handleCellClick(int row, int col) {
        if (board[row][col] == null && isValidMove(row, col, whiteTurn ? Color.WHITE : Color.BLACK)) {
            placePiece(row, col, whiteTurn ? Color.WHITE : Color.BLACK);
            whiteTurn = !whiteTurn;
            turnLabel.setText(whiteTurn ? "White turn" : "Black turn");
            updateScores();
            checkGameOver(); // Check if the game is over
        }
    }

    private void placePiece(int row, int col, Color color) {
        board[row][col] = color;
        updateCell(row, col);
        flipPieces(row, col, color);
        updateScores();
        checkGameOver(); // Check if the game is over
    }

    private void updateCell(int row, int col) {
        StackPane cell = (StackPane) getNodeByRowColumnIndex(row, col, boardGrid);
        if (cell != null) {
            cell.getChildren().clear();
            cell.getChildren().add(createPiece(board[row][col]));
        }
    }

    private javafx.scene.Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }

    private Circle createPiece(Color color) {
        Circle piece = new Circle(20); // Adjust the radius as needed
        piece.setFill(color);
        piece.setStroke(Color.BLACK); // Optional: Add a black stroke for better visibility
        return piece;
    }

    private void clearGhostPieces() {
        StackPane cell;
        Circle piece = new Circle(20); // Adjust the radius as needed
        piece.setFill(Color.DARKGRAY);
        piece.setStroke(Color.DARKGRAY);


        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                cell = (StackPane) getNodeByRowColumnIndex(i, j, boardGrid);
                if (board[i][j] == Color.GRAY) {
                    cell.getChildren().clear();
                    board[i][j] = null;
                }
            }
        }
    }

    private void addGhostPieces() {

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == null && isValidMove(row, col, whiteTurn ? Color.WHITE : Color.BLACK)) {
                    placeGhostPiece(row, col);
                }
            }
        }
    }

    private void placeGhostPiece(int row, int col) {
        board[row][col] = Color.GRAY;
        updateGhostCell(row, col);
    }

    private Circle createGhostPiece() {
        Circle piece = new Circle(20); // Adjust the radius as needed
        piece.setFill(Color.DARKGRAY);
        piece.setStroke(Color.DARKGRAY);
        return piece;
    }

    private void updateGhostCell(int row, int col) {
        StackPane cell = (StackPane) getNodeByRowColumnIndex(row, col, boardGrid);
        if (cell != null) {
            cell.getChildren().clear();
            cell.getChildren().add(createGhostPiece());
        }
    }

    private void flipPieces(int row, int col, Color color) {
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] dir : directions) {
            List<int[]> piecesToFlip = new ArrayList<>();
            int r = row + dir[0];
            int c = col + dir[1];
            while (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] != null && board[r][c] != color) {
                piecesToFlip.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
            }
            if (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == color) {
                for (int[] pos : piecesToFlip) {
                    board[pos[0]][pos[1]] = color;
                    updateCell(pos[0], pos[1]);
                }
            }
        }
        updateScores();
    }

    private boolean isValidMove(int row, int col, Color color) {
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            boolean hasOpponentPiece = false;
            while (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] != null && board[r][c] != color && board[r][c] != Color.GRAY) {
                hasOpponentPiece = true;
                r += dir[0];
                c += dir[1];
            }
            if (hasOpponentPiece && r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == color) {
                return true;
            }
        }
        return false;
    }

    private void resetGame() {
        showNewGameScene();
        timeSeconds = 0;
        timer.cancel();
    }

    private void promptDeleteSaveGame() {
        File saveDir = new File("saves");
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (saveFiles == null || saveFiles.length == 0) {
            System.out.println("No saved games found.");
            return;
        }

        BinarySearchTree bst = new BinarySearchTree();
        for (File file : saveFiles) {
            String name = file.getName().substring(0, file.getName().length() - 4); // Remove .txt extension
            bst.insert(name);
        }

        List<String> choices = Arrays.stream(saveFiles)
                .map(File::getName)
                .map(name -> name.substring(0, name.length() - 4)) // Remove .txt extension
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Delete Saved Game");
        dialog.setHeaderText("Delete a saved game");
        dialog.setContentText("Choose a save file to delete:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileName -> {
            bst.delete(fileName);
            deleteGameFile(fileName);
        });
    }

    private void deleteGameFile(String fileName) {
        String filePath = "saves/" + fileName + ".txt";
        File file = new File(filePath);
        if (file.delete()) {
            System.out.println("Saved game " + fileName + " deleted successfully.");
        } else {
            System.out.println("Failed to delete saved game " + fileName + ".");
        }
    }

    // Method to prompt user for save file name
    private void promptSaveGame() {
        TextInputDialog dialog = new TextInputDialog("savegame");
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Save your game");
        dialog.setContentText("Enter save file name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                SecretKey key = CryptoUtils.loadOrGenerateKey("saves/secretKey.key");
                saveGame(result.get(), key);
            } catch (IOException e) {
                System.out.println("An error occurred while loading the encryption key: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void promptLoadGame() {
        File saveDir = new File("saves");
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".bin"));
        if (saveFiles == null || saveFiles.length == 0) {
            System.out.println("No saved games found.");
            return;
        }

        List<String> choices = Arrays.stream(saveFiles)
                .map(File::getName)
                .map(name -> name.substring(0, name.length() - 4)) // Remove .bin extension
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Load a saved game");
        dialog.setContentText("Choose your saved game:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                SecretKey key = CryptoUtils.loadKey("saves/secretKey.key");
                loadGame(result.get(), key);
            } catch (IOException e) {
                System.out.println("An error occurred while loading the encryption key: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void saveGame(String fileName, SecretKey key) {
        String filePath = "saves/" + fileName + ".bin"; // Store saves in a "saves" directory
        StringBuilder gameData = new StringBuilder();

        gameData.append("Player 1: ").append(
                player1Name.isEmpty() || player1Name.isBlank() ? "White" : player1Name).append("\n");
        gameData.append("Player 2: ").append(
                player2Name.isEmpty() || player2Name.isBlank() ? "Black" : player2Name).append("\n");
        gameData.append("Board Size: ").append(boardSize).append("\n");
        gameData.append("White Turn: ").append(whiteTurn).append("\n");
        gameData.append("Time Taken: ").append(timeSeconds).append("\n");
        gameData.append("Player 1 Score: ").append(player1Score.getText()).append("\n");
        gameData.append("Player 2 Score: ").append(player2Score.getText()).append("\n");

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == null) {
                    gameData.append("EMPTY ");
                } else if (board[row][col] == Color.WHITE) {
                    gameData.append("WHITE ");
                } else if (board[row][col] == Color.BLACK) {
                    gameData.append("BLACK ");
                } else if (board[row][col] == Color.GRAY) {
                    gameData.append("EMPTY "); // Treat ghost pieces as EMPTY
                }
            }
            gameData.append("\n");
        }

        try {
            byte[] encryptedData = CryptoUtils.encrypt(gameData.toString(), key);
            Files.write(Paths.get(filePath), encryptedData);
            System.out.println("Game saved successfully! " + filePath);
        } catch (Exception e) {
            System.out.println("An error occurred while saving the game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadGame(String fileName, SecretKey key) {
        String filePath = "saves/" + fileName + ".bin";
        System.out.println("Loading game from file: " + filePath);

        try {
            byte[] encryptedData = Files.readAllBytes(Paths.get(filePath));
            String gameData = CryptoUtils.decrypt(encryptedData, key);

            BufferedReader reader = new BufferedReader(new StringReader(gameData));
            player1Name = reader.readLine().split(": ")[1];
            player2Name = reader.readLine().split(": ")[1];
            boardSize = Integer.parseInt(reader.readLine().split(": ")[1].trim());
            whiteTurn = Boolean.parseBoolean(reader.readLine().split(": ")[1].trim());
            timeSeconds = Integer.parseInt(reader.readLine().split(": ")[1].trim());
            String p1Score = reader.readLine().split(": ")[1].trim();
            String p2Score = reader.readLine().split(": ")[1].trim();
            player1Score = new Text(p1Score);
            player2Score = new Text(p2Score);

            board = new Color[boardSize][boardSize];
            for (int row = 0; row < boardSize; row++) {
                String[] line = reader.readLine().split(" ");
                for (int col = 0; col < boardSize; col++) {
                    switch (line[col]) {
                        case "WHITE":
                            board[row][col] = Color.WHITE;
                            break;
                        case "BLACK":
                            board[row][col] = Color.BLACK;
                            break;
                        case "EMPTY":
                            board[row][col] = null;
                            break;
                    }
                }
            }

            // Refresh UI to reflect the loaded game state
            showGameBoardScene(true);
            restoreBoardUI();
            updateScores();
            turnLabel.setText(whiteTurn ? "White turn" : "Black turn");
            isPaused = false;
            startTimer(timeSeconds);

            System.out.println("Game loaded successfully!");
        } catch (Exception e) {
            System.out.println("An error occurred while loading the game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void restoreBoardUI() {
        boardGrid.getChildren().clear(); // Clear existing cells to avoid duplicates
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                StackPane cell = createCell(row, col);
                boardGrid.add(cell, col, row);
            }
        }
    }

    private void startTimer(int initialTime) {
        if (timer != null) {
            timer.cancel();
        }
        timeSeconds = initialTime;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    timeSeconds++;
                    int minutes = timeSeconds / 60;
                    int seconds = timeSeconds % 60;
                    timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
                }
            }
        }, 1000, 1000);
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseButton.setText(isPaused ? "Resume" : "Pause");
    }

    private void updateScores() {
        int whiteScore = 0;
        int blackScore = 0;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == Color.WHITE) {
                    whiteScore++;
                } else if (board[row][col] == Color.BLACK) {
                    blackScore++;
                }
            }
        }

        player1Score.setText(String.valueOf(whiteScore));
        player2Score.setText(String.valueOf(blackScore));
    }

    private void checkGameOver() {
        boolean hasEmptyCell = false;
        boolean hasValidMoveWhite = false;
        boolean hasValidMoveBlack = false;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == null) {
                    hasEmptyCell = true;
                    if (isValidMove(row, col, Color.WHITE)) {
                        hasValidMoveWhite = true;
                    }
                    if (isValidMove(row, col, Color.BLACK)) {
                        hasValidMoveBlack = true;
                    }
                }
            }
        }

        if (!hasEmptyCell || (!hasValidMoveWhite && !hasValidMoveBlack)) {
            declareWinner();
        } else if (!hasValidMoveWhite && whiteTurn) {
            whiteTurn = false; // Skip White's turn
            turnLabel.setText("Black turn");
        } else if (!hasValidMoveBlack && !whiteTurn) {
            whiteTurn = true; // Skip Black's turn
            turnLabel.setText("White turn");
        }
    }

    private void declareWinner() {
        int whiteScore = Integer.parseInt(player1Score.getText());
        int blackScore = Integer.parseInt(player2Score.getText());

        String winner;
        if (whiteScore > blackScore) {
            winner = player1Name + " (White) wins!";
        } else if (blackScore > whiteScore) {
            winner = player2Name + " (Black) wins!";
        } else {
            winner = "It's a draw!";
        }

        // Display the winner
        turnLabel.setText(winner);
        timer.cancel();
        pauseButton.setDisable(true);
    }

    private void showPauseMenu() {

        Stage primaryStage = new Stage();

        // Title
        Text title = new Text("Othello");
        title.setFont(Font.font("adobe arabic", FontWeight.BOLD, FontPosture.REGULAR, 50));
        title.setUnderline(true);
        title.setFill(Color.WHITE);

        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);

        // Resume Button
        Button resumeButton = new Button("RESUME");
        resumeButton.setStyle("-fx-padding: 8 15 15 15;" +
                "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;-fx-font-size: 1.1em;-fx-text-fill: white;");
        resumeButton.setOnAction(e -> {
            root.setDisable(false);
            pauseButton.fire();
            primaryStage.close();
        });

        HBox resumeBox = new HBox(10, resumeButton);
        resumeBox.setAlignment(Pos.CENTER);

        // Settings Button
        Button settingsButton = new Button("SETTINGS");
        settingsButton.setStyle("-fx-padding: 8 15 15 15;" +
                " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#4313a3 0%,#3b1290 100%), #40249d, " +
                "#6e3ad8, radial-gradient(center 50% 50%, radius 100%,#6e3ad9,#4e2cc5);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 1.1em;-fx-text-fill: white;");

        HBox settingsBox = new HBox(10, settingsButton);
        settingsBox.setAlignment(Pos.CENTER);

        Setting setting = new Setting();
        settingsButton.setOnAction(e -> Setting.openSettings());

        // Log Out Button
        Button logoutButton = new Button("LOG OUT");
        logoutButton.setStyle("-fx-padding: 8 15 15 15;" +
                " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#1373a3 0%,#126790 100%), #24819d, " +
                "#3aa4d8, radial-gradient(center 50% 50%, radius 100%,#3aa5d9,#2ca3c5);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 1.1em;-fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            root.setDisable(false);
            pauseButton.fire();
            timer.cancel();
            timeSeconds = 0;
            showLoginScene();
            primaryStage.close();
        });

        HBox logoutBox = new HBox(10, logoutButton);
        logoutBox.setAlignment(Pos.CENTER);

        // Exit Button
        Button exitButton = new Button("EXIT");
        exitButton.setStyle("-fx-padding: 8 15 15 15;" +
                " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a3132b 0%,#901228 100%), #9d2445, " +
                "#d83a55, radial-gradient(center 50% 50%, radius 100%,#d93a56,#c52c57);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 1.1em;-fx-text-fill: white;");
        exitButton.setOnAction(e -> System.exit(0));

        HBox exitBox = new HBox(10, exitButton);
        exitBox.setAlignment(Pos.CENTER);

        //Mouse Over Effects

        //Mouse Pressed Events
        exitButton.setOnMouseEntered(event -> {
            exitButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        logoutButton.setOnMouseEntered(event -> {
            logoutButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        settingsButton.setOnMouseEntered(event -> {
            settingsButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        resumeButton.setOnMouseEntered(event -> {
            resumeButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#5d595a 0%,#525051 100%), #625f60, " +
                    "#8f8385, radial-gradient(center 50% 50%, radius 100%,#918285,#7b7677);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });

        exitButton.setOnMouseExited(event -> {
            exitButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a3132b 0%,#901228 100%), #9d2445, " +
                    "#d83a55, radial-gradient(center 50% 50%, radius 100%,#d93a56,#c52c57);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        logoutButton.setOnMouseExited(event -> {
            logoutButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#1373a3 0%,#126790 100%), #24819d, " +
                    "#3aa4d8, radial-gradient(center 50% 50%, radius 100%,#3aa5d9,#2ca3c5);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        settingsButton.setOnMouseExited(event -> {
            settingsButton.setStyle("-fx-padding: 8 15 15 15;" +
                    " -fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#4313a3 0%,#3b1290 100%), #40249d, " +
                    "#6e3ad8, radial-gradient(center 50% 50%, radius 100%,#6e3ad9,#4e2cc5);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;-fx-text-fill: white;");
        });
        resumeButton.setOnMouseExited(event -> {
            resumeButton.setStyle("-fx-padding: 8 15 15 15;" +
                    "-fx-background-insets: 0,0 0 5 0,0 0 6 0,0 0 7 0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color:linear-gradient(from 0% 93% to 0% 100%,#a34313 0%,#903b12 100%), #9d4024, " +
                    "#d86e3a, radial-gradient(center 50% 50%, radius 100%,#d96e3a,#c54e2c);" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75),4,0,0,1);" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 1.1em;" +
                    "-fx-text-fill: white;");
        });

        // Layout
        VBox layout = new VBox(20, titleBox, resumeBox, settingsBox, logoutBox, exitBox);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(Color.rgb(39, 38, 38), CornerRadii.EMPTY,
                Insets.EMPTY)));

        StackPane root = new StackPane(layout);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(root, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }

    class TreeNode {
        String value;
        TreeNode left, right;

        TreeNode(String value) {
            this.value = value;
            left = right = null;
        }
    }

    class BinarySearchTree {
        TreeNode root;

        void insert(String value) {
            root = insertRec(root, value);
        }

        TreeNode insertRec(TreeNode root, String value) {
            if (root == null) {
                root = new TreeNode(value);
                return root;
            }
            if (value.compareTo(root.value) < 0)
                root.left = insertRec(root.left, value);
            else if (value.compareTo(root.value) > 0)
                root.right = insertRec(root.right, value);
            return root;
        }

        void delete(String value) {
            root = deleteRec(root, value);
        }

        TreeNode deleteRec(TreeNode root, String value) {
            if (root == null)
                return root;
            if (value.compareTo(root.value) < 0)
                root.left = deleteRec(root.left, value);
            else if (value.compareTo(root.value) > 0)
                root.right = deleteRec(root.right, value);
            else {
                if (root.left == null)
                    return root.right;
                else if (root.right == null)
                    return root.left;
                root.value = minValue(root.right);
                root.right = deleteRec(root.right, root.value);
            }
            return root;
        }

        String minValue(TreeNode root) {
            String minValue = root.value;
            while (root.left != null) {
                minValue = root.left.value;
                root = root.left;
            }
            return minValue;
        }

        boolean search(String value) {
            return searchRec(root, value);
        }

        boolean searchRec(TreeNode root, String value) {
            if (root == null)
                return false;
            if (value.compareTo(root.value) == 0)
                return true;
            if (value.compareTo(root.value) < 0)
                return searchRec(root.left, value);
            return searchRec(root.right, value);
        }

        void inOrderTraversal(TreeNode node, Consumer<String> action) {
            if (node != null) {
                inOrderTraversal(node.left, action);
                action.accept(node.value);
                inOrderTraversal(node.right, action);
            }
        }
    }
}