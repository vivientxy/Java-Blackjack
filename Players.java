import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Players {
    private String name;
    private List<Cards> hand;
    private BigDecimal wallet;
    private BigDecimal betAmount;


    public Players(String name, BigDecimal wallet) {
        this.name = name;
        this.wallet = wallet;
        this.betAmount = new BigDecimal(0.00);
        this.hand = new ArrayList<Cards>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Cards> getHand() {
        return this.hand;
    }

    public BigDecimal getWallet() {
        return this.wallet;
    }

    public BigDecimal getBetAmount() {
        return this.betAmount;
    }

    public int calculateHandValue() {
        int handValue = 0;
        int aceCount = 0;
        for (Cards card : this.hand) {
            if (card.getValues() == 1) {    // IF CARD IS ACE
                aceCount += 1;
                handValue += card.getValues();
            } else {
                handValue += card.getValues();
            }
        }

        if (this.hand.size() == 2 && aceCount == 2) {
            handValue = 21;         // BAN BAN - STRAIGHT WIN!
        } else if (this.hand.size() == 2 && aceCount == 1) {
            handValue += 10;        // 2 CARDS IN HAND: ACE IS COUNTED AS 11
        } else if (this.hand.size() > 2 && aceCount > 0 && handValue <= 12) {
            handValue += 9;         // >2 CARDS IN HAND WITH TOTAL VALUE <= 12: ACE IS COUNTED AS 10
        }
        return handValue;
    }

    public int countAces() {
        int numOfAces = 0;
        for (Cards card : this.hand) {
            if (card.getValues() == 1) {
                numOfAces += 1;
            }
        }
        return numOfAces;
    }

    public void displayHand() {
        System.out.println(this.name + "'s hand has:");
        for (Cards card : this.hand) {
            System.out.println("\t" + card.getCard());
        }
        System.out.println("Total hand value is: " + calculateHandValue());
    }

    public void drawCard(Cards card) {
        this.hand.add(new Cards(card));
    }

    public void clearHand() {
        this.hand = new ArrayList<Cards>();
    }

    public void betMoney(BigDecimal amount) {
        this.wallet = this.wallet.subtract(amount);
        this.betAmount = this.betAmount.add(amount);
    }

    public void winBet() {
        this.wallet = this.wallet.add(this.betAmount).add(this.betAmount);
        this.betAmount = new BigDecimal(0.00);
    }

    public void loseBet() {
        this.betAmount = new BigDecimal(0.00);
    }

    public void drawBet() {
        this.wallet = this.wallet.add(this.betAmount);
        this.betAmount = new BigDecimal(0.00);
    }

}