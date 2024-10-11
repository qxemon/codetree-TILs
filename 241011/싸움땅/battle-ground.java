import java.util.*;
import java.io.*;

public class Main {

	static int N, M, K;

	static PriorityQueue<Integer>[][] map;

	static Player[] players;

	// 상우하좌
	static int[] dr = { -1, 0, 1, 0 };
	static int[] dc = { 0, 1, 0, -1 };

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());

		// 자바에서는 제네릭 타입으로 배열을 직접 생성할 수 없음
		// 이는 자바의 제네릭이 타입 소거(Type Erasure) 방식으로 구현되어 있기 때문에
		// 런타임에 제네릭 타입 정보가 사라져서 배열에서 타입 안정성을 보장할 수 없기 때문
		map = new PriorityQueue[N + 1][N + 1];
		players = new Player[M + 1];

		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine().trim());
			for (int j = 1; j <= N; j++) {
				int g = Integer.parseInt(st.nextToken());
				map[i][j] = new PriorityQueue<Integer>(Collections.reverseOrder());

				if (g > 0) {
					map[i][j].add(g);
				}

			}
		}

		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int s = Integer.parseInt(st.nextToken());
			players[i] = new Player(r, c, d, s);

		} // end of input

//		print();
//		printPlayer();

		
//		System.out.println("-----------------------------------");
		for (int k = 1; k <= K; k++) {
			simulation();
//			print();
//			printPlayer();
		}

		for (int i = 1; i <= M; i++) {
			System.out.print(players[i].score + " ");
		}

	}

	public static void simulation() {
		for (int i = 1; i <= M; i++) {
			Player p = players[i];

			// 1-1. 플레이어가 향하고 있는 방향대로 한 칸만큼 이동함. 격자를 벗어난다면 정반대 방향으로 바꾼 후 1만큼 이동
			int nr = p.r + dr[p.d];
			int nc = p.c + dc[p.d];

			if (!inRange(nr, nc)) {
				p.d = reverseDir(p.d);
				nr = p.r + dr[p.d];
				nc = p.c + dc[p.d];
			}
			// 일단 이동 완료
			p.r = nr;
			p.c = nc;
				
			// ============================

			// 2. 해당 칸에 플레이어 있는 지 확인
			int who = someoneThere(nr, nc, i);

			// 2-1. 플레이어가 없다면
			if (who == 0) {
				// 총이 있다면
				if (!map[nr][nc].isEmpty()) {
					pickTheGun(nr, nc, i);

				}

			} else if( who > 0) {
				// 2. 플레이어가 있다면 -> 대결
				battle(i, who);

			}
		}
	}

	public static int someoneThere(int r, int c, int i) {
		int who = 0;
		for (int j = 1; j <= M; j++) {
			if (i != j && r == players[j].r && c == players[j].c) {
				who = j;
				break;
			}
		}

		return who;
	}

	public static void battle(int a, int b) {
		Player pa = players[a];
		Player pb = players[b];

		boolean winA = whoWin(pa, pb);

		// a가 이긴 경우
		if (winA) {
			int score = calScore(pa,pb);
			loserAction(b);
			winnerAction(a, score);
		} else {
			int score = calScore(pa,pb);
			loserAction(a);
			winnerAction(b, score);
		}

	}
	
	public static int calScore(Player a, Player b) {
		return Math.abs((a.str+a.gun)-(b.str+b.gun));
	}

	public static boolean whoWin(Player a, Player b) {
		int aSum = a.str + a.gun;
		int bSum = b.str + b.gun;

		boolean result = true;
		if (aSum > bSum) {
			result = true;
		} else if (aSum < bSum) {
			result = false;
		} else if (aSum == bSum) {
			if (a.str < b.str) {
				result = false;
			}
		}

		return result;

	}

	public static void loserAction(int m) {
		Player p = players[m];

		// 1. 총 격자에 반납
		if (p.gun != 0) {
			map[p.r][p.c].add(p.gun);
			p.gun = 0;
		}

		// 2. 이동

		int nr = p.r + dr[p.d];
		int nc = p.c + dc[p.d];

		// 격자 밖이거나 누군가 있다면
		if (!inRange(nr, nc) || someoneThere(nr, nc, m) != 0) {
			int newDir = rotateDir(p.d);
			for (int i = 0; i < 4; i++) {
				nr = p.r + dr[newDir];
				nc = p.c + dc[newDir];
				if(inRange(nr,nc) && someoneThere(nr,nc,m) == 0) {
					p.r = nr;
					p.c = nc;
					p.d = newDir;
					break;
				}
				newDir = rotateDir(newDir);
				
			}
		}
		else {
			p.r = nr;
			p.c = nc;
		}
		
		//만약 해당 칸에 총이 있다면 총을 획득합니다.
		if(!map[p.r][p.c].isEmpty()) {
			pickTheGun(p.r,p.c,m);
		}

	}

	public static void winnerAction(int m, int score) {
		Player p = players[m];
		
		//1. 점수 얻기
		p.score += score;
		
		//2.격자에서 총 얻기
		if(!map[p.r][p.c].isEmpty()) {
			pickTheGun(p.r,p.c,m);			
		}
		
	}

	/**
	 * 필드에 있는 가장 큰 공격력의 총을 줍는 함수
	 * 
	 * @param nr 다음 행
	 * @param nc 다음 열
	 * @param i  플레이어 번호
	 */
	public static void pickTheGun(int nr, int nc, int i) {
		// 기존에 플레이어가 총이 있었다면
		if (players[i].gun > 0) {
			// 최대 힙이기 때문에 꺼낸 것이 가장 큰 값
			int field = map[nr][nc].peek();
			// 땅에 떨어진 가장 큰 것과 기존 것을 비교
			if (players[i].gun < field) {
				field = map[nr][nc].poll();
				int tmp = players[i].gun;
				players[i].gun = field;
				map[nr][nc].add(tmp);
			}
		} else {
			players[i].gun = map[nr][nc].poll();
		}
	}

	
	
	//Utils

	public static int rotateDir(int d) {
		return (d + 1) % 4;
	}

	public static int reverseDir(int d) {
		return (d + 2) % 4;
	}

	public static boolean inRange(int r, int c) {
		return r >= 1 && r <= N && c >= 1 && c <= N;
	}

	public static void printPlayer() {
		for (int i = 1; i <= M; i++) {
			System.out.println(players[i]);
		}
	}

	public static void print() {
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
		System.out.println("========================");
	}

	public static class Player {
		int r;
		int c;
		int d;
		int str;
		int gun;
		int score;

		public Player(int r, int c, int d, int s) {
			this.r = r;
			this.c = c;
			this.d = d;
			this.str = s;
		}

		@Override
		public String toString() {
			return "Player [r=" + r + ", c=" + c + ", d=" + d + ", str=" + str + ", gun=" + gun + ", score=" + score
					+ "]";
		}

		

	}

}