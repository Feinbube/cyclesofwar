package cyclesofwar.window;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            wordlist = Files.readAllLines(Paths.get(dictionaryFileName), Charset.defaultCharset());
        } catch (IOException ex) {
            Logger.getLogger(HumanReadableLongConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    public String ToString(long value) {
        String result = "";
        
        if(wordlist.isEmpty())
            return "" + value;
        
        long current = value;
        while(current > 0) {
            result += wordlist.get((int)(current % wordlist.size())) + splitValue;
            current /= wordlist.size();
        }
        
        return result.trim();
    }
    
    public long FromString(String valueAsString) {
        long result = 0;
        
        if(wordlist.isEmpty())
            return 0;
        
        List<String> words = Arrays.asList(valueAsString.split(splitValue));
        Collections.reverse(words);
        for(int i = 0; i<words.size(); i++) {
            result += Math.pow(wordlist.size(), i) * wordlist.indexOf(words);             
        }
        
        return result;
    }
}
