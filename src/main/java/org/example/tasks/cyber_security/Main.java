package org.example.tasks.cyber_security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

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
        (3*2)/2 + 1 = 4
        abc
        bac
        acb
        cab

        (3*2)/2 - (2*1)/2 + 1 = 3
        aab
        aba
        baa

        (4*3)/2 - (2*1)/2 - (2*1)/2 + 1 = 5
        aabb
        baab
        baba
        abab
        abba
        */

        char[] input = reader.readLine().toCharArray();
        Map<Character, Long> repeats = new HashMap<>(26);
        for (Character c : input) {
            if (!repeats.containsKey(c)) {
                repeats.put(c, 1L);
            } else {
                repeats.put(c, repeats.get(c) + 1L);
            }
        }

        long duplicatesCount = 0;
        for (Long r : repeats.values()) {
            duplicatesCount += r * (r - 1L) / 2L;
        }

        long result = ((long) input.length * (input.length - 1) / 2) + 1L - duplicatesCount;
        writer.write(String.valueOf(result));
        writer.flush();

        reader.close();
        writer.close();
    }
}
