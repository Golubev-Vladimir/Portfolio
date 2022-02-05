package com.golubev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InputFileReader {

    private final FileReader fr; // объявили переменную класса FileReader. Почему именно в этом разделе? private final ??
    private final BufferedReader reader; // объявили переменную класса BufferedReader
    private boolean isAlive; // объявили переменную ?? чтобы убивать отработавшие обьекты
    private String currentLine; // объявили переменную класса String для сохранения прочтенной строки ??

    public InputFileReader(String filePath) throws IOException { // ОБРАБОТКА ВХОДНОГО ФАЙЛА
        File inputFile = new File(filePath); // создаем новый объект класса File (на базе указанного пути к файлу пользователя)
        this.fr = new FileReader(inputFile); // создаем новый объект класса FileReader (будет считывать вышеуказанный файл)
        this.reader = new BufferedReader(fr); // создаем новый объект класса BufferedReader (сохраняем в него прочтенные данные файла)
        this.isAlive = true; //??
        this.currentLine = reader.readLine(); // в переменную записываем 1-ую строку файла
    }

    public void readNextLine() { //читаем следующую линию
        try {
            currentLine = reader.readLine();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void closeReader() { //??
        try {
            isAlive = false;
            reader.close(); //закрытие ресурсов
            fr.close();     //закрытие ресурсов
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentLine() { //??
        return currentLine;
    }

    public boolean isAlive() { //??
        return isAlive;
    }
}