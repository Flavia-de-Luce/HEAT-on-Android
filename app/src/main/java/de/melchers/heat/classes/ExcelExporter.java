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
    private HeatViewModel viewModel;

    public ExcelExporter(ViewModelStoreOwner activity) {
        this.viewModel = new ViewModelProvider(activity).get(HeatViewModel.class);
    }

    public static void importXLSX(String fileName, File sd, File directory) {
        try {
            File file = new File(directory, fileName);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            Workbook workbook;
            workbook = Workbook.getWorkbook(sd);
            Sheet sheet = workbook.getSheet(0);

            System.out.println(sheet.getCell(1, 0).getContents());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * public void loadGameStateOLD(String fileName, File pathName, File fullPath) {
     * int counter = 0;
     * boolean isDefined = true;
     * <p>
     * File directory = new File(pathName.getAbsolutePath());
     * File file = new File(directory, fileName);
     * WorkbookSettings wbSettings = new WorkbookSettings();
     * wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
     * Workbook workbook;
     * <p>
     * try {
     * workbook = Workbook.getWorkbook(fullPath);
     * Sheet playerSheet = workbook.getSheet("Players");
     * Player[] players = new Player[playerSheet.getRows() - 2];
     * while (counter < playerSheet.getRows() - 2) {
     * <p>
     * Player player = new Player(playerSheet.getCell(1, counter + 2).getContents());
     * player.setLastPlacement(Integer.parseInt(playerSheet.getCell(2, counter + 2).getContents()));
     * player.setTotalScore(Integer.parseInt(playerSheet.getCell(3, counter + 2).getContents()));
     * players[counter] = player;
     * counter++;
     * }
     * this.viewModel.players = players;
     * } catch (BiffException | IOException e) {
     * e.printStackTrace();
     * }
     * }
     **/

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
//    public void saveGameState() {
//        File savePath = new File("/storage/emulated/0/Documents/HEAT-Saves");
//        String fileName = "heat_save_v01.xls";
//        File directory = new File(savePath.getAbsolutePath());
//        if (!directory.isDirectory()) {
//            directory.mkdirs();
//        }
//        try {
//            File file = new File(directory, fileName);
//            WorkbookSettings wbSettings = new WorkbookSettings();
//            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
//            WritableWorkbook workbook;
//            workbook = Workbook.createWorkbook(file, wbSettings);
//
//            WritableSheet playerSheet = workbook.createSheet("Players", 0);
//
//            playerSheet.addCell(new Label(1, 1, "Player"));
//            playerSheet.addCell(new Label(2, 1, "Last placement"));
//            playerSheet.addCell(new Label(3, 1, "Total Score"));
//
//            for (int i = 0; i < this.viewModel.players.length; i++) {
//                playerSheet.addCell(new Label(1, i + 2, this.viewModel.players[i].getName()));
//                playerSheet.addCell(new Number(2, i + 2, this.viewModel.players[i].getLastPlacement()));
//                playerSheet.addCell(new Number(3, i + 2, this.viewModel.players[i].getTotalScore()));
//
//                workbook.write();
//                workbook.close();
//            } catch (IOException | WriteException e) {
//                e.printStackTrace();
//            }
//        }
//            }

    public static void export() {
        //File sd = Environment.getExternalStorageDirectory();
        File sd = new File("/storage/emulated/0/download");
        String csvFile = "testExporter.xlsx";

        File directory = new File(sd.getAbsolutePath());

        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheetA first sheetA
            WritableSheet sheetA = workbook.createSheet("sheet A", 0);

            // column and row titles
            sheetA.addCell(new Label(0, 0, "sheet A 1"));
            sheetA.addCell(new Label(1, 0, "sheet A 2"));
            sheetA.addCell(new Label(0, 1, "sheet A 3"));
            sheetA.addCell(new Label(1, 1, "sheet A 4"));
            System.out.println(sheetA.getCell(1, 0).getContents());

            //Excel sheetB represents second sheet
            WritableSheet sheetB = workbook.createSheet("sheet B", 1);

            // column and row titles
            sheetB.addCell(new Label(0, 0, "sheet B 1"));
            sheetB.addCell(new Label(1, 0, "sheet B 2"));
            sheetB.addCell(new Label(0, 1, "sheet B 3"));
            sheetB.addCell(new Label(1, 1, "sheet B 4"));

            // close workbook
            workbook.write();
            workbook.close();

        } catch (Exception e) {
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
//                    sheet.addCell(new Label(1, lastWrittenRow + 1, "Season " + season.id));
//                    lastWrittenRow++;
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
    /**
     public void saveGameStateDEPRICATED() {
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
     WritableSheet[] seasonSheets = new WritableSheet[viewModel.seasons.size()];
     // Erstellen der Tabellen Blätter für jede Season
     for (int i = 0; i < viewModel.seasons.size(); i++) {
     seasonSheets[i] = workbook.createSheet("Season " + viewModel.seasons.get(i).id, i);
     }

     // Für jede Season jeweils die werte Speichern.
     for (WritableSheet sheet : seasonSheets) {
     sheet.addCell(new Label(1, 1, "Player"));
     sheet.addCell(new Label(1, 2, "Last placement"));
     sheet.addCell(new Label(1, 3, "Total Score"));
     for (int i = 0; i < viewModel.players.length; i++) {
     sheet.addCell(new Label(2 + i, 1, this.viewModel.players[i].getName()));
     sheet.addCell(new Number(2 + i, 2, this.viewModel.players[i].getLastPlacement()));
     sheet.addCell(new Number(2 + i, 3, this.viewModel.players[i].getTotalScore()));

     }

     //TODO: NICHT VERGESSEN falls sich die Menge an Kopfzeilen verändert!
     int lastWrittenRow = 3;
     // Each Season
     for (Season season : viewModel.seasons) {
     sheet.addCell(new Number(0, 0, season.cups.size()));
     //                    sheet.addCell(new Label(1, lastWrittenRow + 1, "Season " + season.id));
     //                    lastWrittenRow++;
     // Each Cup
     for (Cup cup : season.cups) {
     sheet.addCell(new Label(1, lastWrittenRow + 1, "Cup " + cup.id));
     lastWrittenRow++;
     // Each Race
     for (int i = 0; i < cup.races.size(); i++) {
     sheet.addCell(new Label(1, lastWrittenRow + 1, "Rennen " + i + ", " + cup.getRaces().get(i).getMapName()));
     lastWrittenRow++;
     // Each Player
     //                        for (Player player:viewModel.players) {
     for (int y = 0; y < viewModel.players.length; y++) {
     // TODO: Das darf nicht funktionieren...
     try {
     //                                playerSheet.addCell(new Number(2 + i, lastWrittenRow, cup.races.get(i).getResults().getJSONObject(cup.races.get(i).getMapName()).getDouble(player.getName())));// .cup.getJSONObject(i).getJSONObject(cup.getCurrentRace().getMapName()).getDouble(player.getName())));
     sheet.addCell(new Number(2 + y, lastWrittenRow, cup.races.get(i).getResults().getDouble(viewModel.players[y].getName())));
     } catch (JSONException e) {
     e.printStackTrace();
     }
     }
     }

     }
     }
     }


     workbook.write();
     workbook.close();
     } catch (IOException | WriteException e) {
     e.printStackTrace();
     }
     }
     **/

    /**
     //TODO: Funktion erst nach der Auswahl der Season aufrufen. Vorher die Season List nutzen
     public void loadGameStateDEPRICATEDandNOTFINALLYIMPLEMENTED(String fileName, File pathName, File fullPath) {
     int counter = 0;

     File directory = new File(pathName.getAbsolutePath());
     File file = new File(directory, fileName);
     WorkbookSettings wbSettings = new WorkbookSettings();
     wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
     Workbook workbook;

     try {
     workbook = Workbook.getWorkbook(fullPath);
     ArrayList<Sheet> seasonSheets = new ArrayList<>();
     for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
     seasonSheets.add(workbook.getSheet(i));
     }
     Sheet sheet1 = seasonSheets.get(0);
     viewModel.players = new Player[sheet1.getColumns()];
     for (int i = 1; i < seasonSheets.get(0).getColumns(); i++) {
     Player player = new Player(sheet1.getCell(1 + i, 1).getContents());
     player.setLastPlacement(Integer.parseInt(sheet1.getCell(1 + i, 2).getContents()));
     player.setTotalScore(Integer.parseInt(sheet1.getCell(1 + i, 3).getContents()));
     viewModel.players[i] = player;
     }
     viewModel.seasons = new ArrayList<>();
     int count = 1;
     //            for (Sheet sheet:seasonSheets) {
     //                Season season = new Season();
     //                season.id = count;
     //                count++;
     //                for (int i = 3; i < sheet.getRows(); i++){
     //                    Cup cup = new Cup();
     //                    sheet.getColumn(1);
     //                    cup.id =
     //                }
     //            }
     Sheet playerSheet = workbook.getSheet("Players");
     Player[] players = new Player[playerSheet.getRows() - 2];
     while (counter < playerSheet.getRows() - 2) {

     Player player = new Player(
     playerSheet.getCell(1, counter + 2).getContents());
     player.setLastPlacement(Integer.parseInt(
     playerSheet.getCell(2, counter + 2).getContents()));
     player.setTotalScore(Integer.parseInt(
     playerSheet.getCell(3, counter + 2).getContents()));
     players[counter] = player;
     counter++;
     }
     this.viewModel.players = players;
     } catch (BiffException | IOException e) {
     e.printStackTrace();
     }
     }
     **/
}
