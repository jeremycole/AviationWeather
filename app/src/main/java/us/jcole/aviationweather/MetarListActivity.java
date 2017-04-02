package us.jcole.aviationweather;

import android.support.v4.app.Fragment;

public class MetarListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new MetarListFragment();
    }
}
