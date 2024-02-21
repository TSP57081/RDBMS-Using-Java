import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryHandler implements QueryHandlerInterface {
    static ArrayList<Integer> lookForValuesAt = new ArrayList<>();
    static ArrayList<ArrayList<String>> table = new ArrayList<>();

    /**
     Handles the logic for CREATE statement
     @param queryArray parameters used by the method
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void createHandler(String[] queryArray) {
        String tableName = queryArray[2];
        ArrayList<String> queryList = new ArrayList<>(List.of(queryArray));
        List<String> columns = queryList.subList(3,queryArray.length);

        // Logic to remove the expressions from the column names provided
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            String updatedString = a.replace("(","");
            String secondUpdateString = updatedString.replace(")","");
            String thirdUpdateString = secondUpdateString.replace(",","");
            String finalString = thirdUpdateString.replace(";","");
            columns.set(i, finalString);
        }
        ArrayList<String> columnNames = new ArrayList<>();
        for (int i = 0; i < columns.size(); i+=2) {
            columnNames.add(columns.get(i));
        }

        try {
            FileWriter writer = new FileWriter(tableName+".csv");

            // Insert records into the CSV file
            writer.append(String.join(",", columnNames)).append("\n");
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Table Creation Successful!");
    }

    /**
     This method contains the logic to fetch the column values from the complete csv line
     @param queryArray Has the query entered in the form of String Array
     @param isTransaction Lets us know if the selection shall be done from the buffer(arraylist) or the csv file
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void selectHandler(String[] queryArray, boolean isTransaction) {
        if (isTransaction) {
            System.out.println();
            for (ArrayList<String> row : table) {
                for (String cell : row) {
                    System.out.print(cell);
                    int numberOfSpaces = 9 - cell.length();
                    while (numberOfSpaces > 0) {
                        System.out.print(" ");
                        numberOfSpaces--;
                    }
                    System.out.print("|");
                }
                System.out.println();
            }
        } else {
            int fromAt = 0;
            for (int i = 1; i < queryArray.length - 1; i++) {
                if (queryArray[i].contentEquals("from") || queryArray[i].contentEquals("FROM")) {
                    fromAt = i;
                    break;
                }
            }
            ArrayList<String> queryElements = new ArrayList<>(List.of(queryArray));
            List<String> columnsToShow = queryElements.subList(1,fromAt);
            for (int i = 0; i < columnsToShow.size(); i++) {
                String updatedColumnname = columnsToShow.get(i).replace(",","").replace(" ","");
                columnsToShow.set(i, updatedColumnname);
            }
            String tableName = queryArray[2 + columnsToShow.size()].replace(";","");
            List<String> condition = queryElements.subList(fromAt+2,queryElements.size());

            ArrayList<List<String>> results = new ArrayList<>();
            Scanner sc = null;
            List<String> columnNames = new ArrayList<>();
            try {
                sc = new Scanner(new File(tableName+".csv"));

                if (columnsToShow.get(0).contentEquals("*")) {
                    boolean columnFlag = false;
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        String[] values = line.split(",");
                        if (!columnFlag) {
                            columnNames.addAll(List.of(values));
                        }
                        results.add(List.of(values));
                    }
                } else {
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        ArrayList<String> values = getAppropriateValues(line, columnsToShow);
                        results.add(values);
                    }
                }

                System.out.println();
                for (int i = 0; i < results.size(); i++) {
                    for (int j = 0; j < results.get(i).size(); j++) {
                        System.out.print(results.get(i).get(j));
                        int numberOfSpaces = 9 - results.get(i).get(j).length();
                        while (numberOfSpaces > 0) {
                            System.out.print(" ");
                            numberOfSpaces--;
                        }
                        System.out.print("|");
                    }
                    System.out.println();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     This method contains the logic to fetch the column values from the complete csv line
     @param line has the complete line information
     @param columnsToShow Has the list of columns we need
     updated contents in the Table or not
     */
    private static ArrayList<String> getAppropriateValues(String line, List<String> columnsToShow) {
        String[] values = line.split(",");
        ArrayList<String> result = new ArrayList<>();
        try {
            int a = lookForValuesAt.get(0);
        } catch (Exception e) {
            for (int i = 0; i < values.length; i++) {
                for (String column : columnsToShow) {
                    if (column.contentEquals(values[i])) {
                        lookForValuesAt.add(i);
                    }
                }
            }
        }
        for (int i = 0; i < lookForValuesAt.size(); i++) {
            result.add(values[i]);
        }
        return result;
    }

    /**
     This method contains the logic to insert values into the csv file of the table
     @param queryArray Used for getting the query words individually
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void insertHandler(String[] queryArray) {
        String tableName = queryArray[2];
        ArrayList<String> queryList = new ArrayList<>(List.of(queryArray));
        List<String> columns = queryList.subList(3,queryArray.length);

        // Logic to remove the expressions from the column names provided
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            String updatedString = a.replace("(","");
            String secondUpdateString = updatedString.replace(")","");
            String thirdUpdateString = secondUpdateString.replace(",","");
            String finalString = thirdUpdateString.replace(";","");
            columns.set(i, finalString);
        }

        try {
            FileWriter writer = new FileWriter(tableName+".csv", true);
            // Insert records into the CSV file
            writer.append(String.join(",", columns)).append("\n");
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     This method contains the logic to UPDATE the values in the Table
     @param queryArray Used for getting the query words individually
     @param isTransaction this variable helps us to know if we are supposed to insert
     updated contents in the Table or not
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void updateHandler(String[] queryArray, boolean isTransaction) {
        String tableName = queryArray[1].replace(";", "");
        String operationSymbol = queryArray[6].replace(",", "");
        String amount = queryArray[7].replace(",", "");
        int amountInInt = Integer.parseInt(amount);
        String referenceColumnValue = queryArray[11].replace(";", "");
        if (isTransaction) {
            for (ArrayList<String> row : table) {
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(0).contentEquals(referenceColumnValue)) {
                        if (operationSymbol.contentEquals("-")) {
                            row.set(3, String.valueOf((Integer.parseInt(row.get(3)) - amountInInt)));
                        } else {
                            row.set(3, String.valueOf((Integer.parseInt(row.get(3)) + amountInInt)));
                        }
                        break;
                    }
                }
                System.out.println();
            }
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(tableName + ".csv"));
                String line;
                StringBuilder updatedValues = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    String[] currentValues = line.split(",");
                    if (currentValues.length > 0 && currentValues[0].equals(referenceColumnValue)) {
                        int currentAmount = Integer.parseInt(currentValues[3]);
                        if (operationSymbol.contentEquals("-")) {
                            currentAmount = currentAmount - amountInInt;
                        } else {
                            currentAmount = currentAmount + amountInInt;
                        }
                        line = currentValues[0] + "," + currentValues[1] + "," + currentValues[2] + "," + currentAmount;
                    }
                    updatedValues.append(line).append("\n");
                }

                reader.close();

                // Write the updated data back to the CSV file
                BufferedWriter writer = new BufferedWriter(new FileWriter(tableName + ".csv"));
                writer.write(updatedValues.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     This method contains the logic to DELETE rows from a table
     @param queryArray Used for getting the query words individually,
     so we can get the individual components from the query
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
     public void deleteHandler(String[] queryArray) {
     String tableName = queryArray[2].replace(",","");
     String referenceColumnValue = queryArray[6].replace(";","");

     try {
         BufferedReader reader = new BufferedReader(new FileReader(tableName + ".csv"));
         String line;
         StringBuilder updatedValues = new StringBuilder();

         while ((line = reader.readLine()) != null) {
             String[] currentValues = line.split(",");
             if (currentValues.length > 0 && !currentValues[0].equals(referenceColumnValue)) {
                 updatedValues.append(line).append("\n");
             }
         }

         reader.close();

         // Write the updated data back to the CSV file
         BufferedWriter writer = new BufferedWriter(new FileWriter(tableName + ".csv"));
         writer.write(updatedValues.toString());
         writer.close();
     } catch (IOException e) {
         throw new RuntimeException(e);
     }
    }

    /**
     This method contains the logic to get the contents from table.csv to an arraylist,
     so that we can keep temporary changes there
     @throws RuntimeException When the Buffered reader is unable to find the file,
     Which in this project's case is never, but Java like to be safe.
     */
    public void startTransaction() {
        String tableName = "ACCOUNT.csv";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(tableName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] currentValues = line.split(",");
                ArrayList<String> row = new ArrayList<>(List.of(currentValues));
                table.add(row);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Transaction has begun. Please enter the transaction queries.");
    }

    /**
     This method contains the logic to store all the information from the temporary Arraylist to
     the csv file permanently.
     */
    public void commit() {
        if (!table.isEmpty()) {
            String tableName = "ACCOUNT";
            try {
                FileWriter writer = new FileWriter(tableName+".csv", false);
                // Insert records into the CSV file
                for (ArrayList<String> row : table) {
                    writer.append(String.join(",", row)).append("\n");
                }
                writer.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     This method contains the logic to empty the data structure used as buffer.
     */
    public void rollback() {
        table.clear();
    }
}
