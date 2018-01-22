package resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.sun.org.apache.regexp.internal.RE;
import org.omg.CORBA.TRANSACTION_MODE;
import representations.CompanyRep;
import representations.PriceInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource {
    private Map<String,Company> companies;

    public CompanyResource(){
        this.companies = new HashMap();

        Company first = new Company("1","Apple","host1", "1111", "Apple Inc. is an American multinational technology company headquartered in Cupertino, California that designs, develops, and sells consumer electronics, computer software, and online services.");
        Company second = new Company("2","Samsung","host1", "1111",  "Samsung Group is a South Korean multinational conglomerate headquartered in Samsung Town, Seoul.");
        Company third = new Company("3","Xiaomi","host1", "1111",  "Xiaomi Inc. (stylized as Mi) is a Chinese electronics and software company headquartered in Beijing.");
        companies.put("Apple",first); companies.put("Samsung",second); companies.put("Xiaomi",third);

        Company fourth = new Company("4","Google","host2", "2222",  "Google LLC is an American multinational technology company that specializes in Internet-related services and products.");
        Company fifth = new Company("5","Facebook","host2", "2222", "Facebook is an American online social media and social networking service based in Menlo Park, California..");
        Company sixth = new Company("6","Twitter","host2", "2222", "Twitter is an online news and social networking service where users post and interact with messages, known as tweets.");
        companies.put("Google",fourth); companies.put("Facebook",fifth); companies.put("Twitter",sixth);

        Company seventh = new Company("7","Amazon","host3", "3333","Amazon.com, Inc., doing business as Amazon is an American electronic commerce and cloud computing company based in Seattle, Washington");
        Company eighth = new Company("8","Ebay","host3", "3333","eBay Inc is a multinational e-commerce corporation headquartered in San Jose, California that facilitates consumer-to-consumer and business-to-consumer sales through its website.");
        Company ninth = new Company("9","AliExpress","host3", "3333","Launched in 2010, AliExpress.com is an online retail service made up of small businesses in China and elsewhere offering products to international online buyers.");
        companies.put("Amazon",seventh); companies.put("Ebay",eighth); companies.put("AliExpress",ninth);
    }

    @GET
    @Path("companies")
    public Response getCompanies(){
        List<CompanyRep> companiesList = new ArrayList();
        for(Company c : companies.values()){
            companiesList.add(new CompanyRep(c.getId(),c.getName(),c.getHost(),c.getPort(),c.getDescription()));
        }
        if(companiesList.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        else return Response.ok(companiesList).build();
    }

    @GET
    @Path("company/{id}")
    public Response getCompany(@PathParam("id") String id){
        if(companies.containsKey(id)){
            Company c = companies.get(id);
            return Response.ok(new CompanyRep(c.getId(),c.getName(),c.getHost(),c.getPort(),c.getDescription())).build();
        }
        else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("company/{id}/info")
    public Response getInfo(@PathParam("id") String id){
        if(companies.containsKey(id)) {
            Company c = companies.get(id);
            float startPrice;
            float endPrice;
            float maxPrice;
            float minPrice;
            PriceInfo dayBeforePI, actualDayPI;
            List<Transaction> dayBeforeTrans = c.getTransactionsFromDayBefore();
            if(dayBeforeTrans == null) {minPrice = 0; maxPrice = 0; startPrice = 0; endPrice = 0;}
            else{
                startPrice = dayBeforeTrans.get(0).getPrice();
                endPrice = dayBeforeTrans.get(dayBeforeTrans.size() - 1).getPrice();
                minPrice = dayBeforeTrans.get(0).getPrice();
                maxPrice = dayBeforeTrans.get(0).getPrice();
                for (Transaction t : dayBeforeTrans) {
                    if(t.getPrice() < minPrice) minPrice = t.getPrice();
                    if(t.getPrice() > maxPrice) maxPrice = t.getPrice();
                }
            }
            dayBeforePI = new PriceInfo(startPrice,endPrice,minPrice,maxPrice);

            List<Transaction> actualDayTrans = c.getTransactionsFromActualDay();
            if(actualDayTrans == null) {minPrice = 0; maxPrice = 0; startPrice = 0; endPrice = 0;}
            else{
                startPrice = actualDayTrans.get(0).getPrice();
                endPrice = actualDayTrans.get(actualDayTrans.size() - 1).getPrice();
                minPrice = actualDayTrans.get(0).getPrice();
                maxPrice = actualDayTrans.get(0).getPrice();
                for (Transaction t : actualDayTrans) {
                    if(t.getPrice() < minPrice) minPrice = t.getPrice();
                    if(t.getPrice() > maxPrice) maxPrice = t.getPrice();
                }
            }
            actualDayPI = new PriceInfo(startPrice,endPrice,minPrice,maxPrice);

            List<PriceInfo> info = new ArrayList();
            info.add(dayBeforePI);
            info.add(actualDayPI);
            return Response.ok(info).build();
        }
        else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("company/{id}")
    public Response putCompany(Company company){
        if(companies.containsKey(company.getId())) return Response.status(Response.Status.CONFLICT).build();
        companies.put(company.getId(),new Company(company.getId(),company.getName(),company.getHost(),company.getPort(),company.getDescription()));
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("company/{id}/order/sell")
    public Response putCompanyOrderSell(Sell order){
        if(!(companies.containsKey(order.getCompany()))) return Response.status(Response.Status.NOT_FOUND).build();
        companies.get(order.getCompany()).addOrder(new Sell(order.getId(), order.getSeller(),order.getCompany(),order.getQuantity(),order.getPrice()));
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("company/{id}/order/buy")
    public Response putCompanyOrderBuy(Buy order){
        if(!(companies.containsKey(order.getCompany()))) return Response.status(Response.Status.NOT_FOUND).build();
        companies.get(order.getCompany()).addOrder(new Buy(order.getId(), order.getBuyer(),order.getCompany(),order.getQuantity(),order.getPrice()));
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("company/{id}/transaction")
    public Response putCompanyTransaction(Transaction transaction){
        if(!(companies.containsKey(transaction.getCompany()))) return Response.status(Response.Status.NOT_FOUND).build();
        companies.get(transaction.getCompany()).addTransaction(transaction);
        return Response.status(Response.Status.CREATED).build();
    }
}
