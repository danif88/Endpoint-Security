package enpoint.log;

/**
 * Utilities log
 */
import java.io.*;
import java.text.*;
import java.util.*;

public class Logger {

    //private static String logFile = "/msglog.txt";
    private final static DateFormat df = new SimpleDateFormat ("yyyy.MM.dd  hh:mm:ss ");
    private final static DateFormat df_file = new SimpleDateFormat ("yyyyMMdd");
    
//    private Logger() { }
//    
//    public static void setLogFilename(String filename) {
//        logFile = filename;
//        new File(filename).delete();
//
//        try {
//            write("LOG file : " + filename);
//        }
//        catch (Exception e) { 
//            System.out.println(stack2string(e));
//        }
//        
//    }
    
    public static void write(String msg) {
    	Date now = new Date();
        write(df_file.format(now) + ".txt", msg);
    }
    
    public static void write(Exception e) {
    	Date now = new Date();
        write(df_file.format(now) + ".txt", stack2string(e));
    }

    public static void write(String file, String msg) {
    	System.out.println("FILE:" + file);
        try {
            Date now = new Date();
            String currentTime = Logger.df.format(now); 
            FileWriter aWriter = new FileWriter(file, true);
            aWriter.write(currentTime + " " + msg 
                    + System.getProperty("line.separator"));
            System.out.println(currentTime + " " + msg);
            aWriter.flush();
            aWriter.close();
        }
        catch (Exception e) {
            System.out.println(stack2string(e));
        }
    }
    
    public static String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
        }
        catch(Exception e2) {
            return "bad stack2string";
        }
    }
}