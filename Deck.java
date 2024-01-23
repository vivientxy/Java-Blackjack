import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import constants.Suits;
import constants.Values;

public class Deck {
    private List<Cards> cards = new ArrayList<Cards>();

    public Deck() {
        for (Suits suit : Suits.values()) {
            for (Values value : Values.values()) {
                this.cards.add(new Cards(value, suit));
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.cards);
    }

    public Cards drawCard() {
        Cards drawnCard = this.cards.get(0);
        this.cards.remove(0);
        return drawnCard;
    }

    public void printDeck() {
        for (Cards card : this.cards) {
            System.out.println(card.getCard());
        }
    }

}
