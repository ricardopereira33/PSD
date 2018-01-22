package resources;

import representations.CompanyInfoRep;
import representations.CompanyRep;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource {
    private Map<String,Company> companies;
    private Clock clock;

    public CompanyResource(){
        this.companies = new HashMap();
        this.clock = new Clock();

        Company first = new Company("Apple","host1", "1111", "Apple Inc. is an American multinational technology company headquartered in Cupertino, California that designs, develops, and sells consumer electronics, computer software, and online services.","Exchange 1",clock);
        Company second = new Company("Samsung","host1", "1111",  "Samsung Group is a South Korean multinational conglomerate headquartered in Samsung Town, Seoul.","Exchange 1",clock);
        Company third = new Company("Xiaomi","host1", "1111",  "Xiaomi Inc. (stylized as Mi) is a Chinese electronics and software company headquartered in Beijing.","Exchange 1",clock);
        companies.put("Apple",first); companies.put("Samsung",second); companies.put("Xiaomi",third);

        Company fourth = new Company("Google","host2", "2222",  "Google LLC is an American multinational technology company that specializes in Internet-related services and products.","Exchange 2",clock);
        Company fifth = new Company("Facebook","host2", "2222", "Facebook is an American online social media and social networking service based in Menlo Park, California..","Exchange 2",clock);
        Company sixth = new Company("Twitter","host2", "2222", "Twitter is an online news and social networking service where users post and interact with messages, known as tweets.","Exchange 2",clock);
        companies.put("Google",fourth); companies.put("Facebook",fifth); companies.put("Twitter",sixth);

        Company seventh = new Company("Amazon","host3", "3333","Amazon.com, Inc., doing business as Amazon is an American electronic commerce and cloud computing company based in Seattle, Washington","Exchange 3",clock);
        Company eighth = new Company("Ebay","host3", "3333","eBay Inc is a multinational e-commerce corporation headquartered in San Jose, California that facilitates consumer-to-consumer and business-to-consumer sales through its website.","Exchange 3",clock);
        Company ninth = new Company("AliExpress","host3", "3333","Launched in 2010, AliExpress.com is an online retail service made up of small businesses in China and elsewhere offering products to international online buyers.","Exchange 3",clock);
        companies.put("Amazon",seventh); companies.put("Ebay",eighth); companies.put("AliExpress",ninth);
    }

    @GET
    @Path("companies")
    public Response getCompanies(){
        List<CompanyRep> companiesList = new ArrayList();
        for(Company c : companies.values()){
            companiesList.add(new CompanyRep(c.getName(),c.getHost(),c.getPort(),c.getDescription(),c.getExchange()));
        }
        if(companiesList.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        else return Response.ok(companiesList).build();
    }

    @GET
    @Path("companies/info")
    public Response getCompaniesInfo(){
        List<CompanyInfoRep> companiesInfoList = new ArrayList();
        for(Company c : companies.values()){
            PriceInfo yesterdayPrices = c.getYesterdayPrices();
            PriceInfo todayPrices = c.getTodayPrices();

            companiesInfoList.add(new CompanyInfoRep(c.getName(),c.getExchange(),
                                                    todayPrices.getStartPrice(),todayPrices.getEndPrice(),todayPrices.getMinPrice(),todayPrices.getMaxPrice(),
                                                    yesterdayPrices.getStartPrice(),yesterdayPrices.getEndPrice(),yesterdayPrices.getMinPrice(),yesterdayPrices.getMaxPrice()));
        }
        if(companiesInfoList.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        else return Response.ok(companiesInfoList).build();
    }

    @GET
    @Path("company/{name}")
    public Response getCompany(@PathParam("name") String name){
        if(companies.containsKey(name)){
            Company c = companies.get(name);
            return Response.ok(new CompanyRep(c.getName(),c.getHost(),c.getPort(),c.getDescription(),c.getExchange())).build();
        }
        else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("company/{name}/info")
    public Response getInfo(@PathParam("name") String name){
        if(companies.containsKey(name)) {
            Company c = companies.get(name);
            PriceInfo yesterdayPrices = c.getYesterdayPrices();
            PriceInfo todayPrices = c.getTodayPrices();

            CompanyInfoRep companyInfo = new CompanyInfoRep(c.getName(),c.getExchange(),
                                                            todayPrices.getStartPrice(),todayPrices.getEndPrice(),todayPrices.getMinPrice(),todayPrices.getMaxPrice(),
                                                            yesterdayPrices.getStartPrice(),yesterdayPrices.getEndPrice(),yesterdayPrices.getMinPrice(),yesterdayPrices.getMaxPrice());
            return Response.ok(companyInfo).build();
        }
        else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("company/{name}")
    public Response putCompany(Company company){
        if(companies.containsKey(company.getName())) return Response.status(Response.Status.CONFLICT).build();
        companies.put(company.getName(),new Company(company.getName(),company.getHost(),company.getPort(),company.getDescription(),company.getExchange(),clock));
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("company/{name}/order/sell")
    public Response putCompanyOrderSell(Sell order){
        if(!(companies.containsKey(order.getCompany()))) return Response.status(Response.Status.NOT_FOUND).build();
        companies.get(order.getCompany()).addOrder(new Sell(order.getId(), order.getSeller(),order.getCompany(),order.getQuantity(),order.getPrice()));
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("company/{name}/order/buy")
    public Response putCompanyOrderBuy(Buy order){
        if(!(companies.containsKey(order.getCompany()))) return Response.status(Response.Status.NOT_FOUND).build();
        companies.get(order.getCompany()).addOrder(new Buy(order.getId(), order.getBuyer(),order.getCompany(),order.getQuantity(),order.getPrice()));
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("company/{name}/transaction")
    public Response putCompanyTransaction(Transaction transaction){
        if(!(companies.containsKey(transaction.getCompany()))) return Response.status(Response.Status.NOT_FOUND).build();
        companies.get(transaction.getCompany()).addTransaction(transaction);
        System.out.println(clock.getDay());
        return Response.status(Response.Status.CREATED).build();
    }
}