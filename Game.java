package sample;

import java.util.Random;

public class Game implements Runnable{
    int firstPlayer, secondPlayer;
    Random r1, r2;


    public Game(int firstPlayer, int secondPlayer){
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        r1 = new Random();
        r2 = new Random();
    }

    @Override
    public void run() {
        Results res = this.executeGame();
        Manager.resultArray[this.firstPlayer][this.secondPlayer] = res;
        Manager.resultArray[this.secondPlayer][this.firstPlayer] = res;
    }

    public Results executeGame(){
        Results res = new Results(firstPlayer, secondPlayer);

        for (int i = 0; i < Manager.steviloTekemMedIgralci; i++){
            ComparatorZ.Variable v1 = ComparatorZ.Variable.values()[r1.nextInt(5)];
            ComparatorZ.Variable v2 = ComparatorZ.Variable.values()[r2.nextInt(5)];

            int winna = ComparatorZ.getWinner(v1, v2);
            updateScore(res, winna);
            if(Manager.dodatenIzpis){
                izpis(v1, v2, winna);
            }
        }

        if(Manager.dodatenIzpis){
            System.out.println();
        }

        return res;
    }

    public static void updateScore(Results res, int winna){
        switch (winna){
            case 0:
                res.ties++;
                break;
            case 1:
                res.wins1++;
                break;
            case 2:
                res.wins2++;
                break;
        }
    }

    public static void izpis(ComparatorZ.Variable v1, ComparatorZ.Variable v2, int winna){
        switch (winna){
            case 0:
                System.out.printf("%s, %s, neodloceno\n", v1.toString(), v2.toString());
                break;
            case 1:
                System.out.printf("%s, %s, zmaga prvi\n", v1.toString(), v2.toString());
                break;
            case 2:
                System.out.printf("%s, %s, zmaga drugi\n", v1.toString(), v2.toString());
                break;
        }


    }

}
