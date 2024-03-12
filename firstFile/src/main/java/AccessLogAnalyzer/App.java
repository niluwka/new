package AccessLogAnalyzer;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class App 
{
    private static String path = "/new/firstFile/access.log";
    private static String outputPath = "/new/firstFile/result.json";
    private static  DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
    private static String dateTimeRegex = ".+\\[([^\\]]{20}\\s\\+[0-9]{4})\\].+";

    public static void main( String[] args ) throws IOException {
        Pattern dateTimePattern = Pattern.compile(dateTimeRegex);

        HashMap<Long, Integer> countPerSecond = new HashMap<>();

            long minTime = Long.MAX_VALUE;
            long maxTime = Long.MIN_VALUE;

            int requestsCount = 0;



    List<String> lines = Files.readAllLines(Paths.get(path));
    for (String line :
            lines) {

        Matcher matcher = dateTimePattern.matcher(line);
        if (!matcher.find()){
            continue;
        }

        String dateTime = matcher.group(1);
     long time = getTimestamp(dateTime);

     if (!countPerSecond.containsKey(time)){
         countPerSecond.put(time, 0);
     }
     countPerSecond.put(time, countPerSecond.get(time) + 1);

     minTime = Math.min(time, minTime);
     maxTime = Math.max(time, maxTime);
        requestsCount++;
    }

    int maxRequestsPerSecond = Collections.max( countPerSecond.values());


    double averageRequestsPerSecound = (double) requestsCount / (maxTime - minTime);

Statistics statistics = new Statistics(maxRequestsPerSecond,averageRequestsPerSecound);

        Gson gson = new Gson();
        String json = gson.toJson(statistics);

        FileWriter writer = new FileWriter(outputPath);
        writer.write(json);
        writer.flush();
        writer.close();


    }
    public static long getTimestamp(String dateTime){

        LocalDateTime time = LocalDateTime.parse(dateTime, formatter);
      return time.toEpochSecond(ZoneOffset.UTC);
    }
}
