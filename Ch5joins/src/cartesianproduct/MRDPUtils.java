package cartesianproduct;

import java.util.HashMap;
import java.util.Map;

public class MRDPUtils {

  public static final String[] REDIS_INSTANCES =
      { "p0", "p1", "p2", "p3", "p4", "p6" };

  // This helper function parses the stackoverflow into a Map for us.
  public static Map<String, String> transformXmlToMap(String xml) {
    Map<String, String> map = new HashMap<String, String>();
    try {
      String[] tokens =
          xml.trim().substring(5, xml.trim().length() - 3).split("\"");
    //  System.out.println("xml : "+xml);
    //  System.out.println("xml.trim().length() "+ xml.trim().length()); cut first 5 to last-3
      
      for (int i = 0; i < tokens.length - 1; i += 2) {
        String key = tokens[i].trim();
        String val = tokens[i + 1];
        map.put(key.substring(0, key.length() - 1), val);
      //  System.out.println("*********Key : "+key + " **Val"+val);
      //  System.out.println("----------------------------------------------------");
      }
    } catch (StringIndexOutOfBoundsException e) {
      System.err.println(xml);
    }

    return map;
  }
}
