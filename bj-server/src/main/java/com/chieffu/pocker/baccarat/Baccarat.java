package com.chieffu.pocker.baccarat;

import com.chieffu.pocker.Ma;
import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import com.chieffu.pocker.blackjack.MockContext;
import com.chieffu.pocker.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Baccarat extends Ma {
    private static final Logger log = LoggerFactory.getLogger(Baccarat.class);

    static Logger logger = LoggerFactory.getLogger(Baccarat.class);

    int[] pai;

    int[][] pk;

    int tableId;

    int currentShoe;

    int latestRound;

    boolean missing;

    public int getTableId() {
        return this.tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getCurrentShoe() {
        return this.currentShoe;
    }

    public void setCurrentShoe(int currentShoe) {
        this.currentShoe = currentShoe;
    }

    public int getLatestRound() {
        return this.latestRound;
    }

    public void setLatestRound(int latestRound) {
        this.latestRound = latestRound;
    }

    public boolean isMissing() {
        return this.missing;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    private double[] longBaoPeiLv = new double[]{31.0D, 11.0D, 7.0D, 5.0D, 3.0D, 2.0D, 2.0D, 1.0D};

    public double[] getLongBaoPeiLv() {
        return this.longBaoPeiLv;
    }

    public void setLongBaoPeiLv(double[] longBaoPeiLv) {
        this.longBaoPeiLv = longBaoPeiLv;
    }

    public Baccarat() {
        init(8);
    }


    public void removeCards(Integer... cards) {
        for (Integer card : cards) {
            Pocker p = Pocker.getPocker(card.intValue());
            removePocker(p);
        }
    }

    private void init(int k) {
        this.pai = new int[10];
        Arrays.fill(this.pai, k * 4);
        this.pai[0] = 4 * k * 4;
        this.pk = new int[13][4];
        for (int i = 0; i < 13; i++)
            Arrays.fill(this.pk[i], k);
    }

    public int countPai() {
        int sum = 0;
        for (int i = 0; i < this.pai.length; i++)
            sum += this.pai[i];
        return sum;
    }

    public int countPai(int num){
        if(num<0||num>10)return 0;
        return pai[num];
    }

    public int countPai1() {
        int sum = 0;
        for (int i = 0; i < this.pk.length; i++) {
            for (int j = 0; j < (this.pk[i]).length; j++)
                sum += this.pk[i][j];
        }
        return sum;
    }

    public double xLongBao() {
        long total = 0L;
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 9; j++) {
                long c = countXZ33(i, j);
                total += c;
            }
        }
        double[] pl = getLongBaoPeiLv();
        return (1.0D - rSmall()) * (countXZ33(9, 0) * pl[0] +
                countXZ33(8, 0) * pl[1] + countXZ33(9, 1) * pl[1] +
                countXZ33(7, 0) * pl[2] + countXZ33(8, 1) * pl[2] + countXZ33(9, 2) * pl[2] +
                countXZ33(6, 0) * pl[3] + countXZ33(7, 1) * pl[3] + countXZ33(8, 2) * pl[3] + countXZ33(9, 3) * pl[3] +
                countXZ33(5, 0) * pl[4] + countXZ33(6, 1) * pl[4] + countXZ33(7, 2) * pl[4] + countXZ33(8, 3) * pl[4] + countXZ33(9, 4) * pl[4] +
                countXZ33(4, 0) * pl[5] + countXZ33(5, 1) * pl[5] + countXZ33(6, 2) * pl[5] + countXZ33(7, 3) * pl[5] + countXZ33(8, 4) * pl[5] + countXZ33(9, 5) * pl[5]) * 1.0D / total +
                rXLp() * pl[6] + rXZLpHe() * pl[7];
    }

    public double zLongBao() {
        double[] pl = getLongBaoPeiLv();
        long total = 0L;
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 9; j++) {
                long c = countXZ33(i, j);
                total += c;
            }
        }
        return (1.0D - rSmall()) * (countXZ33(0, 9) * pl[0] +
                countXZ33(0, 8) * pl[1] + countXZ33(1, 9) * pl[1] +
                countXZ33(0, 7) * pl[2] + countXZ33(1, 8) * pl[2] + countXZ33(2, 9) * pl[2] +
                countXZ33(0, 6) * pl[3] + countXZ33(1, 7) * pl[3] + countXZ33(2, 8) * pl[3] + countXZ33(3, 9) * pl[3] +
                countXZ33(0, 5) * pl[4] + countXZ33(1, 6) * pl[4] + countXZ33(2, 7) * pl[4] + countXZ33(3, 8) * pl[4] + countXZ33(4, 9) * pl[4] +
                countXZ33(0, 4) * pl[5] + countXZ33(1, 5) * pl[5] + countXZ33(2, 6) * pl[5] + countXZ33(3, 7) * pl[5] + countXZ33(4, 8) * pl[5] + countXZ33(5, 9) * pl[5]) / total +
                rXLp() * pl[6] + rXZLpHe() * pl[7];
    }

    public double rLp() {
        return totalLipai() * 1.0D / p(countPai(), 4);
    }

    private double rNlp() {
        return 1.0D - totalLipai() * 1.0D / p(countPai(), 4);
    }

    private long totalLipai() {
        long sum = 0L;
        int i;
        for (i = 0; i <= 9; i++) {
            sum += countXZ22(8, i);
            sum += countXZ22(9, i);
        }
        for (i = 0; i <= 9; i++) {
            sum += countXZ22(i, 8);
            sum += countXZ22(i, 9);
        }
        sum = sum - countXZ22(8, 9) - countXZ22(8, 8) - countXZ22(9, 8) - countXZ22(9, 9);
        return sum;
    }

    public double rLp0(){
        double r = 0.0;
        double total  = c(countPai(),2);
        for(int i=0;i<=9/2;i++){
           r += c(countPai(i),1)*c(countPai(9-i),1);
           if(i==4){
               r+=c(countPai(i),2);
           }else{
               r+=c(countPai(i),1)*c(countPai(8-i),1);
           }
        }
        r+=c(countPai(9),2);
        return r/total;
    }

    public double rSmall() {
        return 1.0D * (totalLipai() + countXZ22(7, 6) + countXZ22(6, 6) + countXZ22(7, 7) + countXZ22(6, 7)) / p(countPai(), 4);
    }

    private long countXZ33(int x, int z) {
        long sum = 0L;
        for (int i1 = 0; i1 <= 9; i1++) {
            int k1 = this.pai[i1];
            if (k1 > 0) {
                this.pai[i1]--;
                for (int i2 = 0; i2 <= 9; i2++) {
                    int q2 = i1 + i2;
                    if (q2 >= 10)
                        q2 -= 10;
                    if (q2 <= 5) {
                        int k2 = this.pai[i2];
                        if (k2 > 0) {
                            this.pai[i2] --;
                            int i3 = x - q2;
                            if (i3 < 0)
                                i3 += 10;
                            int k3 = this.pai[i3];
                            if (k3 > 0) {
                                this.pai[i3] --;
                                for (int j1 = 0; j1 <= 9; j1++) {
                                    int d1 = this.pai[j1];
                                    if (d1 > 0) {
                                        this.pai[j1] --;
                                        for (int j2 = 0; j2 <= 9; j2++) {
                                            int d2 = this.pai[j2];
                                            if (d2 > 0) {
                                                this.pai[j2] --;
                                                int q22 = j1 + j2;
                                                if (q22 >= 10)
                                                    q22 -= 10;
                                                long pre5 = multiply(k1, k2, k3, d1, d2);
                                                if (q22 <= 2) {
                                                    sum = getSum(z, sum, q22, pre5);
                                                } else if (q22 == 3) {
                                                    if (q22 == z && i3 == 8) {
                                                        sum += pre5 * countPai();
                                                    } else if (i3 != 8) {
                                                        sum = getSum(z, sum, q22, pre5);
                                                    }
                                                } else if (q22 == 4) {
                                                    if (q22 == z && (i3 == 0 || i3 == 1 || i3 == 8 || i3 == 9)) {
                                                        sum += p(k1, 1) * p(k2, 1) * p(k3, 1) * p(d1, 1) * p(d2, 1) * countPai();
                                                    } else if (i3 != 0 && i3 != 1 && i3 != 8 && i3 != 9) {
                                                        sum = getSum(z, sum, q22, pre5);
                                                    }
                                                } else if (q22 == 5) {
                                                    if (q22 == z && (i3 == 0 || i3 == 1 || i3 == 2 || i3 == 3 || i3 == 8 || i3 == 9)) {
                                                        sum += pre5 * countPai();
                                                    } else if (i3 != 0 && i3 != 1 && i3 != 2 && i3 != 3 && i3 != 8 && i3 != 9) {
                                                        sum = getSum(z, sum, q22, pre5);
                                                    }
                                                } else if (q22 == 6) {
                                                    if (i3 == 6 || i3 == 7) {
                                                        sum = getSum(z, sum, q22, pre5);
                                                    } else if (q22 == z && i3 != 6 && i3 != 7) {
                                                        sum += pre5 * countPai();
                                                    }
                                                } else if (q22 == 7 && z == 7) {
                                                    sum += pre5 * countPai();
                                                }
                                            }
                                            this.pai[j2] = d2;
                                        }
                                        this.pai[j1] = d1;
                                    }
                                }
                                this.pai[i3] = k3;
                            }
                            this.pai[i2] = k2;
                        }
                    } else if ((q2 == 6 && x == 6) || (q2 == 7 && x == 7)) {
                        int k2 = this.pai[i2];
                        if (k2 > 0) {
                            this.pai[i2] = k2 - 1;
                            for (int j1 = 0; j1 <= 9; j1++) {
                                int d1 = this.pai[j1];
                                if (d1 > 0) {
                                    this.pai[j1] = d1 - 1;
                                    for (int j2 = 0; j2 <= 9; j2++) {
                                        int d2 = this.pai[j2];
                                        if (d2 > 0) {
                                            this.pai[j2] = d2 - 1;
                                            int q22 = j1 + j2;
                                            if (q22 >= 10)
                                                q22 -= 10;
                                            if (q22 <= 2 || q22 <= 5) {
                                                int j3 = z - q22;
                                                if (j3 < 0)
                                                    j3 += 10;
                                                int d3 = this.pai[j3];
                                                if (d3 > 0) {
                                                    this.pai[j3] = d3 - 1;
                                                    sum += multiply(k1, k2, d1, d2, d3) * countPai();
                                                    this.pai[j3] = d3;
                                                }
                                            }
                                            this.pai[j2] = d2;
                                        }
                                    }
                                    this.pai[j1] = d1;
                                }
                            }
                            this.pai[i2] = k2;
                        }
                    }
                }
                this.pai[i1] = k1;
            }
        }
        return sum;
    }

    /**
     * 庄家补牌以6点胜出的概率
     * @return
     */
    public double rZBig6(){
        long sum = 0L;
        for (int i1 = 0; i1 <= 9; i1++) {
            int k1 = this.pai[i1];
            if (k1 > 0) {
                this.pai[i1]--;
                for (int i2 = 0; i2 <= 9; i2++) {
                    int q2 = i1 + i2;
                    if (q2 >= 10)
                        q2 -= 10;
                    if (q2 <= 5) {
                        int k2 = this.pai[i2];
                        if (k2 > 0) {
                            this.pai[i2] --;
                            for(int i3=0;i3<=9;i3++){
                                int x = q2+i3;
                                if(x>=10)x=x-10;
                                if(x<=5){
                                    int k3 = this.pai[i3];
                                    if (k3 > 0) {
                                        this.pai[i3] --;
                                        for (int j1 = 0; j1 <= 9; j1++) {
                                            int d1 = this.pai[j1];
                                            if (d1 > 0) {
                                                this.pai[j1] --;
                                                for (int j2 = 0; j2 <= 9; j2++) {
                                                    int d2 = this.pai[j2];
                                                    if (d2 > 0) {
                                                        this.pai[j2] --;
                                                        int q22 = j1 + j2;
                                                        if (q22 >= 10)
                                                            q22 -= 10;
                                                        long pre5 = multiply(k1, k2, k3, d1, d2);
                                                        if (q22 <= 2) {
                                                            sum = getSum(6, sum, q22, pre5);
                                                        } else if (q22 == 3) {
                                                            if (i3 != 8) {
                                                                sum = getSum(6, sum, q22, pre5);
                                                            }
                                                        } else if (q22 == 4) {
                                                            if (i3 != 0 && i3 != 1 && i3 != 8 && i3 != 9) {
                                                                sum = getSum(6, sum, q22, pre5);
                                                            }
                                                        } else if (q22 == 5) {
                                                            if (i3 != 0 && i3 != 1 && i3 != 2 && i3 != 3 && i3 != 8 && i3 != 9) {
                                                                sum = getSum(6, sum, q22, pre5);
                                                            }
                                                        } else if (q22 == 6) {
                                                            if (i3 == 6 || i3 == 7) {
                                                                sum = getSum(6, sum, q22, pre5);
                                                            }
                                                        }
                                                    }
                                                    this.pai[j2] = d2;
                                                }
                                                this.pai[j1] = d1;
                                            }
                                        }
                                        this.pai[i3] = k3;
                                    }
                                }
                            }
                            this.pai[i2] = k2;
                        }
                    }
                }
                this.pai[i1] = k1;
            }
        }
        return sum/(double)p(countPai(),6);
    }

    /**
     * 庄家不补牌以6点胜出的概率
     * @return
     */
    public double rZSmall6(){
        long sum = 0L;
        for (int i1 = 0; i1 <= 9; i1++) {
            int k1 = this.pai[i1];
            if (k1 > 0) {
                this.pai[i1]--;
                for (int i2 = 0; i2 <= 9; i2++) {
                    int q2 = i1 + i2;
                    if (q2 >= 10)
                        q2 -= 10;
                    if (q2 <= 5) {
                        int k2 = this.pai[i2];
                        if (k2 > 0) {
                            this.pai[i2] --;
                            for(int i3=0;i3<=9;i3++){
                                int x = q2+i3;
                                if(x>=10)x=x-10;
                                if(x<=5) {
                                    int k3 = this.pai[i3];
                                    if (k3 > 0) {
                                        this.pai[i3]--;
                                        for (int j1 = 0; j1 <= 9; j1++) {
                                            int d1 = this.pai[j1];
                                            if (d1 > 0) {
                                                this.pai[j1]--;
                                                int j2=6-j1;
                                                if(j2<0)j2+=10;
                                                int d2 = this.pai[j2];
                                                if (d2 > 0) {
                                                    this.pai[j2]--;
                                                    if ( i3 != 6 && i3 != 7) {
                                                        sum += multiply(k1, k2, k3, d1, d2) * countPai();
                                                    }
                                                }
                                                this.pai[j2] = d2;
                                            }
                                            this.pai[j1] = d1;
                                        }
                                        this.pai[i3] = k3;
                                    }
                                }
                            }
                            this.pai[i2] = k2;
                        }
                    }
                }
                this.pai[i1] = k1;
            }
        }
        return sum/(double)p(countPai(),6);
    }
    private long getSum(int z, long sum, int q22, long pre5) {
        int j3 = z - q22;
        if (j3 < 0)
            j3 += 10;
        int d3 = this.pai[j3];
        if (d3 > 0)
            sum += pre5 * d3;
        return sum;
    }

    private long multiply(int... k) {
        long result = 1L;
        for (int ki : k)
            result *= ki;
        return result;
    }

    private long countXZ22(int x, int z) {
        long sum = 0L;
        for (int i1 = 0; i1 <= 9; i1++) {
            int k1 = this.pai[i1];
            if (k1 > 0) {
                this.pai[i1] = k1 - 1;
                int i2 = x - i1;
                if (i2 < 0)
                    i2 += 10;
                int k2 = this.pai[i2];
                if (k2 > 0) {
                    this.pai[i2] = k2 - 1;
                    for (int j1 = 0; j1 <= 9; j1++) {
                        int d1 = this.pai[j1];
                        if (d1 > 0) {
                            this.pai[j1] = d1 - 1;
                            int j2 = z - j1;
                            if (j2 < 0)
                                j2 += 10;
                            int d2 = this.pai[j2];
                            sum += multiply(k1, k2, d1, d2);
                            this.pai[j1] = d1;
                        }
                    }
                    this.pai[i2] = k2;
                }
                this.pai[i1] = k1;
            }
        }
        return sum;
    }

    public double rXZLpHe() {
        return 1.0D * (countXZ22(8, 8) + countXZ22(9, 9)) / p(countPai(), 4);
    }

    public double expLp() {
        return (rZLp()+rXLp()- (countXZ22(8, 8) + countXZ22(9, 9)+countXZ22(9, 8) + countXZ22(8, 9))/p(countPai(),4))*2.6;
    }

    public double rZLp() {
        long zlp = 0L;
        for (int i = 0; i <= 9; i++) {
            if (i < 8)
                zlp += countXZ22(8, i);
            if (i < 9)
                zlp += countXZ22(9, i);
        }
        return zlp * 1.0D / p(countPai(), 4);
    }

    public void removePocker(Pocker p) {
        if (p != null)
            removePai(p.getNum(), p.getSuit().getHuaSe());
    }
    public void removePocker(List<Pocker> pockers) {
        for(Pocker p:pockers){
            removePocker(p);
        }
    }

    public void removePai(int num, int huaSe) {
        int k = this.pk[num - 1][huaSe-1];
        if (k == 0) {
            log.error(String.format("第 %s 桌 ，第 %s 靴 ，第 %s 轮开的扑克牌：%s 不存在", Integer.valueOf(this.tableId), Integer.valueOf(this.currentShoe), Integer.valueOf(this.latestRound), new Pocker(huaSe, num)));
            throw new RuntimeException(String.format("第 %s 桌 ，第 %s 靴 ，第 %s 轮开的扑克牌：%s 不存在", Integer.valueOf(this.tableId), Integer.valueOf(this.currentShoe), Integer.valueOf(this.latestRound), new Pocker(huaSe, num)));
        }
        this.pk[num - 1][huaSe-1] = k - 1;
        if (num >= 10) {
            this.pai[0] = this.pai[0] - 1;
        } else {
            this.pai[num] = this.pai[num] - 1;
        }
    }

    private int getSum(int[] pk) {
        int sum = 0;
        for (int i = 0; i < pk.length; i++)
            sum += pk[i];
        return sum;
    }

    public double rPair() {
        long sum = 0L;
        for (int i1 = 0; i1 < this.pk.length; i1++) {
            int k1 = getSum(this.pk[i1]);
            sum += c(k1, 2);
        }
        return sum /(double) c(countPai1(), 2);
    }
    public double rAnyPair() {
        long sum = 0L;
        int[] nums = new int[pk.length];
        int countPai = countPai();
        double total = c(countPai(),4);
        for (int i1 = 0; i1 < this.pk.length; i1++) {
            nums[i1]=getSum(this.pk[i1]);
        }
        for(int i=0;i<nums.length;i++){
            sum+=c(nums[i],2)*c(countPai-2,2);
        }
        double pairPair = 0;
        for(int i=0;i<nums.length;i++){
            for(int j=0;j<nums.length;j++){
                if(i!=j)
                pairPair+=c(nums[i],2)*c(nums[j],2);
            }
        }

        return (sum-pairPair)/total;
    }

    public double rXLp() {
        return rZLp();
    }

    public double rZWin() {
        long z2 = 0L;
        for (int i = 0; i <= 9; i++) {
            if (i < 8)
                z2 += countXZ22(i, 8);
            if (i < 9)
                z2 += countXZ22(i, 9);
        }
        z2 += countXZ22(6, 7);
        long z3 = 0L;
        long total = 0L;
        for (int j = 0; j <= 9; j++) {
            for (int k = 0; k <= 9; k++) {
                long c = countXZ33(j, k);
                total += c;
                if (j < k)
                    z3 += c;
            }
        }
        return z2 * 1.0D / p(countPai(), 4) + (1.0D - rSmall()) * z3 / total;
    }

    public double rXWin() {
        long x2 = 0L;
        for (int i = 0; i <= 9; i++) {
            if (i < 8)
                x2 += countXZ22(8, i);
            if (i < 9)
                x2 += countXZ22(9, i);
        }
        x2 += countXZ22(7, 6);
        long x3 = 0L;
        long total = 0L;
        for (int j = 0; j <= 9; j++) {
            for (int k = 0; k <= 9; k++) {
                long c = countXZ33(j, k);
                total += c;
                if (j > k)
                    x3 += c;
            }
        }
        return x2 * 1.0D / p(countPai(), 4) + (1.0D - rSmall()) * x3 / total;
    }

    public double rHe() {
        long x2 = 0L;
        for (int i = 6; i <= 9; i++)
            x2 += countXZ22(i, i);
        long x3 = 0L;
        long total = 0L;
        for (int j = 0; j <= 9; j++) {
            for (int k = 0; k <= 9; k++) {
                long c = countXZ33(j, k);
                total += c;
                if (j == k)
                    x3 += c;
            }
        }
        return 1.0D * x2 / p(countPai(), 4) + (1.0D - rSmall()) * x3 / total;
    }

    public static Map<BaccaratBetType, Double> getResult(int... cards) {
        Pocker px1 = Pocker.getPocker(cards[0]);
        Pocker px2 = Pocker.getPocker(cards[1]);
        Pocker px3 = Pocker.getPocker(cards[2]);
        Pocker pz1 = Pocker.getPocker(cards[3]);
        Pocker pz2 = Pocker.getPocker(cards[4]);
        Pocker pz3 = Pocker.getPocker(cards[5]);
        return getResult(px1, px2, px3, pz1, pz2, pz3);
    }

    public static Map<BaccaratBetType, Double> getResult(Pocker px1, Pocker px2, Pocker px3, Pocker pz1, Pocker pz2, Pocker pz3) {
        double[] pl = {31.0D, 11.0D, 7.0D, 5.0D, 3.0D, 2.0D, 2.0D, 1.0D};
        int x2 = (px1.getBaccaratDot() + px2.getBaccaratDot()) % 10;
        int z2 = (pz1.getBaccaratDot() + pz2.getBaccaratDot()) % 10;
        int x3 = x2;
        int z3 = z2;
        if (px3 != null)
            x3 = (x2 + px3.getBaccaratDot()) % 10;
        if (pz3 != null)
            z3 = (z2 + pz3.getBaccaratDot()) % 10;
        Map<BaccaratBetType, Double> result = new HashMap<>();
        if (px3 == null && pz3 == null) {
            result.put(BaccaratBetType.Big, -1.0D);
            result.put(BaccaratBetType.Small, BaccaratBetType.Small.rate);
        } else {
            result.put(BaccaratBetType.Big, BaccaratBetType.Big.rate);
            result.put(BaccaratBetType.Small, -1.0D);
        }
        if (z3 == x3)
            result.put(BaccaratBetType.Tie, BaccaratBetType.Tie.rate);
        if (px1.getNum() == px2.getNum()) {
            result.put(BaccaratBetType.PlayerPair, BaccaratBetType.PlayerPair.rate);
        } else {
            result.put(BaccaratBetType.PlayerPair, -1.0D);
        }
        if (pz1.getNum() == pz2.getNum()) {
            result.put(BaccaratBetType.BankerPair, BaccaratBetType.BankerPair.rate);
        } else {
            result.put(BaccaratBetType.BankerPair, -1.0D);
        }
        if (px3 == null && pz3 == null) {
            if ((x3 == 9 || x3 == 8) && x3 > z3) {
                result.put(BaccaratBetType.PlayerBonus, 1.0D);
            } else if (x3 == z3 && (x3 == 8 || x3 == 9)) {
                result.put(BaccaratBetType.PlayerBonus, 0.0D);
            } else {
                result.put(BaccaratBetType.PlayerBonus, -1.0D);
            }
        } else {
            int k = x3 - z3;
            if (k <= 3) {
                result.put(BaccaratBetType.PlayerBonus, -1.0D);
            } else {
                result.put(BaccaratBetType.PlayerBonus, pl[9 - k] - 1.0D);
            }
        }
        if (px3 == null && pz3 == null) {
            if ((z3 == 9 || z3 == 8) && x3 < z3) {
                result.put(BaccaratBetType.BankerBonus, 1.0D);
            } else if (x3 == z3 && (z3 == 8 || z3 == 9)) {
                result.put(BaccaratBetType.BankerBonus, 0.0D);
            } else {
                result.put(BaccaratBetType.BankerBonus, -1.0D);
            }
        } else {
            int k = z3 - x3;
            if (k <= 3) {
                result.put(BaccaratBetType.BankerBonus, -1.0D);
            } else {
                result.put(BaccaratBetType.BankerBonus, pl[9 - k] - 1.0D);
            }
        }
        return result;
    }

    public Map<BaccaratBetType, Double> getAllOdds() {
        Map<BaccaratBetType, Double> results = new HashMap<>();
        results.put(BaccaratBetType.Banker, rZWin() * (BaccaratBetType.Banker.rate + 1.0D));
        results.put(BaccaratBetType.Player, rXWin() * (BaccaratBetType.Player.rate + 1.0D));
        results.put(BaccaratBetType.Tie, rHe() * (BaccaratBetType.Tie.rate + 1.0D));
        results.put(BaccaratBetType.BankerPair, rPair() * (BaccaratBetType.BankerPair.rate + 1.0D));
        results.put(BaccaratBetType.PlayerPair, results.get(BaccaratBetType.BankerPair));
        results.put(BaccaratBetType.BankerBonus, zLongBao());
        results.put(BaccaratBetType.PlayerBonus, xLongBao());
        results.put(BaccaratBetType.Big, (1.0D - rSmall()) * (BaccaratBetType.Big.rate + 1.0D));
        results.put(BaccaratBetType.Small, rSmall() * (BaccaratBetType.Small.rate + 1.0D));
        return results;
    }
    public static MockContext mock(int shift,double gate) {
        Baccarat baccarat = new Baccarat();
        List<Pocker> pks = Pocker.randomPocker(8);
        int round = 0;
        List<Pocker> pz = new ArrayList<>();
        List<Pocker> px = new ArrayList<>();
        MockContext total = new MockContext("总");
        MockContext longBaoContext = new MockContext("龙宝");
        MockContext smallContext = new MockContext("小");
        MockContext zContext = new MockContext("庄");
        MockContext lipaiContext = new MockContext("例牌");
        MockContext anyPairContext = new MockContext("超级对");
        int cut =  StringUtils.newRandomInt(416-240, 416-256);
        while (pks.size()>cut&&round<60) {
            round++;

            px.add(pks.remove(pks.size() - 1));
            px.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));

            int x2 = (px.get(0).getBaccaratDot() + px.get(1).getBaccaratDot()) % 10;
            int z2 = (pz.get(0).getBaccaratDot() + pz.get(1).getBaccaratDot()) % 10;
            int x3 = x2;
            int z3 = z2;
            if (x2 == 8 || x2 == 9 || z2 == 8 || z2 == 9 || ((x2 == 6 || x2 == 7) && (z2 == 6 || z2 == 7))) {
                x3 = x2;
                z3 = z2;
            } else if (x2 <= 5) {
                px.add(pks.remove(pks.size() - 1));
                int i3 = px.get(2).getBaccaratDot();
                x3 = (x2 + i3) % 10;
                if (z2 <= 2 || (z2 == 3 && i3 != 8) || (z2 == 4 && i3 != 0 && i3 != 1 && i3 != 8 && i3 != 9) || (z2 == 5 && i3 != 0 && i3 != 1 && i3 != 2 && i3 != 3 && i3 != 8 && i3 != 9) || (z2 == 6 && (i3 == 6 || i3 == 7))) {
                    pz.add(pks.remove(pks.size() - 1));
                    z3 = (z2 + pz.get(2).getBaccaratDot()) % 10;
                } else {
                    z3 = z2;
                }
            } else if (z2 <= 2 || (z2 != 6 && z2 != 7 && z2 < x2)) {
                pz.add(pks.remove(pks.size() - 1));
                z3 = (z2 + pz.get(2).getBaccaratDot()) % 10;
            } else {
                z3 = z2;
                x3 = x2;
            }

