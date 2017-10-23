package lab1;

import java.io.*;
import java.lang.Character;
import java.util.*;
import java.util.regex.Pattern;

public class ProjectOne {
  static int[][] e;
  static int[][] d;
  static int[][] path;
  static String[] txtWordArray;
  static int wordNum = 0;
  static boolean flag = true;
  static final int INFINITY = 10000;
  static StringBuffer preStr = new StringBuffer();
  static StringBuffer pathWay = new StringBuffer();
  static StringBuffer randomPath = new StringBuffer();
  static List<String> wordList = new ArrayList<String>();
  static List<String> edgePairList = new ArrayList<String>();
  static HashMap<String, List<String>> map = new HashMap<String, List<String>>();
  static Pattern p = Pattern.compile("[.,\"\\?!:' ]");
  
  public static void main(String[] args) throws IOException { 
    Scanner scanner1 = new Scanner(System.in);
    System.out.println("Please input file adress : ");
    String fileAdr = scanner1.nextLine();
    final InputStream fi = new FileInputStream(fileAdr);
    int c;
    String word1;
    String word2;
    while ((c = fi.read()) != -1) {
      Character m = new Character((char) c);
      if (Character.isLetter(m)) {
        preStr.append(m.toString());
        } else if (p.matcher(m.toString()).matches()) {
                preStr.append(" ");
            }
        }
        // create word array.
        txtWordArray = preStr.toString().toLowerCase(Locale.ROOT).trim().split("\\s+");
        System.out.print("The Text equal to : ");
        for (String word : txtWordArray) {
            System.out.print(word + " ");
            if (!wordList.contains(word)) {
                wordList.add(word);
                wordNum++; // recored word number
            }
        }
        System.out.println("\nThe number of word is ：" + wordNum);
        e = new int[wordNum][wordNum]; // 建立边集
        buildEdge();
        Graph graph = new Graph(wordList, e, wordNum); // 创建图的实例
        showDirectedGraph(graph);
        createBridgeMap();
        System.out.println("Please input word1 and word2 to query bridge : ");
        word1 = scanner1.nextLine();
        word2 = scanner1.nextLine();
        System.out.println(queryBridgeWords(word1, word2));
        System.out.println("Please input string to create new text : ");
        String inputText = scanner1.nextLine();
        System.out.println(generateNewText(inputText));
        floyd();
        System.out.println("input word1 and word2 to query : ");
        word1 = scanner1.nextLine();
        word2 = scanner1.nextLine();
        System.out.println(calcShortestPath(word1, word2));
        System.out.println("input to find the shortest path to others :");
        word1 = scanner1.nextLine();
        findPathToOther(word1);
        String randomText = randomWalk();
        System.out.println(randomText);
        try {
            File fo = new File("/Users/summerchaser/Desktop/2.txt");
            FileWriter fileWriter = new FileWriter(fo);
            fileWriter.write(randomText);
            fileWriter.close(); // 关闭数据流
        } catch (IOException e) {
            e.printStackTrace();
        }
        fi.close();
    }

    static String queryBridgeWords(String word1, String word2) {
        if (wordList.contains(word1) && wordList.contains(word2)) {
            String key ;
            key = word1 + "#" + word2;
            if (!(wordList.contains(word1) && wordList.contains(word2))) {
                return "No word1 or word2 in the graph!";
            } else if (!map.containsKey(key)) {
                return "No bridge words from word1 to word2!";
            } else {
                StringBuffer result = new StringBuffer("The bridge from "
            + word1 + " to " + word2 + "  are: ");
                for (String bridge : map.get(key)) {
                    result.append(bridge + " ");
                }
                return result.toString();
            }
        } else {
            return "input error";
        }

    }

    protected static void showDirectedGraph(Graph graph) {
        System.out.println("The graph presented is ：");
        for (int i = 0; i < graph.wordNum; i++) {
            for (int j = 0; j < graph.wordNum; j++) {
                if (e[i][j] != INFINITY) {
                    String word1 = graph.wordList.get(i);
                    String word2 = graph.wordList.get(j);
                    System.out.printf("Edge: " + word1 + " --> " 
                    + word2 + "   Weigth: %d\n", e[i][j]);
                }
            }
        }
    }

    protected static void buildEdge() {
        int preNum;
        int curNum;
        int i;
        int j;
        String pre ;
        pre = "#"; 
        for (String word : txtWordArray) {
            if (pre != "#") {
                preNum = wordList.indexOf(pre);
                curNum = wordList.indexOf(word);
                e[preNum][curNum]++;
                pre = word;
            } else {
                pre = word;
            }
        }
        System.out.println("The graph matrix is ：");
        for (i = 0; i < wordNum; i++) {
            for (j = 0; j < wordNum; j++) {
                if (e[i][j] == 0) {
                    e[i][j] = INFINITY;
                    System.out.printf("0 ");
                } else {
                    System.out.printf("%d ", e[i][j]);
                }
            }
            System.out.println("");
        }
    }

