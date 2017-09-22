package your.group.vo;

import javax.validation.constraints.NotNull;

public class BuyVO {

    @NotNull
    private int petId;
    @NotNull
    private int customerId;

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
