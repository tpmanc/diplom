package helpers;

import models.FileModel;
import models.FilePropertyModel;
import models.FileVersionModel;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PEProperties {
    public static Map<Integer, String> parse(String filePath){
        Map<Integer, String> properties = new HashMap<Integer, String>();
        PE pe = null;
        try {
            pe = PEParser.parse(filePath);
            if (pe.getSignature() != null) {
                ResourceDirectory rd = pe.getImageData().getResourceTable();

                ResourceEntry[] entries = ResourceHelper.findResources(rd, ResourceType.VERSION_INFO);
                for (ResourceEntry entry : entries) {
                    byte[] data = entry.getData();
                    VersionInfo version = ResourceParser.readVersionInfo(data);

                    StringFileInfo strings = version.getStringFileInfo();
                    StringTable table = strings.getTable(0);
                    for (int j = 0; j < table.getCount(); j++) {
                        String key = table.getString(j).getKey();
                        String value = table.getString(j).getValue();
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
        }
        return properties;
    }
}
