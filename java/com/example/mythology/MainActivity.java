package com.example.mythology;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.ToDoubleFunction;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mysong;

    private static final int COLUMNS = 3;
    private static final int DIMENSIONS = COLUMNS * COLUMNS;

    public static final String up = "up";
    public static final String down = "down";
    public static final String left = "left";
    public static final String right = "right";

    private static String[] tileList;

    private static GestureDetectGridView mGridView;

    private static int mColumnWidth, mColumnHeight;

    //private static int flag=0;
    //private  shivaSupport gameView;
   // private  Handler handler = new Handler();
    //private final static long Interval = 30;

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mysong = MediaPlayer.create(MainActivity.this,R.raw.quiz_music);
        mysong.start();

        init();

        scramble();

        //display();

        setDimensions();

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FF3D080D"));
        actionBar.setBackgroundDrawable(colorDrawable);

    }

    private void setDimensions() {
        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getMeasuredHeight();

                int statusbarHeight = getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusbarHeight;

                mColumnWidth = displayWidth / COLUMNS;
                mColumnHeight = requiredHeight / COLUMNS;

                display(getApplicationContext());
            }
        });
    }

    private int getStatusBarHeight(Context context) {
        int result=0;
        int resourceId = context.getResources().getIdentifier("status_bar_height","dimen", "android");

        if(resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static void display(Context context) {

        ArrayList<Button> buttons = new ArrayList<>();

        Button button;
        for(int i=0;i<tileList.length;i++) {
            button = new Button(context);

            if(tileList[i].equals("0")) {
                button.setBackgroundResource(R.drawable.shiv_1);
            }
            else if(tileList[i].equals("1")) {
                button.setBackgroundResource(R.drawable.shiv_2);
            }
            else if(tileList[i].equals("2")) {
                button.setBackgroundResource(R.drawable.shiv_3);
            }
            else if(tileList[i].equals("3")) {
                button.setBackgroundResource(R.drawable.shiv_4);
            }
            else if(tileList[i].equals("4")) {
                button.setBackgroundResource(R.drawable.shiv_5);
            }
            else if(tileList[i].equals("5")) {
                button.setBackgroundResource(R.drawable.shiv_6);
            }
            else if(tileList[i].equals("6")) {
                button.setBackgroundResource(R.drawable.shiv_7);
            }
            else if(tileList[i].equals("7")) {
                button.setBackgroundResource(R.drawable.shiv_8);
            }
            else if(tileList[i].equals("8")) {
                button.setBackgroundResource(R.drawable.shiv_9);
            }

            buttons.add(button);
        }
        mGridView.setAdapter(new CustomAdapter(buttons,mColumnWidth,mColumnHeight));
    }

    private void scramble() {
        int index;
        String temp;
        Random random = new Random();

        for(int i=tileList.length-1;i > 0;i--) {
            index = random.nextInt(i+1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i] = temp;
        }
    }

    private void init() {
        mGridView = (GestureDetectGridView) findViewById(R.id.grid);
        mGridView.setNumColumns(COLUMNS);

        tileList = new String[DIMENSIONS];
        for(int i=0;i<DIMENSIONS;i++) {
            tileList[i] = String.valueOf(i);
        }
    }

    public static void swap(Context context,int position,int swap) {
        MainActivity temp = new MainActivity();
        String newPosition = tileList[position+swap];
        tileList[position + swap] = tileList[position];
        tileList[position] = newPosition;
        display(context);

        if (isSolved()) {
            //Toast.makeText(context, "YOU WIN!", Toast.LENGTH_SHORT).show();
            //temp.migrate();
            Intent explicitIntent = new Intent(context,aboutShiva.class);
            context.startActivity(explicitIntent);
        }
    }

    public static void moveTiles(Context context,String direction, int position) {
        //upper-left-corner
        if(position == 0) {
            if(direction.equals("right")) {
                swap(context , position,1);
            } else if(direction.equals("down")) {
                swap(context,position,COLUMNS);
            } else {
                Toast.makeText(context,"Invalid move",Toast.LENGTH_SHORT).show();
            }
        } else if (position > 0 && position < COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) {

                // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                // right-corner tile.
                if (position <= DIMENSIONS - COLUMNS - 1) swap(context, position,
                        COLUMNS);
                else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Center tiles
        } else {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else swap(context, position, COLUMNS);
        }
    }
    private static boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        return solved;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater=getMenuInflater();
        //inflater.inflate(R.menu.example_menu,menu);
        getMenuInflater().inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_action:
                Intent mainIntent = new Intent(MainActivity.this,
                        openingActivityNew.class);
                startActivity(mainIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }else {
            backToast=Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}