    protected static void createBridgeMap() {
        int i;
        for (i = 0; i < txtWordArray.length - 2; i++) {
            String key = txtWordArray[i] + "#" + txtWordArray[i + 2];
            if (map.containsKey(key)) {
                map.get(key).add(txtWordArray[i + 1]);
            } else {
                final List<String> valueList = new ArrayList<String>();
                valueList.add(txtWordArray[i + 1]);
                map.put(key, valueList);
            }
        }
    }

    protected static String generateNewText(String inputText) {
        int i;
        String[] textword = inputText.toLowerCase().trim().split("\\s+");
        StringBuffer newText = new StringBuffer();
        newText.append(textword[0] + " ");
        System.out.println("New text created is ：");
        for (i = 0; i < textword.length - 1; i++) {
            String key = textword[i] + "#" + textword[i + 1];
            if (map.containsKey(key)) {
                String bridge = map.get(key).get(0);
                newText.append(bridge + " " + textword[i + 1] + " ");
            } else {
                newText.append(textword[i + 1] + " ");
            }
        }
        return newText.toString();
    }

    protected static void getPath(int start, int end) {
        if (path[start][end] == -1) {
            return;
        } else {
            getPath(start, path[start][end]);
            pathWay.append(wordList.get(path[start][end]) + " -> ");
        }
    }

    static String calcShortestPath(String word1, String word2) {
        if (wordList.contains(word1) && wordList.contains(word2)) {
            System.out.println("The path " + word1 + " to " + word2 + " is :");
            int start = wordList.indexOf(word1);
            int end = wordList.indexOf(word2);
            if (d[start][end] != INFINITY) {
                pathWay.append(word1 + " -> ");
                getPath(start, end);
                pathWay.append(word2);
                String py = pathWay.toString();
                pathWay.delete(0, pathWay.length());
                return py;
            } else {
                return "no access";
            }
        } else {
            return "input error";
        }
    }

    static void findPathToOther(String word) {
        int s;
        s = wordList.indexOf(word);
        int i;
        for (i = 0; i < wordNum; i++) {
            pathWay.delete(0, pathWay.length());
            if (i != s) {
                final String word1 = wordList.get(s);
                final String word2 = wordList.get(i);
                System.out.println(calcShortestPath(word1, word2));
            }
        }
    }

    static void floyd() {
        int i;
        int j;
        d = new int[wordNum][wordNum];
        path = new int[wordNum][wordNum];
        for (i = 0; i < wordNum; i++) {
            for (j = 0; j < wordNum; j++) {
                d[i][j] = e[i][j];
                path[i][j] = -1;
            }
        }
        for (int k = 0; k < wordNum; k++) {
            for (i = 0; i < wordNum; i++) {
                for (j = 0; j < wordNum; j++) {
                    if (d[i][k] + d[k][j] < d[i][j]) {
                        d[i][j] = d[i][k] + d[k][j];
                        path[i][j] = k;
                    }
                }
            }
        }
    }

    static boolean isEnd(final int s) {
        for (int i = 0; i < wordNum; i++) {
            String edgePair = String.valueOf(s) + "#" + String.valueOf(i);
            if (e[s][i] != INFINITY && !edgePairList.contains(edgePair)) { // 如果存在边
                return false;
            }
        }
        return true; // 不存在边，即是尽头
    }

    static String randomWalk() {
        int ranNum = (int) Math.round(Math.random() * (wordNum - 1));
        String ranWord = wordList.get(ranNum);
        randomPath.append(ranWord);
        System.out.println("System choose -> " + ranWord + "\nRandom walk is ：");
        walkFrom(ranNum);
        String rp = randomPath.toString();
        randomPath.delete(0, randomPath.length());
        return rp;
    }

    static void walkFrom(final int s) {
        for (int i = 0; i < wordNum; i++) {
            if (flag) {
                final String edgePair = String.valueOf(s) + "#" + String.valueOf(i);
                if (e[s][i] != INFINITY && !edgePairList.contains(edgePair)) {
                    edgePairList.add(edgePair);
                    randomPath.append(" -> " + wordList.get(i));
                    walkFrom(i);
                } else if (isEnd(s)) {
                    flag = false;
                    return;
                }
            }
        }
    }
}
