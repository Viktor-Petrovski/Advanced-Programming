import java.util.Random;
import java.util.Scanner;

public class LoadedCoinTest {

    static int heads(Coin c) {
        int heads = 0;
        for(int i = 0; i < 1000; i++) {
            SIDE side = c.flip();
            if(side == SIDE.HEAD)
                heads++;
        }
        return heads;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int probability = scanner.nextInt();

        int heads = heads(new Coin());
        System.out.println(heads > 450 && heads < 550 ? "YES" : "NO");

        heads = heads(new LoadedCoin(probability));
        System.out.println(heads > probability * 10 - 50 && heads < probability * 10 + 50 ? "YES" : "NO");
    }
    enum SIDE {
        HEAD, TAIL
    }
    static class Coin {

        public SIDE flip() {
            Random random = new Random();
            boolean isHead = random.nextBoolean();
            return isHead ? SIDE.HEAD : SIDE.TAIL;
        }
    }

    static class LoadedCoin extends Coin {
        private final int probability;

        public LoadedCoin(int probability) {
            this.probability = probability;
        }

        @Override
        public SIDE flip() {
            Random random = new Random();
            int flip = random.nextInt(100);
            return flip <= probability ? SIDE.HEAD : SIDE.TAIL;
        }
    }
}

