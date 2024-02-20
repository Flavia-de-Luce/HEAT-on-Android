package de.melchers.heat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.melchers.heat.classes.Cup;
import de.melchers.heat.classes.ExcelExporter;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.classes.Player;
import de.melchers.heat.classes.Race;
import de.melchers.heat.classes.Season;
import de.melchers.heat.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final String LAST_OPENED_URI_KEY = "de.melchers.heat.actionopendocument.pref.LAST_OPENED_URI_KEY";
    private ActivityMainBinding binding;
    public HeatViewModel mViewModel;
    private ExcelExporter excelExporter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Implement ViewModel.
        // Hoffnung: MainActivity hält das ViewModel vor und alle Fragments können sich die Daten holen die sie benötigen.
        //           Und MainActivity kann die selben Daten bei änderungen direkt in die .xls speichern :D


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.mViewModel = new ViewModelProvider(this).get(HeatViewModel.class);
        this.excelExporter = new ExcelExporter(this);

        NavController navController = NavHostFragment.findNavController(binding.navHostFragment.getFragment());
        navController.setGraph(R.navigation.mobile_navigation);

        askForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, 11);
        askForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 12);

    }

    private static final int PICK_PDF_FILE = 2;

    public void addNewRace(boolean isInitial) {
        JSONObject temp = new JSONObject();
        Season season;
        Cup cup;
        Race race;
        if (isInitial) {
            season = new Season();
            cup = new Cup();
            race = new Race();
            try {
                for (Player player : mViewModel.players) {
                    temp.put(player.getName(), 0);
                }
//            cup.setRace(new JSONObject().put(viewModel.currentMapName, temp));
                race.setResults(temp);
                race.setMapName(mViewModel.currentMapName);
                cup.setCurrentRace(race);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            cup.races.add(race);
            cup.id = 1;
            season.cups.add(cup);
            season.id = 1;
            mViewModel.seasons.add(season);
            mViewModel.currentSeason = season;
        } else {
            season = mViewModel.currentSeason;
            cup = mViewModel.currentCup;
            try {
                for (Player player : mViewModel.players) {
                    temp.put(player.getName(), calculateSingleResult(player.getLastPlacement()));
                }
                // Wenn aktuell 0 als Renn-Ergebnis gespeichert ist
                if (mViewModel.currentCup.getCurrentRace().getResults().getInt(mViewModel.players[0].getName()) == 0) {
                    race = mViewModel.currentCup.currentRace;
                    race.setId(1);
                    race.setResults(temp);
                    race.setMapName(mViewModel.currentMapName);
                    cup.races.set(0, race);
                } else {
                    race = new Race();
                    race.setId(mViewModel.currentCup.currentRace.getId() + 1);
                    race.setResults(temp);
                    race.setMapName(mViewModel.currentMapName);
                    cup.races.add(race);
                    mViewModel.currentSeason.cups.set(cup.id - 1, cup);
                    mViewModel.seasons.set(season.id - 1, mViewModel.currentSeason);
                }

                cup.setCurrentRace(race);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        mViewModel.currentSeason = season;
        mViewModel.currentCup = cup;
        this.saveGame();

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

    public Player[] calculateResults(Player[] players) {
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

        saveGame();
        return players;
    }

    public void saveGame() {
        this.excelExporter.saveGameStateNew();
    }

    public void loadGame(View v) {
        File pathName = new File("/storage/emulated/0/Documents/HEAT-Saves");
        File fullPath = new File("/storage/emulated/0/Documents/HEAT-Saves/heat_save_v01.xls");
        this.excelExporter.loadGameState("heat_save_v01.xls", pathName, fullPath);
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

}