package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    Connection connection= new Connection();
    Timer timer = new Timer();
    List<String> activeUsers=new ArrayList<>();
    List<String>  receiversList=new ArrayList<>();
    List<CheckBox> checkBoxes=new ArrayList<>();
    @Override
    public void start(Stage primaryStage) throws Exception{


        primaryStage.getIcons().add(new Image("file:src/icon.png"));
        activeUsers.add("Kamila");
        activeUsers.add("Kacper");

        HBox hBox=new HBox(10);
        VBox pionowyVBox = new VBox(10);
        VBox sidePanel = new VBox(10);
        hBox.setStyle("-fx-background-color: #2b2b2b;");
        pionowyVBox.setMinSize(620,370);
        hBox.getChildren().add(pionowyVBox);
        hBox.getChildren().add(sidePanel);
        pionowyVBox.setPadding(new Insets(10));

        Label napis = new Label();
        napis.setText("Chat");
        napis.setStyle("-fx-text-fill: darkorange;");

        Label sidePanelTitle=new Label();
        sidePanelTitle.setText("Active users");
        sidePanelTitle.setStyle("-fx-text-fill: darkorange;");
        sidePanel.getChildren().add(sidePanelTitle);

        for(String item : activeUsers)  checkBoxes.add(new CheckBox(item));
        for (CheckBox checkBox : checkBoxes) checkBox.setStyle("-fx-text-fill: darkorange");
        sidePanel.getChildren().addAll(checkBoxes);


        pionowyVBox.getChildren().add(napis);


        TextArea chat = new TextArea();
        chat.setWrapText(true);
        chat.setStyle("-fx-control-inner-background: #3c3f41; -fx-text-fill: darkorange; -fx-highlight-fill: darkorange; -fx-focus-color: darkorange;");
        chat.setEditable(false);
        chat.setMinHeight(300);
        pionowyVBox.getChildren().add(chat);


        Button send = new Button("Send");
        send.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: darkorange;");
        Button connect= new Button("Connect");
        connect.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: darkorange;");
        TextField address=new TextField();
        address.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: darkorange;");
        address.setPromptText("Type address...");
        TextField port=new TextField();
        port.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: darkorange;");
        port.setPromptText("Type port...");
        TextField nick=new TextField();
        nick.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: darkorange;");
        nick.setPromptText("Type nick...");
        nick.setText("bolvis");
        TextField msg = new TextField();
        msg.setMaxHeight(80);
        msg.setId("textField");
        msg.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: darkorange;");
        msg.setPromptText("Type message...");
        pionowyVBox.getChildren().add(msg);

        send.setOnAction(event -> {
            String receiver="";
            for(CheckBox item:checkBoxes) {if(item.isSelected()){
                receiver=item.getText();
                try {
                connection.send("msg;"+receiver+";"+msg.getText()+"\n");
                msg.setText(null);
            } catch (NullPointerException | IOException e) {
                chat.setText(chat.getText()+"You aren't connected to any server..."+"\n");
                e.printStackTrace();
                break;
            }
            }
            }
        });

        connect.setOnAction(actionEvent -> {
            String ip=address.getText();
            try {
                int p=Integer.parseInt(port.getText());
                chat.setText(chat.getText()+"Connecting to server... "+ip+"\n");
                connection=new Connection(ip,p);
                //connection.send("Nick; "+nick.getText()+"\n"); TODO jak Karol da serwer to włącz
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if(!connection.serverMessage.equals("")) {

                            chat.setText(chat.getText()+connection.serverMessage+"\n");
                            chat.setScrollTop(Long.MAX_VALUE);
                            connection.serverMessage="";
                        }
                    }
                }, 0, 50);
                chat.setScrollTop(Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Button exit = new Button("Exit");
        exit.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: darkorange;");

        exit.setOnAction(event -> {
            try {
                connection.send("leave; "+nick.getText()+"\n");
            } catch (NullPointerException|IOException e) {
                timer.cancel();
                Platform.exit();
            }
            timer.cancel();
                Platform.exit();

        });



        HBox HBox = new HBox(10);
        HBox.getChildren().add(send);
        HBox.getChildren().add(exit);
        HBox.getChildren().add(connect);
        HBox.getChildren().add(address);
        HBox.getChildren().add(port);
        HBox.getChildren().add(nick);



        pionowyVBox.getChildren().add(HBox);

        //TODO to dla wygody na localhost
        address.setText("localhost");
        port.setText("33666");

        Scene scene = new Scene(hBox, 900, 440);
        primaryStage.setTitle("Simple chat");
        primaryStage.setScene(scene);
        primaryStage.show();

    }




    public static void main(String[] args) {launch(args);}

}
