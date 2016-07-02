package modularsystemerrorhandlingssw;

import modularsystemerrorhandlingssw.errors.NoConnectionException;
import modularsystemerrorhandlingssw.errors.NoDataException;
import modularsystemerrorhandlingssw.parsers.TextBitHelpParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Заразка А.В. /КС-МИСАТ
 */
public class ModularSystemErrorHandlingSSW {

    public static void main(String[] args) throws Exception {

        // Файл настроек
        Properties props = new Properties();
        // Читает список свойств (ключ и пары элементов) из потока байт ввода
        props.load(new FileInputStream("config.properties"));

        BitHelp bh = new BitHelp();
        TextBitHelpParser textParser = new TextBitHelpParser();

        // Таблица объектов горловины станции (четная или нечетная)
        String fileName = (String) props.get("bitHelp");
        textParser.parse(fileName, bh);


        ErrorHandler h = new ErrorHandler();
        h.bh = bh;


        // IP-адрес с которого получаем данные работы SSW
        String ipAddrSSW = (String) props.get("IPAddressSSW");

        // Файл для базы данных
        String newFile = (String) props.getProperty("newFileName");

        // Обновление страницы через заданный промежуток времени
        String sleep = (String) props.get("sleep");
        int periodRefreshWebpage = Integer.parseInt(sleep, 10);

        information(ipAddrSSW, sleep, fileName);

        try (WriteFile wf = new WriteFile(newFile)) {

            for (;;) {
                try {
                    InputStream is = imitationClickingButtonQuickLog(ipAddrSSW);
                    if (is == null) {
//                        throw new NoConnectionException("Данная веб-страница недоступна " + ipAddrSSW + ".\nВеб-сайт, возможно, временно недоступен или у вас возникли проблемы с интернет-соединением.");
                         throw new NoConnectionException();
                    }

                    StringBuilder sb = readFromStream(is);
                    String[] line = getLine(sb);

                    // Формируем файл для базы данных
//                    wf.writeFile(newFile, line);

                    // Парсинг ошибок.
                    h.line = line;
                    h.errorHandler(line);

                    // Очищаем Web-страницу
                    imitationClickButtonClearFastStatistics(ipAddrSSW);
                    try {
                        Thread.sleep(periodRefreshWebpage);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }

                } catch (NoConnectionException ex) {
//                    System.out.println(ex);
                    ex.printStackTrace();
                } catch (NoDataException ex) {
                }
            }
        }
    }

    /**
     * Метод отправляет HTTP-запрос используя параметр "ipAddressSSW", имитирует
     * нажатия кнопки (Быстрый лог).
     *
     * @param ipAddressSSW IP-адрес SSW.
     * @return bytesReadInputStream - Байты считаны из входного потока.
     * @throws IOException Данная веб-страница не доступна, или вы можете
     * испытывать проблемы с подключением к Интернету.
     */
    protected static InputStream imitationClickingButtonQuickLog(String ipAddressSSW) throws IOException {
        try {
            URLConnection connection = new URL("http://" + ipAddressSSW + "/errorlog.ssi").openConnection();
            InputStream bytesReadInputStream = connection.getInputStream();
            return bytesReadInputStream;
        } catch (IOException ex) {
        }
        return null;
    }

    /**
     *
     * @param stream
     * @return sb
     * @throws IOException Чтения входного потока невозможно.
     * @throws NullPointerException Данных для обработки не найдено.
     */
    protected static StringBuilder readFromStream(InputStream stream) throws IOException, NullPointerException {
        InputStream is = stream;
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(is)) {
            char[] buffer = new char[256];
            int rc;
            while ((rc = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, rc);
            }
        } catch (IOException | NullPointerException ex) {
        }
        return sb;
    }

    /**
     *
     * @param sb
     * @return lines -
     * @throws NoDataException
     */
    protected static String[] getLine(StringBuilder sb) throws NoDataException {
        int lastIndex = sb.lastIndexOf("</PRE>");
        String datas = sb.substring(lastIndex + 6);
        lastIndex = datas.lastIndexOf("<br>");
        if (lastIndex == -1) {
            throw new NoDataException();
        }
        datas = datas.substring(0, lastIndex);
        String lines[] = datas.split("<br>");
        return lines;
    }

    /**
     * Метод отправляет HTTP-запрос используя параметр "ipAddressSSW", имитирует
     * нажатия кнопки (Очистить быструю статистику).
     *
     * @param IPAddressSSW IP-адрес SSW.
     * @throws IOException Данная веб-страница не доступна, или вы можете
     * испытывать проблемы с подключением к Интернету.
     */
    protected static void imitationClickButtonClearFastStatistics(String IPAddressSSW) throws IOException {
        try {
            URLConnection requestToClearTheWebpage = new URL("http://" + IPAddressSSW + "/clear_fast_stat.cgi?submit=%CE%F7%E8%F1%F2%E8%F2%FC+%E1%FB%F1%F2%F0%F3%FE+%F1%F2%E0%F2%E8%F1%F2%E8%EA%F3").openConnection();
            InputStream is = requestToClearTheWebpage.getInputStream();
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }
    }

    /**
     * Выводит информацию файла (config.properties) и время запуска программы.
     *
     * @param IPAddressSSW IP-адрес SSW.
     * @param sleep Обновление страницы через заданный промежуток времени.
     * @param fileName Таблица объектов горловины станции (четная или нечетная).
     */
    private static void information(String IPAddressSSW, String sleep, String fileName) {
        Date currentDate = new Date();

        DateFormat displayFormat = new SimpleDateFormat("HH:mm:ss\n                dd.MM.yyyy ");
        String dateFormat = displayFormat.format(currentDate);

        System.out.println("program:        LogAnalysisSSW");
        System.out.println("start-up time:  " + dateFormat);
        System.out.println("IP address SSW: " + IPAddressSSW);
        System.out.println("Table objects:  " + fileName);
        System.out.println("Update information every:  " + sleep + " ms" + '\n');
    }
}
