package de.melchers.heat.classes;

import android.app.Activity;
import android.os.Environment;
import android.support.v4.app.INotificationSideChannel;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
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

    public void saveGameState(int playerCount) {
        File savePath = new File("/storage/emulated/0/Documents/HEAT-Saves");
        String fileName = "heat_save_v01.xls";
        File directory = new File(savePath.getAbsolutePath());
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            File file = new File(directory, fileName);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            WritableSheet playerSheet = workbook.createSheet("Players", 0);

            playerSheet.addCell(new Label(1, 1, "Player"));
            playerSheet.addCell(new Label(2, 1, "Last placement"));
            playerSheet.addCell(new Label(3, 1, "Total Score"));

            for (int i = 0; i < playerCount; i++) {
                playerSheet.addCell(new Label(1, i + 2, this.viewModel.players[i].getName()));
                playerSheet.addCell(new Number(2, i + 2, this.viewModel.players[i].getLastPlacement()));
                playerSheet.addCell(new Number(3, i + 2, this.viewModel.players[i].getTotalScore()));

            }
            workbook.write();
            workbook.close();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
    }

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
}
