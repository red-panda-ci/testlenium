package com.testlenium.web.site;

import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;


@Log4j
public class DataSetReader {
    private DataSetReader() {
    }

    /**
     * Json reader for dataset configurations
     *
     * @param dataTitle
     * @return
     * @throws ParseException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static DataSet reader(String dataTitle) throws ParseException, IOException {
        String path = "src/test/resources/dataset.json";
        DataSet promo = new DataSet();
        boolean find = false;
        JSONParser parser = new JSONParser();
        Reader reader = new FileReader(path);
        Object jsonObj = parser.parse(reader);
        JSONObject jsonObject = (JSONObject) jsonObj;
        JSONArray slideContent = (JSONArray) jsonObject.get("dataset");
        Iterator i = slideContent.iterator();

        while (i.hasNext() && !find) {
            JSONObject slide = (JSONObject) i.next();
            String title = (String) slide.get("title");
            if (dataTitle.equals(title)) {
                find = true;
                promo.setTitle(title);
                promo.setUrl((String) slide.get("url"));
            }
        }
        reader.close();
        return promo;
    }

}
