package de.melchers.heat.classes;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import org.json.JSONException;

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

            // Auslesen der Spieler Info
            while (counter < Integer.parseInt(sheet.getCell(1, 0).getContents())) {
                Player player = new Player(sheet.getCell(1 + counter, 2).getContents());
                player.setLastPlacement(Integer.parseInt(sheet.getCell(1 + counter, 3).getContents()));
                player.setTotalScore(Integer.parseInt(sheet.getCell(1 + counter, 4).getContents()));
                this.viewModel.players.add(player);
                counter++;
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
        String fileName = "heat_save_v01.xls";
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
            // Save Player Info
            sheet.addCell(new Label(0, 2, "Player"));
            sheet.addCell(new Label(0, 3, "Last placement"));
            sheet.addCell(new Label(0, 4, "Total Score"));
            for (int i = 0; i < viewModel.players.size(); i++) {
                sheet.addCell(new Label(1 + i, 2, this.viewModel.players.get(i).getName()));
                sheet.addCell(new Number(1 + i, 3, this.viewModel.players.get(i).getLastPlacement()));
                sheet.addCell(new Number(1 + i, 4, this.viewModel.players.get(i).getTotalScore()));

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
