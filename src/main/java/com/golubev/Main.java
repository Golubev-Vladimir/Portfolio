package com.golubev;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    private static SortingOrder sortingOrder = SortingOrder.ASC;
    private static DataType dataType;

    public static void main(String[] args) {
        String outputFileName = null;
        ArrayList<String> inputFilesNames = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(SortingOrder.DESC.getCommand())) {
                sortingOrder = SortingOrder.DESC;
            } else if (args[i].equals(SortingOrder.ASC.getCommand())) {
                sortingOrder = SortingOrder.ASC;
            } else if (args[i].equals(DataType.NUM.getCommand())) {
                dataType = DataType.NUM;
            } else if (args[i].equals(DataType.STR.getCommand())) {
                dataType = DataType.STR;
            } else if (outputFileName == null) {
                outputFileName = args[i];
            } else {
                inputFilesNames.add(args[i]);
            }
        }

        if (dataType == null) {
            System.out.println("data type is not specified");
            System.exit(0);
        }
        if (outputFileName == null) {
            System.out.println("output and input files are not specified");
            System.exit(1);
        }
        if (inputFilesNames.size() == 0) {
            System.out.println("input files are not specified");
            System.exit(2);
        }

        int aliveReadersCounter = inputFilesNames.size();

        InputFileReader[] mixedReaders = new InputFileReader[aliveReadersCounter]; //создаем обьект класса (типа) InputFileReader - массив размером кол-ва файлов ?

        for (int i = 1; i <= aliveReadersCounter; i++) { ///определяем кол-во файлов, представляя их в массив
            try {
                mixedReaders[i - 1] = new InputFileReader("files/" + inputFilesNames.get(i-1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //создаем выходной фйал
        FileWriter outputFileWriter;
        BufferedWriter bufferWriter = null;
        try {
            outputFileWriter = new FileWriter(outputFileName, true); //дописывать к строке
            bufferWriter = new BufferedWriter(outputFileWriter);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while (aliveReadersCounter != 1) { //продолжаем цикл, пока в живых не останется один ридер
            // очищаем массив ридеров от тех, в которых курсов встал на невалидную строку или дошёл до конца файла
            closeReadersWithInvalidValues(mixedReaders);
            InputFileReader[] onlyAliveReaders = filterAliveReaders(mixedReaders);
            aliveReadersCounter = onlyAliveReaders.length; // актуализация кол-ва живых ридеров (массива ридеров)
            // сортируем массив ридеров, чтобы первым был ридер с очередным числом/строкой
            splitSortReadersByCurrentValues(onlyAliveReaders); // мержсорт
            // из первого ридера массива ридеров (он теперь упродочен) переписываем текущую строку в выходной файл
            writeRowToFile(bufferWriter, onlyAliveReaders[0].getCurrentLine());
            // считываем следующую строку в данном ридере (как бы сдвигаем курсов)
            onlyAliveReaders[0].readNextLine();
            // возможно, после перещёлкивания ридера он встал на невалидную строку и теперь мы не можем быть уверенными, что все ридеры живые
            // перекладываем ридеры в массив с названием mixed, чтобы в следующей итерации снова его проверить и почистить от неживых ридеров
            mixedReaders = new InputFileReader[aliveReadersCounter];
            for (int i = 0; i < aliveReadersCounter; i++) {
                mixedReaders[i] = onlyAliveReaders[i];
            }
        }

        String lastFileReaderRow = "";
        while (lastFileReaderRow != null) {
            mixedReaders[0].readNextLine();
            lastFileReaderRow = mixedReaders[0].getCurrentLine();
            closeReadersWithInvalidValues(mixedReaders);
            if (mixedReaders[0].isAlive()) {
                writeRowToFile(bufferWriter, lastFileReaderRow);
            }
        }

        try {
            if (bufferWriter != null) {
                bufferWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (sortingOrder == SortingOrder.DESC) {
            // TODO Реализовать перекладывание строк в результирующий файл в обратном порядке
        }
    }

    private static void writeRowToFile(BufferedWriter bufferWriter, String currentLine) {
        try {
            if (bufferWriter != null) {
                bufferWriter.write(currentLine + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void splitSortReadersByCurrentValues(InputFileReader[] activeReaders) {
        int n = activeReaders.length; //длина массива
        if (n == 1) {
            return; //останавливаем, когда дробить ничего не останется
        }
        int middle = n / 2; //дробим на два пока пустых подмассива
        InputFileReader[] l = new InputFileReader[middle]; // 1 подмассив
        InputFileReader[] r = new InputFileReader[n - middle]; //2 подмассив

        for (int i = 0; i < middle; i++) { //присваиваем значения 1 подмассиву
            l[i] = activeReaders[i];
        }
        for (int i = middle; i < n; i++) { //присваиваем значения 2 подмассиву
            r[i - middle] = activeReaders[i];
        }
        splitSortReadersByCurrentValues(l); //начинаем дробить подмассивы
        splitSortReadersByCurrentValues(r); //начинаем дробить подмассивы
        merge(activeReaders, l, r);
    }

    private static void merge(InputFileReader[] activeReaders, InputFileReader[] l, InputFileReader[] r) {
        int left = l.length;
        int right = r.length;
        int i = 0; //элемент первого массива
        int j = 0; //элемент второго массива
        int k = 0; //элемент итогового массива

        while (i < left && j < right) {
            if (isLeftMoreRight(l[i], r[j])) {
                activeReaders[k] = r[j];
                j++;
            } else {
                activeReaders[k] = l[i];
                i++;
            }
            k++;
        }

        for (int ll = i; ll < left; ll++) {
            activeReaders[k++] = l[ll];
        }
        for (int rr = j; rr < right; rr++) {
            activeReaders[k++] = r[rr];
        }
    }

    private static boolean isLeftMoreRight(InputFileReader l, InputFileReader r) {
        if (dataType == DataType.NUM) {
            int leftNumber = Integer.parseInt(l.getCurrentLine()); //преобразовываем прочтенную строку в число
            int rightNumber = Integer.parseInt(r.getCurrentLine()); //преобразовываем прочтенную строку в число
            if (leftNumber > rightNumber) {
                return true;
            } else {
                return false;
            }
        } else {
            String left = l.getCurrentLine(); //читаем строки
            String right = r.getCurrentLine(); //читаем строки
            if (left.equals(right)) { //если строки идентичны - то выстраиваем по порядку (от левого к правому)
                return true;
            }
            char[] leftString = left.toCharArray(); //преобразовываем в массив символов
            char[] rightString = right.toCharArray(); //преобразовываем в массив символов
            int minLength = Math.min(leftString.length, rightString.length);
            for (int i = 0; i < minLength; i++) {
                if (leftString[i] < rightString[i]) { //если первый символ одной строки меньше второго, значит строка начинается на меньший по Unicode символу
                    return false;
                } else if (leftString[i] > rightString[i]) { //если первый символ одной строки больше второго, значит строка начинается на больший по Unicode символу
                    return true;
                }
            }
            if (leftString.length < rightString.length) { /* если строки не равны, если все символы на про протяжении цикла (кол-во циклов - минимальное число символов из двух строк) равны,
                                                         значит наименьшая строка по символам будет та, что короче*/
                return false;
            } else {
                return true;
            }
        }
    }

    private static void closeReadersWithInvalidValues(InputFileReader[] activeReaders) {
        for (int i = 0; i < activeReaders.length; i++) {
            if (!checkRowIsValid(activeReaders[i].getCurrentLine())) {
                activeReaders[i].closeReader();
            }
        }
    }

    private static InputFileReader[] filterAliveReaders(InputFileReader[] mixedReaders) {
        int aliveReadersCount = 0;
        for (int i = 0; i < mixedReaders.length; i++) {
            if (mixedReaders[i].isAlive()) {
                aliveReadersCount++;
            }
        }
        InputFileReader[] aliveReaders = new InputFileReader[aliveReadersCount];
        int j = 0;
        for (int i = 0; i < mixedReaders.length; i++) {
            if (mixedReaders[i].isAlive()) {
                aliveReaders[j] = mixedReaders[i];
                j++;
            }
        }
        return aliveReaders;
    }

    private static boolean checkRowIsValid(String row) {
        if (dataType == DataType.NUM) {
            try {
                Integer.parseInt(row);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            if (row == null) {
                return false;
            } else {
                for (int i = 0; i < row.length(); i++) { //цикл по символам строки
                    String subChar = String.valueOf(row.charAt(i));
                    if (subChar.equalsIgnoreCase(" ")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
