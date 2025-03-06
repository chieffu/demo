package com.chieffu.pocker.baccarat;

import com.chieffu.pocker.Ma;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class BaccTest extends Ma {
    // 深度克隆当前牌堆状态
    int[] pai;

    void init(int n){
        pai = new int[10];
        pai[0]=n*4*4;
        for(int i=1;i<pai.length;i++){
            pai[i]=n*4;
        }
    }
    int countPai(int num){
        if(num<0||num>=10)return countPai();
        return pai[num];
    }
    private int countPai() {
        return Arrays.stream(this.pai).sum();
    }
    private void removePai(int z3) {
        if(z3>=0&&z3<=9){
            pai[z3]--;
        }
    }
    private void addPai(int z3){
        if(z3>=0&&z3<=9){
            pai[z3]++;
        }
    }

    public double[] calculateXWinProbability(int x) {
        long total = 0L;
        long xWinCount = 0L;
        long zWinCount = 0L;
        long heCount = 0L;
        // 遍历庄家前两张牌的所有可能组合
        for (int z1 = 0; z1 <= 9; z1++) {
            for (int z2 = 0; z2 <= 9; z2++) {
                int zInitial = (z1 + z2) % 10;
                if(zInitial==8||zInitial==9||x==8||x==9){
                    long combinations = calculateCombinations(z1, z2, -1, -1, false);
                    total += combinations;
                    if (x > zInitial) {
                        xWinCount += combinations;
                    } else if (x < zInitial) {
                        zWinCount += combinations;
                    } else {
                        heCount += combinations;
                    }
                    continue;
                }
                boolean xDrawThird = (x <= 5); // 闲是否需要补牌
                // 闲家补牌逻辑
                if (xDrawThird) {
                    for (int x3 = 0; x3 <= 9; x3++) {
                        int xAfterDraw = (x + x3) % 10;

                        // 计算庄家是否需要补牌
                        boolean zDraw = shouldBankerDraw(zInitial, x3);

                        // 处理庄家补牌
                        if (zDraw) {
                            for (int z3 = 0; z3 <= 9; z3++) {
                                int zAfterDraw = (zInitial + z3) % 10;

                                // 计算组合数并累加
                                long combinations = calculateCombinations(z1, z2, x3, z3, xDrawThird);
                                total += combinations;
                                if (xAfterDraw > zAfterDraw) {
                                    xWinCount += combinations;
                                } else if (xAfterDraw < zAfterDraw) {
                                    zWinCount += combinations;
                                } else {
                                    heCount += combinations;
                                }
                            }
                        } else {
                            // 庄家不补牌的情况
                            long combinations = calculateCombinations(z1, z2, x3, -1, xDrawThird);
                            total += combinations;
                            if (xAfterDraw > zInitial) {
                                xWinCount += combinations;
                            } else if (xAfterDraw < zInitial) {
                                zWinCount += combinations;
                            } else {
                                heCount += combinations;
                            }
                        }
                    }
                } else {
                    if(!shouldBankerDraw(zInitial, -1)){
                        long combinations = calculateCombinations(z1, z2, -1, -1, false);
                        total += combinations;
                        if (x > zInitial) {
                            xWinCount += combinations;
                        } else if (x < zInitial) {
                            zWinCount += combinations;
                        } else {
                            heCount += combinations;
                        }
                    } else{
                        for (int z3 = 0; z3 <= 9; z3++) {
                            int zAfterDraw = (zInitial + z3) % 10;

                            long combinations = calculateCombinations(z1, z2, -1, z3, false);
                            total += combinations;
                            if (x > zAfterDraw) {
                                xWinCount += combinations;
                            } else if (x < zAfterDraw) {
                                zWinCount += combinations;
                            } else {
                                heCount += combinations;
                            }
                        }
                    }
                }
            }
        }
//        log.info("组合:{}  total:{}  {}", p(countPai(), 4), total, p(countPai(), 4) == total ? "true" : "false");
        return total == 0 ? new double[]{0, 0, 0} : new double[]{(double) xWinCount / total, (double) zWinCount / total, (double) heCount / total};
    }

    // 判断庄家是否需要补牌
    private boolean shouldBankerDraw(int bankerPoints, Integer playerThirdCard) {
        if (playerThirdCard == -1) {
            return bankerPoints <= 5;
        }
        if (bankerPoints >= 7) return false;
        if (bankerPoints <= 2) return true;


        int thirdCardValue = playerThirdCard;
        switch (bankerPoints) {
            case 3: return thirdCardValue != 8;
            case 4: return !(thirdCardValue == 0 || thirdCardValue == 1 || thirdCardValue == 8 || thirdCardValue == 9);
            case 5: return !(thirdCardValue >= 0 && thirdCardValue <= 3 || thirdCardValue == 8 || thirdCardValue == 9);
            case 6: return thirdCardValue == 6 || thirdCardValue == 7;
            default: return false;
        }
    }

    // 计算组合数（需根据实际剩余牌数实现）
    private long calculateCombinations(int z1, int z2, int x3, int z3, boolean xDrawThird) {
        // 示例实现框架：
        int cz1 = countPai(z1);
        removePai(z1);
        int cz2 = countPai(z2);
        removePai(z2);
        int cx3 = 1;
        if(x3!=-1){
            cx3 = countPai(x3);
            removePai(x3);
        }
        int zx3 = 1;
        if(z3!=-1){
            zx3 = countPai(z3);
           removePai(z3);
        }
        long combinations = 1l*cz1*cz2*cx3*zx3 ;
        if(z3==-1&&x3==-1){
            combinations = combinations*p(countPai(),2) ;
        }else if(z3==-1||x3==-1){
            combinations = combinations*p(countPai(),1) ;
        }
        addPai(z1);
        addPai(z2);

        if(x3!=-1){
            addPai(x3);
        }
        if(z3!=-1){
            addPai(z3);
        }
        return combinations;
    }

    // 凯利公式方法
    public static double kellyCriterion(double p, double b) {
        double q = 1 - p;
        return (b * p - q) / b;
    }

    public static void main(String[] arg) throws Exception {
        BaccTest pock = new BaccTest();
        pock.init(8);
        for (int i = 0; i <= 9; i++) {
            double[] r = pock.calculateXWinProbability(i);
            log.info("{}点概率--闲:{}  庄:{}  和:{} | 期望--闲:{}  庄:{}  和:{} | 凯利公式:闲:{}  庄:{}  和:{}",  i,
                    String.format("%.3f", r[0]),
                    String.format("%.3f", r[1]),
                    String.format("%.3f", r[2]),
                    String.format("%.3f", r[0]*2),
                    String.format("%.3f", r[1]*1.95),
                    String.format("%.3f", r[2]*9),
                    String.format("%.3f", kellyCriterion(r[0], 1)),
                    String.format("%.3f", kellyCriterion(r[1],0.95)),
                    String.format("%.3f", kellyCriterion(r[2],8))
                    );

//            log.info("{}-庄:{}", i, pock.calculateZWinProbabilityGivenX(i));
        }
    }
}
