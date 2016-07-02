package modularsystemerrorhandlingssw;

import java.io.Serializable;
import java.util.*;
import javax.xml.xpath.XPathExpressionException;

/**
 *
 * @author Пелепейченко А.В. /КС-МИСАТ
 */
public class BitHelp implements Serializable {

    private Map<CanBit, CanBit> canBitsMap = new HashMap<>();
    private Map<Long, String> nameByGroup = new HashMap<>();

    public BitHelp() throws XPathExpressionException {
        super();
        //первый встретившийся предок с элементом TCADObj, имеющим аттрибут Name. значение этого аттрибута
        //findRoot = xpath.compile("./ancestor::*[TCADObj/@Name][1]");

    }

    public Set<Long> getAllKnownCanId() {
        Set<Long> set = new HashSet<Long>();
        set.addAll(nameByGroup.keySet());
        for (CanBit cb : canBitsMap.values()) {
            set.add(cb.canAddr);
        }
        return set;
    }

    public Set<Long> getTsCanId() {
        Set<Long> set = new HashSet<Long>();
        set.addAll(nameByGroup.keySet());
        for (CanBit cb : canBitsMap.values()) {
            if (!cb.isTu) {
                set.add(cb.canAddr);
            }
        }
        return set;
    }

    public Set<Long> getTuCanId() {
        Set<Long> set = new HashSet<Long>();
        set.addAll(nameByGroup.keySet());
        for (CanBit cb : canBitsMap.values()) {
            if (cb.isTu) {
                set.add(cb.canAddr);
            }
        }
        return set;
    }

    public void setBitInfos(Map<CanBit, CanBit> v) {
        canBitsMap = v;
    }

    public void addNameByGroup(Long canId, String moduleName) {
        nameByGroup.put(canId, moduleName);
        //System.out.println(""+canId+"->"+moduleName);
    }

    public String getModuleName(Long canId) {
        String result = nameByGroup.get(canId);
        if (result == null) {
            result = "";
        }
        return result;
    }

    public String getNameByCanBit(CanBit cb) {
        if (cb == null) {
            return "-";
        }
        CanBit nameCb = canBitsMap.get(cb);
        if (nameCb != null) {
            StringBuilder nameConstructor = new StringBuilder();
            nameConstructor.setLength(0);
            for (String namePart : nameCb.name) {


                if (namesTranslation != null) {

                    String n = namesTranslation.getProperty(namePart, namePart);
                    //System.out.println("name Part="+namePart+"  name="+n);

                    nameConstructor.append(n);
                } else {
                    nameConstructor.append(namePart);
                }
                nameConstructor.append(" | ");
            }
            if (nameConstructor.length() > 2) {
                nameConstructor.setLength(nameConstructor.length() - 1);
            }
            return nameConstructor.toString();
        } else {
            // Выводит объекты которых нет в базе (null:22 | 0x64007:21 | Warning - Устройство не определено.)
            String groupName = nameByGroup.get(cb.canAddr);
            return groupName + " | 0x" + Long.toHexString(cb.canAddr) + ":" + cb.bit + " | Warning - Устройство не определено.";
        }
    }
    String nullname[] = {""};

    public String getNameByCanBit(long canId, int bitIndex) {
        CanBit cb = new CanBit(canId, bitIndex, nullname, false);
        return getNameByCanBit(cb);
    }

    public CanBit getCanBitConfig(long canId, int bitIndex) {
        CanBit cbKey = new CanBit(canId, bitIndex, nullname, false);
        CanBit cbValue = canBitsMap.get(cbKey);
        return cbValue;
    }
    private Properties namesTranslation;

    public void setNamesTranslation(Properties p) {
        namesTranslation = p;
    }
}
