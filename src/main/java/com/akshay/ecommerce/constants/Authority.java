package com.akshay.ecommerce.constants;

public enum Authority {

    ADMIN("Admin"), SELLER("Seller"), CUSTOMER("Customer");
    private String authorityName;
    Authority(String authorityName) {
        this.authorityName = authorityName;
    }
    public String getAuthorityName() {
        return authorityName;
    }
}


