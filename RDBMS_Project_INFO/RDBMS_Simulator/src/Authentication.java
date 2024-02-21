import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Authentication implements AuthenticationInterface{

    /**
     Constructor for the class
     */
    public Authentication() {
    }

    /**
     This method contains the logic to authenticate the user credentials
     @param userName has the user entered username
     @param password Has the user entered password
     @return isCorrect Which is basically the user is valid or not
     */
    public boolean verifyUser(String userName, String password) {
        boolean isCorrect = false;
        try {
            Scanner scanner = new Scanner(new File("userInfo.csv"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");
                String user = values[0];
                String hashedValueForEnteredPass = getHashedValueFor(password);
                if (user.contentEquals(userName)) {
                    String pass = values[1];
                    if (pass.contentEquals(hashedValueForEnteredPass)) {
                        isCorrect = true;
                        break;
                    }
                }
            }
            scanner.close();
            return isCorrect;
        } catch (Exception e) {
            e.getLocalizedMessage();
            return false;
        }
    }

    /**
     This method contains the logic to add the user sign up information in the ACCOUNTS.csv file
     @param user has the user entered username
     @param pass Has the user selected password
     */
    public void addUser(String user, String pass) {
        try {
            File file = new File("userInfo.csv");
            FileWriter writer = new FileWriter("userInfo.csv", true);
            if (!file.exists()) {
                String[] columnNames = {"Name", "Pass"};
                writer.append(String.join(",", columnNames)).append("\n");
            }
            String[] rowValues = {user, pass};
            writer.append(String.join(",", rowValues)).append("\n");

            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     This method contains the logic to get the hashed value for the password using md5.
     @param pass has the user entered password
     */
    private static String getHashedValueFor(String pass) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(pass.getBytes());
        BigInteger no = new BigInteger(1, digest);
        StringBuilder hashtext = new StringBuilder(no.toString(16));
        while (hashtext.length() < 32) {
            hashtext.insert(0, "0");
        }
        return hashtext.toString();
    }
}
