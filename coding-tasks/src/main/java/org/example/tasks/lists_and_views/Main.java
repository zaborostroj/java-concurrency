package org.example.tasks.lists_and_views;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static Map<String, List<Integer>> lists = new HashMap<>();
    static Map<String, ListReference> views = new HashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        /*
        Пример ввода и вывода числа n, где -10^9 < n < 10^9:
        int n = Integer.parseInt(reader.readLine());
        writer.write(String.valueOf(n));
        */

        String instruction = reader.readLine().trim();
        int instructionsNumber = Integer.parseInt(instruction);

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < instructionsNumber; i++) {
            instruction = reader.readLine().trim();
            Integer subResult = executeInstruction(instruction);
            if (subResult != null) {
                result.add(subResult);
            }
        }

        for (Integer it : result) {
            writer.write(String.valueOf(it));
            writer.newLine();
        }
        reader.close();
        writer.close();
    }

    static Integer executeInstruction(String instruction) {
        String[] instructionParts = instruction.split("\\s");
        if (instructionParts.length > 1) {
            createNewList(instructionParts);
        } else {
            String[] commandParts = instructionParts[0].split("\\.");
            if (commandParts[1].startsWith("get")) {
                return getElement(commandParts);
            } else if (commandParts[1].startsWith("set")) {
                setElement(commandParts);
            } else if (commandParts[1].startsWith("add")) {
                addElement(commandParts);
            }
        }

        return null;
    }

    // List x = new List(1,2,5,14,42)
    // List y = x.subList(1,4)
    static void createNewList(String[] instructionParts) {
        String listName = instructionParts[1];

        if (instructionParts.length == 5) {
            List<Integer> args = Arrays.stream(
                    instructionParts[4].substring(5, instructionParts[4].length() - 1).split(",")
            ).map(Integer::parseInt)
            .toList();

            lists.put(listName, new ArrayList<>(args));
        } else if (instructionParts.length == 4) {
            String[] tmp = instructionParts[3].split("\\.");

            String parentListName = tmp[0];
            List<Integer> args = Arrays.stream(
                            tmp[1].substring(8, tmp[1].length() - 1).split(",")
                    ).map(Integer::parseInt)
                    .toList();

            List<Integer> parentList;
            int startPosition;
            if (lists.containsKey(parentListName)) {
                parentList = lists.get(parentListName);
                startPosition = args.getFirst() - 1;
            } else {
                parentList = views.get(parentListName).getList();
                startPosition = args.getFirst() - 1 + views.get(parentListName).getStartPosition();
            }
            views.put(listName, new ListReference(parentList, startPosition));
        }
    }

    // b.get(1)
    static Integer getElement(String[] commandParts) {
        String listName = commandParts[0];
        int position = Integer.parseInt(
                commandParts[1].substring(4, commandParts[1].length() - 1)
        ) - 1;

        if (lists.containsKey(listName)) {
            return lists.get(listName).get(position);
        } else {
            int vewPosition = position + views.get(listName).getStartPosition();
            return views.get(listName).getList().get(vewPosition);
        }
    }

    // z.set(2,100)
    static void setElement(String[] commandParts) {
        String listName = commandParts[0];
        String[] commandArgs = commandParts[1].substring(4, commandParts[1].length() - 1).split(",");
        int position = Integer.parseInt(commandArgs[0]) - 1;
        int newValue = Integer.parseInt(commandArgs[1]);

        if (lists.containsKey(listName)) {
            lists.get(listName).set(position, newValue);
        } else {
            int viewPosition = position + views.get(listName).getStartPosition();
            views.get(listName).getList().set(viewPosition, newValue);
        }
    }

    // x.add(132)
    static void addElement(String[] commandParts) {
        String listName = commandParts[0];
        int newValue = Integer.parseInt(
                commandParts[1].substring(4, commandParts[1].length() - 1)
        );

        lists.get(listName).add(newValue);
    }

    static class ListReference {
        List<Integer> list;
        int startPosition;

        ListReference(List<Integer> list, int startPosition) {
            this.list = list;
            this.startPosition = startPosition;
        }

        public List<Integer> getList() {
            return list;
        }

        public int getStartPosition() {
            return startPosition;
        }
    }
}
