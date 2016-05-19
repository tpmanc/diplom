package helpers;

import models.helpers.ExportParam;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Команды для экспорта и выполнение команд
 */
public class CommandHelper {
    private final static String defaultXsd = "";

    public static ArrayList<String> execute(String command) {
        ArrayList<String> result = new ArrayList<String>();
        result.add("user 1");
        result.add("user 2");
        result.add("user 3");
        return result;
    }

    public static ArrayList<String> executeLinux(String command, String regexp) throws IOException, InterruptedException {

//        new DefaultExecutor().execute(cmd);

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
//        CommandLine cmd = CommandLine.parse("/usr/bin/php \n $arr = [1,2,3]; \n var_dump($arr);");
        CommandLine cmd = new CommandLine("/usr/bin/php");
        cmd.addArguments(new String[] {
                "-c",
                "'$arr = [1,2,3]; \n var_dump($arr);'"
        }, false);
        DefaultExecutor exec = new DefaultExecutor();
        exec.setStreamHandler(psh);
        exec.execute(cmd);
        System.out.println(stdout.toString());

        ArrayList<String> result = new ArrayList<String>();
        Process p = null;
        p = Runtime.getRuntime().exec(command);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine())!= null) {
            sb.append(line + "\n");
        }
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(sb.toString());
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    public static String executeLinux(String command) throws IOException, InterruptedException {
        Process p = null;
        p = Runtime.getRuntime().exec(command);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine())!= null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
