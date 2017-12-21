import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.PriorityQueue;

public class Exchange{
	
	private Map<String,Company> companies;
	private Map<String,Queue<Sell>> sell_orders;
	private Map<String,Queue<Buy>> buy_orders;

	public Exchange(){
		this.companies = new HashMap();
		this.sell_orders = new HashMap();
		this.buy_orders = new HashMap();
	}

	public Exchange(Map<Integer,Company> companies){
		this.companies = companies;
		this.sell_orders = new HashMap();
		this.buy_orders = new HashMap();
	}


	public void receiveSell(Sell order_sell){
		
	}

	public static void main(String[] args){
	}

}