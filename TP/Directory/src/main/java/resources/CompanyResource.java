package resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.org.apache.regexp.internal.RE;
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
    private int id;

    public CompanyResource(){
        this.companies = new HashMap();
        this.id = 0;

        Company co = new Company("1","Emp","ex1","cenas");
        companies.put("1",co);
        Order o = new Sell("1","tres","1",300,(float)20.3);
        Order o2 = new Buy("2","tres","1",350,(float)40.2);
        co.addOrder(o);
        co.addOrder(o2);

    }

    @GET
    @Path("companies")
    public Response getCompanies(){
        List<CompanyRep> companiesList = new ArrayList();
        for(Company c : companies.values()){
            companiesList.add(new CompanyRep(c.getId(),c.getName(),c.getExange(),c.getDescription()));
        }
        if(companiesList.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        else return Response.ok(companiesList).build();
    }

    @GET
    @Path("company/{id}")
    public Response getCompany(@PathParam("id") String id){
        if(companies.containsKey(id)){
            Company c = companies.get(id);
            return Response.ok(new CompanyRep(c.getId(),c.getName(),c.getExange(),c.getDescription())).build();
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
        companies.put(company.getId(),new Company(company.getId(),company.getName(),company.getExange(),company.getDescription()));
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
