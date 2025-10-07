import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Sleepy {

    // Example data structure to store sleep records
    private static List<Integer> sleepHours = new ArrayList<>();
    private static List<String> sleepDates = new ArrayList<>();

    private static final String dataFileName = "/Users/matti/Documents/SleepTracker/sleepData.txt";
    
    public static void main(String[] args) {
    	
        // Check if no arguments are passed (just 'sleep' command)
        if (args.length == 0) {
            printStats();
        } else {
            String command = args[0];
            
            if (command.equals("add")) {
                try {
                    int hours = Integer.parseInt(args[1]);
                    addSleep(hours);
                } catch (NumberFormatException e) {
                    System.out.println("Please provide a valid number of hours to add.");
                }
            } else if(command.equals("print")) {
            	try {
        			BufferedReader reader = new BufferedReader(new FileReader(dataFileName));
        			String line;
        			while ((line  = reader.readLine()) != null) {
        				System.out.println(line);
        			}
        			reader.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
            }
            
            else {
                System.out.println("Invalid command.");
            }
        }
    }

    private static void addSleep(int hours) {
    	String now = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE);
    	//write to file
    	try
    	{
    	    //String filename= "Users/matti/Documents/SleepTracker/sleepData.txt";
    	    FileWriter fw = new FileWriter(dataFileName,true); //the true will append the new data
    	    fw.write(now + " "+hours+"\n");//appends the string to the file
    	    fw.close();
    	}
    	catch(IOException ioe)
    	{
    	    System.err.println("IOException: " + ioe.getMessage());
    	}
   
        System.out.println(now + ": " + hours + " hours of sleep.");
    }

    private static void printStats() {
    	//read
		String entry;
        try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFileName));
			while ((entry  = reader.readLine()) != null) {
				String date = entry.substring(0, 10);
				sleepDates.add(date);
//				String timeString = entry.replaceAll("[^0-9.]", "");
//				int time = Integer.parseInt(timeString);
				
				String time = entry.substring(11); //????
				//System.out.println(entry);
				sleepHours.add(Integer.parseInt(time));
				//sleepDates.add(date);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        //print
    	System.out.println();
    	System.out.println("------- stats -------");

        if (sleepHours.isEmpty()) {
        System.out.println("+++ no sleep data +++");
        } else {
            int total = 0;
            for (int hours : sleepHours) {
                total += hours;
            }
            double average = (total / (double)sleepHours.size());
            double rounded = Math.floor(average * 100) / 100;
            //System.out.println("Total hours of sleep: " + total);
            System.out.println("average sleep: " + rounded + " h");
        }
        
       //graph
        System.out.println();
    	System.out.println("-------  log  -------");

    	System.out.println("your goal: ........");
        System.out.println();

       for (int i = 0; i < sleepHours.size();i++) {
    	   int hours = sleepHours.get(i);
    	   String graph="";
    	   for (int j = 0; j < hours; j++) {
    		   graph = graph.concat(".");
    	   }
    	   System.out.println(sleepDates.get(i)+" "+ graph);
       }
   		System.out.println("---------------------");
    }
}
