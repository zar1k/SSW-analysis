package modularsystemerrorhandlingssw;

import modularsystemerrorhandlingssw.parsers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс ErrorHandler определяет тип ошибки, и вызывает соответствующей ей
 * парсер.
 * @author Заразка А.В. /КС-МИСАТ
 */
public class ErrorHandler {

    public String[] line;
    public BitHelp bh;
    Map<String, IParser> parserError = new HashMap<>();

    // Конструктор содержит ключ-значение.
    // Добавляем парсеры (ParserErrorХХ) в карту (parserError) для обработки ошибок. 
    public ErrorHandler() {
        parserError.put("1", new ParserError1());
        parserError.put("2", new ParserError2());
        parserError.put("3", new ParserError3());
        parserError.put("4", new ParserError4());
        parserError.put("5", new ParserError5());
    }

    /*
     * Выбор парсера для обработки ошибок.
     */
    public void errorHandler(String[] line) {
        for (String stringParsing : line) {
            String tok[] = stringParsing.split("\\s\\|\\s");

            IParser p = parserError.get(tok[0]);
            if (p != null) {
                p.errorHandler(this);
            } else {
                System.out.println("Нет парсера для данного типа ошибок.");
            }
        }
    }

    /**
     * Работа метода заключается в сравнении побитовой операцией XOR двух
     * CAN-пакетов, на основании чего определяются байты в которых не совпадают
     * биты. В этих байтах, побитовым перебором определяется позиция
     * несовпадающего бита (формируется список).
     *
     * @param can CAN пакет до безопасного "И"/ или до програмного "И".
     * @param canAfterAND CAN пакет после безопасного "И"/ или после програмного
     * "И".
     * @return ArrayList - список битов (allBits) которые не совпали.
     */
    public static ArrayList<Integer> bitIndex(String can, String canAfterAND) {
        // Хранит список полученных битов
        ArrayList<Integer> allBits = new ArrayList();
        // Преобразует строки (can, canAfterAND) в массив символов (charArray, charArrayAND)
        // can - пакет до безопасного "И"
        // canAfterAND - пакет после безопасного "И"
        char[] charArray = can.toCharArray();
        char[] charArrayAND = canAfterAND.toCharArray();

        byte[] arr = new byte[8];
        byte[] arrAND = new byte[8];

        // Заполняем массивы (arr, arrAND)
        for (int i = 0; i < 8; i++) {
            arr[i] = (byte) ((Character.digit(charArray[2 * i], 16) << 4) | (Character.digit(charArray[2 * i + 1], 16)));

            arrAND[i] = (byte) ((Character.digit(charArrayAND[2 * i], 16) << 4) | (Character.digit(charArrayAND[2 * i + 1], 16)));

            // Сравнение массивов arr[i] и arrAND[i] побитовой операцией XOR, 
            // определяет не совпавшые байты (notMatchedByte)
            if (arr[i] != arrAND[i]) {
                int notMatchedByte = (byte) arr[i] ^ arrAND[i];

                // Байты которые не совпали (notMatchedByte) побитово перебераются,
                // определяя позицию бита (bit)
                int mask = 0x01;
                for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                    if ((notMatchedByte & mask) != 0) {
                        int bitNumber = 8 * i + bitIndex;
                        allBits.add(bitNumber);
                    }
                    mask = mask << 1;
                }
            }
        }
        return allBits;
    }
}
