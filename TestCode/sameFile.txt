package hw3;

import java.util.ArrayList;
import java.util.Scanner;

import api.Descriptor;
import api.Dot;
import api.Util;

/**
 * This class encapsulates the game logic for a video game called Dots.
 * The game consists of a 2D array or grid of colored icons, called dots,
 * along with an ordered list that we will call the selection list.  Intuitively,
 * the selection list represents a set of adjacent dots, all of the same color,
 * that have been selected by the player.  When selection is complete (e.g. the
 * mouse is released), the selected dots disappear from the grid, and the dots
 * above shift down  to take their places.  Then new dots fill in each column from the top.
 * A point is scored for each dot that disappears from the grid.  There
 * is a special rule for the case that the selected dots form a loop;
 * then all dots in the grid of the same color disappear too.
 */
public class DotsGame {
    private int r;
    private int c;
    private int score;
    private Dot dot;
    private Dot[][] dots;
    private Generator gen;
    private ArrayList<Descriptor> selectionList;
    private Descriptor descriptor;

    /**
     * Constructs a game with the given number of columns and rows that will use
     * the given Generator instance to create new icons.  The dots
     * in the initial grid are produced by the generator.
     *
     * @param width     number of columns
     * @param height    number of rows
     * @param generator generator for new icons
     */
    public DotsGame(int width, int height, Generator generator) {
        r = width;
        c = height;
        gen = generator;
        selectionList = new ArrayList<>();

        // Constructs a game with a given number of columns and rows.
        dots = new Dot[r][c];

        // The dots in the initial grid are produced by the generator.
        generator.initialize(dots);
    }

    /**
     * Constructs a game based on the given string array according to
     * the conventions of Util.createGridFromString. The given Generator
     * instance is used to create new dpts.
     *
     * @param data      string indicating initial configuration of grid
     * @param generator generator for new icons
     */
    public DotsGame(String[] data, Generator generator) {
        r = data.length;
        c = 0;

        Scanner scanner = new Scanner(data[0]);

        while(scanner.hasNext()) {
            scanner.next();
            c++;
        }

        dots = new Dot[r][c];
        dots = Util.createGridFromString(data);
    }

    /**
     * Returns the Dot object at the given row and column.
     *
     * @param row row within the grid
     * @param col column within the grid
     * @return Dot object at the given row and column
     */
    public Dot getDot(int row, int col) {
        return dots[row][col];
    }

    /**
     * Sets the Dot object at the given row and column.
     *
     * @param row row of the grid to be modified
     * @param col column of the grid to be modified
     * @param dot the given Dot object to set
     */
    public void setDot(int row, int col, Dot dot) {
        dots[row][col] = dot;
    }

    /**
     * Returns the number of columns in this game.
     *
     * @return number of columns
     */
    public int getWidth() {
        return c;
    }

    /**
     * Returns the number of rows in this game.
     *
     * @return number of rows
     */
    public int getHeight() {
        return r;
    }

    /**
     * Returns the current score for this game.
     *
     * @return score for this game
     */
    public int getScore() {
        return score;
    }

    /**
     * Attempts to select the dot at given position. A descriptor for the dot is
     * added to the selection list provided that a) the given position is
     * adjacent to the last one added to the selection list, and b) its type matches
     * the type of those already in the selection list, and c) the given position
     * is not already in the selection list OR it completes a loop.
     * Completing a loop means that the given position matches the first one in
     * the selection list, the list has length at least 3, and the given position does
     * not already occur twice in the list.
     *
     * @param row row of the dot to be selected
     * @param col column of the dot to be selected
     */
    public void select(int row, int col) {
        Descriptor _c = new Descriptor(row, col, getDot(row, col));

        // Checks if the selectionList is empty and add the currently select dot to the selection list.
        if (selectionList.isEmpty()) {
            selectionList.add(_c);
            return;
        }

        // Checks if the selectionList is a loop and checks if it is adjacent to the currently selected dot.
        else if (_c.equals(selectionList.get(0)) && selectionList.size() >= 4 && isAdjacent((selectionList.get(selectionList.size() - 1)), _c)) {
            // Checks to make sure that the values are not currently in the list.
            if (selectionList.contains(_c)) selectionList.add(_c);
        }

        // Checks if the selectionList doesn't make a loop.
        else if (!selectionList.contains(_c) && selectionList.get(0) != _c) {
            Descriptor _p = selectionList.get(selectionList.size() - 1);
            Dot _t = _p.getDot();

            // Make sure types match and that it is adjacent.
            if (dots[row][col].getType() == _t.getType()) {
                if (isAdjacent(_p, _c)) selectionList.add(_c);
            }
        }
    }

