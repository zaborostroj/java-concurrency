package org.example.tasks.mothers_erands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        float a = Float.parseFloat(parts[0]);
        float b = Float.parseFloat(parts[1]);
        float c = Float.parseFloat(parts[2]);
        float v0 = Float.parseFloat(parts[3]);
        float v1 = Float.parseFloat(parts[4]);
        float v2 = Float.parseFloat(parts[5]);

        /*
        a/v0, a/v1, b/v0, b/v1
        a/v0, c/v1, b/v2
        b/v0, c/v1, a/v2
        a/v0, c/v0, c/v1, a/v2
        b/v0, c/v0, c/v1, b/v2
        a/v0, c/v0, c/v1, a/v1, a/v0, a/v1
        b/v0, c/v0, c/v1, b/v1, b/v0, b/v1
        */

        float t1 = a / v0 + a / v1 + b / v0 + b / v1;
        float t2 = a / v0 + c / v1 + b / v2;
        float t3 = b / v0 + c / v1 + a / v2;
        float t4 = a / v0 + c / v0 + c / v1 + a / v2;
        float t5 = b / v0 + c / v0 + c / v1 + b / v2;
        float t6 = a / v0 + c / v0 + c / v1 + a / v1 + a / v0 + a / v1;
        float t7 = b / v0 + c / v0 + c / v1 + b / v1 + b / v0 + b / v1;

        List<Float> times = Arrays.asList(t1, t2, t3, t4, t5, t6, t7);
        float result = Collections.min(times);
        writer.write(String.valueOf(result));
        writer.flush();

        reader.close();
        writer.close();
    }
}
