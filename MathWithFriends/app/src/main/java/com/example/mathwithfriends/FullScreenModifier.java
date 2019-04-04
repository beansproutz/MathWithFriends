package com.example.mathwithfriends;

import android.view.View;

// Simple modifier class used to set screen to immersive fullscreen mode
final class FullScreenModifier {

    // Sets screen to immersive fullscreen mode - hiding the system bars.
    static void setFullscreen(View view) {
        view.setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}