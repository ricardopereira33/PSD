package client;

public class Printer {

    public Printer() { }

    public void printMenuOrder(){
        System.out.println("===== New Order =====");
        System.out.println("1.\tSell");
        System.out.println("2.\tBuy");
        System.out.println("3.\tSubscribe Company");
        System.out.println("4.\tUnsubscribe Company");
        System.out.println("5.\tCompanies List");
        System.out.println("6.\tCompany Information");
        System.out.println("0.\tExit");
        System.out.print("Option: ");
    }

    public void clear(){
        for(int i = 0; i< 100 ; i++)
            System.out.println();
    }
}
