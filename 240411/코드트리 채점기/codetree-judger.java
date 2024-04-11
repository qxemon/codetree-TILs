import java.io.*;
import java.util.*;

public class Main {

    static int Q, N;
    static int query;

    static PriorityQueue<WaitingQueue> waitingQueue; // waitingQueue
    static Map<String, Integer> waitingQueueList; // waitingQueue리스트 저장할 map (중복 체크)
    static Map<Integer, JudgingQueue> judgingMap;
    static Map<String, History> history; // url 시작 종료시간을 정리할 map
    static boolean[] judger; // 채점기 가능 여부
    static Map<Integer, String> process; // 사용중인 채점기와 채점중인 domain 확인 용

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Q = Integer.parseInt(br.readLine());
        StringTokenizer st;

        waitingQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.priority != o2.priority) {
                return Integer.compare(o1.priority, o2.priority);
            }
            return Integer.compare(o1.inputTime, o2.inputTime);
        });
        waitingQueueList = new HashMap<>();
        judgingMap = new HashMap<>();
        history = new HashMap<>();
        process = new HashMap<>();


        for (int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());
            query = Integer.parseInt(st.nextToken());
            if (query == 100) {
                N = Integer.parseInt(st.nextToken());
                judger = new boolean[N + 1];
                String url = st.nextToken();
                ready(url);
            } else if (query == 200) {
                int time = Integer.parseInt(st.nextToken());
                int priority = Integer.parseInt(st.nextToken());
                String url = st.nextToken();

                request(time, priority, url);
            } else if (query == 300) {
                int time = Integer.parseInt(st.nextToken());

                gradingTry(time);
            } else if (query == 400) {
                int time = Integer.parseInt(st.nextToken());
                int idx = Integer.parseInt(st.nextToken());
                end(time, idx);

            } else if (query == 500) {
                print();
            }
        }
    }

    //1. 코드트리 채점기 준비
    // 100 N u0 형태
    // N개의 채점기, 초기 url u0
    public static void ready(String url) {
        StringTokenizer st = new StringTokenizer(url, "/");
        String domain = st.nextToken();

        waitingQueue.add(new WaitingQueue(0, 1, url));
        waitingQueueList.put(url, 1);

//        System.out.println("1: pq size " + pq.size());

    }

    // 2. 코드트리 채점기 요청
    // 200 t p u
    // t초에 우선순위 p 인 url u 문제에 대한 채점 요청 들어옴
    public static void request(int time, int priority, String url) {
        // url 완전 동일한 작업 중복 제거해야함
        StringTokenizer st = new StringTokenizer(url, "/");
        String domain = st.nextToken();

        if (waitingQueueList.getOrDefault(url, 0) == 0) {

            waitingQueue.add(new WaitingQueue(time, priority, url)); // pq
            waitingQueueList.put(url, 1); // 동일 url 체크할 map
        }
    }

    //3. 채점 시도
    // 300 t
    // t초에 채점 대기 큐에서 채점이 가능한 경우 우선순위가 가장 높은 채점 task 골라 진행
    public static void gradingTry(int t) {
        if (waitingQueue.peek() == null) return;

        for (int i = 1; i < judger.length; i++) {
            if (!judger[i]) {


                WaitingQueue wq = waitingQueue.peek();

//                System.out.println(wq.url);
                StringTokenizer st = new StringTokenizer(wq.url, "/");
                String domain = st.nextToken();


                //이전 사용 기록이 있다면 적절성 여부 파악해야하므로
                if (history.get(domain) != null) {
                    History h = history.get(domain);
//                		System.out.println(h.start+" "+h.end);
                    if (t > h.start + (3 * (h.end - h.start))) {
                        // 진행 큐로 옮기기
                        wq = waitingQueue.poll(); // 큐에서 진짜 뺌
                        judgingMap.put(i, new JudgingQueue(t, wq.url, i));
                        process.put(i, wq.url);
                        judger[i] = true;
                        waitingQueueList.remove(wq.url);
                    }

                    break;

                } else if (history.get(domain) == null) {

                    wq = waitingQueue.poll();
                    judgingMap.put(i, new JudgingQueue(t, wq.url, i));
                    judger[i] = true;
                    waitingQueueList.remove(wq.url);
                    break;
                }


            }
        }

    }

    //4. 채점 종료
    // 400 t j_id
    // t초에 j_id번 채점이 종료
    public static void end(int t, int idx) {
        // idx 에 있는 domain 찾고 endTime 갱신하기
        if(judgingMap.get(idx) != null){
            JudgingQueue jq = judgingMap.get(idx);
            String url = jq.url;
            String[] domain = url.split("/");
            int start = jq.start;

            history.put(domain[0], new History(start, t, url, idx));
            judger[idx] = false;
        }

    }

    //5. 채점 대기 큐 조회
    // 500 t
    // t에 채점 대기 큐에 있는 채점 task 수 출력
    public static void print() {
        System.out.println(waitingQueue.size());
    }

    static class JudgingQueue {
        int start;
        String url;
        int jId;

        public JudgingQueue(int start, String url, int jId) {
            this.start = start;
            this.url = url;
            this.jId = jId;
        }
    }

    static class History {
        int start;
        int end;
        String url;
        int jId;

        public History(int start, int end, String url, int jId) {
            this.start = start;
            this.end = end;
            this.url = url;
            this.jId = jId;
        }
    }


    static class WaitingQueue implements Comparator<WaitingQueue> {
        int inputTime;
        int priority;
        String url;

        public WaitingQueue(int inputTime, int priority, String url) {
            this.inputTime = inputTime;
            this.priority = priority;
            this.url = url;
        }

        // 우선순위 오름차순, 그 다음 시간
        @Override
        public int compare(WaitingQueue o1, WaitingQueue o2) {
            if (o1.priority != o2.priority) {
                return Integer.compare(o1.priority, o2.priority);
            }
            return Integer.compare(o1.inputTime, o2.inputTime);
        }
    }


}