    /**
     * Returns a list of descriptors for currently selected dots.
     *
     * @return the selection list
     */
    public ArrayList<Descriptor> getSelectionList() {
        return selectionList;
    }

    /**
     * If the selection list has at least two elements, replaces all selected positions
     * with null, clears the selection list, and updates the score.  If the selection
     * list does not contain at least two elements, no positions are nulled but the
     * selection list is still cleared.  If the selection list includes a completed loop,
     * then all dots of matching type are also nulled and the score is updated accordingly.
     * The method returns a list containing all nulled positions.  (The list is in
     * no particular order but should not contain duplicates.)
     *
     * @return list of descriptors for cells that are nulled as a result of this operation
     */
    public ArrayList<Descriptor> release() {
        ArrayList<Descriptor> nulledList = new ArrayList<>();
        Dot type = selectionList.get(0).getDot();
        Descriptor d1;
        Descriptor d2;

        // Checks if the selection list is a loop
        if ((selectionList.get(0).equals(selectionList.size() - 1) && selectionList.size() >= 3)) {
            selectionList.clear();

            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    if (dots[i][j].equals(type)) {
                        d1 = new Descriptor(i, j, dots[i][j]);
                        selectionList.add(d1);
                    }
                }
            }
        }

        // If the selectionList has at least two elements.
        if (selectionList.size() > 1) {
            while (selectionList.size() != 0) {
                d2 = selectionList.get(0);
                dots[d2.row()][d2.col()] = null;
                nulledList.add(d2);
                selectionList.remove(0);
                score = score + 1;
            }
        }

        else {
            selectionList.remove(0);
        }

        return nulledList;
    }

    /**
     * Collapses the dots in the given column of the current game grid such
     * that all null dots, if any, are at the top of the column
     * and non-null dots are shifted toward the bottom (i.e., as if by gravity).
     * The returned list contains Descriptors representing dots that were moved (if any)
     * with their new row and column; moreover, each Descriptor's <code>getPreviousRow</code>
     * method returns the original row of the dot.  The returned list is
     * in no particular order.
     *
     * @param col column to be collapsed
     * @return list of descriptors for moved dots
     */
    public ArrayList<Descriptor> collapseColumn(int col) {
        // Preforms a simple array swap to drop the columns.
        ArrayList<Descriptor> collapsedList = new ArrayList<>();
        int c = r - 1;

        // Iterates from the bottom of the column to the top.
        for (int i = r - 1; i >= 0; i--) {
            if (dots[i][col] != null) {
                Descriptor d1 = new Descriptor(c, col, dots[i][col]);
                d1.setPreviousRow(i);
                Dot temp = dots[c][col];
                dots[c][col] = dots[i][col];
                dots[i][col] = temp;
                c--;
                d1.toString();
                collapsedList.add(d1);
            }
        }

        return collapsedList;
    }

    /**
     * Fills the null grid positions (if any) at the top of the given column in the
     * current game grid.  The returned list contains Descriptors representing new
     * dots added to the column with their new row and column. The previous row
     * for all descriptors is set to -1. The new dots are
     * produced by the generator's <code>generate</code> method. The list is
     * in no particular order.
     *
     * @param col column to be filled
     * @return list of new descriptors for dots added to the column
     */
    public ArrayList<Descriptor> fillColumn(int col) {
        ArrayList<Descriptor> filledList = new ArrayList<>();

        for (int i = 0; i < r; i++) {
            // Checks for the null objects in the array.
            if (dots[i][col] == null) {
                // Fills the null elements in the array with random dots.
                dots[i][col] = gen.generate();
                Descriptor d1 = new Descriptor(i, col, dots[i][col]);
                d1.setPreviousRow(-1);
                filledList.add(d1);
            }
        }

        // Returns the filled list with there new random dot values.
        return filledList;
    }

    /**
     * Checks whether not two descriptors are adjacent to one another.
     *
     * @param previous The previous descriptor.
     * @param current The current descriptor.
     * @return Whether not the two are adjacent.
     */
    private boolean isAdjacent(Descriptor previous, Descriptor current) {
        // Checks if the column is equal to the previous one.
        boolean colEqual = previous.col() == current.col();
        // Checks if the row is equal to the previous one.
        boolean rowEqual = previous.row() == current.row();

        // Checks if the |pRow - cRow| is only one element away.
        boolean rowAdj = Math.abs(previous.row() - current.row()) == 1;
        // Checks if the |pCol - cCol| is only one away.
        boolean colAdj = Math.abs(previous.col() - current.col()) == 1;

        // Determines whether not an dot is adjacent to another.
        if (rowAdj && colEqual) return true;
        if (colAdj && rowEqual) return true;

        return false;
    }
}