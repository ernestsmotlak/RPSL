package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mpi.MPI;

import java.io.IOException;

public class Main extends Application {

    public static void main (String[] args) {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        Manager.me = MPI.COMM_WORLD.Rank();
        Manager.size = MPI.COMM_WORLD.Size();
        if(me == 0)
            Application.launch();
        else{
            Manager.delavec();
        }
    }
    @Override
    public void start(Stage primaryStage) throws IOException {

        primaryStage.setTitle("Rock Paper Scissors Lizard Spock!");
        Text text = new Text("");

        Button seq = new Button();
        seq.setText("Zaporedno");
        seq.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Manager.execType = Manager.ExecType.SEQUENTIAL;
                text.setText("Working...");
                if(Manager.me == 0){
                    Manager.init();
                    Manager.startTime = System.currentTimeMillis();
                    Manager.exec();
                    Manager.stopTime = System.currentTimeMillis();

                    Manager.evaluate();
                    int winner = Manager.findWinner();
                    String res = Manager.izpisZmagovalca(winner);
                    text.setText(res);
                }
            }
        });
        Button dist = new Button();
        dist.setText("Porazdeljeno");


        dist.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Manager.execType = Manager.ExecType.DISTRIBUTED;
                text.setText("Working...");
                if (Manager.me == 0) {
                    Manager.init();
                    Manager.startTime = System.currentTimeMillis();
                }
                Manager.exec();
                if (Manager.me == 0) {
                    Manager.stopTime = System.currentTimeMillis();

                    Manager.evaluate();
                    int winner = Manager.findWinner();
                    String res = Manager.izpisZmagovalca(winner);
                    text.setText(res);
                }

            }
        });

        Button paralel = new Button();
        paralel.setText("Vzporedno");

        paralel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Manager.execType = Manager.ExecType.PARALLEL;

                text.setText("Working...");
                if(Manager.me == 0){
                    Manager.init();
                    Manager.startTime = System.currentTimeMillis();
                    Manager.exec();
                    Manager.stopTime = System.currentTimeMillis();

                    Manager.evaluate();
                    int winner = Manager.findWinner();
                    String res = Manager.izpisZmagovalca(winner);
                    text.setText(res);
                }
            }
        });

        Label stevilo_igralcev = new Label("Stevilo Igralcev:");
        Label stevilo_tekem_med_igralci = new Label("Stevilo tekem med igralci:");
        TextField tf1=new TextField(String.valueOf(Manager.steviloIgralcev));
        tf1.textProperty().addListener((observable, oldValue, newValue) -> {

            int value = 0;
            try {
                value = Integer.parseInt(newValue);
                if(value > 0){
                    Manager.steviloIgralcev = value;
                    stevilo_igralcev.setTextFill(Color.color(0,0,0));
                }
                else{
                    stevilo_igralcev.setTextFill(Color.color(1,0,0));
                    System.out.println("Stevilka negativna.");
                }
            }catch(NumberFormatException e){
                System.out.println("Vneseno ni številka!");
                stevilo_igralcev.setTextFill(Color.color(1,0,0));
            }

        });
        TextField tf2=new TextField(String.valueOf(Manager.steviloTekemMedIgralci));
        tf2.textProperty().addListener((observable, oldValue, newValue) -> {
            int value = 0;
            try {
                value = Integer.parseInt(newValue);
                if(value > 0){
                    Manager.steviloTekemMedIgralci = value;
                    stevilo_tekem_med_igralci.setTextFill(Color.color(0,0,0));
                }
                else{
                    stevilo_tekem_med_igralci.setTextFill(Color.color(1,0,0));
                    System.out.println("Številka je negativna.");
                }
            }catch(NumberFormatException e){
                System.out.println("Vneseno ni številka!");
                stevilo_tekem_med_igralci.setTextFill(Color.color(1,0,0));
            }

        });


        HBox parameters_1 = new HBox(8);
        parameters_1.setAlignment(Pos.CENTER);
        parameters_1.getChildren().add(stevilo_igralcev);
        parameters_1.getChildren().add(tf1);

        HBox parameters_2 = new HBox(8);
        parameters_2.setAlignment(Pos.CENTER);
        parameters_2.getChildren().add(stevilo_tekem_med_igralci);
        parameters_2.getChildren().add(tf2);

        HBox horizontalButtons = new HBox(8);
        horizontalButtons.setAlignment(Pos.CENTER);
        horizontalButtons.getChildren().add(seq);
        horizontalButtons.getChildren().add(dist);
        horizontalButtons.getChildren().add(paralel);

        HBox horizontalText = new HBox(8);
        horizontalText.setAlignment(Pos.CENTER);
        horizontalText.getChildren().add(text);


        ToggleGroup group = new ToggleGroup();
        RadioButton button1 = new RadioButton("Sequential");
        RadioButton button2 = new RadioButton("Parallel");
        RadioButton button3 = new RadioButton("Distributed");
        button1.setSelected(true);
        button1.setToggleGroup(group);
        button2.setToggleGroup(group);
        button3.setToggleGroup(group);
        HBox radioButtons = new HBox(8);
        radioButtons.setAlignment(Pos.CENTER);
        radioButtons.getChildren().add(button1);
        radioButtons.getChildren().add(button2);
        radioButtons.getChildren().add(button3);

        VBox vertical = new VBox(8);
        vertical.setAlignment(Pos.CENTER);
        vertical.getChildren().add(parameters_1);
        vertical.getChildren().add(parameters_2);
        vertical.getChildren().add(horizontalButtons);
        vertical.getChildren().add(horizontalText);

        primaryStage.setScene(new Scene(vertical, 600, 500));
        primaryStage.show();
    }
}
