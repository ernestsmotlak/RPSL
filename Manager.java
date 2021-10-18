package sample;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import mpi.*;

public class Manager {


    public static boolean dodatenIzpis = false;
    public static int steviloIgralcev = 1000, steviloTekemMedIgralci = 1000;
    public static ExecType execType = ExecType.SEQUENTIAL;
    public static Results[][] resultArray;
    public static int[] numOfWins;
    static int me = 0, size = 0;
    public static long startTime, stopTime;

    public enum ExecType {
        SEQUENTIAL, PARALLEL, DISTRIBUTED
    }


    public static void init(){
        resultArray = new Results[steviloIgralcev][steviloIgralcev];
        numOfWins = new int[steviloIgralcev];
    }

    public static void exec(){
        switch (execType){
            case SEQUENTIAL:
                sequential();
                break;
            case PARALLEL:
                parallel();
                break;
            case DISTRIBUTED:
                distributed();
                break;
            default:
                System.out.println("Error");
        }
    }

    public static void sequential(){
        for(int i = 0; i < steviloIgralcev; i++){
            for(int j = i + 1; j < steviloIgralcev; j++){
                Game g = new Game(i, j);
                Results res = g.executeGame();

                Manager.resultArray[g.firstPlayer][g.secondPlayer] = res;
                Manager.resultArray[g.secondPlayer][g.firstPlayer] = res;
            }
        }
    }

    public static void parallel(){
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(cores);
        for(int i = 0; i < steviloIgralcev; i++){
            for(int j = i + 1; j < steviloIgralcev; j++){
                Game g = new Game(i, j);
                executorService.execute(g);
            }
        }
        executorService.shutdown();
        while (!executorService.isTerminated());
    }

    public static void distributed(){

        if(me == 0){
            farmer(size-1);
        }
        else{
            delavec();
        }
    }

    public static void evaluate(){
        for(int i = 0; i < steviloIgralcev; i++){
            for(int j = i + 1; j < steviloIgralcev; j++){
                Results res = resultArray[i][j];
                if (res.wins1 > res.wins2){
                    numOfWins[res.player1]++;
                }
                else if(res.wins1 < res.wins2){
                    numOfWins[res.player2]++;
                }
            }
        }
    }

    public static int findWinner(){
        int max = 0;
        int maxIndex = 0;
        int stEnakih = 1;
        for(int i = 0; i < steviloIgralcev; i++){
            if(max == numOfWins[i])
                stEnakih++;
            if(numOfWins[i] > max){
                max = numOfWins[i];
                maxIndex = i;
                stEnakih = 1;
            }
        }
        if(stEnakih > 1)
            return -1;
        return maxIndex;
    }

    public static String izpisZmagovalca(int winner){
        String s = "";
        if(winner == -1){
            s += "Neodloceno\n";
            System.out.println("Neodloceno");
        }
        else {
            s += "Zmagovalec je igralec: " + (winner + 1) + " z " + numOfWins[winner] + " zmagami.\n";
            System.out.println("Zmagovalec je igralec: " + (winner + 1) + " z " + numOfWins[winner] + " zmagami.");
        }
        s += (stopTime - startTime) + "ms";
        System.out.println(stopTime - startTime);
        return s;
    }

    public static void farmer(int workers){
        int player12[] = {0,1};
        int game = 0, steviloTekm = binomialCoeff(steviloIgralcev, 2);
        int[] res = new int[5];
        boolean konec = false;

        for(; game < workers; game++){

            MPI.COMM_WORLD.Send(player12, 0, 2, MPI.INT, game+1, game);
            naslednjiDvoboj(player12);
        }

        while(game < steviloTekm){

            mpi.Status status = MPI.COMM_WORLD.Recv(res, 0, 5, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
            Results r = Results.toResults(res);
            resultArray[r.player1][r.player2] = r; resultArray[r.player2][r.player1] = r;
            MPI.COMM_WORLD.Send(player12, 0, 2, MPI.INT, status.source, game++);
            naslednjiDvoboj(player12);
        }

        for(int i = 0; i < workers; i++){

            mpi.Status status = MPI.COMM_WORLD.Recv(res, 0, 5, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
            Results r = Results.toResults(res);
            resultArray[r.player1][r.player2] = r; resultArray[r.player2][r.player1] = r;
            MPI.COMM_WORLD.Send(player12, 0, 2, MPI.INT, status.source, -1);
        }
    }

    public static void delavec(){
        int player12[] = {0,0};
        mpi.Status status = new mpi.Status();
        MPI.COMM_WORLD.Recv(player12, 0, 2, MPI.INT, 0, MPI.ANY_TAG);

        while (status.tag != -1){
            Game g = new Game(player12[0], player12[1]);
            Results res = g.executeGame();
            MPI.COMM_WORLD.Send(res.toArray(), 0, 5, MPI.INT, 0, 0);
            status = MPI.COMM_WORLD.Recv(player12, 0, 2, MPI.INT, 0, MPI.ANY_TAG);

        }
        Manager.delavec();
    }

    public static boolean naslednjiDvoboj(int[] player12){
        if(player12[1] == steviloIgralcev-1){
            if(player12[0] == steviloIgralcev-2)
                return true;
            else{
                player12[0]++;
                player12[1] = player12[0] + 1;
            }
        }
        else{
            player12[1]++;
        }
        return false;
    }

    static int binomialCoeff(int n, int k)
    {
        int res = 1;

        if (k > n - k)
            k = n - k;

        for (int i = 0; i < k; ++i) {
            res *= (n - i);
            res /= (i + 1);
        }

        return res;
    }

}
