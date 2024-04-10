package com.chieffu.pocker.baccarat;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Baccarat {
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

    public static int c(int n, int m) {
        if (n == 0)
            return 0;
        int k = n - m;
        if (k < m)
            return (int) (p(n, k) / p(k, k));
        return (int) (p(n, m) / p(m, m));
    }

    public static long p(int n, int m) {
        if (n <= 0)
            return 0L;
        if (m == 0)
            return 1L;
        long ji = n;
        for (int i = 1; i < m; i++)
            ji *= (n - i);
        return ji;
    }

    public static double[] mock(int shift, List<Pocker> pks) {
        Baccarat pock = new Baccarat();
        pock.init(pks.size() / 52);
        double[] pl = pock.getLongBaoPeiLv();
        int c0 = 0;
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        int c4 = 0;
        int c5 = 0;
        int c6 = 0;
        int c7 = 0;
        int c8 = 0;
        int round = 0;
        double r0 = 0.0D;
        double r1 = 0.0D;
        double r2 = 0.0D;
        double r3 = 0.0D;
        double r4 = 0.0D;
        double r5 = 0.0D;
        double r6 = 0.0D;
        double r7 = 0.0D;
        double r8 = 0.0D;
        double max = -100.0D;
        double min = 100.0D;
        int maxRound = StringUtils.newRandomInt(65, 77);
        while (pks.size() > 60) {
            round++;
            Pocker px1 = pks.remove(pks.size() - 1);
            Pocker px2 = pks.remove(pks.size() - 1);
            Pocker pz1 = pks.remove(pks.size() - 1);
            Pocker pz2 = pks.remove(pks.size() - 1);
            Pocker px3 = null;
            Pocker pz3 = null;
            int x2 = (px1.getBaccaratDot() + px2.getBaccaratDot()) % 10;
            int z2 = (pz1.getBaccaratDot() + pz2.getBaccaratDot()) % 10;
            int x3 = x2;
            int z3 = z2;
            if (x2 == 8 || x2 == 9 || z2 == 8 || z2 == 9 || ((x2 == 6 || x2 == 7) && (z2 == 6 || z2 == 7))) {
                x3 = x2;
                z3 = z2;
            } else if (x2 <= 5) {
                px3 = pks.remove(pks.size() - 1);
                int i3 = px3.getBaccaratDot();
                x3 = (x2 + i3) % 10;
                if (z2 <= 2 || (z2 == 3 && i3 != 8) || (z2 == 4 && i3 != 0 && i3 != 1 && i3 != 8 && i3 != 9) || (z2 == 5 && i3 != 0 && i3 != 1 && i3 != 2 && i3 != 3 && i3 != 8 && i3 != 9) || (z2 == 6 && (i3 == 6 || i3 == 7))) {
                    pz3 = pks.remove(pks.size() - 1);
                    z3 = (z2 + pz3.getBaccaratDot()) % 10;
                } else {
                    z3 = z2;
                }
            } else if (z2 <= 2 || (z2 != 6 && z2 != 7 && z2 < x2)) {
                pz3 = pks.remove(pks.size() - 1);
                z3 = (z2 + pz3.getBaccaratDot()) % 10;
            } else {
                z3 = z2;
                x3 = x2;
            }
            double d4 = pock.xLongBao();
            if (d4 >= 1.0D) {
                c4++;
                logger.info("{}靴{}把 闲龙宝：{}", Integer.valueOf(shift), Integer.valueOf(round), Double.valueOf(d4));
                if (px3 == null && pz3 == null) {
                    if ((x3 == 9 || x3 == 8) && x3 > z3) {
                        r4++;
                    } else if (x3 != z3 || (x3 != 8 && x3 != 9)) {
                        r4--;
                    }
                } else {
                    int k = x3 - z3;
                    if (k <= 3) {
                        r4--;
                    } else {
                        r4 += pl[9 - k] - 1.0D;
                    }
                }
            }
            pock.removePocker(px1);
            pock.removePocker(px2);
            pock.removePocker(px3);
            pock.removePocker(pz1);
            pock.removePocker(pz2);
            pock.removePocker(pz3);
        }
        return new double[]{(c0 + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8), r0 + r1 + r2 + r3 + r4 + r5 + r6 + r7 + r8};
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

    private double rLp() {
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

    public double rSmall() {
        return 1.0D * (totalLipai() + countXZ22(7, 6) + countXZ22(6, 6) + countXZ22(7, 7) + countXZ22(6, 7)) / p(countPai(), 4);
    }

    private long countXZ33(int x, int z) {
        long sum = 0L;
        for (int i1 = 0; i1 <= 9; i1++) {
            int k1 = this.pai[i1];
            if (k1 > 0) {
                this.pai[i1] = k1 - 1;
                for (int i2 = 0; i2 <= 9; i2++) {
                    int q2 = i1 + i2;
                    if (q2 >= 10)
                        q2 -= 10;
                    if (q2 <= 5) {
                        int k2 = this.pai[i2];
                        if (k2 > 0) {
                            this.pai[i2] = k2 - 1;
                            int i3 = x - q2;
                            if (i3 < 0)
                                i3 += 10;
                            int k3 = this.pai[i3];
                            if (k3 > 0) {
                                this.pai[i3] = k3 - 1;
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

    public void removePai(int num, int huaSe) {
        int k = this.pk[num - 1][huaSe];
        if (k == 0) {
            log.error(String.format("第 %s 桌 ，第 %s 靴 ，第 %s 轮开的扑克牌：%s 不存在", Integer.valueOf(this.tableId), Integer.valueOf(this.currentShoe), Integer.valueOf(this.latestRound), new Pocker(huaSe, num)));
            throw new RuntimeException(String.format("第 %s 桌 ，第 %s 靴 ，第 %s 轮开的扑克牌：%s 不存在", Integer.valueOf(this.tableId), Integer.valueOf(this.currentShoe), Integer.valueOf(this.latestRound), new Pocker(huaSe, num)));
        }
        this.pk[num - 1][huaSe] = k - 1;
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
            sum += p(k1, 2);
        }
        return sum * 1.0D / p(countPai1(), 2);
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
            result.put(BaccaratBetType.Big, Double.valueOf(-1.0D));
            result.put(BaccaratBetType.Small, Double.valueOf(BaccaratBetType.Small.rate));
        } else {
            result.put(BaccaratBetType.Big, Double.valueOf(BaccaratBetType.Big.rate));
            result.put(BaccaratBetType.Small, Double.valueOf(-1.0D));
        }
        if (z3 == x3)
            result.put(BaccaratBetType.Tie, Double.valueOf(BaccaratBetType.Tie.rate));
        if (px1.getNum() == px2.getNum()) {
            result.put(BaccaratBetType.PlayerPair, Double.valueOf(BaccaratBetType.PlayerPair.rate));
        } else {
            result.put(BaccaratBetType.PlayerPair, Double.valueOf(-1.0D));
        }
        if (pz1.getNum() == pz2.getNum()) {
            result.put(BaccaratBetType.BankerPair, Double.valueOf(BaccaratBetType.BankerPair.rate));
        } else {
            result.put(BaccaratBetType.BankerPair, Double.valueOf(-1.0D));
        }
        if (px3 == null && pz3 == null) {
            if ((x3 == 9 || x3 == 8) && x3 > z3) {
                result.put(BaccaratBetType.PlayerBonus, Double.valueOf(1.0D));
            } else if (x3 == z3 && (x3 == 8 || x3 == 9)) {
                result.put(BaccaratBetType.PlayerBonus, Double.valueOf(0.0D));
            } else {
                result.put(BaccaratBetType.PlayerBonus, Double.valueOf(-1.0D));
            }
        } else {
            int k = x3 - z3;
            if (k <= 3) {
                result.put(BaccaratBetType.PlayerBonus, Double.valueOf(-1.0D));
            } else {
                result.put(BaccaratBetType.PlayerBonus, Double.valueOf(pl[9 - k] - 1.0D));
            }
        }
        if (px3 == null && pz3 == null) {
            if ((z3 == 9 || z3 == 8) && x3 < z3) {
                result.put(BaccaratBetType.BankerBonus, Double.valueOf(1.0D));
            } else if (x3 == z3 && (z3 == 8 || z3 == 9)) {
                result.put(BaccaratBetType.BankerBonus, Double.valueOf(0.0D));
            } else {
                result.put(BaccaratBetType.BankerBonus, Double.valueOf(-1.0D));
            }
        } else {
            int k = z3 - x3;
            if (k <= 3) {
                result.put(BaccaratBetType.BankerBonus, Double.valueOf(-1.0D));
            } else {
                result.put(BaccaratBetType.BankerBonus, Double.valueOf(pl[9 - k] - 1.0D));
            }
        }
        return result;
    }

    public Map<BaccaratBetType, Double> getAllOdds() {
        Map<BaccaratBetType, Double> results = new HashMap<>();
        results.put(BaccaratBetType.Banker, Double.valueOf(rZWin() * (BaccaratBetType.Banker.rate + 1.0D)));
        results.put(BaccaratBetType.Player, Double.valueOf(rXWin() * (BaccaratBetType.Player.rate + 1.0D)));
        results.put(BaccaratBetType.Tie, Double.valueOf(rHe() * (BaccaratBetType.Tie.rate + 1.0D)));
        results.put(BaccaratBetType.BankerPair, Double.valueOf(rPair() * (BaccaratBetType.BankerPair.rate + 1.0D)));
        results.put(BaccaratBetType.PlayerPair, results.get(BaccaratBetType.BankerPair));
        results.put(BaccaratBetType.BankerBonus, Double.valueOf(zLongBao()));
        results.put(BaccaratBetType.PlayerBonus, Double.valueOf(xLongBao()));
        results.put(BaccaratBetType.Big, Double.valueOf((1.0D - rSmall()) * (BaccaratBetType.Big.rate + 1.0D)));
        results.put(BaccaratBetType.Small, Double.valueOf(rSmall() * (BaccaratBetType.Small.rate + 1.0D)));
        return results;
    }

    public static void main(String[] arg) throws Exception {
        Baccarat pock = new Baccarat();
        pock.init(8);
        logger.info("庄{} {}", Double.valueOf(pock.rZWin()), Double.valueOf(pock.rZWin() * 1.95D));
        logger.info("闲{} {}", Double.valueOf(pock.rXWin()), Double.valueOf(pock.rXWin() * 2.0D));
        logger.info("平{} {}", Double.valueOf(pock.rHe()), Double.valueOf(pock.rHe() * 9.0D));
        logger.info("小{} {}", Double.valueOf(pock.rSmall()), Double.valueOf(pock.rSmall() * 2.5D));
        logger.info("大{} {}", Double.valueOf(1.0D - pock.rSmall()), Double.valueOf((1.0D - pock.rSmall()) * 1.5D));
        logger.info("对{} {}", Double.valueOf(pock.rPair()), Double.valueOf(pock.rPair() * 12.0D));
        logger.info("例牌{} {}", Double.valueOf(pock.rZLp()), Double.valueOf(pock.rZLp() * 5.0D));
        logger.info("闲龙宝 {}", Double.valueOf(pock.xLongBao()));
        logger.info("庄龙宝 {}", Double.valueOf(pock.zLongBao()));
//        double[] rr = new double[4];
//        ThreadPool threadPool = new ThreadPool(4);
//        List<Future<double[]>> futures = new ArrayList<>();
//        for (int shift = 1; shift <= 480; shift++) {
//            pock.init(8);
//            int sft = shift;
//            futures.add(threadPool.submit((Callable) new Object(sft)));
//        }
//        for (Future<double[]> f : futures) {
//            double[] r = f.get();
//            rr[0] = rr[0] + r[0];
//            rr[1] = rr[1] + r[1];
//            if (rr[1] < rr[2])
//                rr[2] = rr[1];
//            if (rr[1] > rr[3])
//                rr[3] = rr[1];
//        }
//        System.out.println(rr[0] + "  " + rr[1] + "    " + rr[2] + "  " + rr[3]);
    }
}

