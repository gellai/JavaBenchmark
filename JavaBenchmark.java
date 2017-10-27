import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Scanner;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

/**
 * 
 * @author gellai.com
 */
class JavaBenchmark {

    private static JavaBenchmark hash,
                                 password;

    private static final PrintStream PS = new PrintStream(System.out);

    private FileInputStream inputStream;
    private Scanner scanner;
    private static MessageDigest md;

    private int testNumber,
                modulus;
    
    private String hashString;

    /**
     * 
     * @param file 
     */
    public JavaBenchmark(String file) {

        try {
            inputStream = new FileInputStream(file);
            scanner = new Scanner(inputStream);
        } catch (IOException e) {
            PS.println(e);
            System.exit(1);
        }

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            PS.println(e);
            System.exit(1);
        }
        
        testNumber = 0;
        modulus = 0;
    }

    /**
     * 
     */
    private void validateHash() {
        if (!hash.hashString.matches("^[a-zA-Z0-9]*$") || hash.hashString.length() != 32) {
            PS.println("\nInvalid hash!");
            System.exit(0);
        }
    }

    /**
     * 
     * @param str
     * @return 
     */
    private String getMD5Hash(String str) {
        String md5;

        md.update(str.getBytes());
        byte[] digest = md.digest();
        md5 = DatatypeConverter.printHexBinary(digest);

        return md5;
    }

    /**
     * 
     */
    private void hashRun() {
        long startTimeMs,
             endTimeMs,
             elapsedTimeMs;
        
        int i = 1;
        
        hash.printTestDetails();
        
        startTimeMs = System.currentTimeMillis();
        
        /**
         * START OF CALCULATION PROCESS
         */
        while (password.scanner.hasNextLine()) {
            String passwordLine = password.scanner.nextLine();

            String md5Hash = password.getMD5Hash(passwordLine);
            
            if( (password.modulus > 0) && (i % password.modulus == 0) ) {
                PS.print("  Tried hashes per " + password.modulus + ": " + i + "\r");
            }
            
            if (md5Hash.equalsIgnoreCase(hash.hashString)) {
                PS.println("  **Password found: " + passwordLine);
                break;
            }           
            i++;
        }
        /**
         * END OF CALCULATION PROCESS
         */
        
        endTimeMs = System.currentTimeMillis();
        
        elapsedTimeMs = endTimeMs - startTimeMs;

        if( (password.modulus == 0) || (i % password.modulus != 0)  ) {
            i--;
            
            for(int s=0; s<50; s++) {
                PS.print(" ");
            }
            
            PS.print("\r");
            PS.println("  Total passwords: " + i);
        }
        
        showElapsedTime(elapsedTimeMs);
    }

    /**
     * 
     * @param elapsedMs 
     */
    private void showElapsedTime(long elapsedMs) {
        double elapsedS = elapsedMs / 1000.0;
        
        PS.println("  Elapsed time: " + elapsedS + " seconds");
        PS.println("----------------------------------------");
    }
    
    /**
     * 
     */
    private void printTestDetails() {
        hash.testNumber++;
        
        PS.println("\nTest: " + hash.testNumber);
        PS.println("  Status displaying steps: " + password.modulus);
        PS.println("  Hash: " + hash.hashString);    
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {

        if (args.length != 2 || args[0] == null || args[1] == null) {
            PS.print("\nMissing arguments!\n\nUsage:\nJavaBenchmark {Hash File} {Password File}\n");
            System.exit(0);
        }
        
        /**
         * Checking if the hash file is valid
         */
        hash = new JavaBenchmark(args[0]);       
        hash.hashString = hash.scanner.nextLine();
        hash.validateHash();
        
        /**
         * The first test is not displaying
         * the status of the progress.
         */
        password = new JavaBenchmark(args[1]); 
        password.modulus = 0;
        password.hashRun();

        /**
         * The second run displays the progress
         * status. 
         */
        password = new JavaBenchmark(args[1]); 
        password.modulus = 1;
        password.hashRun();
    }
}
