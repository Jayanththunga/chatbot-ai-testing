package utility;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

public class GoogleSheetUtil {

   private static final String APPLICATION_NAME = "GSheet-API";
   private static Sheets sheetsService;

   private static Sheets getSheetsService() throws IOException, GeneralSecurityException {
      if (sheetsService == null) {
         InputStream stream = GoogleSheetUtil.class.getClassLoader().getResourceAsStream("gapi_credentials.json");
         if (stream == null) {
            throw new IOException("Credential file not found in resources");
         }

         GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
               .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

         HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
         sheetsService = new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
               requestInitializer).setApplicationName(APPLICATION_NAME).build();
      }
      return sheetsService;
   }

   public static String readCell_withRange(String spreadsheetId, String range) {
      List<List<Object>> values = readSheet(spreadsheetId, range);
      if (values != null && !values.isEmpty() && !values.get(0).isEmpty()) {
         return values.get(0).get(0).toString();
      } else
         return null;
   }

   public static void writeCell_withRange(String spreadsheetId, String range, String value) {
      ValueRange body = new ValueRange().setValues(List.of(List.of(value)));
      try {
         getSheetsService().spreadsheets().values().update(spreadsheetId, range, body).setValueInputOption("RAW")
               .execute();
      } catch (IOException | GeneralSecurityException e) {
         e.printStackTrace();
      }
   }

   public static List<List<Object>> readSheet(String spreadsheetId, String range) {
      ValueRange response;
      try {
         response = getSheetsService().spreadsheets().values().get(spreadsheetId, range).execute();
         return response.getValues();
      } catch (IOException | GeneralSecurityException e) {
         e.printStackTrace();
      }
      return null;
   }

   public static List<String> getAllSheetNames(String spreadsheetId) {
      try {
         Sheets service = getSheetsService();
         List<Sheet> sheets = service.spreadsheets().get(spreadsheetId).execute().getSheets();
         return sheets.stream()
               .map(s -> s.getProperties().getTitle())
               .collect(Collectors.toList());
      } catch (IOException | GeneralSecurityException e) {
         e.printStackTrace();
         return Collections.emptyList();
      }
   }

}