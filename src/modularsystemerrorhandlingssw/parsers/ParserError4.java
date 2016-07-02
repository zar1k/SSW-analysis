package modularsystemerrorhandlingssw.parsers;

import modularsystemerrorhandlingssw.ErrorHandler;

/**
 * Класс ParserError4 Обрабатывает тип ошибки №4 (пакет с ID не из диапазона
 * допустимых ID).
 *
 * @author Заразка А.В. /КС-МИСАТ
 */
public class ParserError4 implements IParser {

    @Override
    public void errorHandler(ErrorHandler h) {
        for (String stringParsing : h.line) {
            String tok[] = stringParsing.split("\\s\\|\\s");

//*****************************************************************************************************************************************************            
            if (tok[0].equals("4")) {
                
                System.out.println("*********************************************************************************************************************");
                System.out.println("Тип ошибки №" + tok[0] + " - пакет с ID не из диапазона допустимых ID");
                System.out.println(stringParsing.replace("\r", "") + '\n');
            }
        }
    }
}
