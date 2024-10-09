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
//			moveAllTraveler();
			
			
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
			int dir = findDir(cur.r,cur.c); // 방향을 구했다.
			
			if(dir == -1) continue;
			
			int nr = cur.r + dr[dir];
			int nc = cur.c + dc[dir];
			
			// 갈 수 있는 길인지 검사
			// 1. 범위 내인가? 2. 빈칸인가
			if (inRange(nr, nc) && map[nr][nc] == 0) {
				result++; // 이동거리 추가
				user[s] = new Point(nr, nc);
//				System.out.println("user"+s+"의 위치 " +user[s].r+" " +user[s].c);
			}
			
		}
	}
	
	public static int findDir(int r, int c) {
		int dir = -1;
		int minDist = distance(r,c);
		
		for(int d =0; d<4; d++) {
			int nr = r + dr[d];
			int nc = c + dc[d];
			
			if(!inRange(nr,nc) || map[nr][nc] > 0) continue;
			
			int dist = distance(nr,nc);
			
			if(minDist > dist) {
				dir = d;
				minDist = dist;
			}
		}
		
		
		return dir;
	}
	
	public static int distance(int r, int c) {
		return Math.abs(r - exit.r) + Math.abs(c - exit.c);
	}
	
	
	
	/**
	 * 가장 작은 회전 사각형을 찾는 함수
	 */
	public static void findSquare() {
		
		for(int n = 2; n<=N; n++) {
			for(int r1 = 1; r1<=N-n+1; r1++) {
				for(int c1 = 1; c1 <=N-n+1; c1++) {
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
	
	
	public static void moveAllTraveler() {
        // m명의 모든 참가자들에 대해 이동을 진행합니다.
        for(int i = 1; i <= M; i++) {
            // 이미 출구에 있는 경우 스킵합니다.
            if(user[i].c == exit.c && user[i].r == exit.r)
                continue;
            
            // 행이 다른 경우 행을 이동시켜봅니다.
            if(user[i].r != exit.r) {
                int nx = user[i].r;
                int ny = user[i].c;
    
                if(exit.r > nx) nx++;
                else nx--;
    
                // 벽이 없다면 행을 이동시킬 수 있습니다.
                // 이 경우 행을 이동시키고 바로 다음 참가자로 넘어갑니다.
                if(map[nx][ny] == 0) {
                    user[i].r = nx;
                    user[i].c = ny;
                    result++;
                    
                    System.out.println("user"+i+"의 위치 " +user[i].r+" " +user[i].c);
                    continue;
                }
            }
    
            // 열이 다른 경우 열을 이동시켜봅니다.
            if(user[i].c != exit.c) {
                int nx = user[i].r;
                int ny = user[i].c;
    
                if(exit.c > ny) ny++;
                else ny--;
    
                // 벽이 없다면 행을 이동시킬 수 있습니다.
                // 이 경우 열을 이동시킵니다.
                if(map[nx][ny] == 0) {
                    user[i].r = nx;
                    user[i].c = ny;
                    result++;
                    
                    System.out.println("user"+i+"의 위치 " +user[i].r+" " +user[i].c);
                    continue;
                }
            }
        
            System.out.println("user"+i+"의 위치 " +user[i].r+" " +user[i].c);
        }
    }
	
    

	
	
	
	
	// ================= 유틸들 ======================
	
	public static boolean inRange(int r, int c) {
		return r >= 1 && r <= N && c >= 1 && c <= N;
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