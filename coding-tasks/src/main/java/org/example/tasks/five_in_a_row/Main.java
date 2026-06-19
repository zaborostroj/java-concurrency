package org.example.tasks.five_in_a_row;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Main {
    static final String DOT = ".";
    static final int GOAL = 4; // количество следующих одинаковых ячеек для победной фигуры

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

        String[][] table = new String[rowsNumber][columnsNumber];
        for (int i = 0; i < rowsNumber; i++) {
            parts = reader.readLine().trim().split("");
            for (int j = 0; j < columnsNumber; j++) {
                table[i][j] = parts[j];
            }
        }

        boolean result = false;
        for (int i = 0; i < rowsNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                String symbol = table[i][j];

                if (symbol.equals(DOT)) {
                    continue;
                }

                result = checkRight(table, i, j, symbol)
                        || checkDown(table, i, j, symbol)
                        || checkDownRight(table, i, j, symbol)
                        || checkDownLeft(table, i, j, symbol);

                if (result) {
                    break;
                }
            }

            if (result) {
                break;
            }
        }

        writer.write(result ? "Yes" : "No");
        writer.flush();
        reader.close();
        writer.close();
    }

    static boolean checkRight(
            String[][] table,
            int i,
            int j,
            String symbol
    ) {
        if (j >= table[0].length - GOAL) {
            return false;
        }

        for (int k = 1; k <= GOAL; k++) {
            if (!table[i][j + k].equals(symbol)) {
                return false;
            }
        }

        return true;
    }

    static boolean checkDown(
            String[][] table,
            int i,
            int j,
            String symbol
    ) {
        if (i >= table.length - GOAL) {
            return false;
        }

        for (int k = 1; k <= GOAL; k++) {
            if (!table[i + k][j].equals(symbol)) {
                return false;
            }
        }

        return true;
    }

    static boolean checkDownRight(
            String[][] table,
            int i,
            int j,
            String symbol
    ) {
        if (j >= table[0].length - GOAL) {
            return false;
        }
        if (i >= table.length - GOAL) {
            return false;
        }

        for (int k = 1; k <= GOAL; k++) {
            if (!table[i + k][j + k].equals(symbol)) {
                return false;
            }
        }

        return true;
    }

    static boolean checkDownLeft(
            String[][] table,
            int i,
            int j,
            String symbol
    ) {
        if (j < GOAL) {
            return false;
        }
        if (i >= table.length - GOAL) {
            return false;
        }

        for (int k = 1; k <= GOAL; k++) {
            if (!table[i + k][j - k].equals(symbol)) {
                return false;
            }
        }

        return true;
    }
}
