package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sample.DatabaseHandler;
import sample.User;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
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

    @FXML public ChoiceBox<String> sexCombo;
    @FXML public DatePicker datePick;
    @FXML public TextField heightField;
    @FXML public ChoiceBox<String> heightUnits;
    @FXML public Label dobLabel;
    @FXML public Label heightLabel;
    @FXML public Label sexLabel;

    @FXML public Button showBMIButton;
    @FXML public Label BMILabel;

    @FXML public TextField bodyFatWaistInput;
    @FXML public TextField bodyFatNeckInput;
    @FXML public TextField bodyFatHipsInput;
    @FXML public Label bodyFatWaistPopUp;
    @FXML public Label bodyFatNeckPopUp;
    @FXML public Label bodyFatHipsPopUp;
    @FXML public Button bodyFatSubmit;
    @FXML public Label bodyFatLabel;

    //https://www.regular-expressions.info/floatingpoint.html
    //Regex for inputfields that will take doubles

    //Regex for inputField checking
    private final String INPUTFIELDNONNUMERIC = "^\\d*\\.?\\d*";

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
            if (!newValue.matches(INPUTFIELDNONNUMERIC)){
                heightField.setText(newValue.replaceAll(INPUTFIELDNONNUMERIC, ""));
            }
        });

        //Set bodyFatWaistInput to digits only
        bodyFatWaistInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(INPUTFIELDNONNUMERIC)) {
                bodyFatWaistInput.setText(newValue.replaceAll(INPUTFIELDNONNUMERIC, ""));
            }
        });

        //Set bodyFatNeckInput to digits only
        bodyFatNeckInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(INPUTFIELDNONNUMERIC)) {
                bodyFatNeckInput.setText(newValue.replaceAll(INPUTFIELDNONNUMERIC, ""));
            }
        });

        //Set bodyFatHipsInput to digits only
        bodyFatHipsInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(INPUTFIELDNONNUMERIC)) {
                bodyFatHipsInput.setText(newValue.replaceAll(INPUTFIELDNONNUMERIC, ""));
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
        bodyFatHipsInput.setEditable(this.user.getSex().equals("Female"));
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
    private float calculateBMI(){
        user.setHeight(dh.getHeightFromUserID(dh.getUserIDFromUsername(user.getUsername())));
        user.setWeight(dh.getMostRecentWeightValFromID(dh.getUserIDFromUsername(user.getUsername())));
        float userHeightMeters = (float) (user.getHeight()/39.97);
        return (user.getWeight()/(userHeightMeters*userHeightMeters));
    }

    /**
     * Method to calculate BMI and display/hide it on a button press
     */
    @FXML private void showBMIButtonAction(){
        DecimalFormat df = new DecimalFormat("#.##");
        float BMI = calculateBMI();
        if (BMILabel.getText().equals("??")){
            BMILabel.setText(String.valueOf(df.format(BMI)));
        }else{
            BMILabel.setText("??");
        }

    }

    /**
     * Method to check Waist Input field matches in regex
     *
     * @return True if check is passed
     */
    @FXML protected boolean checkWaistInput(){
        String name = bodyFatWaistInput.getText();
        if (name.matches(INPUTFIELDNONNUMERIC) && checkValidDouble(name, ".")){
            bodyFatHipsPopUp.setText("");
            return true;
        }
        else{
            bodyFatWaistPopUp.setText("Invalid");
            return false;
        }
    }

    /**
     * Method to check Neck Input field matches in regex
     *
     * @return True if check is passed
     */
    @FXML protected boolean checkNeckInput(){
        String name = bodyFatNeckInput.getText();
        if (name.matches(INPUTFIELDNONNUMERIC) && checkValidDouble(name, ".")){
            bodyFatNeckPopUp.setText("");
            return true;
        }
        else{
            bodyFatNeckPopUp.setText("Invalid");
            return false;
        }
    }

    /**
     * Method to check Hips Input field matches in regex
     *
     * @return True if check is passed
     */
    @FXML protected boolean checkHipsInput(){
        String name = bodyFatHipsInput.getText();
        if (name.matches(INPUTFIELDNONNUMERIC) && checkValidDouble(name, ".")){
            bodyFatHipsPopUp.setText("");
            return true;
        }
        else{
            bodyFatHipsPopUp.setText("Invalid");
            return false;
        }
    }

    public boolean checkBodyFatInputs(){
        return checkWaistInput() && checkNeckInput() && checkHipsInput();
    }

    @FXML private void bodyFatSubmitAction() {

        if (checkBodyFatInputs()){
            float waist;
            float neck;
            if (user.getSex().equals("Male")){
                waist = Float.parseFloat(bodyFatWaistInput.getText());
                neck = Float.parseFloat(bodyFatNeckInput.getText());
                float bfp = (float) ((float) 495.0 / (1.0324 - (0.19077 * (Math.log10((double) waist - neck)))) +
                                                            (0.15456 * Math.log10(user.getHeight()))) - 450;
                System.out.println(bfp);
                if (bodyFatLabel.getText().equals("??")){
                    bodyFatLabel.setText(String.valueOf(bfp));
                }
                else{
                    bodyFatLabel.setText("??");
                }
            }
        }
    }
}

