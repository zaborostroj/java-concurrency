package org.example.tasks.contest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        int tasksNumber = Integer.parseInt(parts[0]);
        int contestTasksNumber = Integer.parseInt(parts[1]);

        parts = reader.readLine().trim().split("\\s");
        Map<Integer, Integer> tasksByThemes = new HashMap<>(tasksNumber);
        for (int i = 0; i < tasksNumber; i++) {
            int theme = Integer.parseInt(parts[i]);
            if (!tasksByThemes.containsKey(theme)) {
                tasksByThemes.put(theme, 1);
            } else {
                tasksByThemes.put(theme, tasksByThemes.get(theme) + 1);
            }
        }


        List<Integer> result;
        Set<Integer> uniqueThemes = tasksByThemes.keySet();
        if (uniqueThemes.size() >= contestTasksNumber) {
            result = uniqueThemes.stream().limit(contestTasksNumber).toList();
        } else {
            result = new ArrayList<>(uniqueThemes);
            int remaining = contestTasksNumber - result.size();
            for (Map.Entry<Integer, Integer> it : tasksByThemes.entrySet()) {
                Integer theme = it.getKey();
                Integer number = it.getValue() - 1;
                if (number == 0) {
                    continue;
                }

                int add = number <= remaining ? number : remaining;
                for (int i = 0; i < add; i++) {
                    result.add(theme);
                }

                remaining -= add;
                if (remaining <= 0) {
                    break;
                }
            }
        }

        for (Integer it : result) {
            writer.write(it + " ");
        }
        writer.flush();

        reader.close();
        writer.close();
    }
}
