import java.util.*;
import java.io.*;


public class Main {

        static int[][] map;

        static int N,M,P,C,D;
        static int[] di = {-1,0,1,0}; //상우하좌
        static int[] dj = {0,1,0,-1};

        static Map<Integer, Santa> santaList;
        static Rudolph rudolph;



        public static void main(String[] args) throws IOException {
            // 여기에 코드를 작성해주세요.
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StringTokenizer st = new StringTokenizer(br.readLine());

            N = Integer.parseInt(st.nextToken()); //게임판 크기
            M = Integer.parseInt(st.nextToken()); //게임 턴
            P = Integer.parseInt(st.nextToken()); //산타 수
            C = Integer.parseInt(st.nextToken()); //루돌프 힘
            D = Integer.parseInt(st.nextToken()); // 산타힘

            santaList = new HashMap<>();
            map = new int[N+1][N+1];

            st = new StringTokenizer(br.readLine());
            int ri = Integer.parseInt(st.nextToken());
            int rj = Integer.parseInt(st.nextToken());

            rudolph = new Rudolph(ri, rj);
            map[ri][rj] = -1; //루돌프

            for(int p=0; p<P; p++){
                st = new StringTokenizer(br.readLine());
                int num = Integer.parseInt(st.nextToken());
                int i = Integer.parseInt(st.nextToken());
                int j = Integer.parseInt(st.nextToken());

                santaList.put(num, new Santa(num, i ,j,0, 0, false));
                map[i][j] = num;
            } // end of input;

            for(int m=1; m<=M; m++){

                int shortI = 10000;
                int shortJ = 10000;
                int closestSanta = 0;

                //루돌프에 가까운 산타 찾기
                for(int p=1; p<=P; p++){
                    if(santaList.get(p).out) continue;

                    Distance currentBest = new Distance((shortI - rudolph.i)*(shortI - rudolph.i)+(shortJ - rudolph.j)*(shortJ - rudolph.j),-shortI,-shortJ);
                    Distance currentResult = new Distance((santaList.get(p).i- rudolph.i)*(santaList.get(p).i- rudolph.i)+(santaList.get(p).j - rudolph.j) * (santaList.get(p).j - rudolph.j), -santaList.get(p).i, -santaList.get(p).j);

                    if(currentResult.compareTo(currentBest) < 0){ // 음수면 정렬 그대로 -> 가까운 것 갱신해야함
                        shortI = santaList.get(p).i;
                        shortJ = santaList.get(p).j;
                        closestSanta = p;

                    }
                }

                //루돌프 이동
                if(closestSanta != 0){
                    Rudolph prev = new Rudolph(rudolph.i, rudolph.j); // 좌표 저장 용
                    int moveI = 0;
                    if(shortI > rudolph.i) moveI = 1;
                    else if(shortI < rudolph.i) moveI = -1;

                    int moveJ = 0;
                    if(shortJ > rudolph.j) moveJ = 1;
                    else if(shortJ < rudolph.j) moveJ = -1;

                    rudolph.i += moveI;
                    rudolph.j += moveJ; // 루돌프 이동
                    map[prev.i][prev.j] = 0; // 루돌프 이전 경로 지우기

                    if(rudolph.i == shortI && rudolph.j == shortJ){
                        int santaCrashI = shortI + moveI * C;
                        int santaCrashJ = shortJ + moveJ * C;

                        int santaCrashContinueI = santaCrashI;// 연쇄추돌 계산을 위한 변수
                        int santaCrashContinueJ = santaCrashJ;


                        santaList.get(closestSanta).stun = m+1;

                        while(inRange(santaCrashContinueI, santaCrashContinueJ) && map[santaCrashContinueI][santaCrashContinueJ] > 0){
                            santaCrashContinueI += moveI;
                            santaCrashContinueJ += moveJ;
                        }

                        while(!(santaCrashContinueI == santaCrashI && santaCrashContinueJ == santaCrashJ)){
                            int beforeI = santaCrashContinueI - moveI;
                            int beforeJ = santaCrashContinueJ - moveJ;

                            //범위 체크
                            if(!inRange(beforeI, beforeJ)) break;

                            int num = map[beforeI][beforeJ];
                            if(!inRange(santaCrashContinueI,santaCrashContinueJ)){
                                santaList.get(num).out = true;
                            }else{
                                map[santaCrashContinueI][santaCrashContinueJ] = map[beforeI][beforeJ];
                                santaList.get(num).i = santaCrashContinueI;
                                santaList.get(num).j = santaCrashContinueJ;
                            }

                            santaCrashContinueI = beforeI;
                            santaCrashContinueJ = beforeJ;

                        }

                        santaList.get(closestSanta).score += C;
                        santaList.get(closestSanta).i = santaCrashI;
                        santaList.get(closestSanta).j = santaCrashJ;
                        if(inRange(santaCrashI, santaCrashJ)){
                            map[santaCrashI][santaCrashJ] = closestSanta;
                        } else {
                            santaList.get(closestSanta).out = true;
                        }
                    }
                }

                map[rudolph.i][rudolph.j] = -1;

                //산타 이동
                for (int p = 1; p <= P ; p++) {
                    //아웃되었거나 스턴 상태이면 continue;
                    if (santaList.get(p).out || santaList.get(p).stun >= m) continue;

                    int minDist = (santaList.get(p).i - rudolph.i) * (santaList.get(p).i - rudolph.i) + (santaList.get(p).j - rudolph.j) * (santaList.get(p).j - rudolph.j);
                    int dir = -1;
                    for (int d = 0; d < 4; d++) {
                        int ni = santaList.get(p).i + di[d];
                        int nj = santaList.get(p).j + dj[d];

                        if (inRange(ni, nj) && map[ni][nj] <= 0) {
                            int dist = (ni - rudolph.i) * (ni - rudolph.i) + (nj - rudolph.j) * (nj - rudolph.j);
                            if (dist < minDist) {
                                minDist = dist;
                                dir = d;
                            }
                        }
                    }
                    if (dir != -1) {
                        int ni = santaList.get(p).i + di[dir];
                        int nj = santaList.get(p).j + dj[dir];

                        if (ni == rudolph.i && nj == rudolph.j) {
                            santaList.get(p).stun = m + 1;

                            int moveI = -di[dir];
                            int moveJ = -dj[dir];

                            int firstI = ni + moveI * D;
                            int firstJ = nj + moveJ * D;
                            int lastI = firstI;
                            int lastJ = firstJ;

                            if (D == 1) {
                                santaList.get(p).score += 1;
                            } else {
                                //산타 연쇄작용
                                while (inRange(lastI, lastJ) && map[lastI][lastJ] > 0) {
                                    lastI += moveI;
                                    lastJ += moveJ;
                                }

                                while (!(lastI == firstI && lastJ == firstJ)) {
                                    int beforeI = lastI - moveI;
                                    int beforeJ = lastJ - moveJ;

                                    if (!inRange(beforeI, beforeJ)) break;

                                    int idx = map[beforeI][beforeJ];
                                    if (!inRange(lastI, lastJ)) {
                                        santaList.get(idx).out = true;
                                    } else {
                                        map[lastI][lastJ] = map[beforeI][beforeJ];
                                        santaList.get(idx).i = lastI;
                                        santaList.get(idx).j = lastJ;
                                    }

                                    lastI = beforeI;
                                    lastJ = beforeJ;

                                }
                                santaList.get(p).score += D;
                                map[santaList.get(p).i][santaList.get(p).j] = 0;
                                santaList.get(p).i = firstI;
                                santaList.get(p).j = firstJ;
                                if (inRange(firstI, firstJ)) {
                                    map[firstI][firstJ] = p;
                                } else {
                                    santaList.get(p).out = true;
                                }

                            }

                        } else {
                            //충돌 안했음
                            map[santaList.get(p).i][santaList.get(p).j] = 0;
                            santaList.get(p).i = ni;
                            santaList.get(p).j = nj;
                            map[ni][nj] = p;

                        }

                    }
                }
                for(int p=1 ; p<=P; p++){
                    if(!santaList.get(p).out) santaList.get(p).score += 1;
                }
            }

            for (int i = 1; i <= P; i++) {
                System.out.print(santaList.get(i).score+ " ");
            }



        }

