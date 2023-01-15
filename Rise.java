import org.w3c.dom.ls.LSException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

class ElementCard {
    String name;
    ArrayList<Integer> whatSetsContainsIt;

    public ElementCard(String name, ArrayList<Integer> whatSetsContainsIt) {
        this.name = name;
        this.whatSetsContainsIt = whatSetsContainsIt;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getWhatSetsContainsIt() {
        return whatSetsContainsIt;
    }

    @Override
    public String toString() {
        return "Element{" +
                "value=" + name +
                ", whatSetsContainsIt=" + whatSetsContainsIt +
                '}';
    }
}
public class Rise extends Task {
    int  n;
    int m;
    int k;
    int p;
    boolean flag = false;
    ArrayList<ArrayList<String>> sets = new ArrayList<>();
    ArrayList<Integer> solution = new ArrayList<>();
    ArrayList<ElementCard> elements = new ArrayList<>();
    boolean response;
    ArrayList<String> wantedList = new ArrayList<>();
    ArrayList<String> alreadyHasList = new ArrayList<>();

    @Override
    public void solve() throws IOException, InterruptedException {
        this.readProblemData();
        for (k = 1; k <= m; k++) {
            this.formulateOracleQuestion();
            askOracle();
            this.decipherOracleAnswer();
            this.writeAnswer();
            if (flag)
                break;
        }

        if (!flag)


    }

    @Override
    public void readProblemData() throws IOException {
        // Create a buffered reader
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader  bufferedReader = new BufferedReader(inputStreamReader);

        // Get first line
        String line = bufferedReader.readLine();
        String[] dimensions = line.split(" ");

        // Find dimensions
        p = Integer.parseInt(dimensions[0]);
        n = Integer.parseInt(dimensions[1]);
        m = Integer.parseInt(dimensions[2]);


        for (int i = 0; i < p; i++) {
            line = bufferedReader.readLine();

            // Create already has cards list
            alreadyHasList.add(line);
        }




        for (int i = 0; i < n; i++) {
            // Read line by line
            line = bufferedReader.readLine();

            // Create wanted cars list
            wantedList.add(line);
        }



        for (int i = 0; i < m; i++) {

            // Parse elements in sets
            int noElements = Integer.parseInt(bufferedReader.readLine());

            ArrayList<String> currentSet = new ArrayList<>();

            for (int j = 0; j < noElements; j++) {
                line = bufferedReader.readLine();
                currentSet.add(line);
            }

            sets.add(currentSet);
        }



        elements.add(new ElementCard(null, new ArrayList<>()));
        int counter = 1;
        for (String card : wantedList) {
            if (!alreadyHasList.contains(card)) {
                elements.add(new ElementCard(card, new ArrayList<>()));

                for (int setNr = 1; setNr <= sets.size(); setNr++) {
                    for (String string : sets.get(setNr - 1)) {
                        if (string.equals(card)) {
                            elements.get(counter).getWhatSetsContainsIt().add(setNr);
                        }
                    }
                }
            counter++;
            }
        }

//        System.out.println(alreadyHasList);
//        System.out.println();
//
//        System.out.println(wantedList);
//        System.out.println();
//
//        System.out.println(sets);
//        System.out.println();
//
//
//        System.out.println(elements);
//        System.out.println();

    }


    @Override
    public void formulateOracleQuestion() throws IOException {
        // Initialise file writer
        FileWriter fileWriter = new FileWriter("sat.cnf");

        int nrClauses = 2 * k * m + k * (m - 1) * (m - 1) + n;
        fileWriter.write("p cnf " + m * k + " " + nrClauses);
        fileWriter.write("\n");

        // Create clauses numbering from 1...n * m stating
        // clauses[i][j] means element i was taken from set j
        int[][] clauses = new int[k][m];
        createClauses(clauses);

        // Write clause for first possible element, second, ..., k-th element
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; j++) {
                fileWriter.write(clauses[i][j] + " ");
            }
            fileWriter.write("0\n");
        }


        // Max one choice of set i for 1...n
        if (sets.size() > 1 && k > 1) {
            for (int i = 0; i < m; i++) {
                for (int l = 0; l < k; l++)
                    fileWriter.write(-clauses[l][i] + " ");
                fileWriter.write("0\n");
            }
        }

        // Clauses for unit of choice pe index
        for (int l = 0; l < k; l++) {
            for (int i = 0; i < m - 1; i++) {
                for (int j = i + 1; j < m; j++) {
                    fileWriter.write(-clauses[l][i] + " ");
                    fileWriter.write(-clauses[l][j] + " ");
                    fileWriter.write("0\n");
                }
            }
        }


        // Final clause to see what sets contain each element
        for (ElementCard element : elements) {
            if (element.getName() != null) {
                for (Integer set : element.getWhatSetsContainsIt())
                    for (int i = 0; i < k; i++)
                        fileWriter.write(clauses[i][set - 1] + " ");
                fileWriter.write("0\n");
            }
        }



        fileWriter.close();

    }

    public void createClauses(int[][] clauses) {
        int counter = 1;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; j++) {
                clauses[i][j] = counter;
                counter++;
            }
        }

    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // Create result string
        String result;

        // Open sol for reading oracle output
        File file = new File("sat.sol");
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(file));

        result = String.valueOf(bufferedReader.readLine());
        bufferedReader.readLine();

        if (result.equals("False")) {
            response = false;
            return;
        }

        response = true;
        bufferedReader.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        if (!response) {
            return;
        }

        System.out.println(k);
        flag = true;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Rise rise = new Rise();
        rise.solve();
    }
}
