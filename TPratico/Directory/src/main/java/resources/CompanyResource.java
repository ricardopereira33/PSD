package resources;

import com.sun.org.apache.regexp.internal.RE;
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
    private int id;

    public CompanyResource(){
        this.companies = new HashMap();
        this.id = 0;

        Company co = new Company("1","Emp","ex1","cenas");
        companies.put("1",co);
        User u = new User("tres");
        Order o = new Sell(u,"1",300,(float)20.3);
        Order o2 = new Buy(u,"1",350,(float)40.2);
        co.addOrder(o);
        co.addOrder(o2);


    }

    @GET
    @Path("companies")
    public List<CompanyRep> getCompanies(){
        List<CompanyRep> companiesList = new ArrayList();
        for(Company c : companies.values()){
            companiesList.add(new CompanyRep(c.getId(),c.getName(),c.getExange(),c.getDescription()));
        }
        return companiesList;
    }

    @GET
    @Path("company/{id}")
    public CompanyRep getCompany(@PathParam("id") String id){
        if(companies.containsKey(id)){
            Company c = companies.get(id);
            return new CompanyRep(c.getId(),c.getName(),c.getExange(),c.getDescription());
        }
        else return null;
    }

    @GET
    @Path("company/{id}/info")
    public List<PriceInfo> getInfo(@PathParam("id") String id){
        if(companies.containsKey(id)) {
            Company c = companies.get(id);
            float startPrice;
            float endPrice;
            float maxPrice;
            float minPrice;
            PriceInfo dayBeforePI, actualDayPI;
            List<Order> dayBeforeOrd = c.getOrdersFromDayBefore();
            List<Transaction> dayBeforeTrans = c.getTransactionsFromDayBefore();
            if(dayBeforeOrd == null) {startPrice = 0; endPrice = 0;}
            else{
                startPrice = dayBeforeOrd.get(0).getPrice();
                endPrice = dayBeforeOrd.get(dayBeforeOrd.size() - 1).getPrice();
            }
            if(dayBeforeTrans == null) {minPrice = 0; maxPrice = 0;}
            else{
                minPrice = dayBeforeTrans.get(0).getPrice();
                maxPrice = dayBeforeTrans.get(0).getPrice();
                for (Transaction t : dayBeforeTrans) {
                    if(t.getPrice() < minPrice) minPrice = t.getPrice();
                    if(t.getPrice() > maxPrice) maxPrice = t.getPrice();
                }
            }
            dayBeforePI = new PriceInfo(startPrice,endPrice,minPrice,maxPrice);

            List<Order> actualDayOrd = c.getOrdersFromActualDay();
            List<Transaction> actualDayTrans = c.getTransactionsFromActualDay();
            if(actualDayOrd == null) {startPrice = 0; endPrice = 0;}
            else {
                startPrice = actualDayOrd.get(0).getPrice();
                endPrice = actualDayOrd.get(actualDayOrd.size() - 1).getPrice();
            }
            if(actualDayTrans == null) {minPrice = 0; maxPrice = 0;}
            else{
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
            return info;
        }
        else return null;
    }

    @PUT
    @Path("company/{id}")
    public Response putCompany(Company company){
        for(Company c : companies.values()){
            if(c.getName().equals(company.getId())) return Response.noContent().build();
        }
        String newId = Integer.toString(id);
        companies.put(newId,company);
        return Response.ok().build();
    }

    @PUT
    @Path("company/{id}/order")
    public Response putCompanyOrder(Order order){
        for(Company c : companies.values()){
            if(c.getName().equals(order.getCompany())) return Response.noContent().build();
        }
        companies.get(order.getCompany()).addOrder(order);
        return Response.ok().build();
    }

    @PUT
    @Path("company/{id}/transaction")
    public Response putCompanyTransaction(Transaction transaction){
        for(Company c : companies.values()){
            if(c.getName().equals(transaction.getCompany())) return Response.noContent().build();
        }
        companies.get(transaction.getCompany()).addTransaction(transaction);
        return Response.ok().build();
    }
}
