package de.melchers.heat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.melchers.heat.classes.Cup;
import de.melchers.heat.classes.ExcelExporter;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.classes.Player;
import de.melchers.heat.classes.Race;
import de.melchers.heat.databinding.ActivityMainBinding;
import de.melchers.heat.ui.dashboard.DashboardFragment;
import de.melchers.heat.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String LAST_OPENED_URI_KEY = "de.melchers.heat.actionopendocument.pref.LAST_OPENED_URI_KEY";
    private ActivityMainBinding binding;
    public HeatViewModel mViewModel;
    private ExcelExporter excelExporter;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.mViewModel = new ViewModelProvider(this).get(HeatViewModel.class);
        this.excelExporter = new ExcelExporter(this);

        navController = NavHostFragment.findNavController(binding.appBarMainInclude.contentMainInclude.navHostFragment.getFragment());
        navController.setGraph(R.navigation.mobile_navigation);

        // Toolbar Navigation Setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.action_bar_open, R.string.action_bar_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        askForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, 11);
        askForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 12);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cup_list) {
            // TODO: Ermittlung des aktiven Fragmentes und anschließende Navigation
            if (mViewModel.currentCup != null) {
                navController.navigate(R.id.cupFragment);
            } else {
                Toast.makeText(this, "Bitte erst ein Spiel starten", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_dashboard) {
            if (mViewModel.currentCup != null) {
                navController.navigate(R.id.navigation_dashboard);
            } else {
                Toast.makeText(this, "Bitte erst ein Spiel starten", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_home) {
            navController.navigate(R.id.navigation_home);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addNewRace(boolean isInitial) {
        HashMap<Player, Integer> temp2 = new HashMap<>();
        Race race;
        for (Player player : mViewModel.players) {
            temp2.put(player, calculateSingleResult(player.getLastPlacement()));
        }
        race = mViewModel.currentRace;
        race.setResults(temp2);
        if (!mViewModel.currentCup.races.contains(race)) {
            mViewModel.currentCup.races.add(race);
        }

        mViewModel.currentRace = race;
        calculateCupTotal(mViewModel.currentCup);
        this.saveGame();

    }

    private void calculateCupTotal(Cup cup) {
        for (Player player : mViewModel.players) {
            int totalScore = 0;
            for (Race race : cup.races) {
                totalScore += race.results.get(player);
            }
            cup.totalScore.put(player, totalScore);
        }
    }

    private int calculateSingleResult(int placement) {
        switch (placement) {
            case 1:
                return 9;
            case 2:
                return 6;
            case 3:
                return 4;
            case 4:
                return 3;
            case 5:
                return 2;
            case 6:
                return 1;
            default:
                Toast.makeText(this, "Fehler bei Punktberechnung", Toast.LENGTH_LONG).show();
                return 0;
        }
    }

    public void calculateResults(ArrayList<Player> players) {
        for (Player player : players) {
            switch (player.getLastPlacement()) {
                case 1:
                    player.setTotalScore(player.getTotalScore() + 9);
                    break;
                case 2:
                    player.setTotalScore(player.getTotalScore() + 6);
                    break;
                case 3:
                    player.setTotalScore(player.getTotalScore() + 4);
                    break;
                case 4:
                    player.setTotalScore(player.getTotalScore() + 3);
                    break;
                case 5:
                    player.setTotalScore(player.getTotalScore() + 2);
                    break;
                case 6:
                    player.setTotalScore(player.getTotalScore() + 1);
                    break;
                default:
                    Toast.makeText(this, "KEINE PLATZIERUNG von" + player.getName(), Toast.LENGTH_LONG).show();
                    break;
            }
        }

//        saveGame();
    }

    public void saveGame() {
        if (!mViewModel.cups.contains(mViewModel.currentCup)) {
            mViewModel.cups.add(mViewModel.currentCup);
        }
        this.excelExporter.saveGameStateNew();
    }

    public void loadGame(View v) {
        File pathName = new File("/storage/emulated/0/Documents/HEAT-Saves");
        File fullPath = new File("/storage/emulated/0/Documents/HEAT-Saves/heat_save_v01.xls");
        this.excelExporter.loadGameStateNew("heat_save_v01.xls", pathName, fullPath);
//        Navigation.findNavController(v).navigate(R.id.navigation_dashboard);
    }

    public void createFile(View view) { //Uri pickerInitialUri
        ExcelExporter.export();
    }

    public void openFile(View view) {
        File sd = new File("/storage/emulated/0/download/testExporter.xlsx");
        ExcelExporter.importXLSX("testExporter.xlsx", sd, new File(sd.getAbsolutePath()));
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, permission + " is already granted.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    public void resetModel() {
        mViewModel.cups = new ArrayList<>();
        mViewModel.players = new ArrayList<>();
        mViewModel.currentCup = null;
        mViewModel.currentRace = new Race();
    }

    /** @noinspection StatementWithEmptyBody*/
//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//            Toast.makeText(this, "Ratta Pew", Toast.LENGTH_LONG).show();
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        }
//
////        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
////        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}