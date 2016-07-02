package modularsystemerrorhandlingssw.parsers;

import modularsystemerrorhandlingssw.ErrorHandler;

import java.util.ArrayList;

/**
 * Класс ParserError5 Обрабатывает тип ошибки №5 (пакет с несовпавшем ID).
 *
 * @author Заразка А.В. /КС-МИСАТ
 */
public class ParserError5 implements IParser {

    @Override
    public void errorHandler(ErrorHandler h) {

        for (String stringParsing : h.line) {
            String tok[] = stringParsing.split("\\s\\|\\s");

            // Идентификатор объекта до безопасного "И".
            String strObjectID = tok[3].substring(1, 9);
            long objectID = Long.parseLong(strObjectID, 16);

            // Идентификатор объекта после прогона через "И".
            String strObjectIDAfterAND = tok[4].substring(1, 9);

            // CAN-пакет.
            String CANPacket = tok[3].substring(10, 26);

            // CAN-пакет после програмного "И".
            String CANPacketAfterAND = tok[4].substring(10, 26);

//*********************************************************************************************************************************************************            
            if (tok[0].equals("5")) {
                
                if (!strObjectID.equalsIgnoreCase(strObjectIDAfterAND)) {

                    // проверочный CAN пакет
                    final String testCANPacket = "0000000000000000";
                    long objectIDAfterAND = Long.parseLong(strObjectIDAfterAND, 16);

                    System.out.println("*********************************************************************************************************************");
                    System.out.println("Тип ошибки №" + tok[0] + " - пакет с несовпавшем ID");
                    System.out.println(stringParsing.replace("\r", ""));

                    // CAN-пакет до элемента "И"
                    System.out.println("Пакет полученный от клиента");
                    ArrayList<Integer> listOfBits = ErrorHandler.bitIndex(CANPacket, testCANPacket);
                    for (int i = 0; i < listOfBits.size(); i++) {
                        int bitIndex = listOfBits.get(i);
                        System.out.println(h.bh.getNameByCanBit(objectID, bitIndex) + '\n');
                    }

                    // CAN-пакет после элемента "И"
                    System.out.println("Пакет после прогона через \"И\"");
                    ArrayList<Integer> listOfBitsAfterAND = ErrorHandler.bitIndex(CANPacketAfterAND, testCANPacket);
                    for (int i = 0; i < listOfBitsAfterAND.size(); i++) {
                        int bitIndex = listOfBitsAfterAND.get(i);
                        System.out.println(h.bh.getNameByCanBit(objectIDAfterAND, bitIndex) + '\n');
                    }

//**********************************************************************************************************************************************************                
//********************************** ИСКЛЮЧИТЕЛЬНАЯ СИТУАЦИЯ ***********************************************************************************************

                } else {

                    /**
                     * Исключительная ситуация, когда ошибка регистрируется по
                     * типу №5 (пакет с несовпавшем ID), а по факту ошибка
                     * относится к типу №2 или №3 (пакеты с несовпадающими
                     * данными после безопасного/програмного "И").
                     */
                    System.out.println("*********************************************************************************************************************");
                    System.out.println("Тип ошибки №" + tok[0] + " - Fatal Error.");
                    System.out.println("ID пакетов равны: " + strObjectID + " == " + strObjectIDAfterAND);
                    System.out.println(stringParsing.replace("\r", "") + '\n');

                    ArrayList<Integer> listOfBits = ErrorHandler.bitIndex(CANPacket, CANPacketAfterAND);
                    for (int i = 0; i < listOfBits.size(); i++) {
                        int bitIndex = listOfBits.get(i);
                        System.out.println(h.bh.getNameByCanBit(objectID, bitIndex) + '\n');
                    }
                }
            }
        }
    }
}
