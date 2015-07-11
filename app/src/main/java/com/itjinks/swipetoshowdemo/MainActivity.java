package com.itjinks.swipetoshowdemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itjinks.swipetoshow.SwipeHolder;
import com.itjinks.swipetoshow.SwipeOnItemTouchAdapter;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new MyAdapter());
        //you only need addOnItemTouchListener here
        recyclerView.addOnItemTouchListener(new SwipeOnItemTouchAdapter(this, recyclerView, linearLayoutManager) {
            @Override
            public void onItemHiddenClick(SwipeHolder swipeHolder, int position) {
                Toast.makeText(MainActivity.this, "onItemHiddenClick " + position, Toast.LENGTH_SHORT).show();
                //call reset to hide.
                swipeHolder.reset();
            }

            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "onItemClick " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //just a simple Adapter nothing more here.
    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //load layout you write,look R.layout.item to see detail.
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 5;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
