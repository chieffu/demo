package com.chieffu.pocker.monteMarlo;

import java.util.Calendar;
/**
 * Represents an Bank
 */
public class Bank {
    private static int chips = 5000;
    private static int tableMinimum;
    private int difference = 0;

    /**
     * Bank constructor
     */
    public Bank(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        // if it is a weekend the table min is 1000, otherwise 500
        tableMinimum = (day == 6 || day == 7) ? 1000 : 500;
    }
    /**
     * Adds chips to chips var
     * @param chips type int
     */
    public void addChips(int chips){
        Bank.chips += chips;
        difference += chips;
    }

    /**
     * Subtracts chips from chips var
     * @param chips type int
     */
    public void subtractChips(int chips){
        Bank.chips -= chips;
        difference -= chips;
    }

    /**
     * @return type int
     */
    public int getChips(){
        return Bank.chips;
    }

    /**
     * @return type int
     */
    public int getTableMinimum(){
        return tableMinimum;
    }

    /**
     * @return type boolean
     */
    public boolean isOutOfChips(){
        return chips <= 0;
    }

    /**
     * @param bet type int
     * @return type boolean
     */
    public boolean hasTableMinium(int bet){
        return bet >= tableMinimum;
    }

    /**
     * @return type int
     */
    public int getDifference(){
        return difference;
    }

    /**
     * Resets the difference int
     */
    public void resetDifference(){
        difference = 0;
    }
}
