package resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {
    private static Gson gson = new Gson();

    public static void main(String[] args) throws Exception{
        Test.getCompanies();
        Test.getCompaniesInfo();
        Test.getCompany("Google");
        Test.getCompanyInfo("Google");

        Thread.sleep(2000);
        Test.sendTransaction(new Transaction("id1","sell","buy",400,3f,"Google"));
        Test.getCompanyInfo("Google");
        Thread.sleep(2000);
        Test.sendTransaction(new Transaction("id1","sell","buy",500,4f,"Google"));
        Test.getCompanyInfo("Google");
        Thread.sleep(2000);
        Test.sendTransaction(new Transaction("id1","sell","buy",600,5f,"Google"));
        Test.getCompanyInfo("Google");
        Thread.sleep(20000);
        Test.sendTransaction(new Transaction("id1","sell","buy",600,5f,"Google"));
        Test.getCompanyInfo("Google");
    }

    public static void sendTransaction(Transaction t) throws Exception{
        String jsonTransaction = gson.toJson(t);
        System.out.println(jsonTransaction);

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(jsonTransaction).getAsJsonObject();
        String imgurl = obj.get("quantity").getAsString();
        System.out.println(imgurl);

        String urly = "http://localhost:8080/company/"+t.getCompany()+"/transaction";
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonTransaction);
        wr.flush();
        wr.close();
        int response = con.getResponseCode();
        System.out.println(response);
    }

    public static void getCompany(String company) throws Exception{
        String urly = "http://localhost:8080/company/"+company;
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        StringBuilder result = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        System.out.println(result.toString());
    }

    public static void getCompanyInfo(String company) throws Exception{
        String urly = "http://localhost:8080/company/"+company+"/info";
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        StringBuilder result = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        System.out.println(result.toString());
    }

    public static void getCompanies() throws Exception{
        String urly = "http://localhost:8080/companies";
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        StringBuilder result = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        System.out.println(result.toString());
    }

    public static void getCompaniesInfo() throws Exception{
        String urly = "http://localhost:8080/companies/info";
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        StringBuilder result = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        System.out.println(result.toString());
    }
}
