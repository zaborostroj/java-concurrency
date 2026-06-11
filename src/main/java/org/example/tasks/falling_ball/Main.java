package org.example.tasks.falling_ball;

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
        1 -> 1,0; -> 1
        2 -> 2,0; 2,1,0; -> 2
        3 -> 3,0; 3,1,0; 3,2,0; 3,2,1,0; -> 4

        4 -> 4,3[3]; 4,2[2]; 4,1[1] -> 4 + 2 + 1 = 7
        5 -> 5,4[4]; 5,3[3]; 5,2[2] -> 7 + 4 + 2 = 13
        */
        /*
        Пример ввода и вывода числа n, где -10^9 < n < 10^9:
        int n = Integer.parseInt(reader.readLine());
        writer.write(String.valueOf(n));
        */

        int stepsNumber = Integer.parseInt(reader.readLine());
        int result = 0;
        Integer[] variantsNumber = new Integer[stepsNumber + 1];

        if (stepsNumber == 1) {
            result = 1;
        }
        if (stepsNumber == 2) {
            result = 2;
        }
        if (stepsNumber == 3) {
            result = 4;
        }

        if (result == 0) {
            variantsNumber[1] = 1;
            variantsNumber[2] = 2;
            variantsNumber[3] = 4;
            for (int i = 4; i <= stepsNumber; i++) {
                variantsNumber[i] = variantsNumber[i - 1] + variantsNumber[i - 2] + variantsNumber[i - 3];
            }
            result = variantsNumber[stepsNumber];
        }

        writer.write(String.valueOf(result));
        writer.flush();

        reader.close();
        writer.close();
    }
}
