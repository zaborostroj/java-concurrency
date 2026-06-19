package org.example.tasks.matches;

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
        1 -> 1 => 1
        2 -> 2 => 1
        3 -> 3 => 1
        4 -> 3 => 2

        5 -> 1 => 1
        6 -> 2 => 1
        7 -> 1- 3 => 1
        8 -> 1- 3- 2 => 2
        9 -> 2- 3- 1 => 1
        */

        /*
        Пример ввода и вывода числа n, где -10^9 < n < 10^9:
        int n = Integer.parseInt(reader.readLine());
        writer.write(String.valueOf(n));
        */
        int matchesNumber = Integer.parseInt(reader.readLine());
        Integer[] bestPositions = new Integer[matchesNumber + 1];

        for (int i = 1; i <= matchesNumber; i++) {
            bestPositions[i] = 2;

            for (int j = 1; j <=3; j++) {
                int diff = i - j;

                if (
                        diff <= 0 ||
                                !isPrime(diff) && bestPositions[diff] == 2
                ) {
                    bestPositions[i] = 1;
                    break;
                }
            }
        }

        writer.write(String.valueOf(bestPositions[matchesNumber]));
        writer.flush();

        reader.close();
        writer.close();
    }

    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
