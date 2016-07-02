package modularsystemerrorhandlingssw.parsers;

import modularsystemerrorhandlingssw.ErrorHandler;

import java.util.ArrayList;

/**
 * Класс ParserError1 обрабатывает тип ошибки №1 (пакет отброшенный потаймауту).
 *
 * @author Заразка А.В. /КС-МИСАТ
 */
public class ParserError1 implements IParser {

    @Override
    public void errorHandler(ErrorHandler h) {
        for (String stringParsing : h.line) {
            String tok[] = stringParsing.split("\\s\\|\\s");

            // Идентификатор объекта до безопасного(или програмного) "И".
            String strObjectID = tok[3].substring(1, 9);
            long objectID = Long.parseLong(strObjectID, 16);

            // CAN-пакет.
            String CANPacket = tok[3].substring(10, 26);

//******************************************************************************************************************************************************            
            if (tok[0].equals("1")) {
                
                // Проверочный CAN-пакет.
                final String testCANPacket = "0000000000000000";

                System.out.println("*********************************************************************************************************************");
                System.out.println("Тип ошибки №" + tok[0] + " - пакет отброшенный по таймауту");
                System.out.println(stringParsing.replace("\r", ""));

                ArrayList<Integer> listOfBits = ErrorHandler.bitIndex(CANPacket, testCANPacket);

                for (int i = 0; i < listOfBits.size(); i++) {
                    int bitIndex = listOfBits.get(i);
                    System.out.println(h.bh.getNameByCanBit(objectID, bitIndex));
                }
            }
        }
    }
}
