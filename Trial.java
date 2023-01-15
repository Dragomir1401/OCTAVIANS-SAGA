import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
class Element {
    int value;
    int whatSetContainsIt;

    public Element(int value, int whatSetContainsIt) {
        this.value = value;
        this.whatSetContainsIt = whatSetContainsIt;
    }

    public int getValue() {
        return value;
    }

    public int getWhatSetContainsIt() {
        return whatSetContainsIt;
    }

}

public class Trial extends Task {
    int  n;
    int m;
    int k;
    ArrayList<ArrayList<Integer>> sets = new ArrayList<>();
    ArrayList<Integer> solution = new ArrayList<>();
    ArrayList<Element> elements = new ArrayList<>();

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
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

        for (int i = 1; i <= n; i++) {
            int freq = 0;
            int setNr = 0;
            for (int poz = 0; poz < sets.size(); poz++) {
                for (Integer integer : sets.get(poz)) {
                    if (integer.equals(i)) {
                        freq++;
                        setNr = poz;
                    }
                }
            }
            if (freq == 1) {
                elements.add(new Element(i, setNr));
            }
        }
    }


    public boolean containsValue(int value) {
        for (Element element : elements) {
            if (element.getValue() == value)
                return true;
        }
        return false;
    }
    @Override
    public void formulateOracleQuestion() throws IOException {
        // Initialise file writer
        FileWriter fileWriter = new FileWriter("sat.cnf");
        fileWriter.write("p cnf " + m * k + " " + n);
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
        for (int i = 0; i < m ; i++) {
            fileWriter.write(-clauses[0][i] + " ");
            fileWriter.write(-clauses[1][i] + " ");
            fileWriter.write("0\n");
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

        // Clauses for edges to have vertices on cover
        for (int i = 0; i < m * k; i++) {
            fileWriter.write(i + 1 + " ");
        }
        fileWriter.write("0\n");

        // Clauses for unique elements
        for (Element element : elements) {
            for (int i = 0; i < k; i++) {
                fileWriter.write(clauses[i][element.getWhatSetContainsIt()] + " ");
            }
            fileWriter.write("0\n");
        }

        // Final clause
        for (int i = 0; i < m; i++)
            if (!containsValue(i + 1)) {
                for (int l = 0; l < k; l++)
                    fileWriter.write(clauses[l][i] + " ");
            }
        fileWriter.write("0\n");
        
        fileWriter.close();

    }

    public static void generateCombinations(int[] combination, int start, int m, int k, int[][] matrix, int index) {
        if (k == 0) {
            matrix[index] = combination.clone();
            return;
        }
        for (int i = start; i <= m - k + 1; i++) {
            combination[k-1] = i;
            generateCombinations(combination, i+1, m, k-1, matrix, index);
            index++;
        }
    }

    public int calculateCombinations(int n, int k) {
        int Cnk = 1;
        for (int i = 2; i <= n; i++)
            Cnk *= i;

        int kf = 1;
        for (int i = 2; i <= k; i++)
            kf *= i;

        int nkf = 1;
        for (int i = 2; i <= n - k; i++)
            nkf *= i;

        Cnk = Cnk / kf / nkf;
        return Cnk;
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
            return;
        }

        String[] arrResult = bufferedReader.readLine().split(" ");
        for (String s : arrResult) {
            if (Integer.parseInt(s) > 0) {
                solution.add(Integer.parseInt(s));
            }
        }

        bufferedReader.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        if(solution != null)
            for(Integer sol : solution) {
                System.out.print(sol + " ");
            }
        System.out.println();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Trial trial = new Trial();
        trial.solve();
    }
}
