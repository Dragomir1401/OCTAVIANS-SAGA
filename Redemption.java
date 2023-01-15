import org.w3c.dom.ls.LSException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Redemption {
    int  n;
    int m;
    int p;
    boolean flag = false;
    ArrayList<ArrayList<String>> sets = new ArrayList<>();
    ArrayList<Integer> solution = new ArrayList<>();
    ArrayList<ElementCard> elements = new ArrayList<>();
    boolean response;
    ArrayList<String> wantedList = new ArrayList<>();
    ArrayList<String> alreadyHasList = new ArrayList<>();

    public void solve() throws IOException {
        this.readProblemData();
        this.find();
        this.writeAnswer();
    }

    public void find() {

        ArrayList<Integer> appearancesCounters = new ArrayList<>();
        ArrayList<Integer> initialPosition;

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

            solution.add(initialPosition.get(0));
        }

    }



    public static ArrayList<Integer> sortIndexesByValue(ArrayList<Integer> arr) {
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

    }


    public void writeAnswer() throws IOException {
        System.out.println(solution.size());
        for (int index = 0; index < solution.size(); index++)
            System.out.print((solution.get(index) + 1) + " ");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Redemption redemption = new Redemption();
        redemption.solve();
    }
}
