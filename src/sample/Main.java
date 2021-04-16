package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

//    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/SplashPage.fxml"));
        primaryStage.setTitle("ProActive");
        primaryStage.setScene(new Scene(root, 1400, 800));

        primaryStage.setMinWidth(1050);
        primaryStage.setMinHeight(500);
        primaryStage.show();

        //VBox content = FXMLLoader.load(getClass().getResource("SplashPage.fxml"));
        //root.setCenter(content);
    }


    public static void main(String[] args) {

        //DatabaseHandler dh = new DatabaseHandler("jdbc:sqlite:proactive.db");

        //User user = new User("Owen","Tasker", User.Sex.MALE, 72.0f, 85.0f,
        //        LocalDate.of(1998, Month.APRIL, 25), "owen.tasker@gmail.com", "owen2test");

        //dh.createUserEntry(user);

        //System.out.println("User Inserted into database");

        //General account for use with this application, dont worry about non-secure password as is ultimately
        //a throwaway account
        //EmailHandler email = new EmailHandler("proactivese13@gmail.com", "f45d09mFAcHr");

        //Configure system to send emails, need to run this at start of each session
        //Properties prop = email.SetUpEmailHandler();

        //Create email session
        //Session session = email.createSession(prop);

        //Send a basic verification email
//        email.sendVerification(session, user.getEmail(), "483RDc");
//        email.sendGoal(session, user.getEmail(), "Set up user table in database", user.getRealName(), "5R3bn2");
//        email.sendGoalCompletion(session, user.getEmail(), "Set up user table in database");

        launch(args);
    }

//    public static Stage getStage(){
//        return stage;
//    }

}
