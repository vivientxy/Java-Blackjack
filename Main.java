import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        /**
         * GAME SET UP
         */
        System.out.println("\nHi! Welcome to the game of Blackjack.\n");

        // get number of players (max 7)

        System.out.print("How many players are there?: ");
        int numOfPlayers = 0;
        while (true) {
            String numOfPlayersStr = scan.nextLine();
            if (!isBigDecimal(numOfPlayersStr)) {
                System.out.print("Please enter a valid number: ");
            } else if (!isPositiveAmount(strToDecimal(numOfPlayersStr))) {
                System.out.print("Please enter a positive number: ");
            } else if (Integer.valueOf(numOfPlayersStr) > 7) {
                System.out.print("Sorry, max number of players is 7. Please enter a new number: ");
            } else {
                numOfPlayers = Integer.valueOf(numOfPlayersStr);
                break;
            }
        }

        // create a list of player objects, with size = numOfPlayers

        List<Players> players = new ArrayList<Players>();

        // get players' name and wallet amount

        System.out.println();
        for (int i = 0; i < numOfPlayers; i++) {
            int playerNum = i+1;
            System.out.print("Player " + playerNum + ", what is your name?: ");
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
            Players player = new Players(name, wallet);
            players.add(player);
        }

        // print out all the players and their names + wallet

        System.out.println("\nWelcome the following players!:");
        for (Players player : players) {
            System.out.println("\t-- " + player.getName() + ", with $" + player.getWallet());
        }

        /**
         * GAME START!!
         */
        boolean gameOn = true;

        while (gameOn) {

            // ask each player how much to bet. check if less than wallet / negative / zero
            
            for (int i = 0; i < players.size(); i++) {
                System.out.print("\n" + players.get(i).getName() + ", you have $" + players.get(i).getWallet() + ". Please enter the amount you want to bet: ");

                // check validity of input:
                boolean valid = false;
                BigDecimal bet = new BigDecimal(0);
                while (!valid) {
                    String strAmount = scan.nextLine();
                    if (!isBigDecimal(strAmount)) {
                        System.out.print("Please enter a valid number: ");
                    } else if (!isPositiveAmount(strToDecimal(strAmount))) {
                        System.out.print("Please enter a positive number: ");
                    } else if (!canAffordBet(strToDecimal(strAmount), players.get(i).getWallet())) {
                        System.out.print("Bet cannot be more than wallet amount! Please enter a new bet: ");
                    } else {
                        bet = strToDecimal(strAmount);
                        valid = true;
                    }
                }
                players.get(i).betMoney(bet);
                System.out.println(players.get(i).getName() + " currently has -- Wallet: $" + players.get(i).getWallet() + ". Bet: $" + players.get(i).getBetAmount());
            }

            // create dealer and deck

            Players dealer = new Players("Dealer", new BigDecimal(0));
            Deck deck = new Deck();
            deck.shuffleDeck();


            // deal 2 cards to each player + dealer

            for (Players player : players) {
                player.drawCard(deck.drawCard());
                player.drawCard(deck.drawCard());
            }
            dealer.drawCard(deck.drawCard());
            dealer.drawCard(deck.drawCard());

            // show each player their hand. ask if they want to draw more cards

            for (Players player : players) {
                System.out.println("-------------------");
                System.out.println(player.getName());
                System.out.println("-------------------");
                player.displayHand();

                while (!exceed21(player)) {
                    System.out.print("Do you want to draw a card? Type Y/N: ");
                    String choice = scan.nextLine();
                    if (!isYesOrNo(choice)) {
                        System.out.println("Please type 'Y' for Yes, 'N' for No.");
                    } else if (choice.equalsIgnoreCase("Y")) {
                        player.drawCard(deck.drawCard());
                        System.out.println("-------------------");
                        player.displayHand();
                    } else if (choice.equalsIgnoreCase("N")) {
                        break;
                    }
                }
            }

            // dealer draw until > 16 -- ARBITRARY LINE, but most real life games have 16 as a minimum hand
            while (!exceed21(dealer)) {
                if (dealer.calculateHandValue() < 16) {
                    dealer.drawCard(deck.drawCard());
                } else {
                    break;
                }
            }

            // show dealer's hand

            System.out.println("-------------------");
            System.out.println("Dealer's Hand:");
            System.out.println("-------------------");
            dealer.displayHand();
            System.out.println();
            System.out.println("-------------------");

            // compare each player against dealer. announce win/loss
            // clear all player hands

            for (Players player : players) {
                BigDecimal bet = player.getBetAmount();
                switch (playerWon(player, dealer)) {
                    case 1:
                        player.winBet();
                        System.out.println(player.getName() + " - Congrats! " + player.getName() + " has won $" + bet + ". Wallet now has $" + player.getWallet());
                        player.clearHand();
                        break;
                    case 0:
                        player.drawBet();
                        System.out.println(player.getName() + " - It was a tie! " + player.getName() + "'s wallet now has $" + player.getWallet());
                        player.clearHand();
                        break;
                    case -1:
                        player.loseBet();
                        System.out.println(player.getName() + " - Sorry! " + player.getName() + " has lost $" + bet + ". Wallet now has $" + player.getWallet());
                        player.clearHand();
                        break;
                    default: break;
                }
            }

            System.out.println();

            // check if any player is bankrupt and kick them out of the game

            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getWallet().compareTo(new BigDecimal(0)) < 1) {
                    System.out.println(players.get(i).getName() + ", you do not have enough money to play anymore. Thanks for your business, come back when you've earned some money :)");
                    players.remove(i);
                    i--;
                }
            }

            // check if there are any players remaining

            if (players.isEmpty()) {
                System.out.println("\nThank you everyone for playing! Please come back again soon :)");
                gameOn = false;
            }

            // ask to play again?


            while (!players.isEmpty()) {
                System.out.print("\nWould everyone like to play again? Type Y/N: ");
                String choice = scan.nextLine();
                if (!isYesOrNo(choice)) {
                    System.out.print("Please type 'Y' for Yes, 'N' for No.");
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