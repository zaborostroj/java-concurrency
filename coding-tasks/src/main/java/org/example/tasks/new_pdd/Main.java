package org.example.tasks.new_pdd;

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
        long x = Long.parseLong(parts[0]);
        long y = Long.parseLong(parts[1]);

        parts = reader.readLine().trim().split("\\s");
        long f = Long.parseLong(parts[0]);
        long g = Long.parseLong(parts[1]);

        long dx = Math.max(Math.abs(x - f) - 1, 0);
        long dy = Math.max(Math.abs(y - g) - 1, 0);
        long turnLeft = (x == f || y == g) ? 0 : 1;

        long result = (dx + dy) * 3 + turnLeft;
        writer.write(String.valueOf(result));
        writer.flush();
        reader.close();
        writer.close();
    }
}
