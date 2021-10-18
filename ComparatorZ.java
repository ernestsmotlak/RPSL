package sample;

public class ComparatorZ {

    public enum Variable{
        SCISSORS, PAPER, ROCK, LIZARD, SPOCK
    }

    public static int getWinner(Variable pick1, Variable pick2){
        switch (pick1){
            case SCISSORS:
                if(pick2.equals(Variable.PAPER) || pick2.equals(Variable.LIZARD))
                    return 1;
                break;
            case PAPER:
                if(pick2.equals(Variable.ROCK) || pick2.equals(Variable.SPOCK))
                    return 1;
                break;
            case ROCK:
                if(pick2.equals(Variable.LIZARD) || pick2.equals(Variable.SCISSORS))
                    return 1;
                break;
            case LIZARD:
                if(pick2.equals(Variable.SPOCK) || pick2.equals(Variable.PAPER))
                    return 1;
                break;
            case SPOCK:
                if(pick2.equals(Variable.SCISSORS) || pick2.equals(Variable.ROCK))
                    return 1;
                break;
        }
        if(pick1.equals(pick2))
            return 0;

        else
            return 2;
    }

}
