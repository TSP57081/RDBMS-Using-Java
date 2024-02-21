import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

public class UserInteraction {
    private static boolean isVerifiedUser = false;
    private static boolean transactionInProgress = false;
    public static void main(String[] args) throws NoSuchAlgorithmException {
        beginAuthenticationFlow();
        if (isVerifiedUser) {
            initiateQueryFlow();
        }
    }

    /**
     This method contains the logic to start the query inputs logic as we need to authenticate the user first.
     It accepts the statements for CREATE, INSERT, UPDATE, SELECT, DELETE and Transactions.
     */
    private static void initiateQueryFlow() {
        QueryHandler qh = new QueryHandler();
        System.out.println("You may proceed to write your SQL queries. Type 'Exit' to exit.");
        Scanner sc = new Scanner(System.in);
        String query = sc.nextLine();
        String[] createArray = query.split(" ");
        String loweredQueryType = createArray[0].toLowerCase();

        if (query.toLowerCase().contentEquals("begin transaction")) {
            transactionInProgress = true;
            qh.startTransaction();
            initiateQueryFlow();
        } else if (query.toLowerCase().contentEquals("commit")) {
            transactionInProgress = false;
            qh.commit();
            System.out.println("Data Committed.");
            initiateQueryFlow();
        } else if (query.contentEquals("end transaction")) {
            transactionInProgress = false;
            System.out.println("Transaction Ended.");
            initiateQueryFlow();
        } else if(query.toLowerCase().contentEquals("rollback")) {
            transactionInProgress = false;
            qh.rollback();
            System.out.println("Rollback complete.");
            initiateQueryFlow();
        } else {
            switch (loweredQueryType) {
                case "create":
                    qh.createHandler(createArray);
                    initiateQueryFlow();
                    break;
                case "select":
                    qh.selectHandler(createArray, transactionInProgress);
                    initiateQueryFlow();
                    break;
                case "insert":
                    qh.insertHandler(createArray);
                    System.out.println("Insertion Completed Successfully!");
                    initiateQueryFlow();
                    break;
                case "update":
                    qh.updateHandler(createArray, transactionInProgress);
                    System.out.println("Updation Completed Successfully!");
                    initiateQueryFlow();
                    break;
                case "delete":
                    qh.deleteHandler(createArray);
                    System.out.println("Deletion Completed Successfully!");
                    initiateQueryFlow();
                    break;
                case "exit":
                    System.out.println("See you soon, Bye.");
                    break;
                default:
                    System.out.println("Invalid Query Entered!");
                    initiateQueryFlow();
            }
        }
    }

    /**
     This method contains the logic to let the user know of all the available options with them
     i.e. Sign in and sign up. Once the user is signed in, only then they can enter queries
     */
    private static void beginAuthenticationFlow() throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello!");
        System.out.println("Let's begin!");
        System.out.println("What do you wish to do?");
        System.out.println("Sign In (Enter 1)");
        System.out.println("Sign Up (Enter 2)");
        String choice = scanner.nextLine();
        if (choice.contentEquals("1")) {
            System.out.println("--------------------LOGIN--------------------");
            System.out.println("Please enter your username:");
            String user = scanner.nextLine();
            System.out.println("Please enter your password:");
            String pass = scanner.nextLine();
            System.out.println("Please re-write the appearing text:");
            String captcha = genarateRandomString();
            System.out.println(captcha);
            String captchaEntered = scanner.nextLine();
            if (!captchaEntered.contentEquals(captcha)) {
                System.out.println("Invalid captcha. Are you really a human?");
                System.out.println("Try Again? Y/N");
                checkForPositiveOrNegativeInput(scanner);
            }
            Authentication a = new Authentication();
            boolean isCorrect = a.verifyUser(user, pass);
            if (isCorrect) {
                isVerifiedUser = isCorrect;
                System.out.println("Authentication Successful!");
                System.out.println();
            } else {
                System.out.println("Authentication Failed!");
                System.out.println("Try Again? Y/N");
                checkForPositiveOrNegativeInput(scanner);
            }
        } else if (choice.contentEquals("2")) {
            System.out.println("--------------------REGISTER--------------------");
            System.out.println("Enter your name:");
            Scanner sc = new Scanner(System.in);
            String userName = sc.nextLine();
            System.out.println("Enter a password of your choice:");
            String chosenPassword = getHashedValueFor(sc.nextLine());
            Authentication auth = new Authentication();
            auth.addUser(userName, chosenPassword);
            beginAuthenticationFlow();
        } else {
            System.out.println("Please enter either 1 or 2");
            beginAuthenticationFlow();
        }
    }

    /**
     This method handles the captcha generation. It provides the caller with an alphanumeric string of length 6.
     @return String This method returns a string containing the captcha code.
     */
    private static String genarateRandomString() {
        String charactersList = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder generatedString = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            int randomInt = random.nextInt(charactersList.length());
            char charAtRandomInt = charactersList.charAt(randomInt);
            generatedString.append(charAtRandomInt);
        }

        return generatedString.toString();
    }

    /**
     This method contains the logic to make sure that the user is always free to do what they want
     @param scanner Used for getting the user input
     */
    private static void checkForPositiveOrNegativeInput(Scanner scanner) {
        String yesOrNo = scanner.nextLine();
        if (yesOrNo.contentEquals("Y") || yesOrNo.contentEquals("y")) {
            try {
                beginAuthenticationFlow();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } else if (yesOrNo.contentEquals("N") || yesOrNo.contentEquals("n")) {
            System.exit(0);
        } else {
            System.out.println("Please input either Y OR N");
            checkForPositiveOrNegativeInput(scanner);
        }
    }

    /**
     This method contains the logic to get hashed value for the passwords as we should not be storing
     the passwords raw
     @param pass Contains the user entered password
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