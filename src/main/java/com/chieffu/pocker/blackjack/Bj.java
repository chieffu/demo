package com.chieffu.pocker.blackjack;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Bj {
//    @Getter
//    static class Stage{
//        Stage parent;
//        List<Stage> next;
//        Integer pai;
//        int[] dot ;
//        public Stage addStage(Integer card){
//            if(next==null)return null;
//            Stage stage = new Stage();
//            stage.pai = card;
//            stage.parent = this;
//            stage.dot = stage.dot();
//            next.add(stage);
//            if(stage.largeDot()>16&& stage.largeDot()<=21||stage.smallDot()>16){
//                stage.next = null;
//            }else{
//                stage.next = new LinkedList<>();
//            }
//            return stage;
//        }
//        public List<Integer> getCards(){
//            List<Integer> list = new LinkedList<>();
//            Stage st = this;
//            while(st!=null&&st.pai!=null){
//                list.add(0,st.pai);
//                st=st.parent;
//            }
//            return list;
//        }
//
//        private int[] dot(){
//            List<Integer> cards = getCards();
//            int sum = cards.stream().mapToInt(Integer::intValue).sum();
//            if (sum <= 11 && cards.contains(1)) {
//                return new int[]{sum, sum + 10};
//            }else{
//                return new int[]{sum};
//            }
//        }
//
//        public int largeDot(){
//            return dot[dot.length-1];
//        }
//        public int smallDot(){
//            return dot[0];
//        }
//
//        public String toString(){
//            return pai==null?"--":largeDot()+" \t "+getCards().stream().map(String::valueOf).collect(Collectors.joining(","));
//        }
//
//        public double rate(int[] pai){
//            double result = 1.0;
//            List<Integer> cards = getCards();
//            int total = Arrays.stream(pai).sum();
//            for(int card:cards){
//                result*=pai[card]-- / (double)total--;
//            }
//            for(int card:cards){
//                pai[card]++;
//            }
//            if(result<0)result = 0;
//            return result;
//        }
//    }
//    Stage zStage0;
//    public Bj(){
//        Stage root = new Stage();
//        root.next = new LinkedList<>();
//        Queue<Stage> stages = new LinkedList<>();
//        stages.add(root);
//        addSubStage(stages);
//        this.zStage0 = root;
//    }
//
//    private void addSubStage(Queue<Stage> stages){
//        while(!stages.isEmpty()) {
//            Stage root = stages.poll();
//            for (int i = 1; i <= 10; i++) {
//                Stage stage = root.addStage(i);
//                if (stage != null) {
//                    stages.add(stage);
//                }
//            }
//        }
//    }
//    private void addFilterEndStage(Queue<Stage> notEndNode,List<Stage> result){
//        while(!notEndNode.isEmpty()) {
//            Stage root = notEndNode.poll();
//            if(root.getNext()==null) {
//                result.add(root);
//                continue;
//            }
//            for(Stage stage:root.getNext()){
//                notEndNode.add(stage);
//            }
//        }
//    }
//
//    public List<Stage> getEndStages(){
//        LinkedList<Stage> stages = new LinkedList<>();
//        stages.add(zStage0);
//        List<Stage> result = new LinkedList<>();
//        addFilterEndStage(stages,result);
//        return result;
//    }
//    public Map<Integer,Double> zRate(int[] pai){
//        Map<Integer,Double> rates = new HashMap<>();
//        List<Stage> stages = getEndStages();
//        Map<Integer,List<Stage>> groups = stages.stream().collect(Collectors.groupingBy(stage->stage.largeDot()));
//        for(int i=17;i<=26;i++){
//            List<Stage> stages1= groups.get(i);
//
//            double rate = 0.0;
//            for (Stage s : stages1) {
//                double r = s.rate(pai);
//               // System.out.println(s + " \t " + r);
//                rate += r;
//            }
//            System.out.println( "---------- dot:" + i + " -------------------size:"+stages1.size()+ " rate: "+rate);
//            rates.put(i,rate);
//        }
//        return rates;
//
//    }

    public static void main(String[] args){
        Blackjack bj = new Blackjack(8);
        long start = System.currentTimeMillis();
        int[] pai =bj.getPai();
        bj.zRate(pai,0);
        long end = System.currentTimeMillis();
        System.out.println("spend time :"+ (end-start)+" ms");
    }

}
