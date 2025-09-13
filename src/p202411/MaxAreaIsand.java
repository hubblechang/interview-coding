package p202411;

public class MaxAreaIsand {
    int[][] visited;
    int maxArea = 0;
    int curArea = 0;
    int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public int maxAreaOfIsland(int[][] grid) {
        visited = new int[grid.length][grid[0].length];
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[0].length; j++) {
                if(grid[i][j] == 1 && visited[i][j] == 0) {
                    curArea = 0;
                    dfs(grid, i, j);
                    maxArea = Math.max(maxArea, curArea);
                }
            }
        }
        return maxArea;
    }

    private void dfs(int[][] grid, int i, int j) {
        curArea++;
        visited[i][j] = 1;
        for(int[] direction : directions) {
            int x = i + direction[0];
            int y = j + direction[1];
            if(x >0 && x < grid.length && y > 0 && y < grid[0].length && grid[x][y] == 1 && visited[x][y] == 0) {
                dfs(grid, x, y);
            }
        }
    }

    public static void main(String[] args) {
        MaxAreaIsand m = new MaxAreaIsand();
        m.maxAreaOfIsland(new int[][]{{1,1}});
    }

}
