import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by matio on 21.11.2016.
 */
public class Main {
    public static LocalDateTime t = LocalDateTime.of(2016,11,21,21,0);
    static int[] table = new int[42];
    public static void main(String[] args) {
        init();
        int time = timeGuard();
        if(timeGuard()!=0){
            cheater();
            System.exit(time);
        }
      //  System.exit(1);
    }
    static void init(){
        for (int i = 0; i < 42; i++) {
            table[i]=1;
        }
        int i = generateCode();
        table[i]=0;
    }

    static int timeGuard(){
        if(t.compareTo(LocalDateTime.now())<0)
            return -1;
        t=LocalDateTime.now();
        return 0;
    }
    static void character(){

    }
    static void winner(){
        System.out.println("I'm here to serve you");
    }
    static void loser(){
        System.out.println("Wrong password ");
    }
    static void cheater(){
        System.out.println("times up");
    }
    static int generateCode(){

    }
}
