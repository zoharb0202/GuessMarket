package guessmarket.engine.dto;

public class PurchaseResultDto {
    private final double sharesCost;
    private final double commission;

    public PurchaseResultDto(double sharesCost, double commission) {
        this.sharesCost = sharesCost;
        this.commission = commission;
    }

    public double getSharesCost() {
        return sharesCost;
    }

    public double getCommission() {
        return commission;
    }

    public double getTotalPaid() {
        return sharesCost + commission;
    }
}
