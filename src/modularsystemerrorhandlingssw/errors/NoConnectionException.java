/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modularsystemerrorhandlingssw.errors;

import java.io.PrintStream;

/**
 *
 * @author Заразка А.В. /КС-МИСАТ
 */
public class NoConnectionException extends Exception {

//    public NoConnectionException(String ipAddrSSW) {
//        super(ipAddrSSW);
//    }

    @Override
    public void printStackTrace(PrintStream stream) {
        System.out.println("Веб-сайт временно недоступен, или у вас возникли проблемы с интернет-соединением.");
    }
    
}
