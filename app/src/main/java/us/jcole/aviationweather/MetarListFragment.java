package us.jcole.aviationweather;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MetarListFragment extends Fragment {
    private AvWx mAvWx;
    private List<String> mStationList;
    private RecyclerView mMetarRecyclerView;
    private MetarAdapter mMetarAdapter;

    private class MetarHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private Metar mMetar;
        private TextView mAirportName;
        private TextView mFlightCategory;
        private TextView mMetarAge;
        private TextView mMetarText;

        public MetarHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_metar, parent, false));
            itemView.setOnClickListener(this);

            mAirportName = (TextView) itemView.findViewById(R.id.airport_name);
            mMetarText = (TextView) itemView.findViewById(R.id.metar_text);
            mFlightCategory = (TextView) itemView.findViewById(R.id.flight_category);
            mMetarAge = (TextView) itemView.findViewById(R.id.metar_age);
        }

        int colorForFlightCategory(String flightCategory) {
            switch (flightCategory) {
                case "VFR":
                    return Color.parseColor("#00dd00");
                case "MVFR":
                    return Color.parseColor("#dd00dd");
                case "IFR":
                    return Color.parseColor("#dd0000");
            }
            return Color.parseColor("#000000");
        }

        int colorForMetarAge(int ageInMinutes) {
            if (ageInMinutes > 65) {
                return Color.parseColor("#dd0000");
            }
            return Color.parseColor("#666666");
        }

        public void bind(Metar metar) {
            mMetar = metar;
            mAirportName.setText(metar.getName());
            mMetarText.setText(metar.getMetarText());
            mFlightCategory.setText(metar.getFlightCategory());
            mFlightCategory.setTextColor(colorForFlightCategory(metar.getFlightCategory()));
            if (metar.getObservationTime() != null) {
                Date now = new Date();
                int ageInMinutes = (int) (now.getTime() - metar.getObservationTime().getTime()) /
                        (1000 * 60);
                mMetarAge.setText(String.format("%d minutes ago", ageInMinutes));
                mMetarAge.setTextColor(colorForMetarAge(ageInMinutes));
            } else {
                mMetarAge.setText("unknown age");
            }
        }

        @Override
        public void onClick(View v) {
        }
    }

    private class MetarAdapter extends RecyclerView.Adapter<MetarHolder> {
        private List<Metar> mMetarList;

        @Override
        public MetarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MetarHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MetarHolder holder, int position) {
            Metar metar = MetarCache.get(position);
            holder.bind(metar);
        }

        @Override
        public int getItemCount() {
            return MetarCache.size();
        }
    }

    void refreshMetarData() {
        MetarCache.clear();
        mAvWx.fetchMetars(mStationList, new AvWx.MetarListener() {
            @Override
            public void onMetar(Metar metar) {
                MetarCache.add(metar);
                mMetarAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAvWx = new AvWx(getContext());

        mStationList = new ArrayList<>();
        mStationList.add("KRNO");
        mStationList.add("KTRK");
        mStationList.add("KTVL");

        View view = inflater.inflate(R.layout.fragment_metar_list, container, false);

        mMetarRecyclerView = (RecyclerView) view.findViewById(R.id.metar_recycler_view);
        mMetarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mMetarAdapter = new MetarAdapter();
        mMetarRecyclerView.setAdapter(mMetarAdapter);

        refreshMetarData();

        return view;
    }

}
