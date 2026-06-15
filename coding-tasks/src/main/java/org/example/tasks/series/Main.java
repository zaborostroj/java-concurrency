package org.example.tasks.series;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
    static class Pair {
        Pair(int e, int c) {
            this.episodesNumber = e;
            this.coefficient = c;
        }
        int episodesNumber;
        int coefficient;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        /*
        Пример ввода и вывода числа n, где -10^9 < n < 10^9:
        int n = Integer.parseInt(reader.readLine());
        writer.write(String.valueOf(n));
        */

        int seasonsNumber = Integer.parseInt(reader.readLine());

        String[] parts = (reader.readLine()).trim().split("\\s");
        Integer[] episodesNumber = new Integer[seasonsNumber];
        for (int i = 0; i < seasonsNumber; i++) {
            episodesNumber[i] = Integer.parseInt(parts[i]);
        }

        parts = (reader.readLine()).trim().split("\\s");
        Integer[] coefficients = new Integer[seasonsNumber];
        for (int i = 0; i < seasonsNumber; i++) {
            coefficients[i] = Integer.parseInt(parts[i]);
        }

        Pair[] pairs = new Pair[seasonsNumber];
        long totalCoeff = 0;
        for (int i = 0; i < seasonsNumber; i++) {
            pairs[i] = new Pair(episodesNumber[i], coefficients[i]);
            totalCoeff += coefficients[i];
        }
        Arrays.sort(pairs, Comparator.comparingInt(p -> p.episodesNumber));

        long sumCoeff = 0;
        int resultEpisodes = 0;
        for (Pair pair : pairs) {
            sumCoeff += pair.coefficient;
            if (sumCoeff * 2 >= totalCoeff) {
                resultEpisodes = pair.episodesNumber;
                break;
            }
        }

        long resultPrice = 0;
        for (Pair pair : pairs) {
            resultPrice += (long) pair.coefficient * Math.abs(pair.episodesNumber - resultEpisodes);
        }

        writer.write(resultEpisodes + " " + resultPrice);

        reader.close();
        writer.close();
    }
}