//            mockSmal(shift, baccarat, round, pz, px, smallContext, gate);
            mockLipai(shift, baccarat, round, pz, px, lipaiContext,gate);
//            mockAnyPair(shift, baccarat, round, pz, px, anyPairContext,gate);

            baccarat.removePocker(px);
            baccarat.removePocker(pz);
            px.clear();
            pz.clear();
        }
//        total.merge(smallContext);
        total.merge(lipaiContext);
//        total.merge(anyPairContext);
        return total;
    }

    public double rOdds(){
        int total = countPai();
        double odds = 0;
        for(int i=1;i<=13;i+=2){
            odds+= Arrays.stream(pk[i-1]).sum();
        }
        return odds/total;
    }
    public double rEven(){
        int total = countPai();
        double even = 0;
        for(int i=2;i<=13;i+=2){
            even+= Arrays.stream(pk[i-1]).sum();
        }
        return even/total;
    }
    public double rRed(){
        int total = countPai();
        double red  = 0;
        for(int i=1;i<=13;i++){
            red+=pk[i-1][1];
            red+=pk[i-1][3];
        }
        return red/total;
    }

    public double rBlack(){
        int total = countPai();
        double black  = 0;
        for(int i=1;i<=13;i++){
            black+=pk[i-1][0];
            black+=pk[i-1][2];
        }
        return black/total;
    }
    public static MockContext mockLongHu(int shift,double gate) {
        Baccarat baccarat = new Baccarat();
        List<Pocker> pks = Pocker.randomPocker(8);
        int round = 0;
        MockContext total = new MockContext("总");
        MockContext redContext = new MockContext("红");
        MockContext blackContext = new MockContext("黑");
        MockContext oddsContext = new MockContext("单");
        MockContext evenContext = new MockContext("双");
        while (round<60) {
            round++;
            Pocker px = pks.remove(pks.size() - 1);
            Pocker pz = pks.remove(pks.size()-1);

            if(baccarat.rOdds()*1.75>gate){
                oddsContext.addCount(2);
                double r = 0;
                if(px.getNum()%2==1){
                    r+=0.75;
                }else{
                    r-=1;
                }
                if(pz.getNum()%2==1){
                    r+=0.75;
                }else{
                    r-=1;
                }
                oddsContext.addResult(r);
            }else if(baccarat.rEven()*2.05>gate){
                evenContext.addCount(2);
                double r = 0;
                if(px.getNum()%2==0){
                    r+=1.05;
                }else{
                    r-=1;
                }
                if(pz.getNum()%2==0){
                    r+=1.05;
                }else{
                    r-=1;
                }
                evenContext.addResult(r);
            }
            if(baccarat.rRed()*1.9>gate){
                redContext.addCount(2);
                double r = 0;
                if(px.getSuit()== SuitEnum.HEART||px.getSuit()==SuitEnum.DIAMOND){
                    r+=0.9;
                }else{
                    r-=1;
                }
                if(pz.getSuit()==SuitEnum.HEART||pz.getSuit()==SuitEnum.DIAMOND){
                    r+=0.9;
                }else {
                    r-=1;
                }
                redContext.addResult(r);
            }else if(baccarat.rBlack()*1.9>gate){
                blackContext.addCount(2);
                double r = 0;
                if(px.getSuit()== SuitEnum.SPADE||px.getSuit()==SuitEnum.CLUB){
                    r+=0.9;
                }else{
                    r-=1;
                }
                if(pz.getSuit()== SuitEnum.SPADE||pz.getSuit()==SuitEnum.CLUB){
                    r+=0.9;
                }else {
                    r-=1;
                }
                blackContext.addResult(r);
            }

            baccarat.removePocker(px);
            baccarat.removePocker(pz);

        }
        total.merge(redContext);
        total.merge(blackContext);
        total.merge(oddsContext);
        total.merge(evenContext);
        return total;
    }
    private static boolean mockSmal(int shift, Baccarat bj, int round, List<Pocker> pz, List<Pocker> px, MockContext xContext,double gate) {
        double expXWin = bj.rSmall()*2.5;
        if(expXWin>=gate) {
            double result = -1;
            if(px.size()==2&&pz.size()==2){
                result +=2.5;
            }
            xContext.addCount();
            xContext.addResult(result);
            log.info("第{}靴第{}局压{}:{} ---- cnt: {} min: {} max: {}  result: {}   结果 {}  {} - {} "
                    , shift, round,xContext.getName(),String.format("%.4f",expXWin), xContext.getCount()
                    , String.format("%.2f",xContext.getMinWin()), String.format("%.2f",xContext.getMaxWin()), String.format("%.2f",xContext.getResult()),String.format("%.2f",result), px,  pz);
            return true;
        }
        return false;
    }
    private static boolean mockLipai(int shift, Baccarat bj, int round, List<Pocker> pz, List<Pocker> px, MockContext xContext,double gate) {
        if(round>25)return false;
        double expLp = bj.expLp();;
        if(expLp>=gate) {
            double result = -1;
            int px2 = px.stream().map(Pocker::getBaccaratDot).reduce((a,b)->a+b).get();
            int pz2 = pz.stream().map(Pocker::getBaccaratDot).reduce((a,b)->a+b).get();
            if(px.size()==2&& (px2==8||px2==9||px2==18)||pz.size()==2 && (pz2==8||pz2==9||pz2==18)){
                result +=2.6;
            }
            xContext.addCount();
            xContext.addResult(result);
            log.info("第{}靴第{}局压{}:{} ---- cnt: {} min: {} max: {}  result: {}   结果 {}  {} - {} "
                    , shift, round,xContext.getName(),String.format("%.4f",expLp), xContext.getCount()
                    , String.format("%.2f",xContext.getMinWin()), String.format("%.2f",xContext.getMaxWin()), String.format("%.2f",xContext.getResult()),String.format("%.2f",result), px,  pz);
            return true;
        }
        return false;
    }
    private static boolean mockAnyPair(int shift, Baccarat bj, int round, List<Pocker> pz, List<Pocker> px, MockContext xContext,double gate) {
        if(round>60)return false;
        double anyPair = bj.rAnyPair()*2.2;
        if(anyPair>=gate) {
            double result = -1;
            Set<Integer> cards = new HashSet<>();
            cards.add(pz.get(0).getNum());
            cards.add(pz.get(1).getNum());
            cards.add(px.get(0).getNum());
            cards.add(px.get(1).getNum());
            if(cards.size()<4){
                result +=2.2;
            }
            xContext.addCount();
            xContext.addResult(result);
            log.info("第{}靴第{}局压{}:{} ---- cnt: {} min: {} max: {}  result: {}   结果 {}  {} - {} "
                    , shift, round,xContext.getName(),String.format("%.4f",anyPair), xContext.getCount()
                    , String.format("%.2f",xContext.getMinWin()), String.format("%.2f",xContext.getMaxWin()), String.format("%.2f",xContext.getResult()),String.format("%.2f",result), px,  pz);
            return true;
        }
        return false;
    }
    public static void main(String[] arg) throws Exception {
        Baccarat pock = new Baccarat();
        pock.init(8);
        logger.info("庄{} {}", pock.rZWin(), pock.rZWin() * 1.95D);
        logger.info("闲{} {}", pock.rXWin(), pock.rXWin() * 2.0D);
        logger.info("平{} {}", pock.rHe(), pock.rHe() * 9.0D);
        logger.info("小{} {}", pock.rSmall(), pock.rSmall() * 2.5D);
        logger.info("大{} {}", 1.0D - pock.rSmall(), (1.0D - pock.rSmall()) * 1.5D);
        logger.info("对{} {}", pock.rPair(), pock.rPair() * 12.0D);
        logger.info("对{}  {} > 1.2", pock.rAnyPair(), pock.rAnyPair()*2.2);
        logger.info("例牌{} {}", pock.rLp0()*2, pock.rLp0()*2 * 2.6);
        logger.info("庄例牌{} {}", pock.rZLp(), pock.rZLp() * 5.0D);
        logger.info("闲龙宝 {}", pock.xLongBao());
        logger.info("庄龙宝 {}", pock.zLongBao());

        double l = 0;
        for(int i=0;i<6;i++){
            l+=pock.countXZ33(i,6);
        }
        logger.info("大老虎 {}",pock.rZBig6()*51);
        logger.info("小老虎 {}",pock.rZSmall6()*23);

        logger.info("超级6 {}  {}",l*1.0/p(pock.countPai(),6),pock.rZBig6()+ pock.rZSmall6());

        MockContext c0 = new MockContext("total");
//        for (int i = 1; i <= 10000; i++) {
//            MockContext c = mockLongHu(i,1.00);
//            log.info("第{}靴---次数 = {} -----max={} ----- min={}----结果 = {}",i, c.getCount(), String.format("%.2f",c.getMaxWin()), String.format("%.2f",c.getMinWin()),String.format("%.2f", c.getResult()));
//            c0.merge(c);
//            log.info("total---次数 = {} -----max={} ----- min={}----结果 = {}", c0.getCount(), String.format("%.2f",c0.getMaxWin()), String.format("%.2f",c0.getMinWin()), String.format("%.2f",c0.getResult()));
//        }

    }
}

