package lab1;

import java.util.List;
//修改记录7
public class Graph { int wordNum;// 顶点数
  List<String> wordList; // 顶点集
  int[][] e; // 边集

  public Graph(final List<String> wordList,final int[][] e, final int wordNum) {
    this.wordList = wordList;
    this.e = e;
    this.wordNum = wordNum;
   
  }

}