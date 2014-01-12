/**
 * Main activity for Fracplorer.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2011-2012 Yujian Zhang
 */

package net.whily.android.fracplorer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class FracplorerActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Enter immersive mode.
    getWindow().getDecorView().
      setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | 
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
    
    setContentView(R.layout.main);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.frac_options, menu);
    return true;
  }
}
