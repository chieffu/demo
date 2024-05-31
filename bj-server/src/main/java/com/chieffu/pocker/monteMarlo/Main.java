package com.chieffu.pocker.monteMarlo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Main Driver Class
 */
public class Main{
    private static int bet = 0;
    private int sideBet = 0;

    public final static int AMOUNTOFDECKS = 8;
    private static int delay = 2000;
    // dealer and human player inclusive
    private final int MAXPLAYERS = 4;
    private Scanner sc;

 
    /**
     * Main class constructor
     */
    public Main(){
        sc = new Scanner(System.in);
    }
    /**
     * main method
     * @param args type String[]
     */
    public static void main(String[] args){
        Main main = new Main();
        main.start();
    }

    
    /**
     * Create a deck, human player, dealer and add them to the players list
     * starts the main menu
     */
    public void start(){
        Bank bank = new Bank();
        Deck deck = new Deck(AMOUNTOFDECKS);
        List<AbstractPlayer> players = new ArrayList<>();
        Human human = new Human(deck);
        Dealer dealer = new Dealer(deck);
        MonteCarloPlayer monteCarloPlayer = new MonteCarloPlayer(deck);

        players.add(monteCarloPlayer);
        players.add(dealer);
        welcome();
        String selection;
        do{
            System.out.println();
            printPlayers(players);
            if(bet != 0){
                if(bank.getDifference() > -1) System.out.println("+" + bank.getDifference());
                else System.out.println(bank.getDifference());
            }
            bank.resetDifference();
            bet = 0;
            sideBet = 0;
            System.out.println("Total chips = " + bank.getChips());

            System.out.println();
            System.out.printf("%-20s %s \n","Play hand:", "[0]");
            System.out.printf("%-20s %s \n","Cash out:", "[1]");
            System.out.printf("%-20s %s \n","Add chips:", "[2]");
            System.out.printf("%-20s %s \n","Add player:", "[3]");
            System.out.printf("%-20s %s \n","Remove player:", "[4]");
            System.out.printf("%-20s %s \n","Generate Hard Table:", "[5]");
            System.out.printf("%-20s %s \n","Generate Soft Table:", "[6]");
            
            selection = "0";// sc.next();
            switch(selection){
                case "0":
                    if(addBet(bank)) gameLogic(deck, players, bank);
                    break;
                case "1":
                    System.out.println("You have left with " + bank.getChips() + " chips.");
                    return;
                case "2":
                    bet = addChips(bank);
                    break;
                case "3":
                    if(players.size() < MAXPLAYERS) addPlayer(pickPlayerToAdd(deck), players);
                    else System.out.println("Sorry the maximum amount of people are at this table.");
                    break; 
                case "4":
                    if(players.size() > MAXPLAYERS - 2) removePlayer(pickPlayerToRemove(players), players);
                    else System.out.println("Sorry you cannot remove the dealer or human players.");
                    break;
                case "5":
                    System.out.println("Generating...");
                    MonteCarlo tableGeneration = new MonteCarlo();
                    tableGeneration.generateMonteCarloTableHardTotal();
                    break;
                case "6":
                    System.out.println("Generating...");
                    tableGeneration = new MonteCarlo();
                    tableGeneration.generateMonteCarloTableSoftTotal();
                    break;
                default:
                    System.out.println("Please enter a valid option");
            }

        } while(selection != "1");

        sc.close();
    }

    
    /**
     * Adds a bet for the current round
     * 
     * @param bank type Bank
     * @return boolean
     */
    public boolean addBet(Bank bank){
        if(!bank.hasTableMinium(bank.getChips())){
            System.out.println("You do not have enough chips to play the table minimum.");
            System.out.println("Please add more chips through the bank.");
            bet = 0;
            return false;
        }
        do{
            System.out.println("How many chips would you like to bet?");
            System.out.println("The table minimum is " + bank.getTableMinimum() + " chips");
//            while(!sc.hasNextInt()) {
//                System.out.println("Please enter a valid option");
//                sc.next();
//            }
            bet = 2000;//sc.nextInt();
            if(bet < bank.getTableMinimum()){
                System.out.println("You need to play the table minimum which is " + bank.getTableMinimum() + ".");
            }
            else if(bet > bank.getChips()){
                System.out.println("You do not have that many chips. Please place a smaller bet.");
            }
        } while(bet > bank.getChips() || bet < bank.getTableMinimum());

        return (bet <= bank.getChips() && bet >= bank.getTableMinimum()) ? true : false;
    }

    
    /**
     * Flow of the blackjack game
     * 
     * @param deck type Deck
     * @param players type List<AbstractPlayer>
     * @param bank type Bank
     */
    public void gameLogic(Deck deck, List<AbstractPlayer> players, Bank bank){
        System.out.println();
        System.out.println("----------------------------------------------");
        delay(delay);
        clearHands(players);
        if(deck.hasCutCardBeenHit()){
            System.out.println("The cut card has been hit!");
            System.out.println("Reshuffling the deck!");
            deck.resetCards();
            delay(delay);
        }
        dealCards(players);
        winCondition(players, bank);
    }

    
    /**
     * Adds chips to bank class 
     * 
     * @param bank type Bank
     * @return type int
     */
    public int addChips(Bank bank){
        System.out.println("How many chips do you want to add?");
        while(!sc.hasNextInt()) {
            System.out.println("Please enter a valid option");
            sc.next();
        }
        int addChips = sc.nextInt();
        bank.addChips(addChips);
        return addChips;
    }

