public interface QueryHandlerInterface {
    /**
     Handles the logic for CREATE statement
     @param queryArray parameters used by the method
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void createHandler(String[] queryArray);

    /**
     This method contains the logic to fetch the column values from the complete csv line
     @param queryArray Has the query entered in the form of String Array
     @param isTransaction Lets us know if the selection shall be done from the buffer(arraylist) or the csv file
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void selectHandler(String[] queryArray, boolean isTransaction);

    /**
     This method contains the logic to insert values into the csv file of the table
     @param queryArray Used for getting the query words individually
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void insertHandler(String[] queryArray);

    /**
     This method contains the logic to UPDATE the values in the Table
     @param queryArray Used for getting the query words individually
     @param isTransaction this variable helps us to know if we are supposed to insert
     updated contents in the Table or not
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void updateHandler(String[] queryArray, boolean isTransaction);

    /**
     This method contains the logic to DELETE rows from a table
     @param queryArray Used for getting the query words individually,
     so we can get the individual components from the query
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void deleteHandler(String[] queryArray);

    /**
     This method contains the logic to get the contents from table.csv to an arraylist,
     so that we can keep temporary changes there
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void startTransaction();

    /**
     This method contains the logic to store all the information from the temporary Arraylist to
     the csv file permanently.
     */
    public void commit();

    /**
     This method contains the logic to empty the data structure used as buffer.
     */
    public void rollback();
}
