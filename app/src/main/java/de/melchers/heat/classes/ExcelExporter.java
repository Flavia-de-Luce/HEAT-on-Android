package de.melchers.heat.classes;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import org.json.JSONException;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelExporter {
    private final HeatViewModel viewModel;

    public ExcelExporter(ViewModelStoreOwner activity) {
        this.viewModel = new ViewModelProvider(activity).get(HeatViewModel.class);
    }

    public void loadGameStateNew(String fileName, File pathName, File fullPath) {
        int counter = 0;

        File directory = new File(pathName.getAbsolutePath());
        File file = new File(directory, fileName);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
        Workbook workbook;

        try {
            workbook = Workbook.getWorkbook(fullPath);
            Sheet sheet = workbook.getSheet("HEAT");
            Sheet playersheet = workbook.getSheet("Player-Info");

            // Auslesen der Player-Season Info
            while (counter < Integer.parseInt(sheet.getCell(1, 0).getContents())) {
                Player player = new Player(sheet.getCell(1 + counter, 2).getContents());
                player.setLastPlacement(Integer.parseInt(sheet.getCell(1 + counter, 3).getContents()));
                player.setTotalScore(Integer.parseInt(sheet.getCell(1 + counter, 4).getContents()));
                this.viewModel.players.add(player);
                counter++;
            }

            // Auslesen der Player Info
            int playerInfoCount = 0;
            for (Player player : this.viewModel.players){
                player.color = playersheet.getCell(1, playerInfoCount + 1).getContents();
                player.setTotalRounds(Integer.parseInt(playersheet.getCell(2, playerInfoCount + 1).getContents()));
                player.setTotalScore(Integer.parseInt(playersheet.getCell(3, playerInfoCount + 1).getContents()));
                player.allPlacements.put(1, Integer.parseInt(playersheet.getCell(4, playerInfoCount + 1).getContents()));
                for (int i = 1; i < this.viewModel.players.size(); i++){
                    player.allPlacements.put(i + 1, Integer.parseInt(playersheet.getCell(i + 4, playerInfoCount + 1).getContents()));
                }
                playerInfoCount++;
            }


            //TODO: NICHT VERGESSEN falls sich die Menge an Kopfzeilen verändert!
            int lastReadRow = 6;
            for (int i = 0; i < Integer.parseInt(sheet.getCell(0, 0).getContents()); i++) {
                Cup cup = new Cup();
                cup.id = i + 1;
                int count = 1;
                // Auslesen der Rennen pro Cup
                while (sheet.getCell(0, lastReadRow + 1).getContents().contains("Rennen")) {
                    Race race = new Race();
                    race.results = new HashMap<>();
                    race.setId(count);
                    String mapName = sheet.getCell(0, lastReadRow + 1).getContents(); // .substring(6)
                    race.setMapName(mapName.substring(mapName.indexOf(",") + 2));
                    for (int y = 0; y < viewModel.players.size(); y++) {
                        race.results.put(viewModel.players.get(y), Integer.valueOf(sheet.getCell(1 + y, lastReadRow + 1).getContents()));
                    }
                    cup.races.add(race);
                    lastReadRow++;
                    count++;
                }
                // Auslesen des Gesamtscores pro Cup
                if (sheet.getCell(0, lastReadRow + 1).getContents().contains("Cup Gesamt")) {
                    for (int y = 0; y < viewModel.players.size(); y++) {
                        cup.totalScore.put(viewModel.players.get(y), Integer.valueOf(sheet.getCell(1 + y, lastReadRow + 1).getContents()));
                    }
                    lastReadRow++;
                }
                lastReadRow += 2;
                viewModel.cups.add(cup);

            }
            viewModel.currentCup = viewModel.cups.get(viewModel.cups.size() - 1);
            if (viewModel.cups.size() != Integer.parseInt(sheet.getCell(0, 0).getContents())) {
                System.out.println("Reele anzahl Cups: " + viewModel.cups.size());
                System.out.println("Angebliche anzahl Cups: " + Integer.parseInt(sheet.getCell(0, 0).getContents()));
            }
        } catch (BiffException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGameStateNew() {
        File savePath = new File("/storage/emulated/0/Documents/HEAT-Saves");
        String fileName = "heat_save_v03.xls";
        File directory = new File(savePath.getAbsolutePath());
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            // Setup zum speichern
            File file = new File(directory, fileName);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("HEAT", 0);
            WritableSheet playerSheet = workbook.createSheet("Player-Info", 1);
            // Save Player-Season Info
            sheet.addCell(new Label(0, 2, "Spieler"));
            sheet.addCell(new Label(0, 3, "Letzte Platzierung"));
            sheet.addCell(new Label(0, 4, "Gesamt Punkte"));
            for (int i = 0; i < viewModel.players.size(); i++) {
                sheet.addCell(new Label(1 + i, 2, this.viewModel.players.get(i).getName()));
                sheet.addCell(new Number(1 + i, 3, this.viewModel.players.get(i).getLastPlacement()));
                sheet.addCell(new Number(1 + i, 4, this.viewModel.players.get(i).getTotalScore()));

            }

            // Save Player Info
            playerSheet.addCell(new Label(0, 0, "Spieler Name"));
            playerSheet.addCell(new Label(1, 0, "Spieler Farbe"));
            playerSheet.addCell(new Label(2, 0, "Anzahl Runden"));
            playerSheet.addCell(new Label(3, 0, "Gesamt Punkte"));
            playerSheet.addCell(new Label(4, 0, "Anzahl erster Platz"));
            switch (this.viewModel.players.size()) {
                case 2:
                    playerSheet.addCell(new Label(5, 0, "Anzahl zweiter Platz"));
                    break;
                case 3:
                    playerSheet.addCell(new Label(5, 0, "Anzahl zweiter Platz"));
                    playerSheet.addCell(new Label(6, 0, "Anzahl dritter Platz"));
                    break;
                case 4:
                    playerSheet.addCell(new Label(5, 0, "Anzahl zweiter Platz"));
                    playerSheet.addCell(new Label(6, 0, "Anzahl dritter Platz"));
                    playerSheet.addCell(new Label(7, 0, "Anzahl vierter Platz"));
                    break;
                case 5:
                    playerSheet.addCell(new Label(5, 0, "Anzahl zweiter Platz"));
                    playerSheet.addCell(new Label(6, 0, "Anzahl dritter Platz"));
                    playerSheet.addCell(new Label(7, 0, "Anzahl vierter Platz"));
                    playerSheet.addCell(new Label(8, 0, "Anzahl fünfter Platz"));
                    break;
                case 6:
                    playerSheet.addCell(new Label(5, 0, "Anzahl zweiter Platz"));
                    playerSheet.addCell(new Label(6, 0, "Anzahl dritter Platz"));
                    playerSheet.addCell(new Label(7, 0, "Anzahl vierter Platz"));
                    playerSheet.addCell(new Label(8, 0, "Anzahl fünfter Platz"));
                    playerSheet.addCell(new Label(9, 0, "Anzahl sechster Platz"));
                    break;
            }
            int playerInfoCount = 0;
            int totalRounds = 0;
            for (Cup cup : this.viewModel.cups) {
                totalRounds += cup.races.size();
            }
            try {
                for (Player player : this.viewModel.players) {
                    playerSheet.addCell(new Label(0, playerInfoCount + 1, player.getName()));
                    playerSheet.addCell(new Label(1, playerInfoCount + 1, player.color));
                    playerSheet.addCell(new Number(2, playerInfoCount + 1, totalRounds));
                    playerSheet.addCell(new Number(3, playerInfoCount + 1, player.getTotalScore()));
                    playerSheet.addCell(new Number(4, playerInfoCount + 1, player.allPlacements.get(1)));
                    switch (this.viewModel.players.size()){
                        case 2:
                            playerSheet.addCell(new Number(5, playerInfoCount + 1, player.allPlacements.get(2)));
                            break;
                        case 3:
                            playerSheet.addCell(new Number(5, playerInfoCount + 1, player.allPlacements.get(2)));
                            playerSheet.addCell(new Number(6, playerInfoCount + 1, player.allPlacements.get(3)));
                            break;
                        case 4:
                            playerSheet.addCell(new Number(5, playerInfoCount + 1, player.allPlacements.get(2)));
                            playerSheet.addCell(new Number(6, playerInfoCount + 1, player.allPlacements.get(3)));
                            playerSheet.addCell(new Number(7, playerInfoCount + 1, player.allPlacements.get(4)));
                            break;
                        case 5:
                            playerSheet.addCell(new Number(5, playerInfoCount + 1, player.allPlacements.get(2)));
                            playerSheet.addCell(new Number(6, playerInfoCount + 1, player.allPlacements.get(3)));
                            playerSheet.addCell(new Number(7, playerInfoCount + 1, player.allPlacements.get(4)));
                            playerSheet.addCell(new Number(8, playerInfoCount + 1, player.allPlacements.get(5)));
                            break;
                        case 6:
                            playerSheet.addCell(new Number(5, playerInfoCount + 1, player.allPlacements.get(2)));
                            playerSheet.addCell(new Number(6, playerInfoCount + 1, player.allPlacements.get(3)));
                            playerSheet.addCell(new Number(7, playerInfoCount + 1, player.allPlacements.get(4)));
                            playerSheet.addCell(new Number(8, playerInfoCount + 1, player.allPlacements.get(5)));
                            playerSheet.addCell(new Number(9, playerInfoCount + 1, player.allPlacements.get(6)));
                            break;
                    }
                    playerInfoCount++;
                }
            } catch (NullPointerException ex){
                System.out.println(ex.getMessage());
            }

            //TODO: NICHT VERGESSEN falls sich die Menge an Kopfzeilen verändert! Immer +1 für Formatierung
            int lastWrittenRow = 4 + 1;
            sheet.addCell(new Number(0, 0, viewModel.cups.size()));
            sheet.addCell(new Number(1, 0, viewModel.players.size()));
            // Each Cup
            for (Cup cup : viewModel.cups) {
                sheet.addCell(new Label(0, lastWrittenRow + 1, "Cup " + cup.id));
                lastWrittenRow++;
                // Each Race
                for (int i = 0; i < cup.races.size(); i++) {
                    sheet.addCell(new Label(0, lastWrittenRow + 1, "Rennen " + (i + 1) + ", " + cup.races.get(i).getMapName()));
                    lastWrittenRow++;
                    // Each Player
//                        for (Player player:viewModel.players) {
                    for (int y = 0; y < viewModel.players.size(); y++) {
                        // TODO: Das darf nicht funktionieren...
                        // playerSheet.addCell(new Number(2 + i, lastWrittenRow, cup.races.get(i).getResults().getJSONObject(cup.races.get(i).getMapName()).getDouble(player.getName())));// .cup.getJSONObject(i).getJSONObject(cup.getCurrentRace().getMapName()).getDouble(player.getName())));
                        sheet.addCell(new Number(1 + y, lastWrittenRow, cup.races.get(i).getResults().get(viewModel.players.get(y))));
                    }
                }
                sheet.addCell(new Label(0, lastWrittenRow + 1, "Cup Gesamt"));
                lastWrittenRow++;
                for (int x = 0; x < viewModel.players.size(); x++) {
                    sheet.addCell(new Number(1 + x, lastWrittenRow, cup.totalScore.get(viewModel.players.get(x))));
                }
                // Für das Format :)
                lastWrittenRow++;
            }
            workbook.write();
            workbook.close();
        } catch (WriteException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
