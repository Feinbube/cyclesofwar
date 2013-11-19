package cyclesofwar.window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HumanReadableLongConverter {

    final String splitValue = " ";
    List<String> wordlist = null;

    public HumanReadableLongConverter(List<String> wordList) {
        this.wordlist = wordList;
    }

    public HumanReadableLongConverter(String dictionaryFileName) {
        try {
            this.wordlist = readFile(dictionaryFileName);
        } catch (Exception ex) {
            this.wordlist = new ArrayList<>();
            Logger.getLogger(HumanReadableLongConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HumanReadableLongConverter() {
        this("/cyclesofwar/resources/dictionary.txt");
    }

    private List<String> readFile(String fileName) throws IOException {
        InputStream stream = getClass().getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));        
        try {
            List<String> result = new ArrayList<>();
            String line = br.readLine();

            while (line != null) {
                if(line.contains("/")){
                    result.add(line.substring(0, line.indexOf("/")));
                } else {
                    result.add(line);
                }
                line = br.readLine();
            }
            return result;
        } finally {
            br.close();
        }
    }

    public String ToString(long value) {
        String result = "";

        if (wordlist.isEmpty()) {
            return "" + value;
        }

        long current = value;
        while (current > 0) {
            result += wordlist.get((int) (current % wordlist.size())) + splitValue;
            current /= wordlist.size();
        }

        return result.trim();
    }

    public long FromString(String valueAsString) {
        long result = 0;

        if (wordlist.isEmpty()) {
            return 0;
        }

        List<String> words = Arrays.asList(valueAsString.split(splitValue));
        Collections.reverse(words);
        for (int i = 0; i < words.size(); i++) {
            result += Math.pow(wordlist.size(), i) * wordlist.indexOf(words);
        }

        return result;
    }
}
