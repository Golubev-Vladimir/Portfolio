## A program for sorting by merging multiple files.
#### This was done as part of self-study

Input files contain data of one of two types: integers or strings. The data is written in a column (each line of the file is a new element). Strings can contain any non-whitespace characters, strings with spaces are considered erroneous. It is also assumed that the files are pre-sorted.

The result of the program should be a new file with the combined contents of the input files, sorted in ascending or descending order by merge sorting.
If the contents of the source files do not allow for merge sorting (for example, the sorting order is broken), partial sorting is performed (as far as possible for this algorithm, how exactly to process the corrupted file is at the discretion of the developer). The output file should contain sorted data even in case of errors, however, erroneous data may be lost.

**Program parameters are set at startup via command line arguments, in order:**
1. sorting mode (-a or -d), optional, sorted in ascending order by default;
2. data type (-s or -i), required;
3. the name of the output file, required;
4. the other parameters are the names of the input files, at least one.
 
**Examples of starting from the command line for Windows:**
1. sort-it.exe -i -a out.txt in.txt (for integers in ascending order);
2. sort-it.exe -s out.txt in1.txt in2.txt in3.txt (for ascending rows);
3. sort-it.exe -d -s out.txt in1.txt in2.txt (for descending rows)

**Приложение написано на Java. Пример кода:**
```java
public class Practicum {
    public static void main(String[] args) {
    }
}
```
------
