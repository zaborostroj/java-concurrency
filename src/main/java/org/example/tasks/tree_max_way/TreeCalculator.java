package org.example.tasks.tree_max_way;

import java.util.ArrayList;
import java.util.List;

public class TreeCalculator {
    public static Node readTree(String input) {
        String[] parts = input.trim().split(",");
        Integer[] values = new Integer[parts.length];

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("null")) {
                values[i] = null;
            } else {
                values[i] = Integer.parseInt(parts[i]);
            }
        }

        Node root = new Node(null, null, values[0]);
        List<Node> parentNodes = List.of(root);
        int iterator = 1;
        for (; ; ) {
            List<Node> childNodes = new ArrayList<>(parentNodes.size() * 2);
            for (Node parent : parentNodes) {
                if (iterator < values.length && values[iterator] != null) {
                    Node left = new Node(null, null, values[iterator]);
                    parent.setLeft(left);
                    childNodes.add(left);
                }
                if (iterator + 1 < values.length && values[iterator + 1] != null) {
                    Node right = new Node(null, null, values[iterator + 1]);
                    parent.setRight(right);
                    childNodes.add(right);
                }

                iterator += 2;
                if (iterator >= values.length) {
                    break;
                }
            }

            if (childNodes.isEmpty()) {
                break;
            }

            parentNodes = childNodes;
        }

        return root;
    }

    public static int calsulateMaxTreePath(Node root) {
        var result = 1;
        List<Node> parentNodes = List.of(root);
        for (;;) {
            List<Node> childrenNodes = new ArrayList<>(parentNodes.size() * 2);
            for (Node node : parentNodes) {
                if (node.getLeft() != null) {
                    childrenNodes.add(node.getLeft());
                }
                if (node.getRight() != null) {
                    childrenNodes.add(node.getRight());
                }

                var subResult = calculateMaxPathFromNode(node);
                if (subResult > result) {
                    result = subResult;
                }
            }

            if (childrenNodes.isEmpty()) {
                break;
            }

            parentNodes = childrenNodes;
        }

        return result;
    }

    private static int calculateMaxPathFromNode(Node root) {
        List<NodeStats> parentNodes = List.of(new NodeStats(root, 1));


        for (;;) {
            List<NodeStats> childNodes = new ArrayList<>(parentNodes.size() * 2);

            for (NodeStats parent : parentNodes) {
                Node left = parent.node().getLeft();
                if (left != null && parent.node().getValue() <= left.getValue()) {
                    childNodes.add(new NodeStats(
                            left,
                            parent.pathLength() + 1
                    ));
                }

                Node right = parent.node().getRight();
                if (right != null && parent.node().getValue() <= right.getValue()) {
                    childNodes.add(new NodeStats(
                            right,
                            parent.pathLength() + 1
                    ));
                }
            }

            if (childNodes.isEmpty()) {
                return parentNodes.stream()
                        .map(NodeStats::pathLength)
                        .max((Integer::compare))
                        .orElse(1);
            }

            parentNodes = childNodes;
        }
    }
}
