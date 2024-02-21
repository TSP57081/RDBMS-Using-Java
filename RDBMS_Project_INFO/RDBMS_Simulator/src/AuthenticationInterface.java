public interface AuthenticationInterface {
    /**
     This method contains the logic to authenticate the user credentials
     @param userName has the user entered username
     @param password Has the user entered password
     @return isCorrect Which is basically the user is valid or not
     */
    public boolean verifyUser(String userName, String password);
    /**
     This method contains the logic to add the user sign up information in the ACCOUNTS.csv file
     @param user has the user entered username
     @param pass Has the user selected password
     */
    public void addUser(String user, String pass);
}
