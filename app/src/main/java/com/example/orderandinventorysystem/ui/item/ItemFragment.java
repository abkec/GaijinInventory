package com.example.orderandinventorysystem.ui.item;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderandinventorysystem.ConnectionPhpMyAdmin;
import com.example.orderandinventorysystem.Model.Item;
import com.example.orderandinventorysystem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ItemFragment extends Fragment implements ItemListAdapter.ItemClickListener {

    ItemListAdapter adapter;
    ArrayList<Item> itemList;
    RecyclerView recyclerView;
    View root;
    TextView con;

    private boolean shouldRefreshOnResume = false;

    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        return fragment;
    }

    public ItemFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(shouldRefreshOnResume){
            con.setVisibility(View.INVISIBLE);
            itemList = new ArrayList<>();
            ShowItemList showItemList = new ShowItemList();
            showItemList.execute("");
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ItemListAdapter(getContext(), itemList);
            adapter.setClickListener(this);
        }
    }

    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ShowItemList showItemList = new ShowItemList();
        showItemList.execute("");
        itemList = new ArrayList<>();
        root = inflater.inflate(R.layout.fragment_item, container, false);
        recyclerView = root.findViewById(R.id.item_recycler_view);
        con = root.findViewById(R.id.connection);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemListAdapter(getContext(), itemList);
        adapter.setClickListener(this);

        FloatingActionButton fab = root.findViewById(R.id.add_item);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), add_item.class));
            }
        });

        return root;
    }

    @Override
    public void onItemClick(View view, int position, String id, String name, double price) {
        Intent intent = new Intent(getContext(), ItemMain.class);
        intent.putExtra("itemID", id);
        startActivity(intent);
    }

    public class ShowItemList extends AsyncTask<String, String, String> {

        String checkConnection = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionPhpMyAdmin connectionClass = new ConnectionPhpMyAdmin();
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    checkConnection = "No";
                } else {

                    String query = " SELECT * FROM ITEM WHERE ITEMSTATUS='Available' ";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        itemList.add(new Item(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getDouble(7), rs.getDouble(8), rs.getString(8)));
                    }

                    checkConnection = "Yes";
                    isSuccess = true;

                }
            } catch (Exception ex) {
                Log.d("Error", ex.toString());
                isSuccess = false;
                checkConnection = "No";
            }

            return checkConnection;
        }

        protected void onPostExecute(String s) {
            recyclerView.setAdapter(adapter);

            if (itemList.size()==0) {

                con.setVisibility(View.VISIBLE);
            }
        }

    }
}