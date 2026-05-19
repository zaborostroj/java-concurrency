package org.example.tasks.tree_max_way;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreeCalculatorTest {
    @Test
    public void treeCalculatorShouldWork() {
        // 1
        Node root = TreeCalculator.readTree("1");
        var result = TreeCalculator.calsulateMaxTreePath(root);

        assertEquals(1, result);
    }

    @Test
    public void shouldCalculateIncreasingPathFromRootToLeaf() {
        //        1
        //      /   \
        //     2     3
        //    / \   / \
        //   4   5 6   7
        Node root = TreeCalculator.readTree("1,2,3,4,5,6,7");

        int result = TreeCalculator.calsulateMaxTreePath(root);

        assertEquals(3, result);
    }

    @Test
    public void shouldStartPathFromAnyNodeNotOnlyRoot() {
        //        10
        //      /    \
        //     5      6
        //    / \    / \
        //   1   2  7   8
        Node root = TreeCalculator.readTree("10,5,6,1,2,7,8");

        int result = TreeCalculator.calsulateMaxTreePath(root);

        assertEquals(2, result);
    }

    @Test
    public void shouldEndPathInMiddleNodeWhenFurtherChildIsSmaller() {
        //        5
        //      /   \
        //     6     1
        //    / \
        //   7   4
        Node root = TreeCalculator.readTree("5,6,1,7,4,null,null");

        int result = TreeCalculator.calsulateMaxTreePath(root);

        assertEquals(3, result);
    }

    @Test
    public void shouldReturnOneWhenAllChildrenAreSmallerThanParents() {
        //        9
        //      /   \
        //     7     8
        //    / \   / \
        //   5   6 3   4
        Node root = TreeCalculator.readTree("9,7,8,5,6,3,4");

        int result = TreeCalculator.calsulateMaxTreePath(root);

        assertEquals(1, result);
    }

    @Test
    public void shouldWorkWithSparseTreeContainingNulls() {
        //        3
        //      /   \
        //     4     2
        //      \
        //       5
        //        \
        //         6
        Node root = TreeCalculator.readTree("3,4,2,null,5,null,null,null,6");

        int result = TreeCalculator.calsulateMaxTreePath(root);

        assertEquals(4, result);
    }
}