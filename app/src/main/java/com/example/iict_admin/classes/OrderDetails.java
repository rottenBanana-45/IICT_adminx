package com.example.iict_admin.classes;

public class OrderDetails {

    public String display_name;
    public String order;
    public String tableno;
    public String oid;

    public String getTableno() {
        return tableno;
    }

    public void setTableno(String tableno) {
        this.tableno = tableno;
    }

    public OrderDetails(){
    }

    public OrderDetails(String display_name, String order) {
        this.display_name = display_name;
        this.order = order;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getOrder() {
        return order;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