        static class Distance implements Comparable<Distance>{
            int dis;
            int i;
            int j;

            public Distance(int dis, int i, int j) {
                this.dis = dis;
                this.i = i;
                this.j = j;
            }

            @Override
            public int compareTo(Distance d) {
                if(this.dis != d.dis){
                    return Integer.compare(this.dis, d.dis);
                }
                if(this.i != d.i){
                    return Integer.compare(this.i, d.i);
                }
                return Integer.compare(this.j, d.j);
            }


        }

        static boolean inRange(int i, int j){
            return i >= 1 && i <= N && j >= 1 && j <= N;
        }

        static class Rudolph{
            int i;
            int j;

            public Rudolph(int i, int j) {
                this.i = i;
                this.j = j;
            }
        }
        static class Santa{
            int num;
            int i;
            int j;
            int score;
            int stun;
            boolean out;

            public Santa(int num, int i, int j, int score, int stun, boolean out) {
                this.num = num;
                this.i = i;
                this.j = j;
                this.score = score;
                this.stun = stun;
                this.out = out;
            }
        }

        static class Pair{
            int i;
            int j;

            public Pair(int i, int j) {
                this.i = i;
                this.j = j;
            }
        }

        public static void print(){
            for (int i = 0; i < N+1; i++) {
                for (int j = 0; j < N+1; j++) {
                    System.out.print(map[i][j]+" ");
                }
                System.out.println();
            }
        }


}