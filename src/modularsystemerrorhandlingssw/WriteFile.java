package modularsystemerrorhandlingssw;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Класс WriteFile создаёт новый файл, с данными для загрузки их в базу данных
 *
 * @author Заразка А.В. /КС-МИСАТ
 */
public class WriteFile implements AutoCloseable {

    PrintWriter out;
    long lastFlush = 0;

    // Конструкто - создаёт объект класса WriteFile
    public WriteFile(String newFileName) throws IOException {
        File file = new File(newFileName);
        // Если файл не существует, создаём его
        if (!file.exists()) {
            file.createNewFile();
        }
        out = new PrintWriter(file.getAbsoluteFile());
    }

    /**
     * Метод writeFile принимает на вход массив строк (String[] text), который
     * обрабатывает и записывает в файл (newFileName). Если newFileName
     * отсутствует, он создаётся автоматически.
     *
     * @param newFileName имя нового файла
     * @param text маиссив строк
     * @throws IOException
     */
    protected void writeFile(String newFileName, String[] text) throws IOException {

        for (String line : text) {
            out.print(line.replaceAll(" ", "").replaceAll("\\|", "\t").replace("\r", "") + "\n");
        }
        long now = System.currentTimeMillis();
        if (now - lastFlush > 60000) {
            out.flush();
            lastFlush = now;
        }
    }

    @Override
    public void close() throws Exception {
        out.close();
    }
}
