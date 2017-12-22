import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectoryAccess {

    public static void main(String[] args) throws Exception{
        String getCompanies = DirectoryAccess.getCompanies();
        System.out.println(getCompanies);
        String getCompany = DirectoryAccess.getCompany("1");
        System.out.println(getCompany);
        String getCompanyInfo = DirectoryAccess.getCompanyInfo("1");
        System.out.println(getCompanyInfo);
    }

    public static String getCompanies() throws Exception{
        StringBuilder result = new StringBuilder();
        String urly = "http://localhost:8080/companies";
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    public static String getCompany(String id) throws Exception{
        StringBuilder result = new StringBuilder();
        String urly = "http://localhost:8080/company/"+id;
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    public static String getCompanyInfo(String id) throws Exception{
        StringBuilder result = new StringBuilder();
        String urly = "http://localhost:8080/company/"+id+"/info";
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

}
