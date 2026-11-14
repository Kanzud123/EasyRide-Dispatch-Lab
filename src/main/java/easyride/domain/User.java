/*abstract class means user plus driver  also inheritance bcz bar bar information likhte hobe na*/
package easyride.domain;

import java.util.Objects;

public abstract class User {
    private final String id;
    private String name;
    private String phone;
    private final UserRole role;
    private Money walletBalance;

    protected User(String id, String name, String phone, UserRole role, Money walletBalance) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.phone = Objects.requireNonNull(phone, "Phone cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.walletBalance = walletBalance != null ? walletBalance : Money.zeroBdt();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = Objects.requireNonNull(phone);
    }

    public UserRole getRole() {
        return role;
    }

    public Money getWalletBalance() {
        return walletBalance;
    }

    public void creditWallet(Money amount) {
        if (amount.isNegative()) {
            throw new IllegalArgumentException("Credit amount cannot be negative");
        }
        this.walletBalance = this.walletBalance.add(amount);
    }

    public void debitWallet(Money amount) {
        if (amount.isNegative()) {
            throw new IllegalArgumentException("Debit amount cannot be negative");
        }
        if (!this.walletBalance.isGreaterThanOrEqual(amount)) {
            throw new IllegalStateException("Insufficient wallet balance");
        }
        this.walletBalance = this.walletBalance.subtract(amount);
    }
}
