package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sample.DatabaseHandler;
import sample.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class to control the Manage Profile Page FXML file
 *
 * @author Owen Tasker
 * @author Samuel Scarfe
 *
 * @version 1.1
 *
 * 1.0 - Initial commit
 * 1.1 - Connected to database, added simple error checking.
 */
public class ManageProfilePageController implements Initializable {

    private User user;
    private DatabaseHandler dh;

    @FXML private ChoiceBox<String> sexCombo;
    @FXML private DatePicker datePick;
    @FXML private TextField heightField;
    @FXML private ChoiceBox<String> heightUnits;
    @FXML private Label dobLabel;
    @FXML private Label heightLabel;
    @FXML private Label sexLabel;

    @FXML private Button showBMIButton;
    @FXML private Label BMILabel;

    @FXML private Button bodyFatSubmit;
    @FXML private Label bodyFatLabel;

    @FXML private Button exportButton;
    @FXML private Label statusLabel;

    private String csvPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;

    //https://www.regular-expressions.info/floatingpoint.html
    //Regex for inputfields that will take doubles

    //Regex for inputField checking
    private final String INPUT_FIELD_NON_NUMERIC = "^\\d*\\.?\\d*";

    /**
     * Pseudo-constructor for controllers, runs after FXML elements have been loaded in and as such allows for
     * modifying their behaviours (e.g. choicebox contents)
     *
     * @param url            FXML defined parameter
     * @param resourceBundle FXML defined parameter
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dh = DatabaseHandler.getInstance();

        sexCombo.getItems().removeAll(sexCombo.getItems());
        sexCombo.getItems().addAll("Male", "Female", "Other");
        heightUnits.getItems().removeAll(heightUnits.getItems());
        heightUnits.getItems().addAll("cm", "inches");
        datePick.setEditable(false);

        //Set heightField to digits only
        //https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(INPUT_FIELD_NON_NUMERIC)){
                heightField.setText(newValue.replaceAll(INPUT_FIELD_NON_NUMERIC, ""));
            }
        });
    }

    /**
     * Method to allow for data to be transferred from another scene to this one
     *
     * @param user Takes in a User object to help with persistence
     */
    public void initData(User user) {
        this.user = user;
        System.out.println(this.user.getSex());
        System.out.println(this.user);
    }

    /**
     * Method that handles the submit button action, checks to see which values have been modified, then performs
     * update operations directly on the database user object
     */
    public void submitButtonAction() {
        System.out.println("submitButton");
        sexLabel.setText("");
        dobLabel.setText("");
        heightLabel.setText("");

        //Take in the value provided by the datepicker
        LocalDate date = datePick.getValue();

        //Try to update database then user. Show appropriate success/fail message to user.
        if (checkDob(date)){
            try {
                dh.editValue("user", "dob", date.toString(), "username", user.getUsername());
                user.setDob(date);
                dobLabel.setText("Date of Birth updated to " + date.toString());
            }
            catch (SQLException e) {
                e.printStackTrace();
                dobLabel.setText("Date of Birth update failed");
            }
        }

        //Take in the value provided to height value
        String updatedHeight = heightField.getText();
        String heightUnit = heightUnits.getValue();

        //Check inputs
        if (checkHeightFields(updatedHeight, heightUnit)) {
            float heightValue = Float.parseFloat(updatedHeight);

            //Convert units and prepare string for printing
            if (heightUnit.equals("inches")) {
                heightValue = (float) (heightValue * 2.5);
                heightUnit = " " + heightUnit;
            }

            //Try to update database then user. Show appropriate success/fail message to user.
            try {
                dh.editValue("user", "height", Integer.parseInt(updatedHeight), "username", user.getUsername());
                user.setHeight(heightValue);
                heightLabel.setText("Height updated to " + updatedHeight + heightUnit);
            }
            catch (SQLException e) {
                e.printStackTrace();
                heightLabel.setText("Height update failed");
            }
        }

        //Takes in the value provided by sexCombo
        String sex = sexCombo.getValue();

        if (sex != null) {
            User.Sex userSex = User.Sex.valueOf(sex.toUpperCase(Locale.ROOT));

            //Try to update database then user. Show appropriate success/fail message to user.
            try {
                dh.editValue("user", "sex", userSex.toString(), "username", user.getUsername());
                user.setSex(userSex);
                sexLabel.setText("Sex updated to " + userSex.toString());
            }
            catch (SQLException e) {
                e.printStackTrace();
                sexLabel.setText("Sex update failed");
            }
        }
    }

    /**
     * Private helper method to check values in the dob field.
     *
     * @param dob the user's new date of birth.
     * @return a boolean value representing whether the inputted values are suitable
     */
    private boolean checkDob(LocalDate dob) {
        if (dob == null) { //Dob not entered, no action needed
            return false;
        }
        else if (dob.isAfter(LocalDate.now())) { //Future date
            dobLabel.setText("Date of Birth cannot be in the future");
            return false;
        }

        return true;
    }

