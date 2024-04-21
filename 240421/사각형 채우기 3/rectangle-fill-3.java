import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine());

        long[] dp = new long[n+1];
        dp[0] = 1;
        dp[1] = 2;
        if(n == 1) {
            System.out.println(2);
            return;
        }

        dp[2] = 7;

        for(int i= 3; i<=n; i++){
            dp[i] = ((dp[i-1] * 3) % 1000000007 + dp[i-2] % 1000000007 - dp[i-3] % 1000000007) % 1000000007;

        }

        System.out.println(dp[n]);


    }
}