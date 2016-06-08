package helpers;

import exceptions.InternalException;
import models.FileTriggerModel;
import models.PropertyModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TriggerHelper {
    public final static String titleKey = "title";
    public final static String versionKey = "version";
    public final static String descriptionKey = "description"; // 1
    public final static String copyrightKey = "copyright"; // 5
    public final static String authorKey = "author"; // 6

    public static Map<Integer, String> getProperties(String extension, String filePath) {
        FileTriggerModel trigger = FileTriggerModel.findByExtension(extension);
        if (trigger != null) {
            Map<Integer, String> properties = new HashMap<Integer, String>();

            String fileCommand = trigger.getCommand().replace("{filePath}", filePath);
            String commandResult = executeCommand(fileCommand);

            if (commandResult != null) {
                Pattern pattern = Pattern.compile(trigger.getRegexp());
                Matcher matcher = pattern.matcher(commandResult);
                if (matcher.find()) {
                    try {
                        properties.put(PropertyModel.PRODUCT_NAME, matcher.group(titleKey));
                    } catch (Exception ignored) {
                    }
                    try {
                        properties.put(PropertyModel.FILE_VERSION, matcher.group(versionKey));
                    } catch (Exception ignored) {
                    }
                    try {
                        properties.put(1, matcher.group(descriptionKey));
                    } catch (Exception ignored) {
                    }
                    try {
                        properties.put(5, matcher.group(copyrightKey));
                    } catch (Exception ignored) {
                    }
                    try {
                        properties.put(6, matcher.group(authorKey));
                    } catch (Exception ignored) {
                    }
                }
            }

            return properties;
        }
        return null;
    }



    private static String executeCommand(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                String value = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                sb.append(value).append("\n");
            }
            reader.close();
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
//            throw new InternalException(e.getMessage());
        }
        return null;
    }
}
