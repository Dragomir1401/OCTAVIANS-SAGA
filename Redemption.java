import java.io.*;
import java.util.ArrayList;

public class Redemption {
    int  n;
    int m;
    int p;
    ArrayList<ArrayList<String>> sets = new ArrayList<>();
    ArrayList<Integer> solution = new ArrayList<>();
    ArrayList<String> wantedList = new ArrayList<>();
    ArrayList<String> alreadyHasList = new ArrayList<>();

    public void solve() throws IOException {
        // Read input
        readProblemData();

        // Find solution
        find();

        // Write result
        writeAnswer();
    }

    public void find() {
        // Arrays for how many wanted cards each set contains and their initial position in the sets
        ArrayList<Integer> appearancesCounters = new ArrayList<>();
        ArrayList<Integer> initialPosition;


        // Initialise array for counting appearances
        for (int i = 0; i < m; i++)
            appearancesCounters.add(i, 0);


        // While we did not find all cards
        while (!wantedList.isEmpty()) {

            // Find deck with most appearances in wanted list
            for (int index = 0; index < m; index++) {
                int appearances = 0;

                for (String card : sets.get(index)) {
                    if (wantedList.contains(card))
                        appearances++;
                }

                appearancesCounters.set(index, appearances);
            }

            initialPosition = sortIndexesByValue(appearancesCounters);


            // Use that deck and save it
            for (String card : sets.get(initialPosition.get(0)))
                wantedList.remove(card);


            // Add solution
            solution.add(initialPosition.get(0));
        }
    }



    public static ArrayList<Integer> sortIndexesByValue(ArrayList<Integer> arr) {
        // Sort indexes by putting first the ones with more wanted cards
        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < arr.size(); i++) {
            indexes.add(i);
        }

        indexes.sort((a, b) -> -Integer.compare(arr.get(a), arr.get(b)));
        return indexes;
    }



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

            // Create wanted cards list
            if (!alreadyHasList.contains(line))
                wantedList.add(line);
        }


        // Create set input
        Rise.createSetInput(bufferedReader, m, sets);
    }


    public void writeAnswer() {
        // Write size of approximation solution
        System.out.println(solution.size());

        // Write solution itself
        for (Integer integer : solution)
            System.out.print((integer + 1) + " ");
    }

    public static void main(String[] args) throws IOException {
        // Solve Redemption
        Redemption redemption = new Redemption();
        redemption.solve();
    }
}
