package org.example.tasks.increments;

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
        int n = Integer.parseInt(parts[0]);
        int k = Integer.parseInt(parts[1]);

        int cycle = 4;
        int lastDigit = n % 10;

        long result;

        if (k == 0) {
            result =  n;
        } else if (n % 10 == 5) {
            result = n + 5;
        } else if (k == 1) {
            result = n + lastDigit;
        } else {
            int fullCycles = k / cycle;
            result = n + lastDigit + fullCycles * 20L;

            for (int i = 0; i < (k % cycle) - 1; i++) {
                result += result % 10;
            }
        }

        writer.write(String.valueOf(result));
        writer.flush();
        reader.close();
        writer.close();
    }
}
