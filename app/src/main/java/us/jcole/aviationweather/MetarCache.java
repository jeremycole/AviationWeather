package us.jcole.aviationweather;

import java.util.ArrayList;
import java.util.List;

class MetarCache {
    private static List<Metar> mMetarCache = new ArrayList<>();

    static List<Metar> getMetarCache() {
        return mMetarCache;
    }

    static int size() {
        return mMetarCache.size();
    }

    static Metar get(int index) {
        return mMetarCache.get(index);
    }

    static boolean add(Metar metar) {
        return mMetarCache.add(metar);
    }

    static boolean remove(Metar metar) {
        return mMetarCache.remove(metar);
    }

    static boolean replace(Metar metar) {
        for (Metar m : mMetarCache) {
            if (m.getIdentifier().equals(metar.getIdentifier())) {
                mMetarCache.remove(m);
            }
        }
        return mMetarCache.add(metar);
    }

    static void clear() {
        mMetarCache.clear();
    }
}
