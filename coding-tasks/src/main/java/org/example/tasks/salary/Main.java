package org.example.tasks.salary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        String[] parts;

        parts = (reader.readLine()).trim().split("\\s+");
        int workersNumber = Integer.parseInt(parts[0]);

        int[] holidays = new int[workersNumber];

        parts = (reader.readLine()).trim().split("\\s+");
        for (int i = 0; i < workersNumber; i++) {
            holidays[i] = Integer.parseInt(parts[i]);
        }
        reader.close();

        // 4
        // 4 2 2 4
        // 0 -> 4 ==> {3->1; 2->1; 1->1}
        // 1 -> 3 ==> {3->1; 2->2; 1->1}
        // 2 -> 4 ==> {3->2; 2->2; 1->1}
        //
        // Каждый работник i может влиять на отрезок из нескольких последующих.
        // Левая граница отрезка l = i + 1. Правая граница отрезка r = i + ai -1.
        // В следующем проходе можно подсчитать, сколько сотрудников слева влияет на премию текущего работника: active += activeInfluencers[i]
        //                     0  1  2  3
        //            i[0]=4:    [      ]
        //            i[1]=2:       []
        //            i[2]=2:          []
        //            i[3]=4:
        // activeInfluencers:  0  1  2  2

        // activeInfluencers сделан на 1 больше количества работников для удобства. Если какой-то работник влияет на самого последнего - то уменьшим счётчик для "после последнего", оно не будет ни на что влиять и уменьшит проверки в коде
        int[] activeInfluencers = new int[workersNumber + 1];
        Arrays.fill(activeInfluencers, 0);

        for (int i = 0; i < workersNumber - 1; i++) {
            int left = i + 1;
            int right = Math.min(workersNumber - 1, i + holidays[i] - 1);

            if (left > right) {
                continue; // данный сотрудник ни на кого не влияет
            }

            activeInfluencers[left]++;
            activeInfluencers[right + 1]--;
        }

        long result = 0;
        int active = 0;
        for (int i = 0; i < workersNumber; i++) {
            active += activeInfluencers[i];
            result += (long) active * holidays[i];
        }

        writer.write(String.valueOf(result));
        writer.flush();
        writer.close();
    }
}
