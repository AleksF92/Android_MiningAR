package net.ledii.ar_excavator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.graphics.ColorUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MiningGame extends View {
    private int[][] rockTiles, treasureTiles;
    private int[] treasureFound;
    private int treasureColor;
    private String rockType;

    MiningGame(Context context) {
        super(context);

        //Add view to layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
        RelativeLayout layout = (RelativeLayout) ((Activity) context).findViewById(R.id.layoutMain);
        layout.addView(this, params);

        //Set size
        int screenW = (int) Global.get(Global.SCREEN_WIDTH);
        int offset = (int) (screenW * 0.025f);
        int size = screenW - (offset * 2);
        getLayoutParams().width = size;
        getLayoutParams().height = size;

        //Set offset
        setX(offset);
        setY(offset);

        //Set click event
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int tiles = treasureTiles.length;
                    float tileSize = getWidth() / (float)tiles;
                    int tileX = (int)(event.getX() / tileSize);
                    int tileY = (int)(event.getY() / tileSize);
                    clickTile(tileX, tileY);
                }
                return false;
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String rockType = "Copper";
                if (Global.randomInt(0, 1) > 0) { rockType = "Adamant"; }
                newGame(rockType);
                return true;
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        if (treasureTiles != null) {
            //Draw grid
            paint.setStrokeWidth(5);
            float tileSize = getWidth() / (float)treasureTiles.length;
            for (int tileY = 0; tileY < treasureTiles.length; tileY++) {
                for (int tileX = 0; tileX < treasureTiles.length; tileX++) {
                    float x1 = tileSize * tileX;
                    float y1 = tileSize * tileY;

                    if (rockTiles != null) {
                        //Set rock texture
                        switch (rockTiles[tileY][tileX]) {
                            case 1: { paint.setColor(Color.parseColor("#3F3E3E")); break; }
                            case 2: { paint.setColor(Color.parseColor("#545252")); break; }
                            case 3: { paint.setColor(Color.parseColor("#777575")); break; }
                            case 4: { paint.setColor(Color.parseColor("#776B5F")); break; }
                            case 5: { paint.setColor(Color.parseColor("#6B584E")); break; }
                            default: { paint.setColor(Color.parseColor("#272626")); break; }
                        }

                        //Draw rock
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawRect(x1, y1, x1 + tileSize, y1 + tileSize, paint);

                        if (rockTiles[tileY][tileX] == 0 && treasureTiles[tileY][tileX] != 0) {
                            int hiddenColor = treasureColor;
                            if (treasureFound[treasureTiles[tileY][tileX] - 1] == 0) {
                                hiddenColor = ColorUtils.blendARGB(treasureColor, Color.parseColor("#000000"), 0.5f);
                            }

                            //Draw treasure
                            paint.setColor(hiddenColor);
                            canvas.drawRect(x1, y1, x1 + tileSize, y1 + tileSize, paint);
                        }
                    }

                    //Draw grid frame
                    paint.setColor(Color.parseColor("#000000"));
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(x1, y1, x1 + tileSize, y1 + tileSize, paint);
                }
            }
        }
    }

    public void newGame(String rockType) {
        //Get rock properties
        int numTiles = 0, numTreasure = 0, difficulty = 0;
        this.rockType = rockType;
        switch (rockType) {
            case "Copper": {
                numTiles = 5;
                numTreasure = Global.randomInt(3, 6);
                difficulty = 1;
                treasureColor = Color.parseColor("#E27A2B");
                break;
            }
            case "Adamant": {
                numTiles = 6;
                numTreasure = Global.randomInt(6, 12);
                difficulty = 2;
                treasureColor = Color.parseColor("#008247");
                break;
            }
        }

        //Randomize treasure tiles
        treasureTiles = new int[numTiles][numTiles];
        int treasureId = 1;
        int treasureLeft = numTreasure;
        while (treasureLeft > 0) {
            int treasureSize = Global.randomInt(1, Math.min(treasureLeft, 4));
            while (true) {
                //Create treasure shape
                int[][] treasure = createTreasure(treasureSize, treasureId);

                //Select random postion
                int startX = Global.randomInt(0, numTiles - treasure.length);
                int startY = Global.randomInt(0, numTiles - treasure.length);

                //Check if positon is free
                boolean positionFree = true;
                for (int tileY = startY; tileY < startY + treasure.length; tileY++) {
                    for (int tileX = startX; tileX < startX + treasure.length; tileX++) {
                        if (treasureTiles[tileY][tileX] != 0) {
                            positionFree = false;
                        }
                    }
                }

                if (positionFree) {
                    //Copy treasure to position
                    for (int tileY = 0; tileY < treasure.length; tileY++) {
                        for (int tileX = 0; tileX < treasure.length; tileX++) {
                            treasureTiles[startY + tileY][startX + tileX] = treasure[tileY][tileX];
                        }
                    }

                    //Successfully placed treasure, exit loop
                    treasureLeft -= treasureSize;
                    treasureId++;
                    break;
                }
            }
        }

        //Set available finds
        treasureFound = new int[treasureId - 1];

        //Randomize rock tiles
        rockTiles = new int[numTiles][numTiles];
        int minThickness = Math.max(difficulty - 3, 1);
        int maxThickness = Math.min(difficulty + 3, 5);
        for (int tileY = 0; tileY < numTiles; tileY++) {
            for (int tileX = 0; tileX < numTiles; tileX++) {
                rockTiles[tileY][tileX] = Global.randomInt(minThickness, maxThickness);
            }
        }

        //Show toast
        String veinInfo = "This vein contains " + numTreasure + " " + rockType + " ore!";
        Toast.makeText(getContext(), veinInfo, Toast.LENGTH_LONG).show();

        //Update tiles
        invalidate();
    }

    private int[][] createTreasure(int treasureSize, int idNum) {
        //Initialize the treasure shape
        int numTiles = (int)Math.ceil(Math.sqrt(treasureSize));
        int[][] treasure = new int[numTiles][numTiles];
        for (int i = 0; i < treasureSize; i++) {
            int tileX = i % numTiles;
            int tileY = i / numTiles;
            treasure[tileY][tileX] = idNum;
        }

        //Rotate 0-3 times
        int numRotates = Global.randomInt(0, 3);
        for (int i = 0; i < numRotates; i++) {
            treasure = rotateTreasure(treasure);
        }

        return treasure;
    }

    private int[][] rotateTreasure(int[][] treasure) {
        //Rotates values in arrays
        int[][] rotTreasure = new int[treasure.length][treasure.length];
        for (int tileY = 0; tileY < treasure.length; tileY++) {
            for (int tileX = 0; tileX < treasure.length; tileX++) {
                rotTreasure[tileX][tileY] = treasure[tileY][tileX];
            }
        }

        return rotTreasure;
    }

    private void clickTile(int tileX, int tileY) {
        if (rockTiles[tileY][tileX] > 0) {
            int toolStrength = 1;
            int damage = Math.min(toolStrength, rockTiles[tileY][tileX]);

            rockTiles[tileY][tileX] -= damage;
            if (rockTiles[tileY][tileX] == 0 && damage > 0) {
                //No more rock
                int treasureId = treasureTiles[tileY][tileX];
                if (treasureId > 0) {
                    //Discovered treasure
                    discoverTreasure(treasureId);
                }
            }
            invalidate();
        }
    }

    private void discoverTreasure(int treasureId) {
        int treasureSize = 0;

        //Check treasure size
        boolean entireTreasure = true;
        for (int tileY = 0; tileY < treasureTiles.length; tileY++) {
            for (int tileX = 0; tileX < treasureTiles.length; tileX++) {
                if (treasureTiles[tileY][tileX] == treasureId) {
                    //Same treasure in tile
                    treasureSize++;

                    if (rockTiles[tileY][tileX] > 0) {
                        //Not discovered entire treasure
                        entireTreasure = false;
                        break;
                    }
                }
            }
            if (!entireTreasure) {
                break;
            }
        }

        //Check size, 0 if not discovered
        if (entireTreasure) {
            treasureFound[treasureId - 1] = treasureSize;

            //Check if all treasure is found
            boolean allTreasure = true;
            for (int i = 0; i < treasureFound.length; i++) {
                if (treasureFound[i] == 0) {
                    allTreasure = false;
                }
            }

            if (allTreasure) {
                String treasureStr = "Good job! You've found all the treasure!";
                Toast.makeText(getContext(), treasureStr, Toast.LENGTH_LONG).show();
            }
        }
    }
}