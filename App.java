import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {
    // declaring class scope variables needed across entire program
    public static int numPlayers;
    public static int numPitchers;
    public static ArrayList<Player> list = new ArrayList<Player>();
    public static ArrayList<String> rankingList = new ArrayList<String>();
    FadeTransition fadeOut = new FadeTransition();
    Label saveConfirm;

    // Vbox for main page
    VBox vbox = new VBox();

    @Override
    public void start(Stage primaryStage) {
        // create intial window, size, and title
        primaryStage.setWidth(1000);
        primaryStage.setHeight(650);
        primaryStage.setTitle("Blue Jays Players");
        // read stats data and ranking data
        readData("src\\stats.txt");
        readRankingData("src\\ranking.txt");
        // create main scenes, such as menu, legend, rankings, and instructions
        // stats view is created later on
        Group root = new Group();
        Scene menu = new Scene(root);
        Group legendRoot = new Group();
        Scene legend = new Scene(legendRoot);
        Group rankingRoot = new Group();
        Scene ranking = new Scene(rankingRoot);
        Group instructionsRoot = new Group();
        Scene instructions = new Scene(instructionsRoot);
        // navigation buttons used to manuever between the main pages
        HBox[] navButtons = new HBox[4];
        // all nodes needed for the ranking view page
        Button[] rankingButton = new Button[3];
        TextField[] rankings = new TextField[numPlayers];
        ImageView[] rankingViewPlayers = new ImageView[numPlayers];
        Label[] rankingViewNames = new Label[numPlayers];
        VBox[] rankingViewGroups = new VBox[numPlayers];
        HBox[] rankingViewHbox = new HBox[numPlayers];
        VBox rankingViewFinal = new VBox();
        ScrollPane rankingViewScroll = new ScrollPane();
        rankingViewScroll.setPrefSize(980, 600);
        // initalize and set event watchers for navigation buttons home, legend and instructions
        Button[] navHome = new Button[3];
        Button[] legendButtons = new Button[3];
        Button[] instructionButton = new Button[3];
        for (int i = 0; i < navHome.length; i++) {
            navHome[i] = new Button("Home");
            legendButtons[i] = new Button("Legend");
            instructionButton[i] = new Button("Instructions");
            navHome[i].setOnMouseClicked(e -> {
                primaryStage.setScene(menu);
            });
            legendButtons[i].setOnMouseClicked(e -> {
                primaryStage.setScene(legend);
            });
            instructionButton[i].setOnMouseClicked(e -> {
                primaryStage.setScene(instructions);
            });
        }
        // set text of ranking button and assign correct button to correct pages
        for (int i = 0; i < navButtons.length; i++) {
            if (i == 0) {
                rankingButton[i] = new Button("Rankings");
                navButtons[i] = new HBox();
                navButtons[i].getChildren().addAll(rankingButton[0], legendButtons[0], instructionButton[0]);
            } else if (i == 1) {
                rankingButton[i] = new Button("Rankings");
                navButtons[i] = new HBox();
                navButtons[i].getChildren().addAll(rankingButton[1], navHome[0], instructionButton[1]);
            } else if (i == 2) {
                rankingButton[i] = new Button("Rankings");
                navButtons[i] = new HBox();
                navButtons[i].getChildren().addAll(navHome[1], legendButtons[1], instructionButton[2]);
            } else {
                navButtons[i] = new HBox();
                navButtons[i].getChildren().addAll(navHome[2], legendButtons[2], rankingButton[2]);
            }
        }
        // save button and validation message created for ranking page
        Button saveRanking = new Button("Save");
        saveConfirm = new Label("   Saved!");
        saveConfirm.setFont(Font.font(16));
        // fade effect created for the validation message
        fadeOut.setNode(saveConfirm);
        fadeOut.setDuration(Duration.seconds(2.5));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setCycleCount(1);
        fadeOut.setAutoReverse(false);
        saveConfirm.setVisible(false);
        // event manager for when save button is clicked
        saveRanking.setOnMouseClicked(e -> {
            // trys to save data
            if (saveData("src\\ranking.txt", rankings)) {
                // if successful, displays Saved! to the user
                // if unsuccessful, code runs in the saveData method displaying the error
                saveConfirm.setText("   Saved!");
                saveConfirm.setVisible(true);
                fadeOut.playFromStart();
            }
        });
        // add save button and confirmation to scene
        navButtons[2].getChildren().addAll(saveRanking, saveConfirm);
        rankingViewFinal.getChildren().add(navButtons[2]);
        // gets rankings from initial data and puts them in the text boxes as defaults
        for (int i = 0; i < rankings.length; i++) {
            rankings[i] = new TextField("" + rankingList.get(i));
            rankings[i].setPrefWidth(50);
            // creates images for this page
            rankingViewPlayers[i] = new ImageView();
            rankingViewPlayers[i].setPreserveRatio(true);
            rankingViewPlayers[i].setFitWidth(125);
            // creates name labels for this page
            rankingViewNames[i] = new Label(list.get(i).name);
            rankingViewNames[i].setFont(Font.font(18));
            rankingViewNames[i].setWrapText(true);
            try {
                // reads actual player picture
                rankingViewPlayers[i].setImage(new Image(new FileInputStream("PlayerPictures\\" + (i + 1) + ".jpg")));
            } catch (Exception e1) {
                try {
                    // if no player picture, display default
                    rankingViewPlayers[i].setImage(new Image(new FileInputStream("PlayerPictures\\noPicture.png")));
                } catch (Exception e) {
                    // if cannot display default, print error statement
                    System.out.println("There was an error loading player images. Please try again later.");
                }
            }
            // format the ranking page with vboxes and hboxes
            rankingViewGroups[i] = new VBox();
            rankingViewHbox[i] = new HBox();
            rankingViewGroups[i].getChildren().addAll(rankingViewPlayers[i], rankingViewNames[i]);
            rankingViewHbox[i].getChildren().addAll(rankingViewGroups[i], rankings[i]);
            rankingViewHbox[i].setSpacing(50);
            rankingViewFinal.getChildren().add(rankingViewHbox[i]);
            rankingViewFinal.setSpacing(20);
        }
        // make page scrollable and add to scene
        rankingViewScroll.setContent(rankingViewFinal);
        rankingRoot.getChildren().add(rankingViewScroll);
        // event manager for rankings buttons on other main pages
        for (int i = 0; i < rankingButton.length; i++) {
            rankingButton[i].setOnMouseClicked(e -> {
                primaryStage.setScene(ranking);
            });
        }

        // this label displays all the text explaining the abbreviations used in the app
        // for legend page
        Label legendText = new Label(
                "Positions:\n1B - 1st basemen\n2B - 2nd basemen\n3B - 3rd basemen\nSS - Shortstop\nC - Catcher\nCI - Corner infield\nMI - Middle infield\nIF - Infield\nLF - Left field\nCF - Center field\nRF - Right field\nOF - Outfield\nUT - Utility player\nSP - Starting pitcher\nRP - Relief pitcher\nCL - Closing pitcher\n\nHitting Stats:\nAB - At bats\nAVG - Batting average(percentage of balls hit)\nRBI - Runs batted in\nHR - Home runs\n\nPitching Stats:\nIP - Innings pitched\nERA - Earned run average(essentially average runs allowed per 9 innings)\nK  - Strikeouts\nWHIP - Walks and hits per innings pitched");
        legendText.setTranslateX(25);
        // formatting vbox for legend page
        VBox legendVbox = new VBox();
        legendVbox.getChildren().addAll(navButtons[1], legendText);
        legendRoot.getChildren().add(legendVbox);
        // create label for instructinos
        Label instructionsText = new Label(
                "This project is made up of 3 classes, the App class, the Player class, and the Pitcher class. The Pitcher class extends the Player class.\nIn order to view the individual stats of any player, you click on their picture on the menu (home) page.\nTo access the ranking or legend page, click on their respective buttons at the top.\nOn the rankings page, rankings will only be saved if you press the save button. They will not be saved if you simply leave the page without pressing save.");
        // format instructions label
        instructionsText.setFont(Font.font(16));
        instructionsText.setMaxWidth(950);
        instructionsText.setWrapText(true);
        // create vbox to format instructions page properly
        VBox instructionsVbox = new VBox();
        instructionsVbox.setTranslateX(25);
        instructionsVbox.getChildren().addAll(navButtons[3], instructionsText);
        instructionsRoot.getChildren().add(instructionsVbox);

        // all nodes needed for menu page and stats pages declared here
        ImageView[] players = new ImageView[numPlayers];
        Scene[] statView = new Scene[numPlayers];
        Group[] statsRoot = new Group[numPlayers];
        Label[] names = new Label[numPlayers];
        VBox[][] group = new VBox[(int) Math.ceil(numPlayers / 5.0)][5];
        HBox[] hbox = new HBox[(int) Math.ceil(numPlayers / 5.0)];
        Button[] homeButton = new Button[numPlayers];
        Label[] position = new Label[numPlayers];
        Label[] age = new Label[numPlayers];
        Label[] jersey = new Label[numPlayers];
        Label[] atBats = new Label[numPlayers];
        Label[] avg = new Label[numPlayers];
        Label[] rbi = new Label[numPlayers];
        Label[] homeRuns = new Label[numPlayers];
        VBox[] stats = new VBox[numPlayers];
        Label[] ip = new Label[numPitchers];
        Label[] era = new Label[numPitchers];
        Label[] k = new Label[numPitchers];
        Label[] whip = new Label[numPitchers];
        ImageView[] statViewPlayers = new ImageView[numPlayers];
        Label[] statViewNames = new Label[numPlayers];
        VBox[] statViewBoxes = new VBox[numPlayers];
        HBox[] statViewHbox = new HBox[numPlayers];
        VBox vbox = new VBox();
        ScrollPane scroll = new ScrollPane();
        // set default scroll pane information for menu page
        scroll.setPrefSize(980, 600);
        scroll.setContent(vbox);
        // add navigation buttons to menu page
        vbox.getChildren().add(navButtons[0]);
        // initialize hboxes for menu page
        for (int i = 0; i < hbox.length; i++) {
            hbox[i] = new HBox();
        }
        // initalize vboxes for menu page that contain player picture and name
        for (int i = 0; i < group.length; i++) {
            for (int j = 0; j < group[i].length; j++) {
                group[i][j] = new VBox();
            }
        }
        // create player images for stats pages and menu page
        for (int i = 0; i < players.length; i++) {
            players[i] = new ImageView();
            statViewPlayers[i] = new ImageView();
            players[i].setPreserveRatio(true);
            statViewPlayers[i].setPreserveRatio(true);
            players[i].setFitWidth(125);
            statViewPlayers[i].setFitWidth(250);
            // try to load correct image files
            try {
                players[i].setImage(new Image(new FileInputStream("PlayerPictures\\" + (i + 1) + ".jpg")));
                statViewPlayers[i].setImage(new Image(new FileInputStream("PlayerPictures\\" + (i + 1) + ".jpg")));
            } catch (Exception e1) {
                try {
                    // if cannot, try to load default image file
                    players[i].setImage(new Image(new FileInputStream("PlayerPictures\\noPicture.png")));
                    statViewPlayers[i].setImage(new Image(new FileInputStream("PlayerPictures\\noPicture.png")));
                } catch (Exception e) {
                    // if cannot, print error statement
                    System.out.println("There was an error loading player images. Please try again later");
                }
            }
            // format name labels for main and stats pages
            names[i] = new Label();
            names[i].setText(list.get(i).name);
            names[i].setFont(Font.font(18));
            names[i].setMaxWidth(125);
            names[i].setWrapText(true);
            statViewNames[i] = new Label(list.get(i).name);
            statViewNames[i].setFont(Font.font(36));
            // create temp counters to properly loop through player pictures and create
            // event managers
            int tempCounter = i / 5;
            int tempCounter2 = i;
            // makes sure tempCounter can never be over 5 (amount of pictures in one row)
            while (tempCounter2 >= 5)
                tempCounter2 -= 5;
            // add pictures and names to boxes
            group[tempCounter][tempCounter2].getChildren().addAll(players[i], names[i]);
            statViewBoxes[i] = new VBox();
            statViewBoxes[i].getChildren().addAll(statViewPlayers[i], statViewNames[i]);
            // add player-name boxes to rows and format them
            hbox[i / 5].getChildren().add(group[tempCounter][tempCounter2]);
            hbox[i / 5].setSpacing(50);
            hbox[i / 5].setTranslateX(50);
            // create a temporary effectively final variable to use within an event manager
            int temp = i;
            players[i].setOnMouseClicked(e -> {
                primaryStage.setScene(statView[temp]);
            });
        }
        // add all rows to menu page vbox
        for (int i = 0; i < hbox.length; i++) {
            vbox.getChildren().addAll(hbox[i]);
        }
        // keeps tracks of how many pitchers there are for looping later
        int pitcherCount = 0;
        // creates stats pages and puts data on them
        for (int i = 0; i < statView.length; i++) {
            statsRoot[i] = new Group();
            statView[i] = new Scene(statsRoot[i]);
            stats[i] = new VBox();
            // put basic information (all players have)
            position[i] = new Label("Position: " + list.get(i).position);
            position[i].setFont(Font.font(36));
            age[i] = new Label("Age: " + list.get(i).age);
            age[i].setFont(Font.font(36));
            jersey[i] = new Label("Jersey Number: " + list.get(i).jersey);
            jersey[i].setFont(Font.font(36));
            stats[i].getChildren().addAll(position[i], age[i], jersey[i]);
            // check if player is a pitcher
            if (list.get(i).position.equals("SP") || list.get(i).position.equals("CL")
                    || list.get(i).position.equals("RP")) {
                // if so, put all pitching stats as well
                // cast is needed because arraylist is of type Player, but only pitcher objects
                // have these as variables
                ip[pitcherCount] = new Label("IP: " + ((Pitcher) list.get(i)).ip);
                ip[pitcherCount].setFont(Font.font(36));
                era[pitcherCount] = new Label("ERA: " + ((Pitcher) list.get(i)).era);
                era[pitcherCount].setFont(Font.font(36));
                k[pitcherCount] = new Label("K: " + ((Pitcher) list.get(i)).k);
                k[pitcherCount].setFont(Font.font(36));
                whip[pitcherCount] = new Label("WHIP: " + ((Pitcher) list.get(i)).whip);
                whip[pitcherCount].setFont(Font.font(36));
                stats[i].getChildren().addAll(ip[pitcherCount], era[pitcherCount], k[pitcherCount], whip[pitcherCount]);
                pitcherCount++;
            }
            // add hitting stats to all
            atBats[i] = new Label("AB: " + list.get(i).ab);
            atBats[i].setFont(Font.font(36));
            avg[i] = new Label("AVG: " + list.get(i).avg);
            avg[i].setFont(Font.font(36));
            rbi[i] = new Label("RBI: " + list.get(i).rbi);
            rbi[i].setFont(Font.font(36));
            homeRuns[i] = new Label("HR: " + list.get(i).hr);
            homeRuns[i].setFont(Font.font(36));
            // add stats to page and format
            stats[i].getChildren().addAll(atBats[i], avg[i], rbi[i], homeRuns[i]);
            stats[i].setTranslateX(50);
            // more formatting of stats pages
            statViewHbox[i] = new HBox();
            statViewHbox[i].getChildren().addAll(statViewBoxes[i], stats[i]);
            statViewHbox[i].setSpacing(100);
            statViewHbox[i].setAlignment(Pos.CENTER);
            // create home button for stats pages and add to scene
            homeButton[i] = new Button("Home");
            homeButton[i].setLayoutX(700);
            statsRoot[i].getChildren().addAll(statViewHbox[i], homeButton[i]);
            // event manager for home button to go back to the menu page
            homeButton[i].setOnMouseClicked(e -> {
                primaryStage.setScene(menu);
            });
        }
        // make menu scrollable
        root.getChildren().addAll(scroll);
        // set initaial scene to menu and show page
        primaryStage.setScene(menu);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param filePath The file path of the file to read data from
     * 
     *                 This method reads data from a ranking text document into a
     *                 rankings arraylist
     */
    public void readRankingData(String filePath) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                rankingList.add(line);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("An error has occured reading the values from the file. Please try again later.");
        }
    }

    /**
     * @param filePath The file path of the file to read data from
     * 
     *                 This method reads stats data from the file path specified and
     *                 stores it in an arraylist
     */

    public void readData(String filePath) {
        BufferedReader br;
        Player temp;
        try {
            // find file and use buffered reader
            br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                // store values from text file in string array
                String[] values = line.split("\\|", 13);
                // if data read says player is a hitter
                if (values[0].equals("h")) {
                    // create a player object
                    temp = new Player(values[1], values[2], values[3], values[4], values[5], values[6], values[7],
                            values[8]);
                } else {
                    // otherwise create a pitcher
                    temp = new Pitcher(values[1], values[2], values[3], values[4], values[5], values[6], values[7],
                            values[8], values[9], values[10], values[11], values[12]);
                    numPitchers++;
                }
                // add created object to arraylist
                list.add(temp);
                // increment number of players who are stored in file
                numPlayers++;
            }
            // close buffered reader
            br.close();
        }
        // if there is an error
        catch (Exception e) {
            // print error statement to console
            System.out.println("An error has occured reading the values from the file. Please try again later.");
        }
    }

    /**
     * 
     * @param filePath The file path of the file to read data from
     * @param rankings A textField array that you read the rankings from
     * @return Returns boolean with true indicating that save was succesful, and
     *         false indicating save was unsuccessful This method saves the rankings
     *         data inputted into the text field into a text document
     */

    public boolean saveData(String filePath, TextField[] rankings) {
        // declare buffered writer object
        BufferedWriter bw;
        // update rankings arraylist with values from textfields in app
        for (int i = 0; i < rankingList.size(); i++) {
            // try-catch loop is in case an invalid integer is entered as a ranking
            try {
                // if attempt to save higher than size of arraylist
                if (Integer.parseInt(rankings[i].getText()) > rankingList.size()) {
                    // tell user that they cannot save outside of size of arraylist
                    saveConfirm.setText("   Please use rankings within the size limit of " + list.size());
                    saveConfirm.setVisible(true);
                    fadeOut.playFromStart();
                    return false;
                }
                rankingList.set(i, rankings[i].getText());
                // if input is invalid, display error message
            } catch (Exception e) {
                saveConfirm.setText("   Invalid integer inputted as ranking.");
                saveConfirm.setVisible(true);
                fadeOut.playFromStart();
                return false;
            }
        }

        try {
            // try to write values to file
            bw = new BufferedWriter(new FileWriter(filePath));
            for (int i = 0; i < rankingList.size(); i++) {
                bw.write(rankingList.get(i) + "\n");
            }
            bw.close();
            return true;
            // if fails, print error staement
        } catch (Exception e) {
            System.out.println("An error has occured saving values to the file. Please try again later.");
            return false;
        }
    }
}
