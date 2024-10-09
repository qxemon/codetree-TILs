import java.util.*;
import java.io.*;

public class Main {
	private static final int MAX_N = 15;

	private static Point[] convenStores;
	private static Point[] customers;

	private static List<Point> basecamp;

	private static int[][] map = new int[MAX_N + 1][MAX_N + 1];
	
	private static boolean[][] visited; // 방문여부
	private static int[][] rec; // bfs했을 시 위치 당 거리 측정 용
	
	
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
		
		visited = new boolean[N+1][N+1];
		rec = new int[N+1][N+1];

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
//			System.out.println(ans + "분 시뮬레이션");
//			System.out.println("===================");
			
			boolean isFinish = true;
			for (int m = 1; m <= M; m++) {
				if (!(convenStores[m].r == customers[m].r && convenStores[m].c == customers[m].c)) {
					isFinish = false;
				}
			}


			if (isFinish) {
				break;
			}

			// 1. 이동
			for (int m = 1; m <= M; m++) {
				// 아직 격자 밖
				if (m > ans)
					continue;
				// 이미 도착
				if (convenStores[m].r == customers[m].r && convenStores[m].c == customers[m].c) {
					continue;
				}

				// 3. 베캠이동
				if (customers[m].r == 0 && customers[m].c == 0) {
					//최단 거리 찾기
					bfs(convenStores[m]);
					
					//베이스캠프 할당
					findBasecamp(m);
					continue;
				}


				// 1. 편의점이동
				// bfs로 최단거리 측정 : visited를 통해 무한 루프 방지해야함
				bfs(convenStores[m]);
				move(m);
				

				// 2. 도착
				if (convenStores[m].r == customers[m].r && convenStores[m].c == customers[m].c) {
					map[convenStores[m].r][convenStores[m].c] = 2;
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

		int minDist = Integer.MAX_VALUE;
		int minR = -1, minC = -1;
		
		for (int d = 0; d < 4; d++) {
			int nr = custR + dr[d];
			int nc = custC + dc[d];
			if(inRange(nr,nc) && visited[nr][nc] && minDist > rec[nr][nc]) {
				minDist = rec[nr][nc];
				minR = nr;
				minC = nc;
			}
			
		}

		customers[n].r = minR;
		customers[n].c = minC;

	}
	
	public static void bfs(Point p) {
		// 초기화 (방문, bfs배열)
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <=N; j++) {
				visited[i][j] = false;
				rec[i][j] = 0;
			}
		}
		
		Queue<Point> queue = new ArrayDeque<>();
		
		queue.add(p);
		visited[p.r][p.c] = true;
		rec[p.r][p.c] = 0;
		
		while(!queue.isEmpty()) {
			Point now = queue.poll();
			for (int d = 0; d < 4; d++) {
				int nr = now.r + dr[d];
				int nc = now.c + dc[d];
				
				if(inRange(nr,nc) && !visited[nr][nc] && map[nr][nc] != 2) {
					visited[nr][nc] = true;
					rec[nr][nc] = rec[now.r][now.c]+1;
					queue.add(new Point(nr,nc));
				}
			}
		}
	}
	
	

	public static void findBasecamp(int num) {
		int conR = convenStores[num].r;
		int conC = convenStores[num].c;

		int minDist = Integer.MAX_VALUE;
		int baseR = -1;
		int baseC = -1;
		
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				if(visited[i][j] && map[i][j] == 1 && minDist > rec[i][j]) {
					minDist = rec[i][j];
					baseR = i;
					baseC = j;
				}
			}
		}
		
		customers[num].r = baseR;
		customers[num].c = baseC;
		
		map[baseR][baseC] = 2;
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