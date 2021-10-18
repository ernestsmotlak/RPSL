package sample;

public class Results {
    int wins1, wins2, ties, player1, player2;

    public Results(int player1, int player2){
        this.wins1 = 0;
        this.wins2 = 0;
        this.ties = 0;
        this.player1 = player1;
        this.player2 = player2;
    }

    public int[] toArray(){
        int[] array = new int[5];
        array[0] = this.wins1;
        array[1] = this.wins2;
        array[2] = this.ties;
        array[3] = this.player1;
        array[4] = this.player2;
        return array;
    }

    public static Results toResults(int[] array){
        Results res = new Results(array[3], array[4]);
        res.wins1 = array[0];
        res.wins2 = array[1];
        res.ties = array[2];
        return res;
    }

}
