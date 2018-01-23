package exchange;

import com.google.gson.Gson;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectorySender {
    private static Gson gson = new Gson();

    /**public static void main(String[] args) throws Exception{
        DirectorySender.createCompany(new Company("2","yaya","ex2","descr"));
        DirectorySender.sendOrderSell(new Sell("tres","2",300, 40));
        DirectorySender.sendOrderBuy(new Buy("tres","2",800, 60));
        DirectorySender.sendTransaction(new Transaction("1","1","1",40,54,"1"));
    }**/

    public static void createCompany(Company c) throws Exception{
        String jsonCompany = gson.toJson(c);
        //System.out.println(jsonCompany);
        String urly = "http://localhost:8080/company/"+c.getName();
        //System.out.println(urly);
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonCompany);
        wr.flush();
        wr.close();
        int response = con.getResponseCode();
        //System.out.println(response);
    }

    public static void sendOrderSell(Sell o) throws Exception{
        String jsonOrder = gson.toJson(o);
        System.out.println(jsonOrder);
        String urly = "http://localhost:8080/company/"+o.getCompany()+"/order/sell";
        //System.out.println(urly);
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonOrder);
        wr.flush();
        wr.close();
        int response = con.getResponseCode();
        //System.out.println(response);
    }

    public static void sendOrderBuy(Buy o) throws Exception{
        String jsonOrder = gson.toJson(o);
        //System.out.println(jsonOrder);
        String urly = "http://localhost:8080/company/"+o.getCompany()+"/order/buy";
        //System.out.println(urly);
        URL url = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonOrder);
        wr.flush();
        wr.close();
        int response = con.getResponseCode();
        //System.out.println(response);
    }

    public static void sendTransaction(Transaction t) throws Exception{
        String jsonTransaction = gson.toJson(t);
        //System.out.println(jsonTransaction);
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
        //System.out.println(response);
    }
}
