package tools;

public class DynamicMatrix2D<T> {
    private T[][] matrix = (T[][]) new Object[1][1];

    public void set(int x, int y, T value) {
        if (x >= matrix.length) {
            T[][] tmp = matrix;
            matrix = (T[][]) new Object[x + 1][];
            System.arraycopy(tmp, 0, matrix, 0, tmp.length);
            for (int i = x; i < x + 1; i++) {
                matrix[i] = (T[]) new Object[y];
            }
        }

        if (y >= matrix[x].length) {
            T[] tmp = matrix[x];
            matrix[x] = (T[]) new Object[y + 1];
            System.arraycopy(tmp, 0, matrix[x], 0, tmp.length);
        }

        matrix[x][y] = value;
    }
    public T get(int x, int y) {
        return x >= matrix.length || y >= matrix[x].length ? null : matrix[x][y];
    }
    public T[] get(int x) {
        return x >= matrix.length ? null : matrix[x];
    }

    public int getYSize() {
        return matrix.length;
    }
    public int getXSize(int y) {
        return matrix[y].length;
    }
}