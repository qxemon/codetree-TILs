import java.io.*;
import java.util.*;

public class Main {

    static int N, Q;

    static int[] parent; // 부모 노드
    static int[] authority; // 권한 == power
    static boolean[] mute; // 알람 on/off 여부
    static int[] chatRoom; // 해당 노드에 도달할 수 있는 채팅 방의 수 // 정답
    static int[][] transfer; // 해당 노드가 전달할 수 있는 알림 수 // 뮤트와 , 부모 옮길 때 갱신

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken()); // 트리 노드 수
        Q = Integer.parseInt(st.nextToken()); // 명령어 수

        parent = new int[N + 1];
        authority = new int[N + 1];
        mute = new boolean[N + 1];
        chatRoom = new int[N + 1];
        transfer = new int[N + 1][22];


        for (int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());
            int command = Integer.parseInt(st.nextToken());

            if (command == 100) {
                makeTree(st);
            } else if (command == 200) {
                int c = Integer.parseInt(st.nextToken());
                alarmToggle(c);
            } else if (command == 300) {
                int c = Integer.parseInt(st.nextToken());
                int p = Integer.parseInt(st.nextToken());
                changePower(c, p);
            } else if (command == 400) {
                int c1 = Integer.parseInt(st.nextToken());
                int c2 = Integer.parseInt(st.nextToken());
                exchangeParent(c1, c2);
            } else if (command == 500) {
                int c = Integer.parseInt(st.nextToken());
                printNode(c);
            }

        }
    }

    public static void makeTree(StringTokenizer st) {
        // 부모 노드 저장하기
        for (int i = 1; i <= N; i++) {
            parent[i] = Integer.parseInt(st.nextToken());
        }

        // 권한 부여 하기 (최대 20임을 유의 할 것
        for (int i = 1; i <= N; i++) {
            authority[i] = Integer.parseInt(st.nextToken());
            if (authority[i] > 20) authority[i] = 20;
        }

        // chatRoom, transfer 초기화
        for (int i = 1; i <= N; i++) {
            int curNode = i;
            int curNodePower = authority[i];
            transfer[curNode][curNodePower]++;
            while (parent[curNode] != 0 && curNodePower != 0) {
                curNode = parent[curNode]; // 부모 노드로 갱신
                curNodePower--;
                if (curNodePower != 0) transfer[curNode][curNodePower]++;
                chatRoom[curNode]++;
            }
        }

    }

    public static void alarmToggle(int c) {
        if (mute[c]) {
            // transfer와 chatRoom 갱신
            int curNode = parent[c];
            int idx = 1;

            while (curNode != 0) {

                for (int i = idx; i < 22; i++) { // 해당 노드의 모든 알람을 더할 거임
                    chatRoom[curNode] += transfer[c][i];
                    if (i > idx) transfer[curNode][i - idx] += transfer[c][i];
                }
                if (mute[curNode]) break; // 해당 노드 상위가 뮤트라면 갱신 안해도됨
                curNode = parent[curNode];
                idx++;
            }
            mute[c] = false;
        } else {
            // transfer와 chatRoom 갱신
            int cur = parent[c];
            int idx = 1;
            while (cur != 0) {
                for (int i = idx; i < 22; i++) {
                    chatRoom[cur] -= transfer[c][i];
                    if (i > idx) transfer[cur][i - idx] -= transfer[c][i];
                }
                if (mute[cur]) break;
                cur = parent[cur];
                idx++;
            }
            mute[c] = true;
        }


    }

    public static void changePower(int c, int p) {
        int before = authority[c];
        p = Math.min(p, 20);
        authority[c] = p;

        // 기존 power 다 지우기 지우기
        transfer[c][before]--; 
        if(!mute[c]){ // 알람 off
            int idx = 1;
            int cur = parent[c]; // 부모 노드 번호
            while(cur != 0){ // 루트 노드 까지
                if(before >= idx) chatRoom[cur]--;
                if(before > idx) transfer[cur][before-idx]--;
                
                if(mute[cur]) break;
                cur = parent[cur]; // 부모 노드 갱신 (더 높은걸로)
                idx++; // 인덱스 늘리기
            }
        }
        
        // 이제 새로운 power 까지 다시 채우기
        transfer[c][p]++;
        if(!mute[c]){
            int idx = 1;
            int cur = parent[c];
            while(cur != 0){
                if(p >= idx) chatRoom[cur]++;
                if(p > idx) transfer[cur][p-idx]++;
                
                if(mute[cur]) break;
                cur = parent[cur];
                idx++;
            }
        }




    }

    public static void exchangeParent(int c1, int c2) {
        if (parent[c1] == parent[c2]) return;
        //토글 상태도 가져가야 됨
        boolean beforeToggleC1 = mute[c1];
        boolean beforeToggleC2 = mute[c2];

        //off 하기
        if (!beforeToggleC1) alarmToggle(c1);
        if (!beforeToggleC2) alarmToggle(c2);

        //교환하기
        int temp = parent[c1];
        parent[c1] = parent[c2];
        parent[c2] = temp;

        // on 하기
        if (!beforeToggleC1) alarmToggle(c1);
        if (!beforeToggleC2) alarmToggle(c2);

    }

    public static void printNode(int c) {
        System.out.println(chatRoom[c]);
    }


}