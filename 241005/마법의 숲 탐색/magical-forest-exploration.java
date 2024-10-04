import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    //북동남서
    static int[] di = {-1, 0, 1, 0};
    static int[] dj = {0, 1, 0, -1};

    static int R, C, K, ci, dir;
    static int[][] map;
    static int ans, num;

    static int[] point = new int[2];

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[R + 3][C];
        ans = 0;
        num = 3;


        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            ci = Integer.parseInt(st.nextToken());
            dir = Integer.parseInt(st.nextToken());

            point[0] = 1;
            point[1] = ci - 1;

            move();

        } // END OF INPUT
        System.out.println(ans);


    }

    static boolean inRange(int r, int c) {
        return 3 <= r && r < R + 3 && 0 <= c && c < C;
    }

    static void move() {
        boolean moved = true;
        while (moved) {
            moved = false;
            if (canDown()) {
                moved = true;
                continue;
            }
            if (canLeft()) {
                moved = true;
                continue;
            }
            if (canRight()) {
                moved = true;
            }
        }

        fill(num);
        num += 2;

//        print();

        if (checkReset()) {
            reset();
        } else {
            ans += bfs();
        }
    }

    static void print() {
        for (int i = 0; i < R + 3; i++) {
            for (int j = 0; j < C; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("==========================");
    }

    static boolean checkReset() {
        for (int i = 0; i < C; i++) {
            if (map[1][i] > 0) {
                return true;
            }
        }
        return false;
    }

    static void fill(int num) {
        map[point[0]][point[1]] = num;
        for (int d = 0; d < 4; d++) {
            map[point[0] + di[d]][point[1] + dj[d]] = num;
        }
        //출구 표시 ( 짝 )
        map[point[0] + di[dir]][point[1] + dj[dir]] = num - 1;
    }


    static void reset() {
        for (int i = 0; i < R + 3; i++) {
            for (int j = 0; j < C; j++) {
                map[i][j] = 0;
            }
        }
    }

    static boolean canDown() {
        //1. 맵 범위 확인
        if (point[0] + 1 < R + 2) {
            //2. 맵 상태 확인 (아래로 내려갈 부분이 비었는지)
            if (map[point[0] + 1][point[1] + 1] == 0 && map[point[0] + 1][point[1] - 1] == 0 && map[point[0] + 2][point[1]] == 0) {
                point[0]++;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    static boolean canLeft() {
        if (point[0] + 1 < R + 2 && point[1] - 2 > 0) {
            if (map[point[0] - 1][point[1] - 1] == 0 && map[point[0]][point[1] - 2] == 0 && map[point[0] + 1][point[1] - 2] == 0 && map[point[0] + 1][point[1] - 1] == 0 && map[point[0] + 2][point[1] - 1] == 0) {
                point[0]++;
                point[1]--;
                dir = (dir + 3) % 4;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    static boolean canRight() {
        if (point[0] + 1 < R + 2 && point[1] + 2 < C) {
            if (map[point[0] - 1][point[1] + 1] == 0 && map[point[0]][point[1] + 2] == 0 && map[point[0] + 1][point[1] + 2] == 0 && map[point[0] + 1][point[1] + 1] == 0 && map[point[0] + 2][point[1] + 1] == 0) {
                point[0]++;
                point[1]++;
                dir = (dir + 1) % 4;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    static int bfs() {

        Queue<int[]> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[R + 3][C];
        queue.offer(point);
        visited[point[0]][point[1]] = true;

        int result = point[0];
        while (!queue.isEmpty()) {
            int[] now = queue.poll();
            for (int d = 0; d < 4; d++) {
                int nr = now[0] + di[d];
                int nc = now[1] + dj[d];
                //1. 범위 안인가?
                if (inRange(nr, nc) && !visited[nr][nc]) {
                    //2. 출구인가? 출구 ( 짝 ) , 일반 공간 ( 홀 )
                    if (map[now[0]][now[1]] % 2 == 1) {
                        if (map[nr][nc] == map[now[0]][now[1]] || map[nr][nc] == map[now[0]][now[1]] - 1) {
                            visited[nr][nc] = true;
                            queue.offer(new int[]{nr, nc});
                            result = Math.max(result, nr);
                        }
                    } else { //현재 요정이 출구에 위치함
                        if (map[nr][nc] > 0) { // 다른 우주선으로의 이동 가능
                            visited[nr][nc] = true;
                            queue.offer(new int[]{nr, nc});
                            result = Math.max(result, nr);
                        }
                    }
                }
            }

        }
        return result - 2;
    }
}