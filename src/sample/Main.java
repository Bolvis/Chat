package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    Connection connection;
    Timer timer = new Timer();
    List<String> activeUsers=new ArrayList<>();
    ObservableList<CheckBox> checkBoxes= FXCollections.observableArrayList();
    ListView<CheckBox> receivers = new ListView<>();
    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.getIcons().add(new Image("file:src/icon.png"));

        HBox HBox = new HBox(10);
        HBox hBox=new HBox(10);
        hBox.getStylesheets().add("file:src/style.css");
        hBox.getStyleClass().add("hBox");

        VBox vBox = new VBox(10);
        vBox.setMinSize(620,370);
        VBox sidePanel = new VBox(10);
        vBox.setPadding(new Insets(10));


        hBox.getChildren().add(vBox);
        hBox.getChildren().add(sidePanel);


        Label title = new Label();
        title.setText("Chat");


        Label sidePanelTitle=new Label();
        sidePanelTitle.setText("Active users");
        sidePanel.getChildren().add(sidePanelTitle);

        receivers.setItems(checkBoxes);
        receivers.setMaxSize(140,800);
        sidePanel.getChildren().add(receivers);

        vBox.getChildren().add(title);


        TextArea chat = new TextArea();
        chat.setWrapText(true);
        chat.setEditable(false);
        chat.setMinHeight(300);
        vBox.getChildren().add(chat);

        Button exit = new Button("Exit");
        Button send = new Button("Send");
        Button connect= new Button("Connect");
        TextField address=new TextField();
        address.setPromptText("Type address...");
        TextField port=new TextField();
        port.setPromptText("Type port...");
        TextField nick=new TextField();
        nick.setPromptText("Type nick...");
        TextField msg = new TextField();
        msg.setMaxHeight(80);
        msg.setId("textField");
        msg.setPromptText("Type message...");
        vBox.getChildren().add(msg);

        send.setOnAction(event -> {

            String receiver="";

            for(CheckBox item:checkBoxes) {if(item.isSelected()){

                receiver=item.getText();

                try {

                connection.send("MSG;"+receiver+";"+msg.getText()+"\n");

            } catch (NullPointerException | IOException e) {

                chat.setText(chat.getText()+"You aren't connected to any server..."+"\n");

                e.printStackTrace();

                break;
            }
            }
            }
            chat.setText(chat.getText()+"you:"+msg.getText()+"\n");

            chat.setScrollTop(Long.MAX_VALUE);

            msg.setText(null);
        });

        connect.setOnAction(actionEvent -> {
            String ip=address.getText();
            try {

                int p=Integer.parseInt(port.getText());

                chat.setText(chat.getText()+"Connecting to server... "+ip+"\n");

                connection=new Connection(ip,p);

                connection.send("NICK; "+nick.getText()+"\n");

                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            connection.listen(connection.socket);
                            if(!connection.serverMessage.equals("")) {
                                System.out.println(connection.serverMessage);
                                String[] packet=connection.serverMessage.split(";");
                                switch(packet[0])
                                {
                                    case "JOIN":
                                        activeUsers.add(packet[1]);
                                        checkBoxes.clear();
                                        for(String item : activeUsers)  checkBoxes.add(new CheckBox(item));
                                        connection.serverMessage="";
                                        break;

                                    case "LEAVE":
                                        activeUsers.remove(packet[1]);
                                        checkBoxes.clear();
                                        for(String item : activeUsers)  checkBoxes.add(new CheckBox(item));
                                        connection.serverMessage="";
                                        break;

                                    case "MSG":
                                        chat.setText(chat.getText()+packet[1]+":"+packet[2]+"\n");
                                        chat.setScrollTop(Long.MAX_VALUE);
                                        connection.serverMessage="";
                                        break;

                                    case "ERROR":
                                        chat.setText(chat.getText()+"Error: "+packet[1]+"\n");
                                        chat.setScrollTop(Long.MAX_VALUE);
                                        connection.serverMessage="";
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0,50);


            } catch (NullPointerException|IOException e) {
                e.printStackTrace();
            }
        });


        exit.setOnAction(event -> {
            try {
                connection.send("LEAVE;"+nick.getText()+"\n");
                timer.cancel();
                Platform.exit();
            } catch (NullPointerException|IOException e) {
                e.printStackTrace();
                timer.cancel();
                Platform.exit();
            }
        });




        HBox.getChildren().add(send);
        HBox.getChildren().add(exit);
        HBox.getChildren().add(connect);
        HBox.getChildren().add(address);
        HBox.getChildren().add(port);
        HBox.getChildren().add(nick);

        vBox.getChildren().add(HBox);

        //TODO to dla wygody na localhost
        address.setText("localhost");
        port.setText("16384");
        nick.setText("bolvis");

        Scene scene = new Scene(hBox, 900, 440);
        primaryStage.setTitle("Simple chat");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {launch(args);}

}
