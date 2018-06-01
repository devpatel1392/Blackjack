package blackjack;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Blackjack extends Application {
    private Deck deck = new Deck();
    private Hand dealer, player;
    private Text message = new Text();
    private Slider bet = new Slider(0, 500, 0);
    private double money = 500;
    private double betAmount;
    
    private SimpleBooleanProperty canPlay = new SimpleBooleanProperty(false);
    
    private HBox handDealer = new HBox(20);
    private HBox handPlayer = new HBox(20);
    
    private Parent createContent() {
        dealer = new Hand(handDealer.getChildren());
        player = new Hand(handPlayer.getChildren());
        
        Pane root = new Pane();
        root.setPrefSize(800, 600);
        
        Region bg = new Region();
        bg.setPrefSize(800, 600);
        bg.setStyle("-fx-bg-color: rgba(0, 0, 0, 1)");
        
        HBox hb1 = new HBox(5);
        hb1.setPadding(new Insets(5,5,5,5));
        Rectangle playField = new Rectangle(550, 560);
        playField.setArcWidth(50);
        playField.setArcHeight(50);
        playField.setFill(Color.GREEN);
        Rectangle betField = new Rectangle(230, 560);
        betField.setArcWidth(50);
        betField.setArcHeight(50);
        betField.setFill(Color.PURPLE);
        
        StackPane playFieldStack = new StackPane();
        
        VBox vb1 = new VBox(50);
        vb1.setAlignment(Pos.TOP_CENTER);
        
        Text scoreDealer = new Text("Dealer: ");
        Text scorePlayer = new Text("Player: ");
        
        vb1.getChildren().addAll(scoreDealer, handDealer, message, handPlayer, scorePlayer);
        
        StackPane betFieldStack = new StackPane();
        
        VBox vb2 = new VBox(20);
        vb2.setAlignment(Pos.CENTER);
        
        Button play = new Button("PLAY");
        Button hit = new Button("HIT");
        Button stand = new Button("STAND");
        
        HBox hb2 = new HBox(15, hit, stand);
        hb2.setAlignment(Pos.CENTER);
        

        bet.prefWidthProperty().bind(vb2.widthProperty().multiply(0.90));
        bet.setMajorTickUnit(100);
        bet.setMinorTickCount(1);
        bet.setShowTickMarks(true);
        bet.setShowTickLabels(true);
        bet.setSnapToTicks(true);
        
        Text moneyAmount = new Text("Money: " + money);
        
        vb2.getChildren().addAll(moneyAmount, bet, play, hb2);
        
        hb1.getChildren().addAll(new StackPane(playField, vb1), new StackPane(betField, vb2));
        root.getChildren().addAll(bg, hb1);
        
        play.disableProperty().bind(canPlay);
        hit.disableProperty().bind(canPlay.not());
        stand.disableProperty().bind(canPlay.not());
        
        scorePlayer.textProperty().bind(new SimpleStringProperty("Player: ").concat(player.valueProperty().asString()));
        scoreDealer.textProperty().bind(new SimpleStringProperty("Dealer: ").concat(dealer.valueProperty().asString()));
        
        player.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue.intValue() >= 21) {
                endGame();
            }
        });
        dealer.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue.intValue() >= 21) {
                endGame();
            }
        });
        
        play.setOnAction(event -> {
            betAmount = bet.getValue();
            startNewGame();
        });
        
        hit.setOnAction(event -> {
            player.takeCard(deck.drawCard());
        });
        
        stand.setOnAction(event -> {
            while (dealer.valueProperty().get() < 17) {
                dealer.takeCard(deck.drawCard());
            }
            endGame();
        });
        return root;
    }
    
    private void startNewGame() {
        canPlay.set(true);
        message.setText("");
        
        deck.refill();
        
        dealer.reset();
        player.reset();
        
        player.takeCard(deck.drawCard());
        dealer.takeCard(deck.drawCard());
        player.takeCard(deck.drawCard());
        dealer.takeCard(deck.drawCard());
    }
    
    private void endGame() {
        canPlay.set(false);
        
        int dealerValue = dealer.valueProperty().get();
        int playerValue = player.valueProperty().get();
        String winner = "Exceptional case: d: " + dealerValue + " p: " + playerValue;
        
        if (dealerValue == 21 || playerValue > 21 || dealerValue == playerValue || (dealerValue < 21 && dealerValue > playerValue)) {
            winner = "DEALER";
            money -= betAmount;
        } else if (playerValue == 21 || dealerValue > 21 || playerValue > dealerValue) {
            winner = "PLAYER";
            money += betAmount;
        }
        
        message.setText(winner + "WON");
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Blackjack");
        primaryStage.show();
    }
}