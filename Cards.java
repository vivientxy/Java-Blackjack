import constants.Suits;
import constants.Values;

public class Cards {
    private Values values;
    private Suits suits;

    public Cards(Values values, Suits suits) {
        this.values = values;
        this.suits = suits;
    }

    public Cards(Cards source) {
        this.values = source.values;
        this.suits = source.suits;
    }

    public int getValues() {
        switch (this.values) {
            case TWO: return 2;
            case THREE: return 3;
            case FOUR: return 4;
            case FIVE: return 5;
            case SIX: return 6;
            case SEVEN: return 7;
            case EIGHT: return 8;
            case NINE: return 9;
            case TEN: return 10;
            case JACK: return 10;
            case QUEEN: return 10;
            case KING: return 10;
            case ACE: return 1;
            default: return 0;
        }
    }

    public String getCard() {
        return this.values + " OF " + this.suits;
    }

    @Override
    public String toString() {
        return this.values + " OF " + this.suits;
    }
}
