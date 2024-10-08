import java.io.*;
import java.util.*;

public class Main {
	private static final int N_MAX = 10;
	private static final int M_MAX = 10;
	private static final int K_MAX = 100;

	private static int N, M, K;
	private static int[][] map;
	private static int[][] copy;
	
	private static Point[] user;
	private static Point exit;

	private static int[] dr = { -1, 1, 0, 0 };
	private static int[] dc = { 0, 0, -1, 1 };

	private static int result;
	
	private static int sr, sc, squareSize; // 회전 정사각형에 대한 정보를 담음
	
	

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken()); // 격자 크기
		M = Integer.parseInt(st.nextToken()); // 참가자 수
		K = Integer.parseInt(st.nextToken()); // 시뮬 시간

		map = new int[N + 1][N + 1];
		copy = new int[N+1][N+1];
		user = new Point[M + 1];
		result = 0;

		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				String next = st.nextToken();
				map[i][j] = Integer.parseInt(next);
				copy[i][j] = Integer.parseInt(next);
			
			}
		}
//		print(map);

		for (int m = 1; m <= M; m++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());

			user[m] = new Point(r, c);
		} 
		
		
		st = new StringTokenizer(br.readLine());
		exit = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		
		// end of input

		// 시뮬레이션
		for (int k = 0; k < K; k++) {

//			System.out.println(k+ "초 후");
//			System.out.println("====================");
			// 참가자 이동
			move();
			
			
			//게임 종료 판단
			boolean isFinish = true;
			
			for (int m = 1; m <=M; m++) {
				if(!(user[m].r == exit.r && user[m].c == exit.c)) {
					isFinish = false;
				}
			}
			
			if(isFinish) break;

			// 회전
			
			//1. 참가자와 출구를 포함한 가장 작은 사각형 찾기
			findSquare();
			
//			System.out.println("이번 사각형 크기는 = " + squareSize);
			//2. 공간회전
			rotate();
			//3. 참가자위치와 출구 회전
			rotateUserAndExit();
			
		}

		System.out.println(result);
		System.out.println(exit.r + " " + exit.c);

	}

	
	/**
	 * 참가자들의 이동을 표현하는 함수
	 */
	public static void move() {
		
		
		for (int s = 1; s <= M; s++) {
			Point cur = user[s];
			
			// 이미 출구 도착했다면
			if (cur.r == exit.r && cur.c == exit.c) {
				continue;
			}
			
			// 1. 출구로 부터 최단 거리 방향 구하기
			int minDist = Integer.MAX_VALUE;
			int dir = 0;
			for (int d = 0; d < 4; d++) {
				int nr = cur.r + dr[d];
				int nc = cur.c + dc[d];
				int dist = Math.abs(nr - exit.r) + Math.abs(nc - exit.c);
				if (dist < minDist) {
					dir = d;
					minDist = dist;
				}
			} // 방향을 구했다.
			
//			System.out.println(s+"의 방향"+dir);
			
			int nr = cur.r + dr[dir];
			int nc = cur.c + dc[dir];
			
			// 갈 수 있는 길인지 검사
			// 1. 범위 내인가? 2. 빈칸인가
			if (inRange(nr, nc) && map[nr][nc] == 0) {
//				System.out.println("이동했어요");
				result++; // 이동거리 추가
				user[s] = new Point(nr, nc);
			}
//			System.out.println("user"+s+"의 위치 " +user[s].r+" " +user[s].c);
		}
	}
	
	/**
	 * 가장 작은 회전 사각형을 찾는 함수
	 */
	public static void findSquare() {
		
		for(int n = 2; n<=N; n++) {
			for(int r1 = 1; r1<=n; r1++) {
				for(int c1 = 1; c1 <=n; c1++) {
					//최대 길이 열 측정
					int r2 = r1 + n - 1;
					int c2 = c1 + n - 1;
					
					//출구가 해당 사각형 내에 존재해야함
					if(!(exit.r >= r1 && exit.r <= r2 && exit.c >= c1 && exit.c <= c2)) {
						continue;
					}
					
					
					//해당 사각 형 내에 존재하는 참가자가 있는지
					boolean haveUser = false;
					for (int m = 1; m <= M; m++) {
						// 범위 내임
						if(user[m].r >= r1 && user[m].r <= r2 && user[m].c >= c1 && user[m].c <= c2) {
							if(!(user[m].r == exit.r && user[m].c == exit.c)) {
								haveUser= true;
							}
						}
					}
					
					//그럼 해당 사각형이 가장 작은 사각형임
					if(haveUser) {
						sr = r1;
						sc = c1;
						squareSize = n;
						
						return;
					}
					
				}
			}
		}
	}
	
	
	/**
	 * 맵을 회전 하는 함수
	 */
	public static void rotate() {
	
		// 1. 범위 안에 있는 벽의 내구도를 -1 합니다.
		for (int i = sr; i < sr + squareSize; i++) {
			for (int j = sc; j < sc + squareSize; j++) {
				if(map[i][j] > 0)
					map[i][j]--;
			}
		}
		
		//2. 맵 회전
		for (int i = sr; i < sr+squareSize; i++) {
			for (int j = sc; j < sc+squareSize; j++) {
				int zr = i - sr, zc = j- sc;
				
				int newR = zc, newC = squareSize-zr-1;
				
				copy[newR+sr][newC+sc] = map[i][j];
				
			}
		}
		
		//3. 원본 변경
		copyArr(copy, map);
		
//		print(map);
		
	}
	
	
	/**
	 * 참가자와 출구를 회전하는 함수
	 */
	public static void rotateUserAndExit() {
		
		for(int u = 1; u <=M; u++) {
			int r = user[u].r;
			int c = user[u].c;
			
			if(r>= sr && r < sr+squareSize && c >= sc && c < sc+squareSize) {
				int zr = r-sr, zc = c-sc;
				
				int newR = zc, newC = squareSize - 1 - zr;
				
				user[u].r = newR + sr;
				user[u].c = newC + sc;				
			}
			
		}
		
		int r = exit.r;
		int c = exit.c;
		
		if(r>= sr && r <= sr+squareSize && c >= sc && c <= sc+squareSize) {
			int zr = r-sr, zc = c-sc;
			
			int newR = zc, newC = squareSize - 1 - zr;
			
			exit.r = newR + sr;
			exit.c = newC + sc;
		}
//		System.out.println("exit: " + exit.r+ " "+ exit.c);
		
	}
	
	
    

	
	
	
	
	// ================= 유틸들 ======================
	
	public static boolean inRange(int r, int c) {
		return r >= 1 && r <= N && c >= 1 && r <= N;
	}
	

	public static void copyArr(int[][] a, int[][] b) {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b.length; j++) {
				b[i][j] = a[i][j];
			}
		}
	}
	
	public static void print(int[][] a) {
		System.out.println();
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("==========================");
	}

	public static class Point {
		int r, c;

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