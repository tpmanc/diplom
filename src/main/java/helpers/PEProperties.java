package helpers;

import models.PropertyModel;
import org.boris.pecoff4j.PE;
import org.boris.pecoff4j.ResourceDirectory;
import org.boris.pecoff4j.ResourceEntry;
import org.boris.pecoff4j.constant.ResourceType;
import org.boris.pecoff4j.io.PEParser;
import org.boris.pecoff4j.io.ResourceParser;
import org.boris.pecoff4j.resources.StringFileInfo;
import org.boris.pecoff4j.resources.StringTable;
import org.boris.pecoff4j.resources.VersionInfo;
import org.boris.pecoff4j.util.ResourceHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Получение свойств из файла
 */
public class PEProperties {
    /**
     * ПОлучение свойств файла
     * @param filePath Путь до файла
     * @return Набор полученных свойств
     */
    public static Map<Integer, String> parse(String filePath){
        Map<Integer, String> properties = new HashMap<Integer, String>();
        PE pe = null;
        try {
            pe = PEParser.parse(filePath);
            if (pe.getSignature() != null) {
                ResourceDirectory rd = pe.getImageData().getResourceTable();

                if (pe.is64()) {
                    properties.put(PropertyModel.FILE_BITS, "x64");
                } else {
                    properties.put(PropertyModel.FILE_BITS, "x86");
                }
                ResourceEntry[] entries = ResourceHelper.findResources(rd, ResourceType.VERSION_INFO);
                for (ResourceEntry entry : entries) {
                    byte[] data = entry.getData();
                    VersionInfo version = ResourceParser.readVersionInfo(data);

                    StringFileInfo strings = version.getStringFileInfo();
                    StringTable table = strings.getTable(0);
                    for (int j = 0; j < table.getCount(); j++) {
                        String key = table.getString(j).getKey();
                        String value = new String(table.getString(j).getValue().getBytes("ISO-8859-1"), "UTF-8");
                        value = value.replaceAll("[^\\x20-\\x7e]", "").replaceAll("[^\\u0000-\\uFFFF]", "").trim();
                        int propertyId = PropertyModel.getDefaultProperty(key);
                        if (propertyId > 0) {
                            properties.put(propertyId, value);
                        } else {
                            System.out.println("Not user properties:");
                            System.out.println(key + " = " + value);
                        }
                    }
                }
            }
        } catch (IOException e) {
             e.printStackTrace();
        } catch (OutOfMemoryError e) {
             e.printStackTrace();
        }
        return properties;
    }
}
