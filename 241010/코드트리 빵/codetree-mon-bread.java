import java.util.*;
import java.io.*;

public class Main {
	private static final int MAX_N = 15;

	private static Point[] convenStores;
	private static Point[] customers;

	private static List<Point> basecamp;

	private static int[][] map = new int[MAX_N + 1][MAX_N + 1];
	private static boolean[][] active = new boolean[MAX_N + 1][MAX_N + 1]; // false 미할당, 이동가능 true: 할당, 이동불가

	private static int N, M;

	// 상좌우하
	private static int[] dr = { -1, 0, 0, 1 };
	private static int[] dc = { 0, -1, 1, 0 };

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());

		map = new int[N + 1][N + 1];
		active = new boolean[N + 1][N + 1];

		convenStores = new Point[M + 1];
		customers = new Point[M + 1];

		basecamp = new ArrayList<>();

		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine().trim());
			for (int j = 1; j <= N; j++) {
				int num = Integer.parseInt(st.nextToken());
				map[i][j] = num;
				if (num == 1) {
					basecamp.add(new Point(i, j));
				}
			}
		}

		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int conR = Integer.parseInt(st.nextToken());
			int conC = Integer.parseInt(st.nextToken());

			convenStores[i] = new Point(conR, conC);
			customers[i] = new Point(0, 0);
		} // end of input

		int ans = 1;

		while (true) {
			// 모든 고객이 편의점에 입장함 -> break;
			
			boolean isFinish = true;
			for (int m = 1; m <= M; m++) {
				if (!(convenStores[m].r == customers[m].r && convenStores[m].c == customers[m].c)) {
					isFinish = false;
				}
			}


			if (isFinish) {
				break;
			}

			for (int m = 1; m <= M; m++) {
				if (m > ans)
					continue;

				// 3. 베캠이동
				if (customers[m].r == 0 && customers[m].c == 0) {
					int assign = findBasecamp(m);
					// 해당 베캠으로 이동
					customers[m].r = basecamp.get(assign).r;
					customers[m].c = basecamp.get(assign).c;
					active[basecamp.get(assign).r][basecamp.get(assign).c] = true;
					
					continue;
				}

				if (convenStores[m].r == customers[m].r && convenStores[m].c == customers[m].c) {
					continue;
				}

				// 1. 편의점이동
				move(m);
				

				// 2. 도착
				if (convenStores[m].r == customers[m].r && convenStores[m].c == customers[m].c) {
					active[convenStores[m].r][convenStores[m].c] = true;
				}

			}

			ans++;
		}

		
		System.out.println(ans-1);

	}
	
	public static void printState() {
		for (int i = 1; i <= M; i++) {
			System.out.println(i + " " + convenStores[i]);
			System.out.println(i + " " + customers[i]);

			System.out.println("===================");
		}
	}
	

	public static void move(int n) {
		int custR = customers[n].r;
		int custC = customers[n].c;

		int convR = convenStores[n].r;
		int convC = convenStores[n].c;

		int dir = findDir(n);
		int nr = custR + dr[dir];
		int nc = custC + dc[dir];

		customers[n].r = nr;
		customers[n].c = nc;

	}

	public static int findBasecamp(int num) {
		int conR = convenStores[num].r;
		int conC = convenStores[num].c;

		int minDist = Integer.MAX_VALUE;
		int result = -1;
		for (int i = 0; i < basecamp.size(); i++) {
			int dist = distance(conR, conC, basecamp.get(i).r, basecamp.get(i).c);

			// 거리가 짧고, 활성화가 안되어 있다면 -> 해당 베캠을 할당
			// 왜 그냥 일련 탐색 해도 되는가? -> 우선순위가 행작고 열작은 순 -> 리스트가 행작고 열작은 순서대로 담겨있기 때문에
			// 같은 거리가 나오더라도 이전 것을 선택하게됨
			if (dist < minDist && !active[basecamp.get(i).r][basecamp.get(i).c]) {
				result = i;
				minDist = dist;
			}
		}

		return result;
	}

	public static int findDir(int n) {
		int dir = -1;
		
		int custR = customers[n].r;
		int custC = customers[n].c;

		int convR = convenStores[n].r;
		int convC = convenStores[n].c;
		
		int minDist = Integer.MAX_VALUE;

		for (int d = 0; d < 4; d++) {
			int nr = custR + dr[d];
			int nc = custC + dc[d];

			int dist = distance(nr, nc, convR, convC);

			// 범위 안이고 이동 가능하며, 거리가 가까워야함
			if (inRange(nr, nc) && !active[nr][nc] && dist < minDist) {
				dir = d;
				minDist = dist;
			}

		}

		return dir;
	}

	public static int distance(int r1, int c1, int r2, int c2) {
		return Math.abs(r2 - r1) + Math.abs(c2 - c1);
	}

	public static boolean inRange(int r, int c) {
		return r >= 1 && r <= N && c >= 1 && c <= N;
	}

	public static void print(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("===================");
	}

	static class Point {
		int r;
		int c;

		public Point(int r, int c) {
			this.r = r;
			this.c = c;
		}

		@Override
		public String toString() {
			return "Point [r=" + r + ", c=" + c + "]";
		}

	}
}