    /**
     * Welcome message
     */
    public void welcome(){
        System.out.println("----------------------------------------------");
        System.out.println("Welcome to blackjack!");
        System.out.println("How do you play?");
        System.out.println("\"You play against the dealer. You're given two cards.");
        System.out.println("Face cards are worth 10 points. The closest to 21 wins. If you go over, you lose.");
        System.out.println("If the dealer goes over, they lose. It's the most popular card game in the world...\"");
        System.out.println("- 21 (2008)");
        System.out.println();
        System.out.println("This game uses " + AMOUNTOFDECKS + " standard 52-card decks, which are shuffled at the beginning");
        System.out.println("If a cut card is hit the current hand will finish but the deck will be shuffled");
        System.out.println("after the hand is over.");
        System.out.println("Being dealt blackjack pays 6:5");
        System.out.println("Insurance pays 2:1");
        System.out.println("----------------------------------------------");
    }

    
    /**
     * Deals two cards to each player
     * 
     * @param players type List<AbstractPlayer>
     */
    public void dealCards(List<AbstractPlayer> players){
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < players.size(); j++){
                players.get(j).takeCard(0);
            }
        }
        for(int i = 0; i < players.size() - 1; i++){
            System.out.println("-------");
            System.out.println(players.get(i).getName() + "'s hand:");
            players.get(i).printHand(0);
            System.out.println("-------");
            if(i != players.size() - 1){
                System.out.println(players.get(i).getName() + "'s total is " + players.get(i).currentTotal(0));
            }
        }
        System.out.println("-------");
        System.out.println(players.get(players.size()-1).getName() + "'s hand:");
        players.get(players.size()-1).printDealerHand(0);
    }

    
    /**
     * Win conditions, and actions that can be taken
     * 
     * @param players type List<AbstractPlayer>
     * @param bank type Bank
     */
    public void winCondition(List<AbstractPlayer> players, Bank bank){
        // hashmap to see what players are still playing the current hand
        // value stores what hand(s) of their's are still playing
        HashMap<AbstractPlayer, ArrayList<Integer>> map = new HashMap<>();
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).currentTotal(0) != 21){
                map.put(players.get(i), new ArrayList<>());
            }
        }
        // loop through the players that are playing the hand except for the dealer
        for(int i = 0; i < players.size() - 1; i++){
            // ask the human player for insurance if the dealer an ace face up, the human does not, and they have at least half of their bet in their bank
            if(players.get(players.size() - 1).firstCardTotal(0) == 11 && (bank.getChips() - bet) >= (bet / 2) && players.get(0).currentTotal(0) != 21 && players.get(i) instanceof Human){
                System.out.println("The dealer's facecard is an Ace, would you like to buy insurance?");
                System.out.println("Insurance pays 2:1 odds");
                System.out.println("You will pay half of your bet which is " + bet / 2);
                System.out.printf("%-10s %s \n","No:", "[0]");
                System.out.printf("%-10s %s \n","Yes:", "[1]");
                while(!sc.hasNextInt()) {
                    System.out.println("Please enter a valid option");
                    sc.next();
                }
                int response = sc.nextInt();
                // if they buy insurance
                if(response == 1){
                    // if the dealer gets a 10 then this pays 2 to 1 odds
                    sideBet = bet / 2;
                    if(players.get(players.size() - 1).currentTotal(0) == 21){
                        System.out.println("The dealer had been dealt blackjack");
                        System.out.println("You get your insurance back!");
                        bank.addChips(sideBet);
                    }
                    else{
                        System.out.println("The dealer does not have blackjack");
                        System.out.println("You have lost your insurance bet!");
                        bank.subtractChips(sideBet);
                    }
                }
            }
            // dealer has blackjack
            if(players.get(players.size() - 1).currentTotal(0) == 21){
                // push, dealer and player have blackjack
                if(players.get(i).currentTotal(0) == 21){
                    System.out.println(players.get(i).getName() + " and the dealer both have blackjack, it's a push!");
                    players.get(i).addToTies();
                    players.get(players.size() - 1).addToTies();
                }
                // player does not have blackjack, loss
                else{
                    System.out.println("The dealer has blackjack, " + players.get(i).getName() + " lost!");
                    players.get(i).addToLosses();
                    players.get(players.size() - 1).addToWins();
                    if(players.get(i) instanceof Human){
                        bank.subtractChips(bet);
                    }
                }
            }
            // dealer does not have blackjack and player does
            else if(players.get(i).currentTotal(0) == 21){
                System.out.println(players.get(i).getName() + " has blackjack, win!");
                players.get(i).addToWins();
                players.get(players.size() - 1).addToLosses();
                if(players.get(i) instanceof Human){
                    // being dealt blackjack pays out 6:5
                    // (6/5) == 1.2
                    bet *= 1.2;
                    bank.addChips(bet);
                }
            }
        }

        // if the dealer did not get blackjack then play the hand
        // make sure dealer is not in the hashmap
        boolean flag = false;
        if(map.containsKey(players.get(players.size()-1))){
            // loop through the players that do not have 21
            for(int i = 0; i < players.size() - 1; i++){
                if(map.containsKey(players.get(i))){
                    for(int j = 0; j < players.get(i).amountOfHands; j++){
                        // play their hand
                        players.get(i).playHand(players.get(players.size() - 1).firstCardTotal(0), j);
                        // check if they did not go over 21
                        if(players.get(i).currentTotal(j) < 22){
                            flag = true;
                            ArrayList<Integer> list = map.get(players.get(i));
                            list.add(j);
                        }
                        // if they did they lost
                        else{
                            if(players.get(i).amountOfHands > 1) System.out.println(players.get(i).getName() + " hand "+ (j+1) + " has busted with " + players.get(i).currentTotal(j));
                            else System.out.println(players.get(i).getName() + " has busted with " + players.get(i).currentTotal(j));
                            players.get(i).addToLosses();
                            if(players.get(i) instanceof Human){
                                bank.subtractChips(bet);
                            }
                        }
                        delay(delay);
                    }
                }
            }
            // check if there is at least 1 player who is still in play and stopped before blackjack
            // if so we are going to get the dealer to the 17 min
            if(flag){
                limbo(players, bank, map);
            }
        }
    }

    /**
     * If there are player(s) that are still in the game and did not get blackjack,
     * the player will play their hand and then the dealer will get to the 17 min
     * 
     * @param players 
     * @param bank
     * @param map
     */
    public void limbo(List<AbstractPlayer> players, Bank bank, HashMap<AbstractPlayer, ArrayList<Integer>> map){
        // play the dealer hand
        players.get(players.size() - 1).playHand(players.get(players.size() - 1).firstCardTotal(0), 0);
        // loop through those players that are in "limbo"
        for(int i = 0; i < players.size() - 1; i++){
            // checks the win conditions 
            if(map.containsKey(players.get(i)) && map.getOrDefault(players.get(i), new ArrayList<>()).size() != 0){
                ArrayList<Integer> list = map.get(players.get(i));
                for(int j = 0; j < list.size(); j++){
                    if(players.get(i).currentTotal(list.get(j)) > players.get(players.size() - 1).currentTotal(0) || players.get(players.size() - 1).currentTotal(0) > 21){
                        if(players.get(i).amountOfHands > 1) System.out.println(players.get(i).getName() + " hand " + (list.get(j)+1) + " has beat the dealer.");
                        else System.out.println(players.get(i).getName() + " has beat the dealer.");
                        players.get(i).addToWins();
                        players.get(players.size() - 1).addToLosses();
                        if(players.get(i) instanceof Human){
                            bank.addChips(bet);
                        }
                    }
                    else if(players.get(i).currentTotal(list.get(j)) < players.get(players.size() - 1).currentTotal(0)){
                        if(players.get(i).amountOfHands > 1) System.out.println("The dealer has beat " + players.get(i).getName() + " hand " + (list.get(j)+1) +".");
                        else System.out.println("The dealer has beat " + players.get(i).getName() + ".");
                        players.get(i).addToLosses();
                        players.get(players.size() - 1).addToWins();
                        if(players.get(i) instanceof Human){
                            bank.subtractChips(bet);
                        }
                    }
                    else if(players.get(i).currentTotal(list.get(j)) == players.get(players.size() - 1).currentTotal(0)){
                        if(players.get(i).amountOfHands > 1) System.out.println(players.get(i).getName() + " hand " + (list.get(j)+1) + " and the dealer both have " + players.get(i).currentTotal(list.get(j)) + ", its a push.");
                        else System.out.println(players.get(i).getName() + " and the dealer both have " + players.get(i).currentTotal(list.get(j)) + ", its a push.");
                        players.get(i).addToTies();
                        players.get(players.size() - 1).addToTies();
                    }
                }
            }
        }
    }

    
    /**
     * Clears the hands for all players
     * 
     * @param players type List<AbstractPlayer>
     */
    public void clearHands(List<AbstractPlayer> players){
        for(int i = 0; i < players.size(); i++){
            for(int j = 0; j < players.get(i).amountOfHands; j++){
                players.get(i).clearHand(j);
            }
        }
        for(int i = 0; i < players.size(); i++){
            players.get(i).removeAllHandsButOne();
        }
    }

    
    /**
     * Delay so the output is not all at once
     * @param delay type int
     */
    public void delay(int delay){
//        try{Thread.sleep(delay);}
//        catch (Exception e) {System.out.println(e);}
    }

    /**
     * Adds a player to the players list
     * Ensuring that the dealer is always last
     * This makes dealing cards easier, dealer alwasys goes last
     * 
     * @param player type AbstractPlayer
     * @param players type List<AbstractPlayer>
     */
    public void addPlayer(AbstractPlayer player, List<AbstractPlayer> players){
        if(player == null) return;
        Dealer tempDealer = (Dealer) players.remove(players.size()-1);
        players.add(player);
        players.add(tempDealer);
    }

    /**
     * Adds selected player to the players list
     * 
     * @param deck type Deck
     * @return type AbstractPlayer
     */
    public AbstractPlayer pickPlayerToAdd(Deck deck){
        System.out.println("Pick a Player to add");
        System.out.println();
        System.out.printf("%-20s %s \n","Random Player:", "[0]");
        System.out.printf("%-20s %s \n","Monte Carlo Player:", "[1]");
        System.out.printf("%-20s %s \n","Book Player:", "[2]");
        String selection;
        do{
            selection = sc.next();
            switch(selection){
                case "0":
                    RandomPlayer randomPlayer = new RandomPlayer(deck);
                    return randomPlayer;
                case "1":
                    MonteCarloPlayer monteCarloPlayer = new MonteCarloPlayer(deck);
                    return monteCarloPlayer;
                case "2":
                    BookPlayer bookPlayer = new BookPlayer(deck);
                    return bookPlayer;
                default:
                    System.out.println("Please enter a valid option");
            }
        } while(true); 
    }

    
    /**
     * Prints out all of the current players at the table
     * 
     * @param players type List<AbstractPlayer>
     */
    public void printPlayers(List<AbstractPlayer> players){
        System.out.print("The players at the table are: (" + players.get(0).getName());
        for(int i = 1; i < players.size(); i++){
            System.out.print(", " + players.get(i).getName());
        }
        System.out.println(")");
        System.out.println();
    }

    

    /**
     * Lists the players at the table that can be removed and retunrns the selected players
     * 
     * @param players type List<AbstractPlayer>
     * @return type AbstractPlayer
     */
    public AbstractPlayer pickPlayerToRemove(List<AbstractPlayer> players){
        ArrayList<Integer> tempArr = new ArrayList<>();
        System.out.println("Pick a player to remove.");
        System.out.println();
        System.out.printf("%-20s %s \n","Go back:", "[0]");

        for(int i = 0; i < players.size(); i++){
            if(!(players.get(i) instanceof Human) && !(players.get(i) instanceof Dealer)){
                System.out.printf("%-20s %s%d%s \n",players.get(i).getName(), "[", i, "]");
                tempArr.add(i);
            }
        }
        int selection;
        while(!sc.hasNextInt()) {
            System.out.println("Please enter a valid option");
            sc.next();
        }
        selection = sc.nextInt();
        return selection == 0 ? null : players.get(selection);
        

    }

    /**
     * Removes the specified player from the table
     * @param player type AbstractPlayer
     */
    public void removePlayer(AbstractPlayer player, List<AbstractPlayer> players){
        players.remove(player);
    }

    /**
     * @return type int
     */
    public int getBet(){
        return bet;
    }

    
    /**
     * @param bank type Bank
     * @return type int
     */
    public int getChips(Bank bank){
        return bank.getChips();
    }

    /**
     * @param newBet type int
     */
    public void setBet(int newBet){
        bet = newBet;
    }

    /**
     * @param delay type int
     */
    public void setDelay(int delay){
        Main.delay = delay;
    }

    /**
     * @return type int
     */
    public int getDelay(){
        return delay;
    }
    

    
}