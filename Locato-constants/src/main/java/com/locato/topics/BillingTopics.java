package com.locato.topics;

public final class BillingTopics {

    private BillingTopics() {
    }

    public static final String BILLING_REQUESTED = "billing.requested";

    public static final String BILLING_CREATED = "billing.created";

    public static final String PAYMENT_COMPLETED = "payment.completed";

    public static final String PAYMENT_FAILED = "payment.failed";

    public static final String REFUND_COMPLETED = "refund.completed";
    
   
    public static final String RECURRING_PAYMENT_REQUESTED = "recurring.payment.requested";
   

}