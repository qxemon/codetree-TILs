import java.util.*;
import java.io.*;

public class Main {

    static int L, Q;
    static List<Question> questions;

    // 필요한것 : 사람관리, 초밥 명령 관리, 입장 시간 관리, 위치관리, 퇴장시간 관리
    static Set<String> people;
    static Map<String, List<Question>> orders;
    static Map<String, Integer> come;
    static Map<String, Integer> out;
    static Map<String, Integer> position;




    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        questions = new ArrayList<>();
        people = new HashSet<>();
        position = new HashMap<>();
        orders = new HashMap<>();
        come = new HashMap<>();
        out = new HashMap<>();


        L = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        for (int q = 1; q <= Q ; q++) {
            int t = -1, x = -1, n = -1;
            String name = "";
            st= new StringTokenizer(br.readLine());
            int req = Integer.parseInt(st.nextToken());
            if(req == 100){
                t = Integer.parseInt(st.nextToken());
                x = Integer.parseInt(st.nextToken());
                name = st.nextToken();
            } else if (req == 200){
                t = Integer.parseInt(st.nextToken());
                x = Integer.parseInt(st.nextToken());
                name = st.nextToken();
                n = Integer.parseInt(st.nextToken());
            } else {
                t = Integer.parseInt(st.nextToken());
            }

            questions.add(new Question(req,t,x,n,name));
            if(req == 100){
                orders.computeIfAbsent(name, k-> new ArrayList<>()).add(new Question(req,t,x,n,name));
            }
            else if(req == 200){
                //손님 입장, people, position, come 추가
                people.add(name);
                position.put(name,x);
                come.put(name,t);
            }
        } // end of input

        // 초밥 먹는 행위 추가
        for(String name : people){
            out.put(name,0); // 나가는 시간 추가 (기본값 0)

            // 나가는 시간 계산
            for(Question q : orders.get(name)){
                int outTime = 0;

                //아직 만날 수 없음
                if(q.t < come.get(name)){
                    // 입장 시간 때의 초밥의 위치
                    int firstPositionOfSushi = (q.x + (come.get(name) - q.t)) % L;

                    //둘이 만나기 위한 추가적인 시간
                    int additionalTime = (position.get(name) - firstPositionOfSushi + L) % L;
                    outTime =  come.get(name) + additionalTime;
                }
                else {
                    int additionalTime = (position.get(name) - q.x + L) % L;
                    outTime = q.t + additionalTime;
                }

                out.put(name, Math.max(out.get(name), outTime));
                //초밥을 먹는 행위의 101번 요청 추가
                questions.add(new Question(101, outTime, -1, -1, name));
            }
        }

        for(String name : people){
            // 초밥집을 나가는 행위인 202 추가
            questions.add(new Question(202, out.get(name),-1,-1,name));
        }

        //정렬 -> 초밥을 먹는 행위를 먼저하고 사진찍는 순으로 정렬
        questions.sort((q1, q2) -> {
            // 시간이 다르면 시간 순
            if(q1.t < q2.t){
                return -1;
            } else if (q1.t > q2.t){
                return 1;
            }

            if(q1.req < q2.req){
                return -1;
            } else if(q1.req > q2.req){
                return 1;
            }

            return 0;
        });

        int countP = 0, countS = 0;
        
        for(int i=0;i<questions.size();i++){
            if(questions.get(i).req == 100){
                countS++;
            } else if(questions.get(i).req == 101){
                countS--;
            } else if(questions.get(i).req == 200){
                countP++;
            } else if(questions.get(i).req == 202){
                countP--;
            }
            else{
                System.out.println(countP+" "+countS);
            }
        }

    }

    static class Question {
        int req, t, x, n;
        String name;

        public Question(int req, int t, int x, int n, String name) {
            this.req = req;
            this.t = t;
            this.x = x;
            this.n = n;
            this.name = name;
        }
    }
}