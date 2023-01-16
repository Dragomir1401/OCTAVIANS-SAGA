import java.io.*;
import java.util.ArrayList;

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
    static int  n;
    static int m;
    static int k;
    static ArrayList<ArrayList<Integer>> sets = new ArrayList<>();
    static ArrayList<Integer> solution = new ArrayList<>();
    static ArrayList<Element> elements = new ArrayList<>();
    static boolean response;

    @Override
    public void solve() throws IOException, InterruptedException {
        // Read input
        readProblemData();

        // Formulate clauses
        formulateOracleQuestion();

        // Ask SAT solver
        askOracle();

        // Decipher answer
        decipherOracleAnswer();

        // Write answer to output
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

            // Sets is a set of arrays of Integers
            for (int j = 0; j < Integer.parseInt(elements[0]); j++) {
                currentSet.add(Integer.parseInt(elements[1 + j]));
            }

            // Add made set to sets
            sets.add(currentSet);
        }


        // Elements contains info about value and what sets contain it
        // Blank first element to match indexes
        elements.add(new Element(-1, new ArrayList<>()));

        int i = 1;
        while (i <= n) {
            elements.add(new Element(i, new ArrayList<>()));

            for (int setNr = 1; setNr <= sets.size(); setNr++) {
                for (Integer integer : sets.get(setNr - 1)) {
                    if (integer.equals(i)) {
                        elements.get(i).getWhatSetsContainsIt().add(setNr);
                    }
                }
            }
            i++;
        }
    }

    public static void writeHeader(FileWriter fileWriter) throws IOException {
        // Write number of clauses matching each for maxes
        int nrClauses = 2 * k * m + k * (m - 1) * (m - 1) + n;
        fileWriter.write("p cnf " + m * k + " " + nrClauses);
        fileWriter.write("\n");
    }

    public static void kPossibleElement(FileWriter fileWriter, int[][] clauses) throws IOException {
        // k * m clauses of matching each possible element in wanted pool
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; j++) {
                fileWriter.write(clauses[i][j] + " ");
            }
            fileWriter.write("0\n");
        }
    }

    public static void singularChoice(FileWriter fileWriter, int[][] clauses) throws IOException {
        // k * m clauses of choosing only one element per each position in solution
        if (sets.size() > 1 && k > 1) {
            for (int i = 0; i < m; i++) {
                for (int l = 0; l < k; l++)
                    fileWriter.write(-clauses[l][i] + " ");
                fileWriter.write("0\n");
            }
        }
    }


    public static void chooseOnlyOneSetPerPosition(FileWriter fileWriter, int[][] clauses) throws IOException {
        // Max k * (m - 1) * (m - 1) clauses which state not being able to put
        // two different sets on the same position
        for (int l = 0; l < k; l++) {
            for (int i = 0; i < m - 1; i++) {
                for (int j = i + 1; j < m; j++) {
                    fileWriter.write(-clauses[l][i] + " ");
                    fileWriter.write(-clauses[l][j] + " ");
                    fileWriter.write("0\n");
                }
            }
        }
    }

    public static void contentClause(FileWriter fileWriter, int[][] clauses) throws IOException {
        // Clauses that make sure each element from 1...n appear in the solution by choosing
        // at least one set which contain each element
        for (Element element : elements) {
            if (element.getValue() > 0) {
                for (Integer set : element.getWhatSetsContainsIt())
                    for (int i = 0; i < k; i++)
                        fileWriter.write(clauses[i][set - 1] + " ");
                fileWriter.write("0\n");
            }
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        // Initialise file writer
        FileWriter fileWriter = new FileWriter("sat.cnf");

        // Write header with p cnf nrOfVariables and then NrOfClauses
        writeHeader(fileWriter);

        // Create clauses numbering from 1...n * m stating
        // clauses[i][j] means element i was taken from set j
        int[][] clauses = new int[k][m];
        createClauses(clauses);

        // Write clause for first possible element, second, ..., k-th possible element
        kPossibleElement(fileWriter, clauses);


        // Max one choice of set i for i = 1...n
        singularChoice(fileWriter, clauses);

        // Clauses for choosing only one set for each position
        chooseOnlyOneSetPerPosition(fileWriter, clauses);


        // Final clause to choose at least one set that contains each element seeked
        contentClause(fileWriter, clauses);

        // Close file writer
        fileWriter.close();
    }

    public static void createClauses(int[][] clauses) {
        // Create clauses with consecutive numbers
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


        // Read result
        result = String.valueOf(bufferedReader.readLine());
        bufferedReader.readLine();


        // Exit in case of false result
        if (result.equals("False")) {
            response = false;
            return;
        }


        // Add into solution variables that are detected as true
        String[] arrResult = bufferedReader.readLine().split(" ");
        for (String s : arrResult) {
            if (Integer.parseInt(s) > 0) {
                solution.add(Integer.parseInt(s));
            }
        }


        // Set response to true and exit
        response = true;
        bufferedReader.close();
    }

    @Override
    public void writeAnswer() {

        // False response is written as such
        if (!response) {
            System.out.print("False\n");
            return;
        }

        // True response is written with solution size and solution ids
        System.out.print("True\n");
        System.out.println(solution.size());

        // Write solutions in range of [1, m] translating constructed variable
        if (solution != null) {
            for (Integer sol : solution) {
                if (sol % m == 0)
                    System.out.print(m + " ");
                else
                    System.out.print(sol % m + " ");
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Solve trial
        Trial trial = new Trial();
        trial.solve();
    }
}