    /**
     * Private helper method for checking the height input fields.
     *
     * @param heightText the user's new height.
     * @param heightUnits the user's chosen units for height.
     * @return a boolean value representing whether the inputted values are suitable.
     */
    private boolean checkHeightFields(String heightText, String heightUnits)
    {
        //Both null, no action needed.
        if ((heightText == null || heightText.equals("")) && (heightUnits == null || heightUnits.equals(""))) {
            return false;
        }
        //Units selected but height not filled
        else if (heightText == null || heightText.equals("")) {
            heightLabel.setText("Please input height");
            return false;
        }
        //Height input as 0
        else if (Integer.parseInt(heightText) == 0) {
            heightLabel.setText("Height cannot be 0");
            return false;
        }
        //Height input correctly but units not selected
        else if (heightUnits == null || heightUnits.equals("")) {
            heightLabel.setText("Please select units");
            return false;
        }

        return true;
    }

    /**
     * Method to check that a string representing a double is a valid double before casting, copied and adapted from
     * https://stackoverflow.com/questions/767759/occurrences-of-substring-in-a-string
     *
     * @param str String that we are checking
     * @param valToFind Value that we are checking for
     *
     * @return Returns true if value is a valid double value (has less than 2 decimal points)
     *
     */
    public boolean checkValidDouble(String str, String valToFind){
        int lastIndex = 0;
        int count = 0;

        while(lastIndex != -1){
            lastIndex = str.indexOf(valToFind, lastIndex);

            if (lastIndex != -1){
                count++;
                lastIndex+=valToFind.length();
            }
        }
        return count <= 1;
    }

    /**
     * Method to calculate BMI on a user to user basis
     *
     * @return Returns a float representing BMI
     */
    private float calculateBMI() {
        user.setHeight(dh.getHeightFromUserID(dh.getUserIDFromUsername(user.getUsername())));
        user.setWeight(dh.getMostRecentWeightValFromID(dh.getUserIDFromUsername(user.getUsername())));
        float userHeightMeters = (float) (user.getHeight()/39.97);
        return (user.getWeight()/(userHeightMeters*userHeightMeters));
    }

    /**
     * Method to calculate BMI and display/hide it on a button press
     */
    @FXML
    private void showBMIButtonAction() {
        DecimalFormat df = new DecimalFormat("#.##");
        float BMI = calculateBMI();
        if (BMILabel.getText().equals("??")) {
            BMILabel.setText(String.valueOf(df.format(BMI)));
        }
        else {
            BMILabel.setText("??");
        }
    }

    /**
     * Private method to control action of submitting for body fat calculation. Calculates and displays a
     * User's body fat from given inputs.
     */
    @FXML
    private void bodyFatSubmitAction() {
        //Bodyfat calculation found at
        //https://www.gaiam.com/blogs/discover/how-to-calculate-your-ideal-body-fat-percentage#
        DecimalFormat df = new DecimalFormat("#.#");

        double bmi = calculateBMI();

        if (bodyFatLabel.getText().equals("??")) {
            if (user.getSex().equals("Male")){
                bodyFatLabel.setText(df.format((1.20*bmi) + (0.23*user.getAge()) - 16.2) + "%");
            }
            else if(user.getSex().equals("Female")){
                bodyFatLabel.setText(df.format((1.20*bmi) + (0.23*user.getAge()) - 5.4) + "%");
            }
            //Not sure how to calculate body fat percentage for non-binary people, as comprimise took difference
            // between male and female settings
            else{
                bodyFatLabel.setText(df.format((1.20*bmi) + (0.23*user.getAge()) - (16.2-5.4)) + "%");
            }
        }
        else {
            bodyFatLabel.setText("??");
        }
    }

    public void exportCSV(ActionEvent actionEvent) throws IOException {
        try {
            statusLabel.setText("");
            exportButton.setDisable(true);

            System.out.println(dh.getNutrientEntries(user.getUsername(), null));

            // https://stackabuse.com/reading-and-writing-csvs-in-java/
            List<List<String>> rows = Arrays.asList(
                    Arrays.asList("Jean", "author", "Java"),
                    Arrays.asList("David", "editor", "Python"),
                    Arrays.asList("Scott", "editor", "Node.js")
            );

            LocalTime time = LocalTime.now();

            FileWriter csvWriter = new FileWriter(
                    new File(csvPath + "ProActive data " + LocalDate.now() + "-" + time.getHour() + "." +
                            time.getMinute() + "." + time.getSecond() + ".csv")
            );

            csvWriter.append("Name");
            csvWriter.append(",");
            csvWriter.append("Role");
            csvWriter.append(",");
            csvWriter.append("Topic");
            csvWriter.append("\n");

            for (List<String> rowData : rows) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        exportButton.setDisable(false);

        statusLabel.setText("Exported data to: " + csvPath);
    }
}

