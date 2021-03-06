package helpers;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Команды для экспорта и выполнение команд
 */
public class CommandHelper {

    public static ArrayList<String> executeWindows(String command, String regexp, int interpreter) throws IOException, InterruptedException {
        ArrayList<String> result = new ArrayList<String>();

        if (interpreter == 1) {
            String path = createFile(command, ".bat");
            String[] cmd = {"cmd.exe", "/C", path};

            Process p =  Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                String value = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                sb.append(value).append("\n");
            }
            reader.close();
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(sb.toString());
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
        } else if (interpreter == 2) {
            String path = createFile(command, ".ps1");
            String[] cmd = {"powershell", path};

            Process p =  Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                String value = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                sb.append(value).append("\n");
            }
            reader.close();
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(sb.toString());
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
        } else if (interpreter == 3) {
            String path = createFile(command, ".js");
            String[] cmd = {"cscript", "/nologo", path};

            Process p =  Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                String value = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                sb.append(value).append("\n");
            }
            reader.close();
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(sb.toString());
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
        } else if (interpreter == 4) {
            String path = createFile(command, ".vbs");
            String[] cmd = {"cscript", "/nologo", path};

            Process p =  Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                String value = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                sb.append(value).append("\n");
            }
            reader.close();
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(sb.toString());
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
        }
        return result;
    }

    public static String executeWindows(String command, int interpreter) throws IOException, InterruptedException {
        if (interpreter == 1) {
            String path = createFile(command, ".bat");
            String[] cmd = {"cmd.exe", "/C", path};

            Process p =  Runtime.getRuntime().exec(cmd);
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
        } else if (interpreter == 2) {
            String path = createFile(command, ".ps1");
            String[] cmd = {"powershell", path};

            Process p =  Runtime.getRuntime().exec(cmd);
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
        } else if (interpreter == 3) {
            String path = createFile(command, ".js");
            String[] cmd = {"cscript", "/nologo", path};

            Process p =  Runtime.getRuntime().exec(cmd);
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
        } else if (interpreter == 4) {
            String path = createFile(command, ".vbs");
            String[] cmd = {"cscript", "/nologo", path};

            Process p =  Runtime.getRuntime().exec(cmd);
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
        }
        return "";
    }

    private static String createFile(String text, String extension) throws IOException {
        File temp = File.createTempFile("repository-temp", extension);
        System.out.println("Temp file : " + temp.getAbsolutePath());
        //write it
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(text);
        bw.close();
        boolean t = temp.setExecutable(true);
        System.out.println(t);
        String path = temp.getAbsolutePath();
        return path;
    }

    public static ArrayList<String> executeLinux(String command, String regexp) throws IOException, InterruptedException {
        String com = command.replaceAll("\\r", "\n");
        com = com.replaceAll("\\r\\n", "\n");
        String path = createFile(com, ".sh");
        ArrayList<String> result = new ArrayList<String>();
        Process p;
        p = Runtime.getRuntime().exec(path);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine())!= null) {
            sb.append(line).append("\n");
        }
        reader.close();
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(sb.toString());
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    public static String executeLinux(String command) throws IOException, InterruptedException {
        String com = command.replaceAll("\\r", "\n");
        com = com.replaceAll("\\r\\n", "\n");
        String path = createFile(com, ".sh");
        ArrayList<String> result = new ArrayList<String>();
        Process p;
        p = Runtime.getRuntime().exec(path);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine())!= null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
