import java.io.*;
import java.util.*;

public class Main {
	static final int  MAX_L = 41;
	static final int MAX_N = 31;
	
	static int l,n,q;
	//맵  0:빈칸, 1:함정, 2:벽
	static int[][] board = new int[MAX_L][MAX_L];
	
	static int[] di = {-1,0,1,0}, dj = {0,1,0,-1}; // 상 우 하 좌
	
	static List<Knight> knightList;
	
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		l = Integer.parseInt(st.nextToken());
		n = Integer.parseInt(st.nextToken());
		q = Integer.parseInt(st.nextToken());
		
		knightList = new ArrayList<>();
		knightList.add(new Knight(0,0,0,0,0));
		for (int i = 1; i < l+1; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j < l+1; j++) {
				board[i][j] = Integer.parseInt(st.nextToken());
			}
		} // 체스판 입력 완료
		
		//기사 정보 입력
		for (int i = 1; i <= n; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			
			knightList.add(new Knight(r,c,h,w,k));
			knightList.get(i).earlyK = knightList.get(i).k;
		}
		
		for (int i = 1; i <= q; i++) {
			st = new StringTokenizer(br.readLine());
			int num = Integer.parseInt(st.nextToken());
			int dir = Integer.parseInt(st.nextToken());
			simulation(num, dir);
			
		}
		
		long ans = 0;
		
		for (int i = 1; i <= n; i++) {
			if(knightList.get(i).k > 0) {
				ans += knightList.get(i).earlyK - knightList.get(i).k;
			}
		}
		
		System.out.println(ans);
		
	}
	
	//움직임 여부
	static boolean bfs(int num, int dir) {
		Queue<Integer> queue = new ArrayDeque<>();
		
		// 데미지, 움직임 여부, 다음 위치 초기화 작업
		for (int i = 1; i <= n; i++) {
			knightList.get(i).dmg = 0;
			knightList.get(i).isMoved = false;
			
		}
		
		// 명령으로 주어진 기사 번호 넣기
		queue.add(num);
		knightList.get(num).isMoved = true;
		
		while(!queue.isEmpty()) {
			int now = queue.poll();
			
			knightList.get(now).ni = knightList.get(now).r + di[dir];
			knightList.get(now).nj = knightList.get(now).c + dj[dir];
			//경계 체크
			if(knightList.get(now).ni < 1 || knightList.get(now).ni + knightList.get(now).h -1 > l || knightList.get(now).nj < 1 || knightList.get(now).nj + knightList.get(now).w - 1 > l) {
				return false;
			}
			// 대상 기사가 다른 기사나 벽에 충돌하는지 체크
			for (int i = knightList.get(now).ni; i <= knightList.get(now).ni + knightList.get(now).h -1 ; i++) {
				for (int j = knightList.get(now).nj; j <= knightList.get(now).nj + knightList.get(now).w - 1; j++) {
					if(board[i][j] == 1) {
						knightList.get(now).dmg++;
					}
					if(board[i][j] == 2) { // 벽이면 움직임 x
						return false;
					}
				}
			}
			
			//충돌 경우 연쇄 작용 처리
			for (int i = 1; i <= n; i++) {
				if(knightList.get(i).isMoved || knightList.get(i).k <= 0) continue;
				if(knightList.get(i).r > knightList.get(now).ni + knightList.get(now).h-1 || knightList.get(now).ni > knightList.get(i).r + knightList.get(i).h - 1) {
					continue;
				}
				if(knightList.get(i).c > knightList.get(now).nj + knightList.get(now).w-1 || knightList.get(now).nj > knightList.get(i).c + knightList.get(i).w - 1) {
					continue;
				}
				knightList.get(i).isMoved = true;
				queue.add(i);
			}
			
		} //bfs 종료
		
		
		//움직인 기사는 데미지 없음
		knightList.get(num).dmg = 0;
		return true;
	}
	// 실제 움직임 업데이트
	static void simulation(int num, int dir) {
		// 체력이 0이면 무시
		if(knightList.get(num).k <= 0 ) return;
		
		if(bfs(num, dir)) {
			for (int i = 1; i <= n; i++) {
				knightList.get(i).r = knightList.get(i).ni;
				knightList.get(i).c = knightList.get(i).nj;
				knightList.get(i).k -= knightList.get(i).dmg;
			}
		}
		
	}
	
	
	static class Knight{
		int r,c,h,w,k; // 행, 열 , 높이, 너비, 체력

		int earlyK; // 초기 체력: 나중에 남은 체력과 차이로 입은 데미지 계산하게 
		int ni, nj;
		int dmg;
		boolean isMoved;
		
		public Knight(int r, int c, int h, int w, int k) {
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
		}
		
		
		
	}
	
}