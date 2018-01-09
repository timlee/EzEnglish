package wordsCard;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import sqlite.Words;

/**
 * Created by User on 2016/2/3.
 */
public class AddWordsActivity {

    Words addWords;
    Words[] addWordsData;
    int w_id = 0;
    String word = "";
    int r_id = 0;


    public Words[] readExcel() {
        String fileposition = "C:\\Users\\User\\wtlab\\Knowmemo\\knowMemo_newDB_words.xls";
        try {
            InputStream is = new FileInputStream(fileposition);
            Workbook book = Workbook.getWorkbook(is);
            Sheet sheet = book.getSheet(0);
            int Cols = sheet.getColumns();

            addWords = new Words(w_id,word,r_id);;

            for (int i = 0; i < Cols; ++i) {

                addWords.setW_id(Integer.parseInt(sheet.getCell(i, 0).getContents()));
                addWords.setWord(sheet.getCell(i, 1).getContents());
                addWords.setR_id(Integer.parseInt(sheet.getCell(i, 2).getContents()));

                addWordsData[i] = addWords;

            }

            book.close();

        } catch (BiffException e) {
            System.out.print("BiffExeception : " );
            e.printStackTrace();
        } catch (IOException e) {
            System.out.print("IOException : " );
            e.printStackTrace();
        }

        return addWordsData;
    }



}


