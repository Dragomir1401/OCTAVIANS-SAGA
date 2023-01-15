import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
class Element {
    int value;
    ArrayList<Integer> whatSetsContainsIt;

    public Element(int value, ArrayList<Integer> whatSetContainsIt) {
        this.value = value;
        this.whatSetsContainsIt = whatSetContainsIt;
    }

    public int getValue() {
        return value;
    }

    public ArrayList<Integer> getWhatSetsContainsIt() {
        return whatSetsContainsIt;
    }

    @Override
    public String toString() {
        return "Element{" +
                "value=" + value +
                ", whatSetsContainsIt=" + whatSetsContainsIt +
                '}';
    }
}

public class Trial extends Task {
    int  n;
    int m;
    int k;
    ArrayList<ArrayList<Integer>> sets = new ArrayList<>();
    ArrayList<Integer> solution = new ArrayList<>();
    ArrayList<Element> elements = new ArrayList<>();
    boolean response;

    @Override
    public void solve() throws IOException, InterruptedException {
        this.readProblemData();
        this.formulateOracleQuestion();
        askOracle();
        this.decipherOracleAnswer();
        this.writeAnswer();
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
        n = Integer.parseInt(dimensions[0]);
        m = Integer.parseInt(dimensions[1]);
        k = Integer.parseInt(dimensions[2]);

        for (int i = 0; i < m; i++) {
            // Read line by line
            line = bufferedReader.readLine();

            // Parse elements in sets
            String[] elements = line.split(" ");
            ArrayList<Integer> currentSet = new ArrayList<>();

            for (int j = 0; j < Integer.parseInt(elements[0]); j++) {
                currentSet.add(Integer.parseInt(elements[1 + j]));
            }

            sets.add(currentSet);
        }

        elements.add(new Element(-1, new ArrayList<>()));
        for (int i = 1; i <= n; i++) {
            elements.add(new Element(i, new ArrayList<>()));
            for (int setNr = 1; setNr <= sets.size(); setNr++) {
                for (Integer integer : sets.get(setNr - 1)) {
                    if (integer.equals(i)) {
                        elements.get(i).getWhatSetsContainsIt().add(setNr);
                    }
                }
            }
        }
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
        for (Element element : elements) {
            if (element.getValue() > 0) {
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

        if(result.equals("False")) {
            response = false;
            return;
        }

        String[] arrResult = bufferedReader.readLine().split(" ");
        for (String s : arrResult) {
            if (Integer.parseInt(s) > 0) {
                solution.add(Integer.parseInt(s));
            }
        }
        response = true;
        bufferedReader.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        if (!response) {
            System.out.print("False\n");
            return;
        }
        System.out.print("True\n");
        System.out.println(solution.size());

        if (solution != null)
            for (Integer sol : solution) {
                if (sol % m == 0)
                    System.out.print(m + " ");
                else
                    System.out.print(sol % m + " ");
            }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Trial trial = new Trial();
        trial.solve();
    }
}
