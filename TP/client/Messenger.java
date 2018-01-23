package client;

import client.Protos.MsgCS;
import client.Protos.Request_Login;
import client.Protos.Client;
import client.Protos.Reply_Login;
import client.Protos.OrderRequest;
import client.Protos.OrderReply;

public class Messenger {

    public static MsgCS newReqLogin() {
        return
            MsgCS.newBuilder()
            .setType("1")
            .setReqL(
                Request_Login.newBuilder()
                .setMsg("Request_Login")
                )
            .build();
    }

    public static MsgCS newClient(String user, String pass){
        return
            MsgCS.newBuilder()
            .setType("2")
            .setInfo(
                Client.newBuilder()
                .setUser(user)
                .setPass(pass)
                )
            .build();
    }

    public static MsgCS newRepLogin(boolean valid) {
        return
            MsgCS.newBuilder()
            .setType("4")
            .setRepL(
                Reply_Login.newBuilder()
                .setValid(valid)
                .setMsg("Request_Login")
                )
            .build();
    }

    public static MsgCS newOrderRequest(String user, String type, String company, int quantity, float price) {
        return
            MsgCS.newBuilder()
            .setCompany(company)
            .setType("3")
            .setOrderRequest(
                OrderRequest.newBuilder()
                .setType(type)
                .setCompanyId(company)
                .setQuantity(quantity)
                .setPrice(price)
                )
            .setInfo(
                Client.newBuilder()
                .setUser(user)
                )
            .build();
    }

    public static MsgCS newOrderReply(String user, String notification){
        return 
            MsgCS.newBuilder()
            .setType("4")
            .setOrderReply(
                OrderReply.newBuilder()
                .setUser(user)
                .setNotification(notification)
                )
            .build();
    }
}