package com.chieffu.pocker.monteMarlo;
import java.util.Scanner;
/**
 * Human Player class
 */
public class Human extends AbstractPlayer{
    /**
     * @param deck type Deck
     */
    public Human(Deck deck){
        super(deck);
        name = "Human Player";
    }

    /**
     * @param name type String
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Uses the scanner to ask the user if they want another card
     * @param DealerFaceUpTotal type String
     */
    @Override
    public int playHand(int DealerFaceUpTotal, int handNum){
        Main main = new Main();
        Bank bank = new Bank();
        Scanner sc = new Scanner(System.in);
        String selection;
        boolean hasSplit = false;
        do{
            System.out.println();
            if(hasSplit || handNum > 0) System.out.println(getName() + " Hand " + (handNum+1));
            System.out.println("Your total is " + currentTotal(handNum));
            if(currentTotal(handNum) == 21) return 21;
            System.out.printf("%-15s %s \n","Stand:", "[0]");
            System.out.printf("%-15s %s \n","Hit:", "[1]");
            System.out.printf("%-15s %s \n","Double Down:", "[2]");
            if(main.getChips(bank) >= (main.getBet() * 2) && canSplit(handNum)){
                System.out.printf("%-15s %s \n","Split:", "[3]");
            }
            
            
            // add split option
            selection = sc.next();
            switch(selection){
                case "0":
                    return currentTotal(handNum);
                case "1":
                    takeCard(handNum);
                    System.out.println("-------");
                    printLastCard(handNum);
                    if(isBust(handNum)) {
                        return currentTotal(handNum);
                    }
                    else{
                        if(currentTotal(handNum) == 21){
                            return 21;
                        }
                        System.out.println("-------");
                        System.out.println("Your current total is " + currentTotal(handNum));
                    }
                    break;
                case "2":
                    // make sure player can double their bet
                    if(main.getChips(bank) < (main.getBet() * 2)){
                        System.out.println("You do not have enough chips to double down!");
                        break;
                    }
                    else if(amountOfHands > 1){
                        System.out.println("You have split so you cannot double down!");
                        break;
                    }
                    main.setBet(main.getBet() * 2);
                    System.out.println("Doubling down, taking one card!");
                    takeCard(handNum);
                    System.out.println("-------");
                    printLastCard(handNum);
                    System.out.println("-------");
                    System.out.println("Your total is " + currentTotal(handNum));
                    return currentTotal(handNum);
                case "3":
                    // make sure player can split
                    if(!canSplit(handNum)){
                        System.out.println("Please enter a valid option");
                        break;
                    }
                    else if(main.getChips(bank) < (main.getBet() * 2)){
                        System.out.println("You do not have enough chips to split!");
                        break;
                    }
                    else if(amountOfHands == 4){
                        System.out.println("You have split the maximum amount of hands!");
                        break;
                    }
                    hasSplit = true;
                    System.out.println("Splitting hands!");
                    int addedHand = addHand();
                    addCard(removeLastCard(handNum), addedHand);
                    takeCard(handNum);
                    takeCard(addedHand);
                    break;
                default:
                    System.out.println("Please enter a valid option");
            }
            
        } while(true);
    }



}
