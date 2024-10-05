import java.util.*;
import java.io.*;

public class Main {

	private static final int MAX_M_LENGTH = 305;

	private static int K, M;
	private static int[][] map;
	private static int[][] map_copy;
	private static int[][] max_map;

	private static Queue<Integer> relics = new ArrayDeque<Integer>();

	// 좌상 ~ 우하까지 순서대로
	private static int[] dr = { -1, -1, -1, 0, 0, 0, 1, 1, 1 };
	private static int[] dc = { -1, 0, 1, -1, 0, 1, -1, 0, 1 };

	private static int[] di = { -1, 1, 0, 0 };
	private static int[] dj = { 0, 0, -1, 1 };

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		K = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		map = new int[5][5];
		map_copy = new int[5][5];
		max_map = new int[5][5];

		int[][] original = new int[3][3];
		int[][] copy = new int[3][3];

		for (int i = 0; i < 5; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < 5; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		st = new StringTokenizer(br.readLine());
		for (int i = 0; i < M; i++) {
			relics.add(Integer.parseInt(st.nextToken()));
		}
		// end of input

		for (int kk = 0; kk < K; kk++) {
			int ans = 0;

			max_map = new int[5][5];
			// 1. 각 중심점에 관해 9번의 회전 실험 및 초기 탐색을 진행할 것
			int rote_cnt= 100;
			for (int i = 1; i < 4; i++) {
				for (int j = 1; j < 4; j++) {
					
					copyArr(map, map_copy);
					
					// 중심점을 구함
					int d = 0;
					
					for (int k = 0; k < 3; k++) {
						for (int l = 0; l < 3; l++) {
							original[k][l] = map[i + dr[d]][j + dc[d]];
							d++;
						}
					}
					
					
					
					// 1. 90도 회전
					rotate(original, copy);
					
					// 1-1. 맵에 회전 유적 삽입
					fill_rotatedArea(i, j, copy);
//					print(map_copy);
					
					
					// 1-2. 맵의 가치 계산
					int cal = bfs(map_copy);
					
//					System.out.println(i+" "+ j +" " + cal);
//					System.out.println();
					if(cal > ans) {
						rote_cnt = 1;
						ans = cal;
						copyArr(map_copy, max_map);
					} else if (cal == ans && ans != 0) {
						if(rote_cnt > 1) {
							rote_cnt = 1;
							ans = cal;
							copyArr(map_copy,max_map);
						}
					}
					
					
					
					// 2. 180도 회전
					copyArr(map, map_copy);
					rotate180(original, copy);
					fill_rotatedArea(i, j, copy);
					cal = bfs(map_copy);
					if(cal > ans) {
						ans = cal;
						copyArr(map_copy, max_map);
					}else if (cal == ans && ans != 0) {
						if(rote_cnt > 2) {
							rote_cnt = 2;
							ans = cal;
							copyArr(map_copy,max_map);
						}
					}
					
					
					
					// 3. 270도 회전
					copyArr(map, map_copy);
					rotate270(original, copy);
					fill_rotatedArea(i, j, copy);
					cal = bfs(map_copy);
					if(cal > ans) {
						ans = cal;
						copyArr(map_copy, max_map);
					}
					
				}
			}
			
			
			//예외 회전 시 유적을 찾지 못함 -> 해당 회차 탐색 종료
			if(isEmptyArr(max_map)) {
				break;
			}
			
			
			//갱신
			copyArr(max_map, map);
			
			
//			print(map);
			// 2. 빈 유적을 채우고 연쇄작용 계산하기
			while(true) {
				fill();
//				print(map);
				int plusScore = bfs(map);
//				print(map);
				if(plusScore == 0) break;
				ans += plusScore;
				
				
			
			}
			
			System.out.print(ans+" ");
			
		}

		

	}
	
	private static void fill() {
		//열이작고 행이 큰 순서대로
		for(int j=0; j < 5; j++) {
			for (int i = 4; i >= 0; i--) {
				if(map[i][j]==0 && !relics.isEmpty()) {
					map[i][j] = relics.poll();
				}
			}
		}
	}

	private static boolean isEmptyArr(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				if(a[i][j] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	private static void fill_rotatedArea(int i, int j, int[][] copy) {
		int m = 0;
		for (int k = i - 1; k < i + 2; k++) {
			int n = 0;
			for (int l = j - 1; l < j + 2; l++) {
				map_copy[k][l] = copy[m][n];
				n++;
			}
			m++;
		}
	}

	private static boolean inRange(int i, int j) {
		return i >= 0 && i < 5 && j >= 0 && j < 5;
	}

	private static int bfs(int[][] a) {
		int score = 0;
		boolean[][] visited = new boolean[5][5];

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (!visited[i][j]) {
					Queue<int[]> queue = new ArrayDeque<>();
					Queue<int[]> trace = new ArrayDeque<>();

					queue.offer(new int[] { i, j });
					trace.offer(new int[] { i, j });
					visited[i][j] = true;
					while (!queue.isEmpty()) {
						int[] cur = queue.poll();

						for (int d = 0; d < 4; d++) {
							int ni = cur[0] + di[d];
							int nj = cur[1] + dj[d];

							// 체크: 범위 안, 같은 숫자, 방문 x
							if (inRange(ni, nj) && a[cur[0]][cur[1]] == a[ni][nj] && !visited[ni][nj]) {
								queue.offer(new int[] { ni, nj });
								trace.offer(new int[] { ni, nj });
								visited[ni][nj] = true;
							}
						}
					}

					if (trace.size() >= 3) {
						score += trace.size();
						// 맵의 유물 삭제
						while (!trace.isEmpty()) {
							int[] t = trace.poll();
							a[t[0]][t[1]] = 0;
						}
					}

				}
			}
		}

		return score;
	}

	private static void copyArr(int[][] a, int[][] b) {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b.length; j++) {
				b[i][j] = a[i][j];
			}
		}
	}

	private static void print(int[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				System.out.print(arr[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println("============================");
	}
	

	private static void rotate270(int[][] a, int[][] b) {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b.length; j++) {
				b[b.length - 1 - j][i] = a[i][j];
			}
		}
	}

	private static void rotate180(int[][] a, int[][] b) {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b.length; j++) {
				b[i][j] = a[b.length - 1 - i][b.length - 1 - j];
			}
		}
	}

	private static void rotate(int[][] a, int[][] b) {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				b[i][j] = a[b.length - 1 - j][i];
			}
		}
	}

}