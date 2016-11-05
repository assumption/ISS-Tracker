package edu.calpoly.isstracker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.calpoly.isstracker.IssData.AsyncTaskCallback;
import edu.calpoly.isstracker.IssData.IssData;
import edu.calpoly.isstracker.IssData.ListItem;
import isstracker.calpoly.edu.iss_tracker.R;

public class IssDataFragment extends Fragment {

    private CardView header;
    private ListView dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean tablet = getResources().getBoolean(R.bool.tablet);

        int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        boolean navBar = id > 0 && getResources().getBoolean(id);
        boolean landscape = getResources().getBoolean(R.bool.landscape);

        View v = inflater.inflate(R.layout.iss_data_sheet_fragment, container, false);
        header = (CardView) v.findViewById(R.id.header);

        IssData issData = new IssData();
        issData.retrieveAstronauts(new AsyncTaskCallback() {
            @Override
            public void done(IssData issData) {
                updateData(issData);
            }

            @Override
            public void timeoutError() {

            }
        });

        dataList = (ListView) v.findViewById(R.id.data_list);
        dataList.setAdapter(new ListViewAdapter(issData.getDataListItems()));
        dataList.setDivider(null);
        //set text paddingBottom and alpha
        dataList.setAlpha(tablet ? 1.0f : 0.0f);
        if(navBar && !landscape){
            dataList.setPadding(dataList.getPaddingLeft(),
                    dataList.getPaddingTop(),
                    dataList.getPaddingRight(),
                    dataList.getPaddingBottom() + (int) getResources().getDimension(R.dimen.nav_bar_height));
        }

        return v;
    }

    public void onSlide(float slideOffset){
        //elevate and color toolbar
        int header_color = ContextCompat.getColor(getContext(), R.color.grey_800);
        header.setBackgroundColor(Color.argb((int) (slideOffset * 255), Color.red(header_color),
                Color.green(header_color), Color.blue(header_color)));
        header.setCardElevation(slideOffset * getResources().getDimension(R.dimen.toolbar_elevation));

        //fade in text
        dataList.setAlpha(slideOffset);

        if(slideOffset == 0.0f){
            dataList.setSelection(0);
        }
    }

    public void updateData(IssData issData){
        ListViewAdapter adapter = (ListViewAdapter) dataList.getAdapter();
        adapter.listItems = issData.getDataListItems();
        adapter.notifyDataSetChanged();
    }

    public static class ListViewAdapter extends BaseAdapter{

        List<ListItem> listItems;

        ListViewAdapter(List<ListItem> listItems){
            this.listItems = listItems;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Object getItem(int i) {
            return listItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            ListItem item = (ListItem) getItem(i);
            if(item == null){
                return convertView;
            }
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_fragment_list_item, parent ,false);
            }

            TextView left_text = (TextView) convertView.findViewById(R.id.left_text);
            left_text.setText(item.left_text);
            TextView right_text = (TextView) convertView.findViewById(R.id.right_text);
            right_text.setText(item.right_text);

            int default_padding = (int) parent.getContext().getResources().getDimension(R.dimen.list_item_default_padding);
            int padding = (int) parent.getContext().getResources().getDimension(R.dimen.list_item_headline_padding);
            float default_text_size = parent.getContext().getResources().getDimension(R.dimen.list_item_default_text_size);
            float headline_text_size = parent.getContext().getResources().getDimension(R.dimen.list_item_headline_text_size);

            if(item.isHeadline){
                right_text.setVisibility(View.GONE);
                left_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, headline_text_size);
                left_text.setPadding(default_padding, default_padding + padding, default_padding, default_padding + padding/2);
            } else if(item.isAstronaut){
                right_text.setVisibility(View.GONE);
                left_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, default_text_size);
                left_text.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.text_color_secondary));
                left_text.setPadding(default_padding + padding, default_padding, default_padding, default_padding);
            } else {
                right_text.setVisibility(View.VISIBLE);
                left_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, default_text_size);
                left_text.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.text_color_primary));
                left_text.setPadding(default_padding + padding, default_padding, default_padding, default_padding);
            }

            return convertView;
        }
    }
}
