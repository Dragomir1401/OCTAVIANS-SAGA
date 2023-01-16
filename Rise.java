import java.io.*;
import java.util.ArrayList;

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
    static int m;
    static int k;
    int p;
    boolean flag = false;
    static ArrayList<ArrayList<String>> sets = new ArrayList<>();
    ArrayList<Integer> solution = new ArrayList<>();
    static ArrayList<ElementCard> elements = new ArrayList<>();
    boolean response;
    ArrayList<String> wantedList = new ArrayList<>();
    ArrayList<String> alreadyHasList = new ArrayList<>();

    @Override
    public void solve() throws IOException, InterruptedException {
        // Read input
        readProblemData();

        // Find minimum of sets needed to cover wantedList
        for (k = 1; k <= m; k++) {
            // Formulate clauses
            formulateOracleQuestion();

            // Ask oracle
            askOracle();

            // Decipher the answer it gives
            decipherOracleAnswer();

            // Write answer
            writeAnswer();

            // If flag is set to true exit means we found the minimum solution
            if (flag)
                break;
        }


        // If solution was not found write that we need all the sets
        if (!flag)
            System.out.println(m);
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


        // Create list of already owned cards
        for (int i = 0; i < p; i++) {
            line = bufferedReader.readLine();

            // Create already has cards list
            alreadyHasList.add(line);
        }


        // Create list of wanted cards
        for (int i = 0; i < n; i++) {
            // Read line by line
            line = bufferedReader.readLine();

            // Create wanted cards list
            wantedList.add(line);
        }


        // Create sets input
        createSetInput(bufferedReader, m, sets);


        // Elements contains info about value and what sets contain it
        // Blank first element to match indexes
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
    }

    static void createSetInput(BufferedReader bufferedReader, int m, ArrayList<ArrayList<String>> sets) throws IOException {
        String line;

        for (int i = 0; i < m; i++) {

            // Parse elements in sets
            int noElements = Integer.parseInt(bufferedReader.readLine());

            // Create current set
            ArrayList<String> currentSet = new ArrayList<>();

            for (int j = 0; j < noElements; j++) {
                line = bufferedReader.readLine();
                currentSet.add(line);
            }

            // Add current set to sets
            sets.add(currentSet);
        }
    }

    public void writeHeader(FileWriter fileWriter) throws IOException {
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
        for (ElementCard element : elements) {
            if (element.getName() != null) {
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

    public void createClauses(int[][] clauses) {
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

        // If negative response exit
        if (!response) {
            return;
        }


        // Print the minimum found
        System.out.println(k);


        // Print solutions found in range 1...m
        if (solution != null)
            for (Integer sol : solution) {
                if (sol % m == 0)
                    System.out.print(m + " ");
                else
                    System.out.print(sol % m + " ");
            }

        // Set flag to found
        flag = true;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Solve Rise
        Rise rise = new Rise();
        rise.solve();
    }
}
