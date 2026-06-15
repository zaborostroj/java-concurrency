package org.example.tasks.cutted_string;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        String[] parts = reader.readLine().trim().split("\\s");
        int stringLength = Integer.parseInt(parts[0]);
        int partsNumber = Integer.parseInt(parts[1]);

        String sourceString = reader.readLine().trim();
        /*
            cabacaqwertyerty
            erty
            caba
            caqw

            caba [0]
            caqw [1]
            erty [2, 3]

            [-1, -1, -1, -1]
            [-1, -1, 1, -1]
            [2, -1, 1, -1]
            [2, 3, 1, -1]
            [2, 3, 1, 4]
        */

        Map<String, List<Integer>> subStrings = new HashMap<>();
        int partLength = stringLength / partsNumber;
        for (int i = 0; i < partsNumber; i ++) {
            String key = sourceString.substring(i * partLength, (i + 1) * partLength);

            if (!subStrings.containsKey(key)) {
                List<Integer> value = new ArrayList<>();
                value.add(i);
                subStrings.put(key, value);
            } else {
                subStrings.get(key).add(i);
            }
        }

        int[] result = new int[partsNumber];
        for (int i = 0; i < partsNumber; i++) {
            String input = reader.readLine().trim();

            List<Integer> positions = subStrings.get(input);
            int currentInputPosition = positions.removeFirst();

            result[currentInputPosition] = i + 1;
        }

        for (int position : result) {
            writer.write(position + " ");
        }

        reader.close();
        writer.close();
    }
}
