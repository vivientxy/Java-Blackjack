import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        /**
         * GAME SET UP
         */
        System.out.println("\nHi! Welcome to the game of Blackjack.\n");

        // get player's name and wallet amount

        System.out.print("What is your name?: ");
        String name = scan.nextLine();

        System.out.print("How much money do you have?: ");
        boolean valid = false;
        BigDecimal wallet = new BigDecimal(0);
        while (!valid) {
            String strAmount = scan.nextLine();
            if (!isBigDecimal(strAmount)) {
                System.out.print("Please enter a valid number: ");
            } else if (!isPositiveAmount(strToDecimal(strAmount))) {
                System.out.print("Please enter a positive number: ");
            } else {
                wallet = strToDecimal(strAmount);
                valid = true;
            }
        }
        Players player1 = new Players(name, wallet);
        System.out.println("\nWelcome Player " + player1.getName() + "! Wallet currently has: $" + player1.getWallet());

        /**
         * GAME START!!
         */
        boolean gameOn = true;

        while (gameOn) {
            player1.clearHand();

            // ask player how much to bet. check if less than wallet / negative / zero
            
            System.out.print("\nPlease enter the amount you want to bet: ");
            valid = false;
            BigDecimal bet = new BigDecimal(0);
            while (!valid) {
                String strAmount = scan.nextLine();
                if (!isBigDecimal(strAmount)) {
                    System.out.print("Please enter a valid number: ");
                } else if (!isPositiveAmount(strToDecimal(strAmount))) {
                    System.out.print("Please enter a positive number: ");
                } else if (!canAffordBet(strToDecimal(strAmount), player1.getWallet())) {
                    System.out.print("Bet cannot be more than wallet amount! Please enter a new bet: ");
                } else {
                    bet = strToDecimal(strAmount);
                    valid = true;
                }
            }
            player1.betMoney(bet);
            System.out.println("\nPlayer " + player1.getName() + " currently has -- Wallet: $" + player1.getWallet() + ". Bet: $" + player1.getBetAmount());
            System.out.println("-------------------");

            // create dealer and deck

            Players dealer = new Players("Dealer", new BigDecimal(0));
            Deck deck = new Deck();
            deck.shuffleDeck();


            // deal 2 cards each

            dealer.drawCard(deck.drawCard());
            player1.drawCard(deck.drawCard());
            dealer.drawCard(deck.drawCard());
            player1.drawCard(deck.drawCard());

            // show player their hand. ask if they want to draw more cards

            player1.displayHand();
            while (!exceed21(player1)) {
                System.out.print("Do you want to draw a card? Type Y/N: ");
                String choice = scan.nextLine();
                if (!isYesOrNo(choice)) {
                    System.out.println("Please type 'Y' for Yes, 'N' for No.");
                } else if (choice.equalsIgnoreCase("Y")) {
                    player1.drawCard(deck.drawCard());
                    System.out.println("-------------------");
                    player1.displayHand();
                } else if (choice.equalsIgnoreCase("N")) {
                    break;
                }
            }

            // dealer draw until > player or > 21
            while (!exceed21(dealer)) {
                if (exceed21(player1)) {
                    break;
                } else if (dealer.calculateHandValue() <= player1.calculateHandValue()) {
                    dealer.drawCard(deck.drawCard());
                } else {
                    break;
                }
            }

            // compare against dealer. announce win/loss
            System.out.println();
            dealer.displayHand();
            System.out.println("-------------------");

            switch (playerWon(player1, dealer)) {
                case 1:
                    System.out.println("Congrats! " + player1.getName() + " has won $" + player1.getBetAmount());
                    player1.winBet();
                    System.out.println(player1.getName() + " now has -- Wallet: $" + player1.getWallet() + ". Bet: $" + player1.getBetAmount());
                    break;
                case 0:
                    System.out.println("It was a tie!");
                    player1.drawBet();
                    System.out.println(player1.getName() + " now has -- Wallet: $" + player1.getWallet() + ". Bet: $" + player1.getBetAmount());
                    break;
                case -1:
                    System.out.println("Sorry, you lost this time!");
                    player1.loseBet();
                    System.out.println(player1.getName() + " now has -- Wallet: $" + player1.getWallet() + ". Bet: $" + player1.getBetAmount());
                    break;
                default: break;
            }

            // check if wallet has enough money to play
            if (player1.getWallet().compareTo(new BigDecimal(0)) < 1) {
                System.out.println("You do not have enough money to play anymore. Thanks for your business, come back when you've earned some money :)");
                gameOn = false;
                break;
            }

            // ask to play again?
            System.out.print("\nWould you like to play again? Type Y/N: ");
            while (true) {
                String choice = scan.nextLine();
                if (!isYesOrNo(choice)) {
                    System.out.println("Please type 'Y' for Yes, 'N' for No.");
                } else if (choice.equalsIgnoreCase("Y")) {
                    gameOn = true;
                    break;
                } else if (choice.equalsIgnoreCase("N")) {
                    System.out.println("Thank you for playing! Please come back again soon :)");
                    gameOn = false;
                    break;
                }
            }
            
        }
        scan.close();
    }

    // methods to check if input is BigDecimal, >0 and <wallet
    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount.compareTo(new BigDecimal(0.00)) == 1;
    }

    public static boolean canAffordBet(BigDecimal bet, BigDecimal wallet) {
        return bet.compareTo(wallet) < 1;
    }

    public static boolean isBigDecimal(String amount) {
        try {
            BigDecimal.valueOf(Double.parseDouble(amount));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static BigDecimal strToDecimal(String amount) {
        return BigDecimal.valueOf(Double.parseDouble(amount));
    }

    // method to check if player won against dealer
    public static int playerWon(Players player, Players dealer) {
        if (player.calculateHandValue() > 21) {
            return -1;
        } else if (dealer.calculateHandValue() > 21) {
            return 1;
        } else if (player.calculateHandValue() > dealer.calculateHandValue()) {
            return 1;
        } else if (player.calculateHandValue() == dealer.calculateHandValue()) {
            return 0;
        } else if (player.calculateHandValue() < dealer.calculateHandValue()) {
            return -1;
        } else {
            return 100;
        }
    }

    // method to check if player has exceeded 21
    public static boolean exceed21(Players player) {
        return player.calculateHandValue() > 21;
    }

    // method to check if input is Y or N
    public static boolean isYesOrNo(String value) {
        if (value.equalsIgnoreCase("Y") || value.equalsIgnoreCase("N")) {
            return true;
        } else {
            return false;
        }
    }
}