import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Created by matio_000 on 22.11.2016.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Lamacz hasel 1.0");
        System.out.println("P.S nie jest to metoda bruta forsa :P ");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj nr indeksu:\n");
        String indeks = scanner.nextLine();
        System.out.println(generateFirstPart(atoi(indeks)));
    }
    private static String generateFirstPart(int index){
        StringBuilder firstPart = new StringBuilder();
        Stream.iterate(0,n->n+1).limit(26).forEach(n-> firstPart.append((char)((n*7+index)%26+97)));
        return firstPart.toString();
    }
    private static int atoi(String co){
        try{
            return new Integer(co);
        }catch (NumberFormatException e){
            System.err.println("Nie podales liczby!!!!!!\n" +
                    "Będzie RANDOM!");
            return new Random().nextInt();
        }

    }
}
