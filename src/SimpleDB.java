import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleDB {
    private static DataStore ds = new DataStore();
    
    public static void main(String[] args) {
        DataStore ds = new DataStore();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;                
            while((input=br.readLine())!=null){
                if (!processCommand (input))
                    return;
            }
        }catch(IOException io){
            io.printStackTrace();
        }   
    }
    
    private static boolean processCommand (String input) {
        String[] token = input.split("\\s+");
        String command = token[0];
        try {
            switch (command) {
                case "GET": 
                    String value = ds.get(token[1]);
                    System.out.println(value == null ? "NULL" : value);
                    break;
                case "SET":
                    ds.set(token[1], token[2]);
                    break;
                case "UNSET":
                    ds.unset(token[1]);
                    break;
                case "NUMEQUALTO":
                    System.out.println(ds.count(token[1]));
                    break;
                case "END":
                    return false;
                case "BEGIN":
                    ds.createTransaction();
                    break;
                case "COMMIT":
                    ds.commit();
                    break;
                case "ROLLBACK":
                    ds.rollback();
                    break;
                default: 
                    System.out.println("Invalid Command.");  
            }
        } catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Invalid num of parameters for command: " + input);
        }
        return true;
    }
}

