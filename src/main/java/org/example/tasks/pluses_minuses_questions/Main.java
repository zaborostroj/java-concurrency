package org.example.tasks.pluses_minuses_questions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        /*
        Пример ввода и вывода числа n, где -10^9 < n < 10^9:
        int n = Integer.parseInt(reader.readLine());
        writer.write(String.valueOf(n));
        */

        String[] parts = reader.readLine().trim().split("\\s");
        int rowsNumber = Integer.parseInt(parts[0]);
        int columnsNumber = Integer.parseInt(parts[1]);

        int[] rowSums = new int[rowsNumber];
        int[] columnSums = new int[columnsNumber];

        String[][] table = new String[rowsNumber][columnsNumber];
        for (int i = 0; i < rowsNumber; i++) {
            parts = reader.readLine().trim().split("");
            for (int j = 0; j < columnsNumber; j++) {
                String symbol = parts[j];
                table[i][j] = symbol;

                switch (symbol) {
                    case "+":
                        rowSums[i] = rowSums[i] + 1;
                        columnSums[j] = columnSums[j] + 1;
                        break;
                    case "-":
                        rowSums[i] = rowSums[i] - 1;
                        columnSums[j] = columnSums[j] - 1;
                        break;
                    case "?":
                        rowSums[i] = rowSums[i] + 1;
                        columnSums[j] = columnSums[j] - 1;
                        break;
                    default:
                        break;
                }
            }
        }

        int result = Integer.MIN_VALUE;
        for (int i = 0; i < rowsNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                int subResult = rowSums[i] - columnSums[j];
                if (table[i][j].equals("?")) {
                    subResult -= 2;
                }
                if (result < subResult) {
                    result = subResult;
                }
            }
        }

        writer.write(String.valueOf(result));
        writer.flush();
        reader.close();
        writer.close();
    }
}
