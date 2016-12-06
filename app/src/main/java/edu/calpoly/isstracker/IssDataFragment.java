package edu.calpoly.isstracker;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.calpoly.isstracker.IssData.AsyncTaskCallback;
import edu.calpoly.isstracker.IssData.IssData;
import edu.calpoly.isstracker.IssData.ListItem;

public class IssDataFragment extends Fragment {

    private CardView header;
    private RecyclerView recyclerView;

    private BottomSheetCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean tablet = getResources().getBoolean(R.bool.tablet);

        int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        boolean navBar = id > 0 && getResources().getBoolean(id);
        boolean landscape = getResources().getBoolean(R.bool.landscape);

        View v = inflater.inflate(R.layout.iss_data_sheet_fragment, container, false);
        header = (CardView) v.findViewById(R.id.header);
        if(!getResources().getBoolean(R.bool.tablet)){
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(callback != null){
                        callback.onClick();
                    }
                }
            });
        } else {
            header.findViewById(R.id.button).setVisibility(View.GONE);
        }

        IssData issData = new IssData();
        issData.retrieveAstronauts(new AsyncTaskCallback() {
            @Override
            public void done(IssData issData) {
                updateData(issData);
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerViewAdapter(issData.getDataListItems()));
        //set text paddingBottom and alpha
        if(navBar && !landscape){
            recyclerView.setPadding(recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    recyclerView.getPaddingRight(),
                    recyclerView.getPaddingBottom() + (int) getResources().getDimension(R.dimen.nav_bar_height));
        }

        onSlide(tablet ? 1.0f : 0.0f);

        return v;
    }

    public void onSlide(float slideOffset){
        //elevate and color toolbar
        int header_color = ContextCompat.getColor(getContext(), R.color.a_bit_lighter_than_grey_900);
        header.setBackgroundColor(Color.argb((int) (slideOffset * 255), Color.red(header_color),
                Color.green(header_color), Color.blue(header_color)));
        header.setCardElevation(slideOffset * getResources().getDimension(R.dimen.toolbar_elevation));

        if(!getResources().getBoolean(R.bool.tablet)){
            header.findViewById(R.id.button).setRotation(slideOffset*180);
        }

        //fade in text
        recyclerView.setAlpha(slideOffset);
    }

    public void updateData(IssData issData){
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) recyclerView.getAdapter();
        adapter.items = issData.getDataListItems();
        adapter.notifyDataSetChanged();
    }

    public void setCallback(BottomSheetCallback callback){
        this.callback = callback;
    }

    public static class RecyclerViewAdapter extends RecyclerView.Adapter {

        List<ListItem> items;

        RecyclerViewAdapter(List<ListItem> items){
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_fragment_list_item, parent, false);
            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder itemViewHolder = ((ItemViewHolder) holder);
            itemViewHolder.setData(items.get(position));

            itemViewHolder.left_text_view.setText(itemViewHolder.left_text);
            itemViewHolder.right_text_view.setText(itemViewHolder.right_text);

            int default_padding = (int) itemViewHolder.itemView.getContext().getResources().getDimension(R.dimen.list_item_default_padding);
            int padding = (int) itemViewHolder.itemView.getContext().getResources().getDimension(R.dimen.list_item_headline_padding);
            float default_text_size = itemViewHolder.itemView.getContext().getResources().getDimension(R.dimen.list_item_default_text_size);
            float headline_text_size = itemViewHolder.itemView.getContext().getResources().getDimension(R.dimen.list_item_headline_text_size);

            if(itemViewHolder.isHeadline){
                itemViewHolder.right_text_view.setVisibility(View.GONE);
                itemViewHolder.left_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, headline_text_size);
                itemViewHolder.left_text_view.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                itemViewHolder.left_text_view.setTextColor(ContextCompat.getColor(itemViewHolder.itemView.getContext(),
                        R.color.text_color_primary));
                itemViewHolder.left_text_view.setPadding(default_padding,
                        default_padding + padding, default_padding, default_padding + padding/2);
            } else if(itemViewHolder.isAstronaut){
                itemViewHolder.right_text_view.setVisibility(View.GONE);
                itemViewHolder.left_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, default_text_size);
                itemViewHolder.left_text_view.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                itemViewHolder.left_text_view.setTextColor(ContextCompat.getColor(itemViewHolder.itemView.getContext(),
                        R.color.text_color_secondary));
                itemViewHolder.left_text_view.setPadding(default_padding + padding,
                        default_padding, default_padding, default_padding);
            } else {
                itemViewHolder.right_text_view.setVisibility(View.VISIBLE);
                itemViewHolder.left_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, default_text_size);
                itemViewHolder.left_text_view.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                itemViewHolder.left_text_view.setTextColor(ContextCompat.getColor(itemViewHolder.itemView.getContext(),
                        R.color.text_color_primary));
                itemViewHolder.left_text_view.setPadding(default_padding + padding,
                        default_padding, default_padding, default_padding);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView left_text_view;
            TextView right_text_view;

            String left_text = "";
            String right_text = "";
            boolean isHeadline = false;
            boolean isAstronaut = false;

            ItemViewHolder(View itemView) {
                super(itemView);

                left_text_view = (TextView) itemView.findViewById(R.id.left_text);
                right_text_view = (TextView) itemView.findViewById(R.id.right_text);
            }

            public void setData(ListItem data){
                this.left_text = data.left_text;
                this.right_text = data.right_text;
                this.isHeadline = data.isHeadline;
                this.isAstronaut = data.isAstronaut;
            }
        }
    }

    public interface BottomSheetCallback {
        void onClick();
    }
}
