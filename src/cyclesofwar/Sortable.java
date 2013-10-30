package cyclesofwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sortable {

    /*
     * sorts a list using a comparator
     * e.g.: Fleet.sortBy(Fleet.ArrivalTimeComparator, fleets);
     */
    public static <T> void sortBy(Comparator<T> comparator, List<T> list) {
        Collections.sort(list, comparator);
    }

    /*
     * returns the minimum of a list using a comparator
     * e.g.: Fleet.min(Fleet.ArrivalTimeComparator, fleets);
     */
    public static <T> T min(Comparator<T> comparator, List<T> list) {
        return Collections.min(list, comparator);
    }

    /*
     * returns the maximum of a list using a comparator
     * e.g.: Fleet.max(Fleet.ArrivalTimeComparator, fleets);
     */
    public static <T> T max(Comparator<T> comparator, List<T> list) {
        return Collections.max(list, comparator);
    }

    /*
     * returns a new list with items sorted according to the comparator
     * e.g.: List<Fleet> sortedFleets = Fleet.sortedBy(Fleet.ArrivalTimeComparator, fleets);
     */
    public static <T> List<T> sortedBy(Comparator<T> comparator, List<T> list) {
        List<T> result = new ArrayList<>();
        result.addAll(list);
        sortBy(comparator, result);
        return result;
    }
}
