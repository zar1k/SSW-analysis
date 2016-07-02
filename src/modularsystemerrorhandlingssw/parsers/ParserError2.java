package modularsystemerrorhandlingssw.parsers;

import modularsystemerrorhandlingssw.ErrorHandler;

import java.util.ArrayList;

/**
 * Класс ParserError2 Обрабатывает тип ошибки №2 (пакеты с несовпадающими
 * данными после безопасного "И").
 *
 * @author Заразка А.В. /КС-МИСАТ
 */
public class ParserError2 implements IParser {

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

            // CAN-пакет после безопасного "И".
            String CANPacketAfterAND = tok[4].substring(10, 26);

//**********************************************************************************************************************************************************
            if (tok[0].equals("2")) {
                
                if (strObjectID.equalsIgnoreCase(strObjectIDAfterAND)) {
                    System.out.println("*********************************************************************************************************************");
                    System.out.println("Тип ошибки №" + tok[0] + " - пакеты с несовпадающими данными после безопасного \"И\"");
                    System.out.println(stringParsing.replace("\r", ""));

                    ArrayList<Integer> listOfBits = ErrorHandler.bitIndex(CANPacket, CANPacketAfterAND);
                    for (int i = 0; i < listOfBits.size(); i++) {
                        int bitIndex = listOfBits.get(i);
                        System.out.println(h.bh.getNameByCanBit(objectID, bitIndex) + '\n');
                    }

//**********************************************************************************************************************************************************
//********************************** ИСКЛЮЧИТЕЛЬНАЯ СИТУАЦИЯ ***********************************************************************************************
                    
                } else {
                    /**
                     * Исключительная ситуация, когда ошибка регистрируется по
                     * типу №2 (пакеты с несовпадающими данными после
                     * безопасного "И"), а по факту ошибка относится к типу №5
                     * (пакет с несовпавшем ID).
                     */
                    // Проверочный CAN-пакет.
                    final String testCANPacket = "0000000000000000";
                    long objectIDAfterAND = Long.parseLong(strObjectIDAfterAND, 16);

                    System.out.println("*********************************************************************************************************************");
                    System.out.println("Тип ошибки №" + tok[0] + " - Fatal Error.");
                    System.out.println("Пакет с несовпавшем ID: " + strObjectID + " != " + strObjectIDAfterAND);
                    System.out.println(stringParsing.replace("\r", "") + '\n');

                    // CAN-пакет до элемента "И".
                    System.out.println("Пакет полученный от клиента");
                    ArrayList<Integer> listOfBits = ErrorHandler.bitIndex(CANPacket, testCANPacket);
                    for (int i = 0; i < listOfBits.size(); i++) {
                        int bitIndex = listOfBits.get(i);
                        System.out.println(h.bh.getNameByCanBit(objectID, bitIndex) + '\n');
                    }

                    // CAN-пакет после элемента "И".
                    System.out.println("Пакет после прогона через \"И\"");
                    ArrayList<Integer> listOfBitsAfterAND = ErrorHandler.bitIndex(CANPacketAfterAND, testCANPacket);
                    for (int i = 0; i < listOfBitsAfterAND.size(); i++) {
                        int bitIndex = listOfBitsAfterAND.get(i);
                        System.out.println(h.bh.getNameByCanBit(objectIDAfterAND, bitIndex) + '\n');
                    }
                }
            }
        }
    }
}
