package org.example.tasks.hiking;

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

        /*
                    0  1 2 3 4 5 6 7 8  9
                    s L L L L     L   L
                    ----------------------
                            R   R R R R   f

          dp[0][i]  0  1 2 3 3 3 3 4 4  5
          dp[1][i]  1  1 1 2 2 3 4 5 5  5
        */
        char[] input = reader.readLine().trim().toCharArray();

        int movesToLeftShore = 0;
        int movesToRightShore = 1;

        for (int i = 0; i < input.length; i++) {
            int tmpL = movesToLeftShore;
            int tmpR = movesToRightShore;

            if (input[i] == 'L') {
                movesToLeftShore = Math.min(tmpL + 1, tmpR + 1);
                movesToRightShore = Math.min(tmpL + 2, tmpR);
            } else if (input[i] == 'R') {
                movesToLeftShore = Math.min(tmpL, tmpR + 2);
                movesToRightShore = Math.min(tmpL + 1, tmpR + 1);
            } else {
                movesToLeftShore = Math.min(tmpL + 1, tmpR + 2);
                movesToRightShore = Math.min(tmpL + 2, tmpR + 1);
            }
        }

        writer.write(String.valueOf(movesToRightShore));
        reader.close();
        writer.close();
    }
}
