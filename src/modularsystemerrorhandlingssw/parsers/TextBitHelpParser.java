package modularsystemerrorhandlingssw.parsers;

import modularsystemerrorhandlingssw.BitHelp;
import modularsystemerrorhandlingssw.CanBit;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Пелепейченко А.В. /КС-МИСАТ
 */
public class TextBitHelpParser {

    Map<CanBit, CanBit> map = new HashMap();

    public void parse(String fileName, BitHelp bh) throws FileNotFoundException, IOException {
        parse(new File(fileName), bh);
    }

    public void parse(File file, BitHelp bh) throws FileNotFoundException, IOException {

        FileInputStream fstream = new FileInputStream(file);

        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;

        Pattern ptrn = Pattern.compile("^\\ *\\d+:\\d+\\s0x[0-9a-fA-F]*:\\d+\\(.*\\)");//123:234 0x23dF0:32(sdfert)

        while ((strLine = br.readLine()) != null) {
            if (strLine.toLowerCase().startsWith("canid")) {
                parseGroup(strLine, bh);
                continue;
            }
            Matcher m = ptrn.matcher(strLine);
            if (m.matches()) {
                parseBit(strLine, bh);
            }

        }
        in.close();

        bh.setBitInfos(map);
    }

    //format:
    //canId 0x23001 54401/1
    private void parseGroup(String strLine, BitHelp bh) {
        String[] tokens = strLine.split(" ");
        int firstSpace = strLine.indexOf(" ", 0);
        int secondSpace = strLine.indexOf(" ", firstSpace + 1);
        String title = strLine.substring(secondSpace);
        long canId = Long.parseLong(tokens[1].substring(2), 16);
        bh.addNameByGroup(canId, title);
        if (canId >= 0x24000 && canId <= 0x24fff) {

            //1 диапазон
            canId = canId & 0xFF3FF;
            bh.addNameByGroup(canId, title);

            //2 диапазон
            canId = canId | 0x400;
            bh.addNameByGroup(canId, title);

            //4 диапазон
            canId = canId | 0x800;
            bh.addNameByGroup(canId, title);

            //3 диапазан
            canId = canId & 0xffbff;
            bh.addNameByGroup(canId, title);
        }
    }

    //format canId:bit(help)
    /**
     *
     * 54401:1 0x23001:32(31ПК) ^ ^ ^ ^ ^^^^ - help ^ ^ ^ ^ bitIndex ^ ^ ^ canId
     * ^ ^contactnum ^moduleAddr
     */
    private void parseBit(String strLine, BitHelp bh) {
        if (strLine == null || ! !strLine.startsWith("0x")) {
            return;
        }

        String grpknt = strLine.substring(0, strLine.indexOf(" "));
        strLine = strLine.substring(strLine.indexOf(" ") + 1);

        String canIdTok = strLine.substring(0, strLine.indexOf(":"));
        long canid = Long.parseLong(canIdTok.substring(2), 16);

        String otherTok = strLine.substring(1 + strLine.indexOf(":"));

        int braceStart = otherTok.indexOf("(");
        String bitStr = otherTok.substring(0, braceStart);
        int bit = parseInt(bitStr);

        String help = otherTok.substring(braceStart + 1, otherTok.length() - 1);

        String[] helps = {grpknt, canIdTok + ":" + bit, help};
        CanBit cb = new CanBit(canid, bit, helps, true);
        cb.setToolTip(grpknt);
        map.put(cb, cb);

        CanBit cb1 = new CanBit(canid - 0x800, bit, helps, true);
        cb1.setToolTip(grpknt);
        map.put(cb1, cb1);
    }

    public static int parseInt(String v) {
        if (v.startsWith("0x")) {
            return Integer.parseInt(v.substring(2), 16);
        } else {
            return Integer.parseInt(v);
        }
    }
}
